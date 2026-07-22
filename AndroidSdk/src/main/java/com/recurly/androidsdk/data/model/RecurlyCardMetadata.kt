package com.recurly.androidsdk.data.model

/**
 *
 * Shared non-sensitive UI metadata for the loose (individual) card input views: the
 * brand-derived CVV digit length. This lets [com.recurly.androidsdk.presentation.view.RecurlyCVV]
 * stay in sync with the card brand detected by
 * [com.recurly.androidsdk.presentation.view.RecurlyCreditCardNumber] when the two are used
 * independently.
 *
 * This object intentionally holds NO cardholder data (no PAN, no CVV, no expiration). Actual
 * card values are owned by each input view instance directly; see [com.recurly.androidsdk.data.model.tokenization.RecurlyCardParams].
 *
 */

internal object RecurlyCardMetadata {

    private var cvvLength = 3

    internal fun getCvvLength(): Int {
        return cvvLength
    }

    internal fun setCvvLength(length: Int) {
        cvvLength = length
    }

}
