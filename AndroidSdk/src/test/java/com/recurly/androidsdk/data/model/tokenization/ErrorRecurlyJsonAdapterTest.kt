package com.recurly.androidsdk.data.model.tokenization

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ErrorRecurlyJsonAdapterTest {

    private val gson = errorSafeGson()

    @Test
    fun errorAbsentLists_defaultToEmpty() {
        val error = gson.fromJson("""{"code":"500","message":"boom"}""", ErrorRecurly::class.java)

        assertThat(error.errorCode).isEqualTo("500")
        assertThat(error.errorMessage).isEqualTo("boom")
        assertThat(error.fields).isEmpty()
        assertThat(error.details).isEmpty()
    }

    @Test
    fun errorAbsentScalars_defaultToEmptyStrings() {
        val error = gson.fromJson("""{"fields":["number"]}""", ErrorRecurly::class.java)

        assertThat(error.errorCode).isEmpty()
        assertThat(error.errorMessage).isEmpty()
        assertThat(error.fields).containsExactly("number")
    }

    @Test
    fun errorNullTokens_defaultToEmpty() {
        val error = gson.fromJson(
            """{"code":null,"message":null,"fields":null,"details":null}""",
            ErrorRecurly::class.java
        )

        assertThat(error.errorCode).isEmpty()
        assertThat(error.errorMessage).isEmpty()
        assertThat(error.fields).isEmpty()
        assertThat(error.details).isEmpty()
    }

    @Test
    fun errorDetailsAbsentMessages_defaultToEmpty() {
        val error = gson.fromJson(
            """{"code":"x","message":"y","details":[{"field":"number"}]}""",
            ErrorRecurly::class.java
        )

        assertThat(error.details).hasSize(1)
        assertThat(error.details[0].field).isEqualTo("number")
        assertThat(error.details[0].messageList).isEmpty()
    }

    @Test
    fun errorDetailsDirectParse_defaultsAbsentMessagesToEmpty() {
        val details = errorSafeGson().fromJson("""{"field":"n"}""", ErrorDetails::class.java)

        assertThat(details?.field).isEqualTo("n")
        assertThat(details?.messageList).isEmpty()
    }

    @Test
    fun errorPresentValues_preserved() {
        val error = gson.fromJson(
            """{"code":"validation_failed","message":"Invalid card","fields":["number","cvv"],""" +
                """"details":[{"field":"number","messages":["Invalid card number"]}]}""",
            ErrorRecurly::class.java
        )

        assertThat(error.fields).containsExactly("number", "cvv").inOrder()
        assertThat(error.details).hasSize(1)
        assertThat(error.details[0].field).isEqualTo("number")
        assertThat(error.details[0].messageList).containsExactly("Invalid card number")
    }

    @Test
    fun errorWholeObjectNull_readsAsNull() {
        assertThat(gson.fromJson("null", ErrorRecurly::class.java)).isNull()
    }

    @Test
    fun errorExplicitNullKeyInResponse_readsAsNullError() {
        val response = gson.fromJson(
            """{"id":"tok_abc123","type":"credit_card","error":null}""",
            TokenizationResponse::class.java
        )

        assertThat(response.token).isEqualTo("tok_abc123")
        assertThat(response.error).isNull()
    }

    @Test
    fun errorNullDetailsElementAndUnknownKeys_skipped() {
        val error = gson.fromJson(
            """{"code":"x","unknown":"y","details":[null,{"field":"n","messages":["m"],"extra":1}]}""",
            ErrorRecurly::class.java
        )

        assertThat(error.errorCode).isEqualTo("x")
        assertThat(error.details).hasSize(1)
        assertThat(error.details[0].field).isEqualTo("n")
        assertThat(error.details[0].messageList).containsExactly("m")
    }

    @Test
    fun errorRoundTrip_preservesValues() {
        val json = """{"code":"validation_failed","message":"Invalid card","fields":["number","cvv"],""" +
            """"details":[{"field":"number","messages":["Invalid card number"]}]}"""

        val original = gson.fromJson(json, ErrorRecurly::class.java)
        val roundTripped = gson.fromJson(gson.toJson(original), ErrorRecurly::class.java)

        assertThat(roundTripped).isEqualTo(original)
    }
}