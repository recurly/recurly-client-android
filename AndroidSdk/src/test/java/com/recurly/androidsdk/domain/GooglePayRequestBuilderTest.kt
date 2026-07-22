package com.recurly.androidsdk.domain

import com.google.common.truth.Truth.assertThat
import com.google.gson.JsonParser
import com.recurly.androidsdk.data.model.googlepay.GooglePayEnvironment
import com.recurly.androidsdk.data.model.googlepay.GooglePayPaymentMethod
import com.recurly.androidsdk.data.model.googlepay.RecurlyGooglePayMethod
import com.recurly.androidsdk.data.model.googlepay.RecurlyGooglePayParams
import com.recurly.androidsdk.data.model.tokenization.RecurlyException
import org.junit.Test

class GooglePayRequestBuilderTest {

    @Test
    fun assertCardNetworkSupported_bnplOnlyGateway_throwsBnplNotSupported() {
        val bnplOnlyMethod = buildMethod(cardNetworks = listOf("AFFIRM", "KLARNA"))

        val exception = org.junit.Assert.assertThrows(RecurlyException::class.java) {
            GooglePayRequestBuilder.assertCardNetworkSupported(bnplOnlyMethod)
        }

        assertThat(exception.error.errorCode).isEqualTo("google-pay-bnpl-not-supported")
    }

    @Test
    fun assertCardNetworkSupported_supportedCardNetworkPresent_doesNotThrow() {
        val method = buildMethod(cardNetworks = listOf("VISA", "AFFIRM"))

        GooglePayRequestBuilder.assertCardNetworkSupported(method)
    }

    @Test
    fun buildIsReadyToPayRequestJson_bnplOnlyGateway_throwsBeforeBuildingRequest() {
        val bnplOnlyMethod = buildMethod(cardNetworks = listOf("KLARNA"))

        org.junit.Assert.assertThrows(RecurlyException::class.java) {
            GooglePayRequestBuilder.buildIsReadyToPayRequestJson(bnplOnlyMethod)
        }
    }

    @Test
    fun buildIsReadyToPayRequestJson_supportedNetworks_omitsTokenizationSpecification() {
        val method = buildMethod(cardNetworks = listOf("VISA", "MASTERCARD"), authMethods = listOf("PAN_ONLY"))

        val json = JsonParser.parseString(GooglePayRequestBuilder.buildIsReadyToPayRequestJson(method)).asJsonObject

        assertThat(json.get("apiVersion").asInt).isEqualTo(2)
        val cardMethod = json.getAsJsonArray("allowedPaymentMethods")[0].asJsonObject
        assertThat(cardMethod.get("type").asString).isEqualTo("CARD")
        assertThat(cardMethod.has("tokenizationSpecification")).isFalse()
        val networks = cardMethod.getAsJsonObject("parameters").getAsJsonArray("allowedCardNetworks")
        assertThat(networks.map { it.asString }).containsExactly("VISA", "MASTERCARD")
    }

    @Test
    fun buildIsReadyToPayRequestJson_filtersUnsupportedNetworksFromAllowedCardNetworks() {
        val method = buildMethod(cardNetworks = listOf("VISA", "AFFIRM"))

        val json = JsonParser.parseString(GooglePayRequestBuilder.buildIsReadyToPayRequestJson(method)).asJsonObject

        val cardMethod = json.getAsJsonArray("allowedPaymentMethods")[0].asJsonObject
        val networks = cardMethod.getAsJsonObject("parameters").getAsJsonArray("allowedCardNetworks")
        assertThat(networks.map { it.asString }).containsExactly("VISA")
    }

