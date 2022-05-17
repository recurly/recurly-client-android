package com.recurly.androidsdk.data

import com.recurly.androidsdk.data.model.tokenization.TokenizationRequest
import com.recurly.androidsdk.data.model.tokenization.TokenizationResponse
import com.recurly.androidsdk.data.network.TokenService

/**
 * Part of the MVVM architecture pattern, manages and decides if the call should
 * go to the api or the ROOM of the app
 */
class TokenRepository constructor(
    private val apiClient: TokenService
) {
    internal suspend fun getToken(request: TokenizationRequest): TokenizationResponse{
        return apiClient.getToken(request)
    }

}