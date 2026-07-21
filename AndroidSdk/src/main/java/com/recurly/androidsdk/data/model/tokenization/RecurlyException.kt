package com.recurly.androidsdk.data.model.tokenization

/**
 * Thrown by [com.recurly.androidsdk.RecurlyClient.tokenize] when a tokenization request fails,
 * whether due to a validation error, a card decline, or a network failure.
 *
 * @property error the underlying error detail returned by the API, or a synthesized
 * [ErrorRecurly] describing a network/connection failure
 */
class RecurlyException(val error: ErrorRecurly) : Exception(error.errorMessage)
