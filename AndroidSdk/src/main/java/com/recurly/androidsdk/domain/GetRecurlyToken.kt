package com.recurly.androidsdk.domain

import com.recurly.androidsdk.data.TokenRepository
import com.recurly.androidsdk.data.model.tokenization.TokenizationRequest
import com.recurly.androidsdk.data.model.tokenization.TokenizationResponse

/**
 * Case of use that manages the call to the repository
 */
class GetRecurlyToken constructor(
    private val repository: TokenRepository
){

    suspend operator fun invoke(request: TokenizationRequest):TokenizationResponse = repository.getToken(request)

}