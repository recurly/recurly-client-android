package com.recurly.androidsdk.data.network

import com.google.common.truth.Truth.assertThat
import com.recurly.androidsdk.data.model.tokenization.TokenizationRequest
import com.recurly.androidsdk.data.network.core.NullOnEmptyConverterFactory
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class TokenServiceTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var tokenService: TokenService

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
        tokenService = TokenService(apiClient)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun getToken_successResponse_returnsParsedToken() = runTest {
        mockWebServer.enqueue(
            MockResponse().setResponseCode(200)
                .setBody("""{"id":"tok_abc123","type":"credit_card"}""")
        )

        val result = tokenService.getToken(buildRequest())

        assertThat(result.token).isEqualTo("tok_abc123")
        assertThat(result.type).isEqualTo("credit_card")
    }


    @Test
    fun getToken_successResponseWithCard_returnsParsedCardMetadata() = runTest {
        mockWebServer.enqueue(
            MockResponse().setResponseCode(200).setBody(
                """{"id":"tok_abc123","type":"credit_card","card":{"brand":"visa","first_six":"411111","last_four":"1111","exp_month":12,"exp_year":2030,"issuing_country":"US","funding_source":"credit"}}"""
            )
        )

        val result = tokenService.getToken(buildRequest())

        assertThat(result.card).isNotNull()
        assertThat(result.card?.brand).isEqualTo("visa")
        assertThat(result.card?.firstSix).isEqualTo("411111")
        assertThat(result.card?.lastFour).isEqualTo("1111")
        assertThat(result.card?.expMonth).isEqualTo(12)
        assertThat(result.card?.expYear).isEqualTo(2030)
        assertThat(result.card?.issuingCountry).isEqualTo("US")
        assertThat(result.card?.fundingSource).isEqualTo("credit")
    }

    @Test
    fun getToken_request_doesNotSendDeviceIdOrSessionId() = runTest {
        mockWebServer.enqueue(
            MockResponse().setResponseCode(200)
                .setBody("""{"id":"tok_abc123","type":"credit_card"}""")
        )

        tokenService.getToken(buildRequest())

        val recordedBody = mockWebServer.takeRequest().body.readUtf8()
        assertThat(recordedBody).doesNotContain("deviceId")
        assertThat(recordedBody).doesNotContain("sessionId")
    }

    @Test
    fun getToken_errorResponseWithParsableBody_returnsParsedError() = runTest {
        mockWebServer.enqueue(
            MockResponse().setResponseCode(422).setBody(
                """{"error":{"code":"invalid-card-number","message":"Card number is invalid","fields":["number"],"details":[]}}"""
            )
        )

        val result = tokenService.getToken(buildRequest())

        assertThat(result.token).isNull()
        assertThat(result.error.errorCode).isEqualTo("invalid-card-number")
        assertThat(result.error.errorMessage).isEqualTo("Card number is invalid")
    }

    @Test
    fun getToken_errorResponseWithUnparsableBody_returnsFallbackError() = runTest {
        mockWebServer.enqueue(
            MockResponse().setResponseCode(500).setBody("Internal Server Error")
        )

        val result = tokenService.getToken(buildRequest())

        assertThat(result.token).isNull()
        assertThat(result.error.errorCode).isEqualTo("500")
    }

    @Test
    fun getToken_successResponseEmptyBody_returnsEmptyBodyError() = runTest {
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(""))

        val result = tokenService.getToken(buildRequest())

        assertThat(result.token).isNull()
        assertThat(result.error.errorMessage).isEqualTo("Empty response body")
    }

    @Test
    fun getToken_errorResponseParsedWithoutError_returnsFallbackError() = runTest {
        mockWebServer.enqueue(MockResponse().setResponseCode(422).setBody("{}"))

        val result = tokenService.getToken(buildRequest())

        assertThat(result.token).isNull()
        assertThat(result.error.errorCode).isEqualTo("422")
    }

    private fun buildRequest() = TokenizationRequest(
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
        cardNumber = "4111111111111111",
        expirationMonth = 12,
        expirationYear = 2030,
        cvvCode = "123",
        sdkVersion = "3.0.0",
        publicKey = "test-public-key"
    )
}
