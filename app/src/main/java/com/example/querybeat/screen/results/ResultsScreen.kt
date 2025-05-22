package com.example.querybeat.screen.results

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.querybeat.components.BaseScreen
import com.example.querybeat.util.ResultsUiState
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

@Composable
fun ResultsScreen(query: String,
                  navController: NavController,
                  currentRoute: String,
                  drawerState: DrawerState) {
    val viewModel: ResultsViewModel = hiltViewModel()
    val resultState: ResultsUiState = viewModel.result.collectAsState().value

    // Default static query to run if no query is passed via navigation
    val staticQuery = remember {
        """
        query AllCountries {
          countries {
            code
            name
            emoji
          }
        }
        """.trimIndent()
    }

    // State for user input query and variables (for re-running queries on this screen)
    // Initialize userQuery with the navigated query, or the staticQuery if navigated query is blank
    var userQuery by remember { mutableStateOf(query.ifBlank { staticQuery }) }
    // userVariables should start blank, as variables are optional and not typically part of the initial navigated query string
    var userVariables by remember { mutableStateOf("") }

    // Run the query once when the screen is initially composed
    LaunchedEffect(Unit) { // Keyed on Unit to ensure it runs only once on initial composition
        Log.d("ResultsScreen", "LaunchedEffect - Running initial query: $userQuery")
        if (userQuery.isNotBlank()) {
            // Extract operation name from userQuery for the initial run
            val operationName = if (userQuery.contains("query ") || userQuery.contains("mutation ")) {
                val regex = "(query|mutation)\\s+([a-zA-Z_][a-zA-Z0-9_]*)".toRegex()
                regex.find(userQuery)?.groups?.get(2)?.value ?: "RawGraphQLQuery"
            } else {
                "RawGraphQLQuery" // Default if no explicit operation type/name
            }
            // For the initial run, variables are expected to be null or empty
            viewModel.runQuery(userQuery, null, operationName)
        }
    }
    BaseScreen(
        navController = navController,
        currentRoute = currentRoute,
        drawerState = drawerState,
        title = "Query Results",
    ){ paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()) // Enable scrolling for long content
        ) {
            Text("Current Query:", style = MaterialTheme.typography.titleMedium)
            // Display the current query that was executed or can be re-executed
            Text(userQuery, style = MaterialTheme.typography.bodySmall)

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            Text("Edit Query (Optional):", style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(
                value = userQuery,
                onValueChange = { userQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 80.dp, max = 200.dp), // Allow multiline but constrain height
                placeholder = { Text("Enter your GraphQL query here...") },
                singleLine = false // Allow multi-line input
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Type New Variables (Optional):", style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(
                value = userVariables,
                onValueChange = { userVariables = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp),
                placeholder = { Text("""Enter variables JSON, e.g. {"code":"CA"}""") },
                maxLines = 5
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    if (userQuery.isNotBlank()) {
                        Log.d("ResultsScreen", "Button Clicked - Re-running query: $userQuery with variables: $userVariables")

                        // Extract operation name from userQuery (repeated for clarity/independence of button click)
                        val operationName = if (userQuery.contains("query ") || userQuery.contains("mutation ")) {
                            val regex = "(query|mutation)\\s+([a-zA-Z_][a-zA-Z0-9_]*)".toRegex()
                            regex.find(userQuery)?.groups?.get(2)?.value ?: "RawGraphQLQuery"
                        } else {
                            "RawGraphQLQuery" // Default if no explicit operation type/name
                        }

                        // Pass userQuery as the primary query string.
                        // Pass userVariables only if it's not blank, otherwise it's null (no variables).
                        viewModel.runQuery(
                            userQuery,
                            userVariables.takeIf { it.isNotBlank() },
                            operationName
                        )
                    }
                },
                enabled = userQuery.isNotBlank(), // Button is enabled only if there's a query to run
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Re-run Query")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Result:", style = MaterialTheme.typography.titleMedium)

            // Display query result based on the UI state
            when (resultState) {
                is ResultsUiState.Idle -> {
                    Log.d("ResultsScreen", "Result State: Idle")
                    Text("Waiting for query...")
                }
                is ResultsUiState.Loading -> {
                    Log.d("ResultsScreen", "Result State: Loading")
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                }
                is ResultsUiState.Success -> {
                    Log.d("ResultsScreen", "Result State: Success - Data: ${resultState.data}")
                    // Use prettyPrintJson to format the JSON data for better readability
                    Text(prettyPrintJson(resultState.data))
                }
                is ResultsUiState.Error -> {
                    Log.e("ResultsScreen", "Result State: Error - Message: ${resultState.message}")
                    Text(
                        "Error: ${resultState.message}",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

/**
 * Utility function to pretty print a JSON string.
 * It attempts to parse the string into a JsonElement and then re-encode it with pretty printing.
 * If parsing fails, it returns the original string.
 */
fun prettyPrintJson(jsonString: String): String {
    return try {
        // Parse the string into a JsonElement
        val jsonElement = Json.parseToJsonElement(jsonString)
        // Use a Json instance with prettyPrint enabled to encode it back
        Json { prettyPrint = true }.encodeToString(JsonElement.serializer(), jsonElement)
    } catch (e: Exception) {
        // If parsing fails (e.g., it's not valid JSON), return the original string
        jsonString
    }
}