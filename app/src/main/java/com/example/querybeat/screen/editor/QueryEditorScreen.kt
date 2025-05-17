package com.example.querybeat.screen.editor

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.querybeat.components.BaseScreen

@Composable
fun QueryEditorScreen(
    onRunQuery: (String) -> Unit
) {
    BaseScreen(title = "GraphQL Query Editor") { paddingValues ->

        // Use Box to fill the available space with padding from Scaffold
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp) // inner padding around content
        ) {
            val queryText = remember { mutableStateOf("") }

            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Make OutlinedTextField fill most of the space, with weight to take all space above button
                OutlinedTextField(
                    value = queryText.value,
                    onValueChange = { queryText.value = it },
                    label = { Text("Enter GraphQL Query") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f), // take all space except button
                    maxLines = Int.MAX_VALUE,
                    textStyle = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { onRunQuery(queryText.value) },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Run")
                }
            }
        }
    }
}



