package com.recurly.androidsdk

import com.recurly.androidsdk.data.TokenRepository
import com.recurly.androidsdk.data.model.RecurlySessionData
import com.recurly.androidsdk.data.model.tokenization.ErrorRecurly
import com.recurly.androidsdk.data.model.tokenization.RecurlyBillingInfo
import com.recurly.androidsdk.data.model.tokenization.RecurlyCardParams
import com.recurly.androidsdk.data.model.tokenization.RecurlyException
import com.recurly.androidsdk.data.model.tokenization.RecurlyToken
import com.recurly.androidsdk.data.model.tokenization.TokenizationRequest
import com.recurly.androidsdk.data.model.tokenization.toPublic
import com.recurly.androidsdk.data.network.RecurlyApiClient
import com.recurly.androidsdk.data.network.TokenService
import com.recurly.androidsdk.data.network.core.RetrofitHelper
import com.recurly.androidsdk.domain.GetRecurlyToken
import kotlinx.coroutines.CancellationException

/**
 * Entry point for Recurly credit card tokenization.
 *
 * Requests are automatically routed to Recurly's EU data center when [publicKey] is an EU
 * site's public key (prefixed `fra-`); no additional configuration is required.
 *
 * @param publicKey your Recurly site's public key
 */
class RecurlyClient private constructor(
    private val publicKey: String,
    private val getRecurlyToken: GetRecurlyToken
) {

    constructor(publicKey: String) : this(
        publicKey,
        GetRecurlyToken(
            TokenRepository(TokenService(RetrofitHelper.getRetrofit(publicKey).create(RecurlyApiClient::class.java)))
        )
    )

    /**
     * DI seam for offline testing: allows injecting a [RecurlyApiClient] pointed at a mock
     * server instead of building one via [RetrofitHelper].
     */
    internal constructor(publicKey: String, apiClient: RecurlyApiClient) : this(
        publicKey,
        GetRecurlyToken(TokenRepository(TokenService(apiClient)))
    )

    /**
     * Tokenizes the given card and billing information.
     *
     * @param card a [RecurlyCardParams] snapshot obtained from a Recurly card input view
     * @param billingInfo the billing information to submit alongside the card
     * @return the resulting [RecurlyToken]
     * @throws RecurlyException if the tokenization fails (validation error, decline, or network failure)
     */
    suspend fun tokenize(card: RecurlyCardParams, billingInfo: RecurlyBillingInfo): RecurlyToken {
        val request = TokenizationRequest(
            firstName = billingInfo.firstName,
            lastName = billingInfo.lastName,
            company = billingInfo.company,
            addressOne = billingInfo.addressOne,
            addressTwo = billingInfo.addressTwo,
            city = billingInfo.city,
            state = billingInfo.state,
            postalCode = billingInfo.postalCode,
            country = billingInfo.country,
            phone = billingInfo.phone,
            vatNumber = billingInfo.vatNumber,
            taxIdentifier = billingInfo.taxIdentifier,
            taxIdentifierType = billingInfo.taxIdentifierType,
            cardNumber = card.cardNumber,
            expirationMonth = card.expirationMonth,
            expirationYear = card.expirationYear,
            cvvCode = card.cvvCode,
            sdkVersion = RecurlySessionData.versionName,
            publicKey = publicKey
        )

        val response = try {
            getRecurlyToken(request)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            throw RecurlyException(
                ErrorRecurly(
                    "connection_failed",
                    e.message ?: "Network request failed",
                    emptyList(),
                    emptyList()
                )
            )
        }

        if (response.token.isNullOrEmpty() || response.type.isNullOrEmpty()) {
            throw RecurlyException(response.error)
        }

        return RecurlyToken(
            id = response.token,
            type = response.type,
            card = response.card?.toPublic()
        )
    }
}
