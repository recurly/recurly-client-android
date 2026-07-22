package com.recurly.androidsdk.data.network

import com.google.common.truth.Truth.assertThat
import com.google.gson.JsonObject
import com.recurly.androidsdk.data.model.googlepay.GooglePayTokenRequest
import com.recurly.androidsdk.data.network.core.NullOnEmptyConverterFactory
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class GooglePayServiceTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var googlePayService: GooglePayService

    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()
        val apiClient = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(NullOnEmptyConverterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RecurlyApiClient::class.java)
        googlePayService = GooglePayService(apiClient)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun getMerchantInfo_successResponse_returnsParsedPaymentMethods() = runTest {
        mockWebServer.enqueue(
            MockResponse().setResponseCode(200).setBody(
                """{"site_mode":"test","payment_methods":[{"card_networks":["VISA","MASTERCARD"],"auth_methods":["PAN_ONLY","CRYPTOGRAM_3DS"],"payment_gateway":{"gateway":"example","gatewayMerchantId":"merchant123"},"gateway_code":"gw_123"}]}"""
            )
        )

        val result = googlePayService.getMerchantInfo("gw_123", "USD", "US")

        assertThat(result.siteMode).isEqualTo("test")
        assertThat(result.paymentMethods).hasSize(1)
        val method = result.paymentMethods.first()
        assertThat(method.cardNetworks).containsExactly("VISA", "MASTERCARD")
        assertThat(method.authMethods).containsExactly("PAN_ONLY", "CRYPTOGRAM_3DS")
        assertThat(method.paymentGateway).containsEntry("gateway", "example")
        assertThat(method.gatewayCode).isEqualTo("gw_123")
    }

    @Test
    fun getMerchantInfo_request_sendsGatewayCurrencyAndCountryAsQueryParams() = runTest {
        mockWebServer.enqueue(
            MockResponse().setResponseCode(200).setBody("""{"payment_methods":[]}""")
        )

        googlePayService.getMerchantInfo("gw_123", "USD", "US")

        val recordedPath = mockWebServer.takeRequest().path
        assertThat(recordedPath).contains("gateway_code=gw_123")
        assertThat(recordedPath).contains("currency=USD")
        assertThat(recordedPath).contains("country=US")
    }

    @Test
    fun getMerchantInfo_emptyPaymentMethods_returnsEmptyList() = runTest {
        mockWebServer.enqueue(
            MockResponse().setResponseCode(200).setBody("""{"site_mode":"test","payment_methods":[]}""")
        )

        val result = googlePayService.getMerchantInfo(null, "USD", "US")

        assertThat(result.paymentMethods).isEmpty()
    }

    @Test
    fun getMerchantInfo_errorResponseWithParsableBody_returnsParsedError() = runTest {
        mockWebServer.enqueue(
            MockResponse().setResponseCode(422).setBody(
                """{"error":{"code":"google-pay-not-configured","message":"Google Pay is not configured","fields":[],"details":[]}}"""
            )
        )

        val result = googlePayService.getMerchantInfo("gw_123", "USD", "US")

        assertThat(result.error?.errorCode).isEqualTo("google-pay-not-configured")
        assertThat(result.error?.errorMessage).isEqualTo("Google Pay is not configured")
    }

    @Test
    fun getMerchantInfo_errorResponseWithUnparsableBody_loggingDisabled_returnsGenericMessage() = runTest {
        mockWebServer.enqueue(
            MockResponse().setStatus("HTTP/1.1 500 Custom Server Error").setBody("Internal Server Error")
        )

        val result = googlePayService.getMerchantInfo("gw_123", "USD", "US")

        assertThat(result.error?.errorCode).isEqualTo("500")
        assertThat(result.error?.errorMessage).isEqualTo("Request failed")
    }

    @Test
    fun getMerchantInfo_successResponseEmptyBody_returnsEmptyBodyError() = runTest {
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(""))

        val result = googlePayService.getMerchantInfo("gw_123", "USD", "US")

        assertThat(result.paymentMethods).isEmpty()
        assertThat(result.error?.errorMessage).isEqualTo("Empty response body")
    }

    @Test
    fun postToken_successResponse_returnsParsedToken() = runTest {
        mockWebServer.enqueue(
            MockResponse().setResponseCode(200)
                .setBody("""{"id":"tok_abc123","type":"credit_card"}""")
        )

        val result = googlePayService.postToken(buildTokenRequest())

        assertThat(result.token).isEqualTo("tok_abc123")
        assertThat(result.type).isEqualTo("credit_card")
    }

    @Test
    fun postToken_request_sendsGatewayCodeDeviceIdAndSessionId() = runTest {
        mockWebServer.enqueue(
            MockResponse().setResponseCode(200)
                .setBody("""{"id":"tok_abc123","type":"credit_card"}""")
        )

        googlePayService.postToken(buildTokenRequest())

        val recordedBody = mockWebServer.takeRequest().body.readUtf8()
        assertThat(recordedBody).contains("\"gateway_code\":\"gw_123\"")
        assertThat(recordedBody).contains("\"device_id\":\"test-device-id\"")
        assertThat(recordedBody).contains("\"session_id\":\"test-session-id\"")
    }

    @Test
    fun postToken_errorResponseWithParsableBody_returnsParsedError() = runTest {
        mockWebServer.enqueue(
            MockResponse().setResponseCode(422).setBody(
                """{"error":{"code":"invalid-payment-data","message":"Payment data is invalid","fields":[],"details":[]}}"""
            )
        )

        val result = googlePayService.postToken(buildTokenRequest())

        assertThat(result.token).isNull()
        assertThat(result.error.errorCode).isEqualTo("invalid-payment-data")
    }

    @Test
    fun postToken_errorResponseWithUnparsableBody_returnsFallbackError() = runTest {
        mockWebServer.enqueue(
            MockResponse().setResponseCode(500).setBody("Internal Server Error")
        )

        val result = googlePayService.postToken(buildTokenRequest())

        assertThat(result.token).isNull()
        assertThat(result.error.errorCode).isEqualTo("500")
    }

    private fun buildTokenRequest() = GooglePayTokenRequest(
        gatewayCode = "gw_123",
        paymentData = JsonObject().apply { addProperty("apiVersion", 2) },
        firstName = "John",
        lastName = "Doe",
        company = "",
        addressOne = "123 Main St",
        addressTwo = "",
        city = "Boston",
        state = "MA",
        postalCode = "02110",
        country = "US",
        phone = "",
        vatNumber = "",
        taxIdentifier = "",
        taxIdentifierType = "",
        sdkVersion = "3.2.0",
        publicKey = "test-public-key",
        deviceId = "test-device-id",
        sessionId = "test-session-id"
    )
}
