package com.recurly.androidsdk.data.network

import com.google.gson.Gson
import com.recurly.androidsdk.data.model.tokenization.ErrorRecurly
import com.recurly.androidsdk.data.model.tokenization.TokenizationRequest
import com.recurly.androidsdk.data.model.tokenization.TokenizationResponse
import com.recurly.androidsdk.data.network.core.RetrofitHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TokenService(
    private val apiClient: RecurlyApiClient = RetrofitHelper.getRetrofit().create(RecurlyApiClient::class.java)
) {

    private val gson = Gson()

    /**
     * @param request TokenizationRequest
     * @return TokenizationResponse
     *
     * If the api call succeeds the response will be the parsed TokenizationResponse.
     *
     * If the api call fails, the response error body is parsed into an ErrorRecurly;
     * if it cannot be parsed, a fallback ErrorRecurly built from the HTTP status is returned.
     */
    suspend fun getToken(request: TokenizationRequest): TokenizationResponse {
        return withContext(Dispatchers.IO) {
            val response =
                apiClient.recurlyTokenization(
                    first_name = request.firstName,
                    last_name = request.lastName,
                    company = request.company,
                    address1 = request.addressOne,
                    address2 = request.addressTwo,
                    city = request.city,
                    state = request.state,
                    postal_code = request.postalCode,
                    country = request.country,
                    phone = request.phone,
                    vat_number = request.vatNumber,
                    tax_identifier = request.taxIdentifier,
                    tax_identifier_type = request.taxIdentifierType,
                    number = request.cardNumber,
                    month = request.expirationMonth,
                    year = request.expirationYear,
                    cvv = request.cvvCode,
                    version = request.sdkVersion,
                    key = request.publicKey,
                    deviceId = request.deviceId,
                    sessionId = request.sessionId
                )
            if (response.isSuccessful) {
                response.body() ?: TokenizationResponse(
                    null, null, ErrorRecurly("", "Empty response body", emptyList(), emptyList())
                )
            } else {
                val parsed = runCatching {
                    gson.fromJson(response.errorBody()?.string(), TokenizationResponse::class.java)
                }.getOrNull()
                // Gson populates fields via reflection and leaves object refs at their JVM
                // default (null) when a JSON key is absent, bypassing Kotlin's non-null type
                // guarantee for ErrorRecurly. The null check below is a real runtime necessity,
                // not dead code, even though the compiler cannot see it.
                @Suppress("SENSELESS_COMPARISON")
                parsed?.takeIf { it.error != null } ?: TokenizationResponse(
                    null, null, ErrorRecurly(
                        response.code().toString(),
                        response.message(),
                        emptyList(),
                        emptyList()
                    )
                )
            }
        }
    }

}