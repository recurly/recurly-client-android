package com.recurly.androidsdk.data.model.tokenization

import com.google.gson.annotations.SerializedName

/**
 * The Error Details show the field from the error and why it turns back an error inside
 * the messages field
 */

data class ErrorDetails(
    @SerializedName("field")
    val field: String,
    @SerializedName("messages")
    val messageList: List<String>
)
