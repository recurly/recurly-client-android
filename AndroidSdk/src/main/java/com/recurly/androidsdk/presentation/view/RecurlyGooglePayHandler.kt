package com.recurly.androidsdk.presentation.view

import android.app.Activity
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.wallet.IsReadyToPayRequest
import com.google.android.gms.wallet.PaymentData
import com.google.android.gms.wallet.PaymentDataRequest
import com.google.android.gms.wallet.PaymentsClient
import com.google.android.gms.wallet.Wallet
import com.google.gson.JsonParser
import com.recurly.androidsdk.data.model.googlepay.GooglePayPaymentMethod
import com.recurly.androidsdk.data.model.googlepay.GooglePayTokenRequest
import com.recurly.androidsdk.data.model.googlepay.RecurlyGooglePayMethod
import com.recurly.androidsdk.data.model.googlepay.RecurlyGooglePayParams
import com.recurly.androidsdk.data.model.googlepay.toPublic
import com.recurly.androidsdk.data.model.tokenization.ErrorRecurly
import com.recurly.androidsdk.data.model.tokenization.RecurlyBillingInfo
import com.recurly.androidsdk.data.model.tokenization.RecurlyException
import com.recurly.androidsdk.data.model.tokenization.RecurlyToken
import com.recurly.androidsdk.data.model.tokenization.toPublic
import com.recurly.androidsdk.domain.GetGooglePayMerchantInfo
import com.recurly.androidsdk.domain.GetGooglePayToken
import com.recurly.androidsdk.domain.GooglePayRequestBuilder
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Orchestrates a native Google Pay payment: checks readiness, shows the Google Pay sheet, and
 * exchanges the resulting `PaymentData` for a Recurly token.
 *
 * Obtained via [com.recurly.androidsdk.RecurlyClient.googlePay]. Must be created before the host
 * [activity] reaches the `STARTED` lifecycle state (e.g. from `onCreate`), since it registers an
 * [ActivityResultLauncher] internally.
 */
