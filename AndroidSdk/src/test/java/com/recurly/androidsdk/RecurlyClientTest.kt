package com.recurly.androidsdk

import com.google.common.truth.Truth.assertThat
import com.recurly.androidsdk.data.model.tokenization.RecurlyBillingInfo
import com.recurly.androidsdk.data.model.tokenization.RecurlyCardParams
import com.recurly.androidsdk.data.model.tokenization.RecurlyException
import com.recurly.androidsdk.data.network.RecurlyApiClient
import com.recurly.androidsdk.data.network.core.NullOnEmptyConverterFactory
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RecurlyClientTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var recurlyClient: RecurlyClient
    private lateinit var loggingRecurlyClient: RecurlyClient

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
        recurlyClient = RecurlyClient("test-public-key", apiClient)
        loggingRecurlyClient = RecurlyClient("test-public-key", apiClient, enableLogging = true)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun tokenize_successResponse_returnsRecurlyToken() = runTest {
        mockWebServer.enqueue(
            MockResponse().setResponseCode(200).setBody(
                """{"id":"tok_abc123","type":"credit_card","card":{"brand":"visa","first_six":"411111","last_four":"1111","exp_month":12,"exp_year":2030,"issuing_country":"US","funding_source":"credit"}}"""
            )
        )

        val token = recurlyClient.tokenize(buildCardParams(), buildBillingInfo())

        assertThat(token.id).isEqualTo("tok_abc123")
        assertThat(token.type).isEqualTo("credit_card")
        assertThat(token.card?.brand).isEqualTo("visa")
        assertThat(token.card?.firstSix).isEqualTo("411111")
    }

    @Test
    fun tokenize_errorResponse_throwsRecurlyException() = runTest {
        mockWebServer.enqueue(
            MockResponse().setResponseCode(422).setBody(
                """{"error":{"code":"invalid-card-number","message":"Card number is invalid","fields":["number"],"details":[]}}"""
            )
        )

        val exception = runCatching {
            recurlyClient.tokenize(buildCardParams(), buildBillingInfo())
        }.exceptionOrNull()

        assertThat(exception).isInstanceOf(RecurlyException::class.java)
        assertThat((exception as RecurlyException).error.errorCode).isEqualTo("invalid-card-number")
    }

    @Test
    fun tokenize_networkFailure_throwsRecurlyExceptionWithConnectionFailedCode() = runTest {
        mockWebServer.shutdown()

        val exception = runCatching {
            recurlyClient.tokenize(buildCardParams(), buildBillingInfo())
        }.exceptionOrNull()

        assertThat(exception).isInstanceOf(RecurlyException::class.java)
        assertThat((exception as RecurlyException).error.errorCode).isEqualTo("connection_failed")
    }

    @Test
    fun tokenize_networkFailure_loggingDisabled_returnsGenericMessage() = runTest {
        mockWebServer.shutdown()

        val exception = runCatching {
            recurlyClient.tokenize(buildCardParams(), buildBillingInfo())
        }.exceptionOrNull()

        assertThat((exception as RecurlyException).error.errorMessage).isEqualTo("Network request failed")
    }

    @Test
    fun tokenize_networkFailure_loggingEnabled_returnsRawExceptionMessage() = runTest {
        mockWebServer.shutdown()

        val exception = runCatching {
            loggingRecurlyClient.tokenize(buildCardParams(), buildBillingInfo())
        }.exceptionOrNull()

        assertThat(exception).isInstanceOf(RecurlyException::class.java)
        val error = (exception as RecurlyException).error
        assertThat(error.errorCode).isEqualTo("connection_failed")
        assertThat(error.errorMessage).isNotEqualTo("Network request failed")
    }

    private fun buildBillingInfo() = RecurlyBillingInfo(firstName = "John", lastName = "Doe")

    private fun buildCardParams(): RecurlyCardParams =
        RecurlyCardParams.testInstance(
            cardNumber = "4111111111111111",
            expirationMonth = 12,
            expirationYear = 2030,
            cvvCode = "123"
        )
}
