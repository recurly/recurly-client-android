package com.recurly.androidsdk.data

import com.recurly.androidsdk.data.model.googlepay.GooglePayInfoResponse
import com.recurly.androidsdk.data.model.googlepay.GooglePayTokenRequest
import com.recurly.androidsdk.data.model.tokenization.TokenizationResponse
import com.recurly.androidsdk.data.network.GooglePayService

/**
 * Thin suspend pass-through from the domain layer to [GooglePayService].
 */
internal class GooglePayRepository constructor(
    private val apiClient: GooglePayService
) {
    suspend fun getMerchantInfo(gatewayCode: String?, currency: String, country: String): GooglePayInfoResponse {
        return apiClient.getMerchantInfo(gatewayCode, currency, country)
    }

    suspend fun postToken(request: GooglePayTokenRequest): TokenizationResponse {
        return apiClient.postToken(request)
    }
}
