package com.example.querybeat.screen.schema

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.querybeat.components.BaseScreen
import com.example.querybeat.data.model.GraphQLArgument
import com.example.querybeat.data.model.GraphQLField
import com.example.querybeat.data.model.GraphQLType
import com.example.querybeat.data.model.GraphQLTypeKind
import com.example.querybeat.screen.schema.components.TypeCard
import com.example.querybeat.util.SchemaUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun SchemaExplorerScreen(
    navController: NavController,
    currentRoute: String,
    drawerState: DrawerState,
    viewModel: SchemaExplorerViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsState().value

    var parsedSchema by remember { mutableStateOf<List<GraphQLType>>(emptyList()) }
    var isParsed by remember { mutableStateOf(false) }

    LaunchedEffect(uiState) {
        if (uiState is SchemaUiState.Success) {
            isParsed = false
            parsedSchema = withContext(Dispatchers.IO) {
                parseGraphQLSchemaString(uiState.schemaContent)
            }
            isParsed = true
        }
    }

    BaseScreen(
        navController = navController,
        currentRoute = currentRoute,
        drawerState = drawerState,
        title = "Schema Explorer",
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = "GraphQL Schema Definition",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary
            )
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))

            when (uiState) {
                is SchemaUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is SchemaUiState.Success -> {
                    if (!isParsed) {
                        // Show loading spinner or blank space while parsing
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    } else {
                        if (parsedSchema.isEmpty()) {
                            // Show raw schema text ONLY if parsing done but no types found
                            val highlightedText = highlightGraphQLSchema(uiState.schemaContent)
                            Surface(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(8.dp))
                                    .padding(12.dp)
                                    .verticalScroll(rememberScrollState())
                            ) {
                                Text(
                                    text = highlightedText,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontFamily = FontFamily.Monospace,
                                        lineHeight = 20.sp
                                    )
                                )
                            }
                        } else {
                            // Show parsed types UI
                            SchemaDisplay(parsedSchema = parsedSchema, navController = navController)
                        }
                    }
                }


                is SchemaUiState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Error loading schema:\n${uiState.message}",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SchemaDisplay(parsedSchema: List<GraphQLType>, navController: NavController) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(8.dp))
            .padding(12.dp)
    ) {
        val userDefinedTypes = parsedSchema.filter { !it.name.startsWith("__") }

        items(userDefinedTypes) { type ->
            TypeCard(type = type, navController = navController)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

/** Parses a GraphQL schema string and returns a list of GraphQLType */
fun parseGraphQLSchemaString(schemaString: String): List<GraphQLType> {
    val types = mutableListOf<GraphQLType>()
    val lines = schemaString.lines()
        .map { it.trim() }
        .filter { it.isNotBlank() && !it.startsWith("\"\"\"") && !it.startsWith("#") }

    val typeDefinitionRegex = "(type|input|scalar|enum|interface|union)\\s+([a-zA-Z_][a-zA-Z0-9_]*)".toRegex()

    // Critical fix: field regex to also capture arguments
    val fieldRegex = "([a-zA-Z_][a-zA-Z0-9_]*)\\s*(?:\\(([^)]*)\\))?\\s*:\\s*([a-zA-Z_][a-zA-Z0-9_!\\[\\]]*)(?:\\s*=\\s*(.+))?".toRegex()
    val argRegex = "([a-zA-Z_][a-zA-Z0-9_]*)\\s*:\\s*([a-zA-Z_][a-zA-Z0-9_!\\[\\]]*)(?:\\s*=\\s*([^,)]*))?".toRegex()

    var currentType: GraphQLType? = null
    var collectingBlockContent = false

    lines.forEach { line ->
        val typeMatch = typeDefinitionRegex.find(line)
        if (typeMatch != null) {
            if (currentType != null && !collectingBlockContent && currentType.kind != GraphQLTypeKind.SCALAR && currentType.kind != GraphQLTypeKind.UNION) {
                types.add(currentType)
            }

            val kindString = typeMatch.groupValues[1]
            val typeName = typeMatch.groupValues[2]

            val kind = when {
                typeName == "Query" -> GraphQLTypeKind.QUERY
                typeName == "Mutation" -> GraphQLTypeKind.MUTATION
                typeName == "Subscription" -> GraphQLTypeKind.SUBSCRIPTION
                kindString == "type" -> GraphQLTypeKind.OBJECT
                kindString == "input" -> GraphQLTypeKind.INPUT_OBJECT
                kindString == "scalar" -> GraphQLTypeKind.SCALAR
                kindString == "enum" -> GraphQLTypeKind.ENUM
                kindString == "interface" -> GraphQLTypeKind.INTERFACE
                kindString == "union" -> GraphQLTypeKind.UNION
                else -> GraphQLTypeKind.OBJECT
            }

            currentType = GraphQLType(name = typeName, kind = kind)

            if (kind == GraphQLTypeKind.SCALAR || kind == GraphQLTypeKind.UNION) {
                if (kind == GraphQLTypeKind.UNION && line.contains("=")) {
                    val possibleTypesPart = line.substringAfter("=").trim()
                    val possibleTypes = possibleTypesPart.split("|").map { it.trim() }.filter { it.isNotBlank() }
                    currentType = currentType.copy(possibleTypes = possibleTypes)
                }
                types.add(currentType)
                currentType = null
                collectingBlockContent = false
            } else {
                collectingBlockContent = line.contains("{")
            }

        } else if (line.contains("{") && currentType != null) {
            collectingBlockContent = true
        } else if (line.contains("}") && currentType != null) {
            collectingBlockContent = false
            types.add(currentType)
            currentType = null
        } else if (collectingBlockContent && currentType != null) {
            val fieldMatch = fieldRegex.find(line)
            if (fieldMatch != null) {
                val fieldName = fieldMatch.groupValues[1]
                val argsString = fieldMatch.groupValues.getOrNull(2)?.takeIf { it.isNotBlank() }
                val rawTypeName = fieldMatch.groupValues[3]
                val defaultValue = fieldMatch.groupValues.getOrNull(4)?.takeIf { it.isNotBlank() }

                val isNonNull = rawTypeName.endsWith("!")
                val isList = rawTypeName.startsWith("[") && rawTypeName.endsWith("]")
                val actualTypeName = rawTypeName.removePrefix("[").removeSuffix("!").removeSuffix("]")

                val args = mutableListOf<GraphQLArgument>()
                argsString?.split(",")?.forEach { argPart ->
                    val argMatch = argRegex.find(argPart.trim())
                    if (argMatch != null) {
                        val argName = argMatch.groupValues[1]
                        val rawArgType = argMatch.groupValues[2]
                        val argDefaultValue = argMatch.groupValues.getOrNull(3)?.takeIf { it.isNotBlank() }

                        val isArgNonNull = rawArgType.endsWith("!")
                        val actualArgType = rawArgType.removeSuffix("!")
                        args.add(GraphQLArgument(argName, actualArgType, argDefaultValue, isArgNonNull))
                    }
                }

                val newField = GraphQLField(fieldName, actualTypeName, isList, isNonNull, args)

                when (currentType.kind) {
                    GraphQLTypeKind.OBJECT,
                    GraphQLTypeKind.INTERFACE,
                    GraphQLTypeKind.QUERY,
                    GraphQLTypeKind.MUTATION,
                    GraphQLTypeKind.SUBSCRIPTION -> {
                        currentType = currentType.copy(fields = currentType.fields.orEmpty() + newField)
                    }
                    GraphQLTypeKind.INPUT_OBJECT -> {
                        currentType = currentType.copy(inputFields = currentType.inputFields.orEmpty() + GraphQLArgument(fieldName, actualTypeName, defaultValue, isNonNull))
                    }
                    else -> {
                        // Do nothing for scalar, union, enum here
                    }
                }
            }
        }
    }

    if (currentType != null) {
        types.add(currentType)
    }

    return types
}

/** Highlight keywords in a raw GraphQL schema string using AnnotatedString */
fun highlightGraphQLSchema(schemaString: String): androidx.compose.ui.text.AnnotatedString {
    val keywords = setOf(
        "type", "query", "mutation", "subscription", "interface", "enum",
        "scalar", "input", "union", "on", "implements", "true", "false", "null"
    )

    return buildAnnotatedString {
        val tokens = schemaString.split("\\s+".toRegex())
        tokens.forEachIndexed { index, token ->
            if (token.trim() in keywords) {
                withStyle(style = SpanStyle(color = Color(0xFF6A1B9A), fontFamily = FontFamily.Monospace)) {
                    append(token)
                }
            } else if (token.startsWith("#")) {
                withStyle(style = SpanStyle(color = Color.Gray)) {
                    append(token)
                }
            } else {
                append(token)
            }
            if (index != tokens.lastIndex) append(" ")
        }
    }
}