    @Test
    fun buildPaymentDataRequestJson_paymentGatewaySpec_includesTokenizationSpecificationAndTransactionInfo() {
        val method = buildMethod(
            cardNetworks = listOf("VISA"),
            paymentGateway = mapOf("gateway" to "example", "gatewayMerchantId" to "merchant123")
        )
        val params = RecurlyGooglePayParams(
            googleMerchantId = "merchant-id",
            googleBusinessName = "Acme Co",
            currency = "USD",
            country = "US",
            total = "10.00"
        )

        val json = JsonParser.parseString(GooglePayRequestBuilder.buildPaymentDataRequestJson(method, params)).asJsonObject

        val cardMethod = json.getAsJsonArray("allowedPaymentMethods")[0].asJsonObject
        val spec = cardMethod.getAsJsonObject("tokenizationSpecification")
        assertThat(spec.get("type").asString).isEqualTo("PAYMENT_GATEWAY")
        assertThat(spec.getAsJsonObject("parameters").get("gateway").asString).isEqualTo("example")

        val merchantInfo = json.getAsJsonObject("merchantInfo")
        assertThat(merchantInfo.get("merchantId").asString).isEqualTo("merchant-id")
        assertThat(merchantInfo.get("merchantName").asString).isEqualTo("Acme Co")

        val transactionInfo = json.getAsJsonObject("transactionInfo")
        assertThat(transactionInfo.get("totalPrice").asString).isEqualTo("10.00")
        assertThat(transactionInfo.get("currencyCode").asString).isEqualTo("USD")
        assertThat(transactionInfo.get("countryCode").asString).isEqualTo("US")
        assertThat(transactionInfo.get("totalPriceStatus").asString).isEqualTo("FINAL")
    }

    @Test
    fun buildPaymentDataRequestJson_directSpec_includesDirectTokenizationSpecification() {
        val method = buildMethod(cardNetworks = listOf("VISA"), direct = mapOf("protocolVersion" to "ECv2"))
        val params = RecurlyGooglePayParams(
            googleMerchantId = "merchant-id",
            googleBusinessName = "Acme Co",
            currency = "USD",
            country = "US",
            total = "10.00"
        )

        val json = JsonParser.parseString(GooglePayRequestBuilder.buildPaymentDataRequestJson(method, params)).asJsonObject

        val spec = json.getAsJsonArray("allowedPaymentMethods")[0].asJsonObject.getAsJsonObject("tokenizationSpecification")
        assertThat(spec.get("type").asString).isEqualTo("DIRECT")
        assertThat(spec.getAsJsonObject("parameters").get("protocolVersion").asString).isEqualTo("ECv2")
    }

    @Test
    fun buildAllowedPaymentMethodsJson_publicModel_omitsTokenizationSpecification() {
        val method = RecurlyGooglePayMethod(cardNetworks = listOf("VISA", "MASTERCARD"), authMethods = listOf("PAN_ONLY"))

        val json = JsonParser.parseString(GooglePayRequestBuilder.buildAllowedPaymentMethodsJson(method)).asJsonArray

        val cardMethod = json[0].asJsonObject
        assertThat(cardMethod.has("tokenizationSpecification")).isFalse()
        assertThat(cardMethod.getAsJsonObject("parameters").getAsJsonArray("allowedCardNetworks").map { it.asString })
            .containsExactly("VISA", "MASTERCARD")
    }

    @Test
    fun buildAllowedPaymentMethodsJson_publicModel_bnplOnlyGateway_throws() {
        val method = RecurlyGooglePayMethod(cardNetworks = listOf("AFFIRM"), authMethods = emptyList())

        org.junit.Assert.assertThrows(RecurlyException::class.java) {
            GooglePayRequestBuilder.buildAllowedPaymentMethodsJson(method)
        }
    }

    @Test
    fun walletEnvironmentConstant_mapsTestAndProductionToGoogleConstants() {
        assertThat(GooglePayRequestBuilder.walletEnvironmentConstant(GooglePayEnvironment.TEST)).isEqualTo(3)
        assertThat(GooglePayRequestBuilder.walletEnvironmentConstant(GooglePayEnvironment.PRODUCTION)).isEqualTo(1)
    }

    private fun buildMethod(
        cardNetworks: List<String>,
        authMethods: List<String> = listOf("PAN_ONLY"),
        paymentGateway: Map<String, String>? = null,
        direct: Map<String, String>? = null
    ) = GooglePayPaymentMethod(
        cardNetworks = cardNetworks,
        authMethods = authMethods,
        paymentGateway = paymentGateway,
        direct = direct,
        gatewayCode = "gw_123"
    )
}
