package com.recurly.androidsdk.data.network

import com.recurly.androidsdk.data.model.tokenization.TokenizationRequest
import com.recurly.androidsdk.data.model.tokenization.TokenizationResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * Here should be all the calls to the api with retrofit
 */
interface RecurlyApiClient {

    @FormUrlEncoded
    @POST("js/v1/tokens")
    suspend fun recurlyTokenization(
        @Field(value = "first_name", encoded = true) first_name: String,
        @Field(value = "last_name", encoded = true) last_name: String,
        @Field(value = "company", encoded = true) company: String,
        @Field(value = "address1", encoded = true) address1: String,
        @Field(value = "address2", encoded = true) address2: String,
        @Field(value = "city", encoded = true) city: String,
        @Field(value = "state", encoded = true) state: String,
        @Field(value = "postal_code", encoded = true) postal_code: String,
        @Field(value = "country", encoded = true) country: String,
        @Field(value = "phone", encoded = true) phone: String,
        @Field(value = "vat_number", encoded = true) vat_number: String,
        @Field(value = "tax_identifier", encoded = true) tax_identifier: String,
        @Field(value = "tax_identifier_type", encoded = true) tax_identifier_type: String,
        @Field(value = "number", encoded = true) number: Long,
        @Field(value = "month", encoded = true) month: Int,
        @Field(value = "year", encoded = true) year: Int,
        @Field(value = "cvv", encoded = true) cvv: Int,
        @Field(value = "version", encoded = true) version: String,
        @Field(value = "key", encoded = true) key: String,
        @Field(value = "deviceId", encoded = true) deviceId: String,
        @Field(value = "sessionId", encoded = true) sessionId: String
    ): Response<TokenizationResponse>

}