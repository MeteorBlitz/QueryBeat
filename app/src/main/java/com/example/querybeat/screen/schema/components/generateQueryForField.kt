package com.example.querybeat.screen.schema.components

import com.example.querybeat.data.model.GraphQLField
import java.util.Locale

fun generateQueryForField(field: GraphQLField, providedArgs: Map<String, String>): String {
    val fieldName = field.name

    val argsPart = if (field.args.isNotEmpty()) {
        field.args.joinToString(", ") { arg ->
            val userValue = providedArgs[arg.name]?.trim()

            val formattedValue = if (userValue.isNullOrBlank()) {
                when (arg.name) {
                    "code" -> when (field.name) {
                        "continent" -> "\"NA\""
                        "country" -> "\"US\""
                        else -> "\"\""
                    }
                    "filter" -> "{}"
                    else -> when (arg.typeName) {
                        "ID", "String" -> "\"\""
                        "Boolean" -> "false"
                        "Int", "Float" -> "0"
                        else -> "null"
                    }
                }
            } else {
                when (arg.typeName) {
                    "ID", "String" -> "\"$userValue\""
                    "Boolean" -> userValue.lowercase()
                    "Int", "Float" -> userValue
                    else -> userValue
                }
            }

            "${arg.name}: $formattedValue"
        }
    } else ""

    val selectionSet: String = when (field.typeName.removeSuffix("!").removePrefix("[").removeSuffix("]")) {
        "Country" -> "{ code name emoji capital continent { name } }"
        "Continent" -> "{ code name countries { name code } }"
        "Language" -> "{ code name native }"
        "State" -> "{ code name }"
        "Subdivision" -> "{ code name }"
        "User" -> "{ id name email }"
        else -> ""
    }

    val operationType = "query"
    val operationName = fieldName.replaceFirstChar {
        if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString()
    } + "Generated"

    return """
        $operationType $operationName {
          $fieldName${if (argsPart.isNotBlank()) "($argsPart)" else ""} $selectionSet
        }
    """.trimIndent()
}
