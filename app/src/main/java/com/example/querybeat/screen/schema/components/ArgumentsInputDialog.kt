package com.example.querybeat.screen.schema.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.querybeat.data.model.GraphQLField

@Composable
fun ArgumentsInputDialog(
    field: GraphQLField,
    onDismiss: () -> Unit,
    onQueryGenerated: (String) -> Unit
) {
    val argValues = remember(field.args) {
        mutableStateMapOf<String, String>().apply {
            field.args.forEach { arg -> put(arg.name, "") }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Enter Arguments for '${field.name}'", style = MaterialTheme.typography.titleLarge)
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                field.args.forEach { arg ->
                    OutlinedTextField(
                        value = argValues[arg.name].orEmpty(),
                        onValueChange = { newValue -> argValues[arg.name] = newValue },
                        label = { Text("${arg.name}: ${arg.formattedType}") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        singleLine = true,
                        keyboardOptions = if (arg.typeName == "Int" || arg.typeName == "Float") {
                            KeyboardOptions(keyboardType = KeyboardType.Number)
                        } else {
                            KeyboardOptions.Default
                        }
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                val generatedQuery = generateQueryForField(field, argValues)
                onQueryGenerated(generatedQuery)
            }) {
                Text("Run Query")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}