class RecurlyGooglePayHandler internal constructor(
    private val activity: ComponentActivity,
    private val getGooglePayMerchantInfo: GetGooglePayMerchantInfo,
    private val getGooglePayToken: GetGooglePayToken,
    private val publicKey: String,
    private val sdkVersion: String,
    private val deviceId: String,
    private val sessionId: String,
    private val enableLogging: Boolean
) {

    private var pendingContinuation: Continuation<PaymentData>? = null

    private val resolutionLauncher: ActivityResultLauncher<IntentSenderRequest> =
        activity.registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            val continuation = pendingContinuation
            pendingContinuation = null
            if (continuation == null) return@registerForActivityResult
            when {
                result.resultCode == Activity.RESULT_OK && result.data != null -> {
                    val paymentData = PaymentData.getFromIntent(result.data!!)
                    if (paymentData != null) {
                        continuation.resume(paymentData)
                    } else {
                        continuation.resumeWithException(RecurlyException(connectionFailure("Empty Google Pay result")))
                    }
                }
                result.resultCode == Activity.RESULT_CANCELED ->
                    continuation.resumeWithException(CancellationException("Google Pay sheet was canceled"))
                else ->
                    continuation.resumeWithException(RecurlyException(connectionFailure("Google Pay sheet failed")))
            }
        }

    /**
     * Resolves the server-driven Google Pay payment method configuration for [params], to
     * configure a [RecurlyGooglePayButton] with, or `null` if Google Pay is not configured for
     * this gateway/currency/country.
     *
     * @throws RecurlyException if fetching the configuration fails (network failure)
     */
    suspend fun getPaymentMethod(params: RecurlyGooglePayParams): RecurlyGooglePayMethod? {
        return fetchMethod(params)?.toPublic()
    }

    /**
     * Checks whether the device can pay with Google Pay for the given [params]. Returns `false`
     * (rather than throwing) on any failure, including a network failure while resolving the
     * merchant configuration, since this is meant to gate whether a Google Pay button is shown.
     */
    suspend fun isReadyToPay(params: RecurlyGooglePayParams): Boolean {
        return try {
            val method = fetchMethod(params) ?: return false
            val request = IsReadyToPayRequest.fromJson(GooglePayRequestBuilder.buildIsReadyToPayRequestJson(method))
            awaitIsReadyToPay(paymentsClient(params), request)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Shows the Google Pay sheet and tokenizes the resulting payment method.
     *
     * Only one call to this function (or its callback overload) may be in flight at a time per
     * handler instance; a concurrent call throws immediately rather than interrupting the one
     * already showing the payment sheet.
     *
     * @throws RecurlyException if the merchant configuration is invalid, the Google Pay sheet
     * fails, another request is already in progress, or the subsequent tokenization request fails
     */
    suspend fun requestPayment(params: RecurlyGooglePayParams, billingInfo: RecurlyBillingInfo): RecurlyToken {
        val method = fetchMethod(params) ?: throw RecurlyException(
            ErrorRecurly(
                "google-pay-not-configured",
                "Google Pay is not configured for this gateway.",
                emptyList(),
                emptyList()
            )
        )
        val client = paymentsClient(params)
        val paymentDataRequest = PaymentDataRequest.fromJson(
            GooglePayRequestBuilder.buildPaymentDataRequestJson(method, params)
        )

        val paymentData = loadPaymentData(client, paymentDataRequest)
        val paymentDataJson = JsonParser.parseString(paymentData.toJson()).asJsonObject

        val request = GooglePayTokenRequest(
            gatewayCode = method.gatewayCode ?: params.gatewayCode,
            paymentData = paymentDataJson,
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
            sdkVersion = sdkVersion,
            publicKey = publicKey,
            deviceId = deviceId,
            sessionId = sessionId
        )

        val response = try {
            getGooglePayToken(request)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            throw RecurlyException(connectionFailure(e.message ?: "Network request failed"))
        }

        if (response.token.isNullOrEmpty() || response.type.isNullOrEmpty()) {
            throw RecurlyException(response.error)
        }

        return RecurlyToken(id = response.token, type = response.type, card = response.card?.toPublic())
    }

    /**
     * Callback overload of [requestPayment] for non-coroutine callers. Launched on
     * [activity]'s [androidx.lifecycle.lifecycleScope].
     */
    fun requestPayment(
        params: RecurlyGooglePayParams,
        billingInfo: RecurlyBillingInfo,
        onResult: (Result<RecurlyToken>) -> Unit
    ) {
        activity.lifecycleScope.launch {
            onResult(runCatching { requestPayment(params, billingInfo) })
        }
    }

    private suspend fun fetchMethod(params: RecurlyGooglePayParams): GooglePayPaymentMethod? {
        val info = try {
            getGooglePayMerchantInfo(params.gatewayCode, params.currency, params.country)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            throw RecurlyException(connectionFailure(e.message ?: "Network request failed"))
        }
        return info.paymentMethods.firstOrNull()
    }

    private fun paymentsClient(params: RecurlyGooglePayParams): PaymentsClient {
        return Wallet.getPaymentsClient(
            activity,
            Wallet.WalletOptions.Builder()
                .setEnvironment(GooglePayRequestBuilder.walletEnvironmentConstant(params.environment))
                .build()
        )
    }

    private suspend fun awaitIsReadyToPay(client: PaymentsClient, request: IsReadyToPayRequest): Boolean {
        return suspendCancellableCoroutine { continuation ->
            client.isReadyToPay(request).addOnCompleteListener { task ->
                continuation.resume(task.isSuccessful && task.result == true)
            }
        }
    }

    /**
     * @throws RecurlyException if another Google Pay request is already awaiting the payment
     * sheet's result on this handler; only one [requestPayment] can be in flight at a time since
     * [pendingContinuation] and [resolutionLauncher] are shared per-handler state.
     */
    private suspend fun loadPaymentData(client: PaymentsClient, request: PaymentDataRequest): PaymentData {
        if (pendingContinuation != null) {
            throw RecurlyException(connectionFailure("A Google Pay request is already in progress"))
        }
        return suspendCancellableCoroutine { continuation ->
            pendingContinuation = continuation
            continuation.invokeOnCancellation {
                if (pendingContinuation === continuation) {
                    pendingContinuation = null
                }
            }
            client.loadPaymentData(request).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    pendingContinuation = null
                    continuation.resume(task.result)
                } else {
                    val exception = task.exception
                    if (exception is ResolvableApiException) {
                        resolutionLauncher.launch(IntentSenderRequest.Builder(exception.resolution).build())
                    } else {
                        pendingContinuation = null
                        continuation.resumeWithException(
                            RecurlyException(connectionFailure(exception?.message ?: "Google Pay sheet failed"))
                        )
                    }
                }
            }
        }
    }

    private fun connectionFailure(message: String) = ErrorRecurly(
        "connection_failed",
        if (enableLogging) message else "Network request failed",
        emptyList(),
        emptyList()
    )
}
