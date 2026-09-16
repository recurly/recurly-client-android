package com.recurly.androidsdk.data.network

import com.recurly.androidsdk.data.model.googlepay.GooglePayInfoResponse
import com.recurly.androidsdk.data.model.googlepay.GooglePayTokenRequest
import com.recurly.androidsdk.data.model.tokenization.TokenizationRequest
import com.recurly.androidsdk.data.model.tokenization.TokenizationResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * Here should be all the calls to the api with retrofit
 */
internal interface RecurlyApiClient {

    @FormUrlEncoded
    @POST("js/v1/tokens")
    suspend fun recurlyTokenization(
        @Field(value = "first_name") first_name: String,
        @Field(value = "last_name") last_name: String,
        @Field(value = "company") company: String,
        @Field(value = "address1") address1: String,
        @Field(value = "address2") address2: String,
        @Field(value = "city") city: String,
        @Field(value = "state") state: String,
        @Field(value = "postal_code") postal_code: String,
        @Field(value = "country") country: String,
        @Field(value = "phone") phone: String,
        @Field(value = "vat_number") vat_number: String,
        @Field(value = "tax_identifier") tax_identifier: String,
        @Field(value = "tax_identifier_type") tax_identifier_type: String,
        @Field(value = "number") number: String,
        @Field(value = "month") month: Int,
        @Field(value = "year") year: Int,
        @Field(value = "cvv") cvv: String,
        @Field(value = "version") version: String,
        @Field(value = "key") key: String,
        @Field(value = "device_id") device_id: String,
        @Field(value = "session_id") session_id: String
    ): Response<TokenizationResponse>

    /**
     * Fetches the server-driven Google Pay configuration (allowed card networks, auth methods,
     * and gateway tokenization spec) for the given gateway/currency/country. The returned values
     * must be used as-is to build the Google Pay [com.google.android.gms.wallet.PaymentDataRequest] —
     * never hardcode card networks or gateway tokenization parameters.
     */
    @GET("js/v1/google_pay/info")
    suspend fun getGooglePayMerchantInfo(
        @Query("gateway_code") gatewayCode: String?,
        @Query("currency") currency: String,
        @Query("country") country: String
    ): Response<GooglePayInfoResponse>

    /**
     * Exchanges a Google Pay [com.google.android.gms.wallet.PaymentData] payload for a
     * single-use Recurly token.
     */
    @POST("js/v1/google_pay/token")
    suspend fun recurlyGooglePayTokenization(
        @Body request: GooglePayTokenRequest
    ): Response<TokenizationResponse>

}