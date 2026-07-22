package com.recurly.androidsdk.data.model.googlepay

/**
 * The public, SDK-consumer-facing view of a Google Pay payment method configuration, resolved
 * via [com.recurly.androidsdk.presentation.view.RecurlyGooglePayHandler.getPaymentMethod].
 *
 * Use this to configure a [com.recurly.androidsdk.presentation.view.RecurlyGooglePayButton]'s
 * allowed payment methods; the underlying gateway tokenization configuration stays internal to
 * the SDK.
 *
 * @property cardNetworks the card networks Recurly's configured gateway accepts for Google Pay
 * @property authMethods the Google Pay authentication methods accepted (e.g. PAN_ONLY, CRYPTOGRAM_3DS)
 */
data class RecurlyGooglePayMethod(
    val cardNetworks: List<String>,
    val authMethods: List<String>
)

internal fun GooglePayPaymentMethod.toPublic(): RecurlyGooglePayMethod =
    RecurlyGooglePayMethod(cardNetworks = cardNetworks, authMethods = authMethods)
