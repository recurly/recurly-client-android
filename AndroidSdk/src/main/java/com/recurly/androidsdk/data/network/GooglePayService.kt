package com.recurly.androidsdk.data.network

import com.google.gson.Gson
import com.recurly.androidsdk.data.model.googlepay.GooglePayInfoResponse
import com.recurly.androidsdk.data.model.googlepay.GooglePayTokenRequest
import com.recurly.androidsdk.data.model.tokenization.ErrorRecurly
import com.recurly.androidsdk.data.model.tokenization.TokenizationResponse
import com.recurly.androidsdk.data.network.core.RetrofitHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * @param apiClient the Retrofit client to use; defaults to a client pointed at the correct
 * US/EU data center for an empty (unset) public key
 * @param enableLogging when `true`, raw transport failure detail is surfaced in the returned
 * [ErrorRecurly]; otherwise a generic client-safe message is returned instead, mirroring
 * [TokenService]'s error-redaction behavior.
 */
internal class GooglePayService(
    private val apiClient: RecurlyApiClient = RetrofitHelper.getRetrofit().create(RecurlyApiClient::class.java),
    private val enableLogging: Boolean = false
) {

    private val gson = Gson()

    /**
     * Fetches the server-driven Google Pay configuration for the given gateway/currency/country.
     *
     * @return the parsed [GooglePayInfoResponse] on success, or one carrying a synthesized
     * [ErrorRecurly] on failure.
     */
    suspend fun getMerchantInfo(gatewayCode: String?, currency: String, country: String): GooglePayInfoResponse {
        return withContext(Dispatchers.IO) {
            val response = apiClient.getGooglePayMerchantInfo(gatewayCode, currency, country)
            if (response.isSuccessful) {
                response.body() ?: GooglePayInfoResponse(
                    error = ErrorRecurly("", "Empty response body", emptyList(), emptyList())
                )
            } else {
                parseError(response.errorBody()?.string())?.let { GooglePayInfoResponse(error = it) }
                    ?: GooglePayInfoResponse(error = fallbackError(response.code()))
            }
        }
    }

    /**
     * Exchanges a Google Pay `PaymentData` payload for a single-use Recurly token.
     *
     * @return the parsed [TokenizationResponse] on success, or one carrying a synthesized
     * [ErrorRecurly] on failure, mirroring [TokenService.getToken].
     */
    suspend fun postToken(request: GooglePayTokenRequest): TokenizationResponse {
        return withContext(Dispatchers.IO) {
            val response = apiClient.recurlyGooglePayTokenization(request)
            if (response.isSuccessful) {
                response.body() ?: TokenizationResponse(
                    null, null, error = ErrorRecurly("", "Empty response body", emptyList(), emptyList())
                )
            } else {
                val parsed = runCatching {
                    gson.fromJson(response.errorBody()?.string(), TokenizationResponse::class.java)
                }.getOrNull()
                @Suppress("SENSELESS_COMPARISON")
                parsed?.takeIf { it.error != null } ?: TokenizationResponse(
                    null, null, error = fallbackError(response.code())
                )
            }
        }
    }

    private fun parseError(body: String?): ErrorRecurly? {
        val parsed = runCatching { gson.fromJson(body, GooglePayInfoResponse::class.java) }.getOrNull()
        @Suppress("SENSELESS_COMPARISON")
        return parsed?.takeIf { it.error != null }?.error
    }

    private fun fallbackError(code: Int): ErrorRecurly = ErrorRecurly(
        code.toString(),
        if (enableLogging) "Request failed with status $code" else "Request failed",
        emptyList(),
        emptyList()
    )
}
