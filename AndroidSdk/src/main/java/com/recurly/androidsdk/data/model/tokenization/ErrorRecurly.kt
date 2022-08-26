package com.recurly.androidsdk.data.model.tokenization

import com.google.gson.annotations.SerializedName
import com.recurly.androidsdk.data.model.tokenization.ErrorDetails

/**
 * This is the main Error model from tokenization, which contains code and message to make it
 * more understandable and easier to catch
 */

data class ErrorRecurly(
    @SerializedName("code")
    val errorCode: String,
    @SerializedName("message")
    val errorMessage: String,
    @SerializedName("fields")
    val fields: List<String>,
    @SerializedName("details")
    val details: List<ErrorDetails>
)
