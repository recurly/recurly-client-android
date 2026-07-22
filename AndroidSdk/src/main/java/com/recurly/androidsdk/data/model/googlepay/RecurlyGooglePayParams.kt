package com.recurly.androidsdk.data.model.googlepay

/**
 * Integrator-supplied configuration for a Google Pay payment request.
 *
 * @property gatewayCode optional Recurly gateway code to use; when `null`, the gateway/currency/
 * country combination configured on your Recurly site is used to resolve it via `google_pay/info`
 * @property googleMerchantId your Google Pay Business Console merchant id (required for [GooglePayEnvironment.PRODUCTION])
 * @property googleBusinessName the business name shown on the Google Pay sheet
 * @property currency the ISO 4217 currency code for the transaction, e.g. "USD"
 * @property country the ISO 3166-1 alpha-2 country code for the transaction, e.g. "US"
 * @property total the total transaction amount, formatted as Google Pay expects, e.g. "10.00"
 * @property environment [GooglePayEnvironment.TEST] or [GooglePayEnvironment.PRODUCTION]. Defaults
 * to [GooglePayEnvironment.TEST]; switch to [GooglePayEnvironment.PRODUCTION] only once your
 * integration has completed Google's business review, per README-GOOGLE-PAY-CONFIG.md
 * @property requireBillingAddress whether to request a full billing address from the Google Pay sheet
 */
data class RecurlyGooglePayParams(
    val gatewayCode: String? = null,
    val googleMerchantId: String,
    val googleBusinessName: String,
    val currency: String,
    val country: String,
    val total: String,
    val environment: GooglePayEnvironment = GooglePayEnvironment.TEST,
    val requireBillingAddress: Boolean = false
)

/**
 * Selects which Google Pay environment [com.recurly.androidsdk.presentation.view.RecurlyGooglePayHandler]
 * talks to. [TEST] returns synthetic payment data usable end-to-end without a Google Pay Business
 * Console registration; [PRODUCTION] requires a completed Google business review.
 */
enum class GooglePayEnvironment {
    TEST,
    PRODUCTION
}
