package com.recurly.androidsdk.data.model.googlepay

import com.google.gson.annotations.SerializedName

/**
 * Server-driven Google Pay configuration returned by `GET js/v1/google_pay/info`.
 *
 * This describes which card networks, authentication methods, and tokenization spec
 * (gateway or direct) are enabled for the requested gateway/currency/country. These values
 * must be used as-is when building the Google Pay request — never hardcode card networks or
 * gateway tokenization parameters.
 */
internal data class GooglePayInfoResponse(
    @SerializedName("site_mode")
    val siteMode: String? = null,
    @SerializedName("payment_methods")
    val paymentMethods: List<GooglePayPaymentMethod> = emptyList(),
    @SerializedName("error")
    val error: com.recurly.androidsdk.data.model.tokenization.ErrorRecurly? = null
)

/**
 * A single Google Pay payment method configuration entry from [GooglePayInfoResponse].
 *
 * Exactly one of [paymentGateway] or [direct] is populated, matching Google Pay's
 * `PAYMENT_GATEWAY` and `DIRECT` tokenization specification types respectively.
 */
internal data class GooglePayPaymentMethod(
    @SerializedName("card_networks")
    val cardNetworks: List<String> = emptyList(),
    @SerializedName("auth_methods")
    val authMethods: List<String> = emptyList(),
    @SerializedName("payment_gateway")
    val paymentGateway: Map<String, String>? = null,
    @SerializedName("direct")
    val direct: Map<String, String>? = null,
    @SerializedName("gateway_code")
    val gatewayCode: String? = null
)
