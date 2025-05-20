package com.example.querybeat.graphql

import android.util.Log
import com.apollographql.apollo.api.Adapter
import com.apollographql.apollo.api.CompiledField
import com.apollographql.apollo.api.CustomScalarAdapters
import com.apollographql.apollo.api.ObjectType
import com.apollographql.apollo.api.Query
import com.apollographql.apollo.api.json.JsonReader
import com.apollographql.apollo.api.json.JsonWriter
import kotlinx.serialization.json.* // Necessary for JsonElement, JsonObject, JsonArray, JsonPrimitive, JsonNull

class RawStringQuery(
    private val queryString: String,
    private val operationNameString: String,
    private val queryVariables: Map<String, JsonElement>,
    private val rootOperationSchemaTypeName: String
) : Query<RawStringQuery.Data> {

    data class Data(val rawData: JsonObject?) : Query.Data

    override fun document(): String = queryString

    override fun name(): String = operationNameString

    override fun id(): String = operationNameString // Use stable id

    override fun serializeVariables(
        writer: JsonWriter,
        customScalarAdapters: CustomScalarAdapters,
        withDefaultValues: Boolean
    ) {
        Log.d("RawStringQuery", "serializeVariables: Function entered!")
        Log.d("RawStringQuery", "Serializing variables: $queryVariables")

        try {
            // DO NOT call writer.beginObject() or writer.endObject() here.
            // Apollo already handles the outer object for variables.
            for ((key, value) in queryVariables) {
                Log.d("RawStringQuery", "serializeVariables: writing key = $key, value = $value")
                writer.name(key)
                writeJsonElementUsingApolloInternal(writer, value, customScalarAdapters)
            }
            Log.d("RawStringQuery", "serializeVariables: finished writing variables.")

        } catch (e: Exception) {
            Log.e("RawStringQuery", "Error during serialization: ${e.message}", e)
        }
    }

    private fun writeJsonElementUsingApolloInternal(
        writer: JsonWriter,
        element: JsonElement,
        customScalarAdapters: CustomScalarAdapters
    ) {
        when (element) {
            is JsonObject -> {
                writer.beginObject()
                element.forEach { (key, value) ->
                    writer.name(key)
                    writeJsonElementUsingApolloInternal(writer, value, customScalarAdapters)
                }
                writer.endObject()
            }
            is JsonArray -> {
                writer.beginArray()
                element.forEach { item ->
                    writeJsonElementUsingApolloInternal(writer, item, customScalarAdapters)
                }
                writer.endArray()
            }
            is JsonPrimitive -> {
                if (element.isString) {
                    writer.value(element.content)
                } else if (element.booleanOrNull != null) {
                    writer.value(element.boolean)
                } else if (element.longOrNull != null) {
                    writer.value(element.long)
                } else if (element.doubleOrNull != null) {
                    writer.value(element.double)
                } else {
                    writer.nullValue()
                }
            }
            is JsonNull -> {
                writer.nullValue()
            }
        }
    }

    override fun adapter(): Adapter<Data> = object : Adapter<Data> {
        override fun fromJson(reader: JsonReader, customScalarAdapters: CustomScalarAdapters): Data {
            val jsonElement = reader.toJsonElement() // This now correctly calls the *single* extension function
            Log.d("RawStringQuery", "fromJson received JsonElement: $jsonElement") // ADDED LOG
            // Ensure jsonElement is indeed a JsonObject here. It should be.
            if (jsonElement !is JsonObject) {
                Log.e("RawStringQuery", "fromJson: Expected JsonObject but got ${jsonElement::class.simpleName}")
                // Handle this unexpected case, maybe return Data(null) or throw
                return Data(null)
            }
            return Data(jsonElement)
        }

        override fun toJson(writer: JsonWriter, customScalarAdapters: CustomScalarAdapters, value: Data) {
            writer.beginObject()
            value.rawData?.forEach { (key, jsonElement) ->
                writer.name(key)
                writeJsonElementUsingApolloInternal(writer, jsonElement, customScalarAdapters)
            }
            writer.endObject()
        }
    }

    override fun rootField(): CompiledField {
        val schemaTypeForRootField = ObjectType.Builder(name = rootOperationSchemaTypeName).build()
        return CompiledField.Builder(name = "data", type = schemaTypeForRootField).build()
    }
}

// Extension function to convert Apollo JsonReader to kotlinx.serialization JsonElement
// (ENSURE ONLY ONE COPY OF THIS FUNCTION EXISTS IN YOUR PROJECT)
fun JsonReader.toJsonElement(): JsonElement = when (peek()) {
    JsonReader.Token.BEGIN_OBJECT -> {
        beginObject()
        val content = mutableMapOf<String, JsonElement>()
        while (hasNext()) {
            val name = nextName()
            content[name] = toJsonElement()
        }
        endObject()
        JsonObject(content)
    }
    JsonReader.Token.BEGIN_ARRAY -> {
        beginArray()
        val list = mutableListOf<JsonElement>()
        while (hasNext()) {
            list.add(toJsonElement())
        }
        endArray()
        JsonArray(list)
    }
    JsonReader.Token.BOOLEAN -> JsonPrimitive(nextBoolean())
    JsonReader.Token.NUMBER -> {
        val numberStr = nextString() ?: ""
        numberStr.toLongOrNull()?.let { JsonPrimitive(it) }
            ?: numberStr.toDoubleOrNull()?.let { JsonPrimitive(it) }
            ?: JsonPrimitive(numberStr)
    }
    JsonReader.Token.STRING -> JsonPrimitive(nextString() ?: "")
    JsonReader.Token.NULL -> {
        nextNull()
        JsonNull
    }
    else -> throw IllegalStateException("Unexpected token: ${peek()} while parsing to JsonElement")
}