package com.recurly.androidsdk.data.model.tokenization

import com.recurly.androidsdk.presentation.view.RecurlyCVV
import com.recurly.androidsdk.presentation.view.RecurlyCreditCardNumber
import com.recurly.androidsdk.presentation.view.RecurlyExpirationMMYY

/**
 * An immutable snapshot of the card data entered into a Recurly card input view, ready to
 * be passed to [com.recurly.androidsdk.RecurlyClient.tokenize].
 *
 * Raw card data is never exposed publicly: a [RecurlyCardParams] can only be obtained from
 * a Recurly-provided card input view, keeping integrators out of PCI scope for the PAN/CVV.
 *
 * Call the input view's `validateData()` before building a [RecurlyCardParams] to ensure the
 * entered card data is complete and valid.
 */
class RecurlyCardParams internal constructor(
    internal val cardNumber: String,
    internal val expirationMonth: Int,
    internal val expirationYear: Int,
    internal val cvvCode: String
) {
    companion object {
        /**
         * Builds a [RecurlyCardParams] snapshot from the individual (loose) card input views.
         *
         * @param number the card number input view
         * @param expiration the expiration (MM/YY) input view
         * @param cvv the CVV input view
         */
        fun from(
            number: RecurlyCreditCardNumber,
            expiration: RecurlyExpirationMMYY,
            cvv: RecurlyCVV
        ): RecurlyCardParams = RecurlyCardParams(
            cardNumber = number.getCardNumber(),
            expirationMonth = expiration.getExpirationMonth(),
            expirationYear = expiration.getExpirationYear(),
            cvvCode = cvv.getCvvCode()
        )

        /**
         * Test-only factory for constructing a [RecurlyCardParams] directly, bypassing the
         * card input views. Internal visibility keeps this out of the public API surface.
         */
        internal fun testInstance(
            cardNumber: String,
            expirationMonth: Int,
            expirationYear: Int,
            cvvCode: String
        ): RecurlyCardParams = RecurlyCardParams(cardNumber, expirationMonth, expirationYear, cvvCode)
    }
}
