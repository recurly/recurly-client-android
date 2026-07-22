package com.recurly.androidsdk.domain

import com.recurly.androidsdk.data.GooglePayRepository
import com.recurly.androidsdk.data.model.googlepay.GooglePayInfoResponse

/**
 * Case of use that fetches the server-driven Google Pay configuration.
 */
internal class GetGooglePayMerchantInfo constructor(
    private val repository: GooglePayRepository
) {

    suspend operator fun invoke(gatewayCode: String?, currency: String, country: String): GooglePayInfoResponse =
        repository.getMerchantInfo(gatewayCode, currency, country)

}
