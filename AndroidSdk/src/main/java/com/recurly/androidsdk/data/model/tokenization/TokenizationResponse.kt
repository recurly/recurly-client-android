package com.recurly.androidsdk.data.model.tokenization

import com.google.gson.annotations.SerializedName

/**
 * Tokenization Response
 *
 * It´s the data model that is turned back from the server
 *
 * If the tokenization is successful it will send back the type and id, and error will be null
 *
 * If the tokenization was not successful id and type will be null, and error will be fulfilled
 *
 */

data class TokenizationResponse(
    @SerializedName("type")
    val type: String ?= "",
    @SerializedName("id")
    val token: String ?= "",
    @SerializedName("error")
    val error: ErrorRecurly
)
