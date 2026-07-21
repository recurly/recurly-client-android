package com.recurly.androidsdk.data

import com.recurly.androidsdk.data.model.tokenization.TokenizationRequest
import com.recurly.androidsdk.data.model.tokenization.TokenizationResponse
import com.recurly.androidsdk.data.network.TokenService

/**
 * Thin suspend pass-through from the domain layer to [TokenService].
 */

internal class TokenRepository constructor(
    private val apiClient: TokenService
) {
    suspend fun getToken(request: TokenizationRequest): TokenizationResponse{
        return apiClient.getToken(request)
    }

}