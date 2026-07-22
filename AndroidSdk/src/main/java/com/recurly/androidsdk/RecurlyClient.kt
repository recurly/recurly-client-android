package com.recurly.androidsdk

import android.content.Context
import androidx.activity.ComponentActivity
import com.recurly.androidsdk.data.GooglePayRepository
import com.recurly.androidsdk.data.TokenRepository
import com.recurly.androidsdk.data.model.DeviceSessionProvider
import com.recurly.androidsdk.data.model.RecurlySessionData
import com.recurly.androidsdk.data.model.tokenization.ErrorRecurly
import com.recurly.androidsdk.data.model.tokenization.RecurlyBillingInfo
import com.recurly.androidsdk.data.model.tokenization.RecurlyCardParams
import com.recurly.androidsdk.data.model.tokenization.RecurlyException
import com.recurly.androidsdk.data.model.tokenization.RecurlyToken
import com.recurly.androidsdk.data.model.tokenization.TokenizationRequest
import com.recurly.androidsdk.data.model.tokenization.toPublic
import com.recurly.androidsdk.data.network.GooglePayService
import com.recurly.androidsdk.data.network.RecurlyApiClient
import com.recurly.androidsdk.data.network.TokenService
import com.recurly.androidsdk.data.network.core.RetrofitHelper
import com.recurly.androidsdk.domain.GetGooglePayMerchantInfo
import com.recurly.androidsdk.domain.GetGooglePayToken
import com.recurly.androidsdk.domain.GetRecurlyToken
import com.recurly.androidsdk.presentation.view.RecurlyGooglePayHandler
import kotlinx.coroutines.CancellationException

class RecurlyClient private constructor(
    private val publicKey: String,
    private val getRecurlyToken: GetRecurlyToken,
    private val googlePayRepository: GooglePayRepository,
    private val enableLogging: Boolean,
    private val deviceId: String,
    private val sessionId: String
) {

    private val getGooglePayMerchantInfo = GetGooglePayMerchantInfo(googlePayRepository)
    private val getGooglePayToken = GetGooglePayToken(googlePayRepository)

    constructor(publicKey: String, enableLogging: Boolean = false) : this(
        publicKey,
        context = null,
        apiClient = RetrofitHelper.getRetrofit(publicKey).create(RecurlyApiClient::class.java),
        enableLogging = enableLogging
    )

    /**
     * @param context used to persist [deviceId] across app launches (SDK-private storage only;
     * never `ANDROID_ID` or another hardware/advertising identifier). Without this overload,
     * [deviceId] is a random value scoped to this [RecurlyClient] instance instead.
     */
    constructor(publicKey: String, context: Context, enableLogging: Boolean = false) : this(
        publicKey,
        context = context.applicationContext,
        apiClient = RetrofitHelper.getRetrofit(publicKey).create(RecurlyApiClient::class.java),
        enableLogging = enableLogging
    )

    /**
     * DI seam for offline testing: allows injecting a [RecurlyApiClient] pointed at a mock
     * server instead of building one via [RetrofitHelper].
     */
    internal constructor(publicKey: String, apiClient: RecurlyApiClient, enableLogging: Boolean = false) : this(
        publicKey,
        context = null,
        apiClient = apiClient,
        enableLogging = enableLogging
    )

    private constructor(publicKey: String, context: Context?, apiClient: RecurlyApiClient, enableLogging: Boolean) : this(
        publicKey,
        GetRecurlyToken(TokenRepository(TokenService(apiClient, enableLogging))),
        GooglePayRepository(GooglePayService(apiClient, enableLogging)),
        enableLogging,
        DeviceSessionProvider.deviceId(context),
        DeviceSessionProvider.newSessionId()
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
            publicKey = publicKey,
            deviceId = deviceId,
            sessionId = sessionId
        )

        try {
            val response = try {
                getRecurlyToken(request)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                throw RecurlyException(
                    ErrorRecurly(
                        "connection_failed",
                        if (enableLogging) (e.message ?: "Network request failed") else "Network request failed",
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
        } finally {
            // Drop the plaintext PAN/CVV copy as soon as the request has been sent, win or lose,
            // to shrink how long cardholder data lingers on the JVM heap.
            request.cardNumber = ""
            request.cvvCode = ""
        }
    }

    /**
     * Starts a native Google Pay payment flow.
     *
     * Must be called before [activity] reaches the `STARTED` lifecycle state (e.g. from
     * `onCreate`), since the returned [RecurlyGooglePayHandler] registers an
     * [androidx.activity.result.ActivityResultLauncher] internally.
     *
     * @param activity the host activity used to show the Google Pay sheet
     * @return a [RecurlyGooglePayHandler] to check readiness and request payment
     */
    fun googlePay(activity: ComponentActivity): RecurlyGooglePayHandler {
        return RecurlyGooglePayHandler(
            activity,
            getGooglePayMerchantInfo,
            getGooglePayToken,
            publicKey,
            RecurlySessionData.versionName,
            deviceId,
            sessionId,
            enableLogging
        )
    }
}
