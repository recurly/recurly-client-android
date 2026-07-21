package com.recurly.androidsdk.data.model.tokenization

/**
 * The result of a successful credit card tokenization.
 *
 * @property id the single-use token id to submit to your server
 * @property type the token type, e.g. "credit_card"
 * @property card metadata about the tokenized card, or null if unavailable (e.g. Apple Pay)
 */
data class RecurlyToken(
    val id: String,
    val type: String?,
    val card: RecurlyTokenCard?
)
