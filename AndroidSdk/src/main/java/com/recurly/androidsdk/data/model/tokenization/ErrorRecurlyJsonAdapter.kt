package com.recurly.androidsdk.data.model.tokenization

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter

/**
 * Defaults absent/null JSON keys to `""`/empty lists; Gson's reflective adapter would leave the
 * non-null Kotlin properties at their JVM defaults.
 */
internal class ErrorRecurlyTypeAdapter : TypeAdapter<ErrorRecurly>() {

    private val detailsAdapter = ErrorDetailsTypeAdapter()

    override fun read(reader: JsonReader): ErrorRecurly? {
        if (reader.peek() == JsonToken.NULL) {
            reader.nextNull()
            return null
        }
        var code = ""
        var message = ""
        var fields: List<String> = emptyList()
        var details: List<ErrorDetails> = emptyList()
        reader.beginObject()
        while (reader.hasNext()) {
            when (reader.nextName()) {
                "code" -> code = reader.readStringOrEmpty()
                "message" -> message = reader.readStringOrEmpty()
                "fields" -> fields = reader.readStringListOrEmpty()
                "details" -> {
                    if (reader.peek() == JsonToken.NULL) {
                        reader.nextNull()
                    } else {
                        val parsed = mutableListOf<ErrorDetails>()
                        reader.beginArray()
                        while (reader.hasNext()) {
                            detailsAdapter.read(reader)?.let(parsed::add)
                        }
                        reader.endArray()
                        details = parsed
                    }
                }
                else -> reader.skipValue()
            }
        }
        reader.endObject()
        return ErrorRecurly(code, message, fields, details)
    }

    override fun write(writer: JsonWriter, value: ErrorRecurly) {
        writer.beginObject()
        writer.name("code").value(value.errorCode)
        writer.name("message").value(value.errorMessage)
        writer.name("fields")
        writer.beginArray()
        value.fields.forEach { writer.value(it) }
        writer.endArray()
        writer.name("details")
        writer.beginArray()
        value.details.forEach { detailsAdapter.write(writer, it) }
        writer.endArray()
        writer.endObject()
    }
}

/**
 * Defaults absent/null JSON keys to `""`/empty lists, matching the [ErrorRecurly] adapter contract.
 */
internal class ErrorDetailsTypeAdapter : TypeAdapter<ErrorDetails>() {

    override fun read(reader: JsonReader): ErrorDetails? {
        if (reader.peek() == JsonToken.NULL) {
            reader.nextNull()
            return null
        }
        var field = ""
        var messages: List<String> = emptyList()
        reader.beginObject()
        while (reader.hasNext()) {
            when (reader.nextName()) {
                "field" -> field = reader.readStringOrEmpty()
                "messages" -> messages = reader.readStringListOrEmpty()
                else -> reader.skipValue()
            }
        }
        reader.endObject()
        return ErrorDetails(field, messages)
    }

    override fun write(writer: JsonWriter, value: ErrorDetails) {
        writer.beginObject()
        writer.name("field").value(value.field)
        writer.name("messages")
        writer.beginArray()
        value.messageList.forEach { writer.value(it) }
        writer.endArray()
        writer.endObject()
    }
}

private fun JsonReader.readStringOrEmpty(): String =
    if (peek() == JsonToken.NULL) {
        nextNull()
        ""
    } else {
        nextString()
    }

private fun JsonReader.readStringListOrEmpty(): List<String> {
    if (peek() == JsonToken.NULL) {
        nextNull()
        return emptyList()
    }
    val values = mutableListOf<String>()
    beginArray()
    while (hasNext()) values.add(readStringOrEmpty())
    endArray()
    return values
}

/**
 * [Gson] with the [ErrorRecurly]/[ErrorDetails] adapters registered; shared by the Retrofit
 * converter and the tokenization services.
 */
internal fun errorSafeGson(): Gson = GsonBuilder()
    .registerTypeAdapter(ErrorRecurly::class.java, ErrorRecurlyTypeAdapter())
    .registerTypeAdapter(ErrorDetails::class.java, ErrorDetailsTypeAdapter())
    .create()