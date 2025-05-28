package com.example.querybeat.screen.schema.components

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.querybeat.data.model.GraphQLField
import com.example.querybeat.navigation.Screen

@Composable
fun FieldRow(
    field: GraphQLField,
    isQueryField: Boolean,
    navController: NavController,
    onShowArgsDialog: (GraphQLField) -> Unit // This lambda tells the parent to show the args dialog
) {
    val fieldContent = buildAnnotatedString {
        withStyle(SpanStyle(color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.SemiBold)) {
            append(field.name)
        }
        append(": ")
        withStyle(SpanStyle(color = MaterialTheme.colorScheme.tertiary, fontWeight = FontWeight.Normal)) {
            append(field.formattedType)
        }
        if (field.args.isNotEmpty()) {
            append("(")
            field.args.forEachIndexed { index, arg ->
                withStyle(SpanStyle(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f))) {
                    append("${arg.name}: ${arg.formattedType}")
                }
                if (index < field.args.lastIndex) {
                    append(", ")
                }
            }
            append(")")
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp)
            .let {
                if (isQueryField) { // Only query/mutation/subscription fields are clickable
                    it.clickable {
                        if (field.args.isNotEmpty()) {
                            // If this field has arguments, trigger the dialog by calling the lambda
                            onShowArgsDialog(field)
                        } else {
                            // If no arguments, generate query with empty args and navigate directly
                            val generatedQuery = generateQueryForField(field, emptyMap())
                            navController.navigate(Screen.Results.route + "?query=${Uri.encode(generatedQuery)}")
                        }
                    }
                } else it // If not a query field, it's not clickable to run a query
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = fieldContent,
            style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace)
        )
        if (isQueryField) {
            Spacer(modifier = Modifier.weight(1f))
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Run Query")
        }
    }
}