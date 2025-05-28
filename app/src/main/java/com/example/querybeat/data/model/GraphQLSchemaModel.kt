package com.example.querybeat.data.model

/**
 * Represents different kinds of GraphQL types.
 */
enum class GraphQLTypeKind {
    SCALAR,
    OBJECT,
    INTERFACE,
    UNION,
    ENUM,
    INPUT_OBJECT,
    LIST,
    NON_NULL,

    // Additional categories for Explorer UI clarity
    QUERY,
    MUTATION,
    SUBSCRIPTION
}

/**
 * Represents a field within an Object, Interface, or root Query/Mutation/Subscription type.
 */
data class GraphQLField(
    val name: String,
    val typeName: String,         // The raw type name, e.g., "String", "Country!"
    val isList: Boolean = false,  // True if the field is a list type, e.g., [Country!]!
    val isNonNull: Boolean = false, // True if the field is non-nullable, e.g., String!
    val args: List<GraphQLArgument> = emptyList(),  // Arguments the field accepts
    val description: String? = null
) {
    /**
     * Formats the field type with list and non-null decorations.
     * Example: "[Country!]!"
     */
    val formattedType: String
        get() {
            var formatted = typeName
            if (isList) formatted = "[$formatted]"
            if (isNonNull) formatted += "!"
            return formatted
        }
}

/**
 * Represents an argument for a GraphQL field.
 */
data class GraphQLArgument(
    val name: String,
    val typeName: String,          // Argument type name, e.g., "ID!", "String"
    val defaultValue: String? = null,
    val isNonNull: Boolean = false // True if argument is non-nullable
) {
    /**
     * Formats the argument type with non-null and default value if present.
     */
    val formattedType: String
        get() {
            var formatted = typeName
            if (isNonNull) formatted += "!"
            if (defaultValue != null) formatted += " = $defaultValue"
            return formatted
        }
}

/**
 * Represents a GraphQL Type definition (Object, Scalar, Enum, etc.).
 */
data class GraphQLType(
    val name: String,
    val kind: GraphQLTypeKind,
    val description: String? = null,

    // Fields for OBJECT, INTERFACE, QUERY, MUTATION, SUBSCRIPTION types
    val fields: List<GraphQLField> = emptyList(),

    // Enum values for ENUM kind
    val enumValues: List<String> = emptyList(),

    // Input fields for INPUT_OBJECT kind
    val inputFields: List<GraphQLArgument> = emptyList(),

    // Possible types for UNION or INTERFACE kinds
    val possibleTypes: List<String> = emptyList(),

    // Used for LIST or NON_NULL wrappers, holds the underlying type name
    val ofType: String? = null
)
