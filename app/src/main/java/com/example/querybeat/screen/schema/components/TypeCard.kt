package com.example.querybeat.screen.schema.components

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.querybeat.data.model.GraphQLField
import com.example.querybeat.data.model.GraphQLType
import com.example.querybeat.data.model.GraphQLTypeKind
import com.example.querybeat.navigation.Screen
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TypeCard(type: GraphQLType, navController: NavController) {

    var expanded by remember { mutableStateOf(false) }
    var fieldForArgsInput by remember { mutableStateOf<GraphQLField?>(null) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        onClick = { expanded = !expanded }
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val kindDisplayName = type.kind.name.lowercase(Locale.ROOT).replace("_", " ")
                Text(
                    text = "$kindDisplayName ",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
                Text(
                    text = type.name,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.weight(1f))
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (expanded) "Collapse" else "Expand"
                )
            }
            type.description?.let {
                if (expanded) {
                    Text(it, style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace), modifier = Modifier.padding(top = 4.dp))
                }
            }

            if (expanded) {
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                // IMPORTANT FIX: Apply a max height to the inner scrollable Column
                Column(modifier = Modifier
                    .heightIn(max = 300.dp) // <--- ADD THIS LINE with a max height
                    .verticalScroll(rememberScrollState())) {
                    when (type.kind) {
                        GraphQLTypeKind.OBJECT, GraphQLTypeKind.INTERFACE, GraphQLTypeKind.QUERY, GraphQLTypeKind.MUTATION, GraphQLTypeKind.SUBSCRIPTION -> {
                            if (type.fields.isNotEmpty()) {
                                Text("Fields:", style = MaterialTheme.typography.titleSmall, modifier = Modifier.padding(bottom = 4.dp))
                                type.fields.forEach { field ->
                                    FieldRow(
                                        field = field,
                                        isQueryField = type.kind == GraphQLTypeKind.QUERY || type.kind == GraphQLTypeKind.MUTATION || type.kind == GraphQLTypeKind.SUBSCRIPTION,
                                        navController = navController,
                                        onShowArgsDialog = { clickedField ->
                                            fieldForArgsInput = clickedField
                                        }
                                    )
                                }
                            } else {
                                Text("No fields defined.", style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace), modifier = Modifier.padding(start = 8.dp))
                            }
                        }
                        GraphQLTypeKind.INPUT_OBJECT -> {
                            if (type.inputFields.isNotEmpty()) {
                                Text("Input Fields:", style = MaterialTheme.typography.titleSmall, modifier = Modifier.padding(bottom = 4.dp))
                                type.inputFields.forEach { inputField ->
                                    Text(
                                        text = "  ${inputField.name}: ${inputField.formattedType}",
                                        style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace),
                                        modifier = Modifier.padding(start = 8.dp, top = 4.dp, bottom = 4.dp)
                                    )
                                }
                            } else {
                                Text("No input fields defined.", style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace), modifier = Modifier.padding(start = 8.dp))
                            }
                        }
                        GraphQLTypeKind.ENUM -> {
                            if (type.enumValues.isNotEmpty()) {
                                Text("Enum Values:", style = MaterialTheme.typography.titleSmall, modifier = Modifier.padding(bottom = 4.dp))
                                type.enumValues.forEach { enumValue ->
                                    Text(" - $enumValue", style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace), modifier = Modifier.padding(start = 8.dp))
                                }
                            } else {
                                Text("No enum values defined.", style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace), modifier = Modifier.padding(start = 8.dp))
                            }
                        }
                        GraphQLTypeKind.SCALAR -> {
                            Text("This is a scalar type.", style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace), modifier = Modifier.padding(start = 8.dp))
                        }
                        GraphQLTypeKind.UNION -> {
                            if (type.possibleTypes.isNotEmpty()) {
                                Text("Possible Types:", style = MaterialTheme.typography.titleSmall, modifier = Modifier.padding(bottom = 4.dp))
                                type.possibleTypes.forEach { possibleType ->
                                    Text(" - $possibleType", style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace), modifier = Modifier.padding(start = 8.dp))
                                }
                            } else {
                                Text("No possible types defined.", style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace), modifier = Modifier.padding(start = 8.dp))
                            }
                        }
                        GraphQLTypeKind.LIST, GraphQLTypeKind.NON_NULL -> {
                            Text("Wraps type: ${type.ofType}", style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace), modifier = Modifier.padding(start = 8.dp))
                        }
                        else -> {
                            Text("Unhandled type kind: ${type.kind.name}", style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace), modifier = Modifier.padding(start = 8.dp))
                        }
                    }
                }
            }
        }
    }

    fieldForArgsInput?.let { field ->
        ArgumentsInputDialog(
            field = field,
            onDismiss = { fieldForArgsInput = null },
            onQueryGenerated = { generatedQuery ->
                fieldForArgsInput = null
                navController.navigate(Screen.Results.route + "?query=${Uri.encode(generatedQuery)}")
            }
        )
    }
}