package com.recurly.androidsdk.domain

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.recurly.androidsdk.data.model.googlepay.GooglePayEnvironment
import com.recurly.androidsdk.data.model.googlepay.GooglePayPaymentMethod
import com.recurly.androidsdk.data.model.googlepay.RecurlyGooglePayMethod
import com.recurly.androidsdk.data.model.googlepay.RecurlyGooglePayParams
import com.recurly.androidsdk.data.model.tokenization.ErrorRecurly
import com.recurly.androidsdk.data.model.tokenization.RecurlyException

internal object GooglePayRequestBuilder {

    /**
     * Card networks Google Pay itself recognizes for card-present tokenization. If a
     * [GooglePayPaymentMethod.cardNetworks] list contains none of these, it can only describe a
     * non-card payment method (e.g. a Buy-Now-Pay-Later gateway such as Affirm/Klarna/Afterpay)
     * which Google Pay's native card sheet does not support tokenizing; see
     * [assertCardNetworkSupported].
     */
    private val SUPPORTED_CARD_NETWORKS = setOf(
        "AMEX", "DISCOVER", "INTERAC", "JCB", "MASTERCARD", "VISA"
    )

    /**
     * @throws RecurlyException with code `google-pay-bnpl-not-supported` when [method] carries no
     * networks Google Pay's native card sheet can tokenize (e.g. a Buy-Now-Pay-Later-only gateway
     * configuration), mirroring recurly-js's pre-tokenization BNPL guard.
     */
    fun assertCardNetworkSupported(method: GooglePayPaymentMethod) =
        assertCardNetworkSupported(method.cardNetworks)

    fun assertCardNetworkSupported(cardNetworks: List<String>) {
        val supported = cardNetworks.any { it.uppercase() in SUPPORTED_CARD_NETWORKS }
        if (!supported) {
            throw RecurlyException(
                ErrorRecurly(
                    "google-pay-bnpl-not-supported",
                    "This Google Pay gateway configuration does not support native card tokenization.",
                    emptyList(),
                    emptyList()
                )
            )
        }
    }

    /**
     * Builds the JSON array Google Pay's `ButtonOptions.setAllowedPaymentMethods` and the
     * `allowedPaymentMethods` field of `IsReadyToPayRequest`/`PaymentDataRequest` expect.
     */
    fun buildAllowedPaymentMethodsJson(method: GooglePayPaymentMethod, includeTokenizationSpecification: Boolean): String {
        assertCardNetworkSupported(method)
        return JsonArray().apply { add(cardPaymentMethod(method, includeTokenizationSpecification)) }.toString()
    }

    /**
     * Builds the JSON array for [com.google.android.gms.wallet.button.ButtonOptions.setAllowedPaymentMethods]
     * from the public [RecurlyGooglePayMethod] surfaced to SDK consumers. No tokenization
     * specification is included; a button only needs to advertise supported networks, never the
     * gateway's tokenization configuration.
     */
    fun buildAllowedPaymentMethodsJson(method: RecurlyGooglePayMethod): String {
        assertCardNetworkSupported(method.cardNetworks)
        return JsonArray().apply {
            add(cardPaymentMethodJson(method.cardNetworks, method.authMethods, tokenizationSpec = null))
        }.toString()
    }

    /**
     * Builds the JSON for Google Pay's `IsReadyToPayRequest.fromJson`.
     */
    fun buildIsReadyToPayRequestJson(method: GooglePayPaymentMethod): String {
        return JsonObject().apply {
            addProperty("apiVersion", 2)
            addProperty("apiVersionMinor", 0)
            add("allowedPaymentMethods", JsonParser.parseString(buildAllowedPaymentMethodsJson(method, includeTokenizationSpecification = false)))
        }.toString()
    }

    fun buildPaymentDataRequestJson(method: GooglePayPaymentMethod, params: RecurlyGooglePayParams): String {
        return JsonObject().apply {
            addProperty("apiVersion", 2)
            addProperty("apiVersionMinor", 0)
            add("allowedPaymentMethods", JsonParser.parseString(buildAllowedPaymentMethodsJson(method, includeTokenizationSpecification = true)))
            add("merchantInfo", JsonObject().apply {
                addProperty("merchantId", params.googleMerchantId)
                addProperty("merchantName", params.googleBusinessName)
            })
            add("transactionInfo", JsonObject().apply {
                addProperty("totalPriceStatus", "FINAL")
                addProperty("totalPrice", params.total)
                addProperty("currencyCode", params.currency)
                addProperty("countryCode", params.country)
            })
        }.toString()
    }

    /**
     * Maps [RecurlyGooglePayParams.environment] to Google Pay's `WalletConstants.ENVIRONMENT_*`
     * integer constants (`3` = TEST, `1` = PRODUCTION), avoiding a hard dependency on the Wallet
     * SDK types from this pure-logic layer.
     */
    fun walletEnvironmentConstant(environment: GooglePayEnvironment): Int = when (environment) {
        GooglePayEnvironment.TEST -> 3
        GooglePayEnvironment.PRODUCTION -> 1
    }

    private fun cardPaymentMethod(method: GooglePayPaymentMethod, includeTokenizationSpecification: Boolean): JsonObject {
        val tokenizationSpec: Pair<String, Map<String, String>>? = if (includeTokenizationSpecification) {
            when {
                method.paymentGateway != null -> "PAYMENT_GATEWAY" to method.paymentGateway
                method.direct != null -> "DIRECT" to method.direct
                else -> null
            }
        } else {
            null
        }
        return cardPaymentMethodJson(method.cardNetworks, method.authMethods, tokenizationSpec)
    }

    private fun cardPaymentMethodJson(
        cardNetworks: List<String>,
        authMethods: List<String>,
        tokenizationSpec: Pair<String, Map<String, String>>?
    ): JsonObject {
        return JsonObject().apply {
            addProperty("type", "CARD")
            add("parameters", JsonObject().apply {
                add("allowedAuthMethods", JsonArray().apply { authMethods.forEach { add(it) } })
                add("allowedCardNetworks", JsonArray().apply {
                    cardNetworks.filter { it.uppercase() in SUPPORTED_CARD_NETWORKS }.forEach { add(it) }
                })
            })
            if (tokenizationSpec != null) {
                val (specType, specParams) = tokenizationSpec
                add("tokenizationSpecification", JsonObject().apply {
                    addProperty("type", specType)
                    add("parameters", JsonObject().apply {
                        specParams.forEach { (key, value) -> addProperty(key, value) }
                    })
                })
            }
        }
    }
}
