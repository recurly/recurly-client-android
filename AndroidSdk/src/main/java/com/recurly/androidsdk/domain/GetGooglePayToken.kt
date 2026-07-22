package com.recurly.androidsdk.domain

import com.recurly.androidsdk.data.GooglePayRepository
import com.recurly.androidsdk.data.model.googlepay.GooglePayTokenRequest
import com.recurly.androidsdk.data.model.tokenization.TokenizationResponse

/**
 * Case of use that exchanges a Google Pay `PaymentData` payload for a single-use Recurly token.
 */
internal class GetGooglePayToken constructor(
    private val repository: GooglePayRepository
) {

    suspend operator fun invoke(request: GooglePayTokenRequest): TokenizationResponse =
        repository.postToken(request)

}
