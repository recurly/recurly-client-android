package com.recurly.androidsdk.data.network

import com.recurly.androidsdk.data.model.tokenization.ErrorRecurly
import com.recurly.androidsdk.data.model.tokenization.TokenizationRequest
import com.recurly.androidsdk.data.model.tokenization.TokenizationResponse
import com.recurly.androidsdk.data.network.core.RetrofitHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class TokenService {

    private val retrofit = RetrofitHelper.getRetrofit()

    /**
     * @param TokenizationRequest
     * @return TokenizationResponse
     *
     * If the api call succeeds the response will a TokenizationResponse
     *
     * If the api call fails the response will be an empty Response Object with everything set
     * on empty
     *
     */
    suspend fun getToken(request: TokenizationRequest): TokenizationResponse {
        return withContext(Dispatchers.IO) {
            try {
                val response =
                    retrofit.create(RecurlyApiClient::class.java).recurlyTokenization(
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

                response.body() ?: TokenizationResponse(
                    "", "", ErrorRecurly(
                        errorCode = response.code().toString(),
                        errorMessage = response.message(),
                        fields = emptyList(),
                        details = emptyList()
                    )
                )
            } catch (exception: HttpException) {
                exception.printStackTrace()
                TokenizationResponse(
                    error = ErrorRecurly(
                        errorCode = exception.code().toString(),
                        errorMessage = exception.message(),
                        fields = listOf(exception.response()?.body().toString()),
                        details = emptyList(),
                    )
                )
            } catch (ioException: IOException) {
                ioException.printStackTrace()
                TokenizationResponse(
                    error = ErrorRecurly(
                        errorCode = "recurly-client-android-internet-connection",
                        errorMessage = "Your internet connection appears to be offline",
                        fields = emptyList(),
                        details = emptyList(),
                    )
                )
            } catch (exception: Exception) {
                exception.printStackTrace()
                TokenizationResponse(
                    error = ErrorRecurly(
                        errorCode = "recurly-client-android-networking",
                        errorMessage = exception.localizedMessage ?: "",
                        fields = emptyList(),
                        details = emptyList(),
                    )
                )
            }
        }
    }
}