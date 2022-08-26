package com.recurly.androidsdk.data.model.tokenization

import com.google.gson.annotations.SerializedName

/**
 * Tokenization Request
 *
 * This is a data class that is used to have the model structure that the server awaits
 * to make the Tokenization, some fields are not required, but we send them as empty strings
 * instead of using null
 *
 */

data class TokenizationRequest(
    @SerializedName("first_name")
    var firstName: String,
    @SerializedName("last_name")
    var lastName: String,
    @SerializedName("company")
    var company: String,
    @SerializedName("address1")
    var addressOne: String,
    @SerializedName("address2")
    var addressTwo: String,
    @SerializedName("city")
    var city: String,
    @SerializedName("state")
    var state: String,
    @SerializedName("postal_code")
    var postalCode: String,
    @SerializedName("country")
    var country: String,
    @SerializedName("phone")
    var phone: String,
    @SerializedName("vat_number")
    var vatNumber: String,
    @SerializedName("tax_identifier")
    var taxIdentifier: String,
    @SerializedName("tax_identifier_type")
    var taxIdentifierType: String,
    @SerializedName("number")
    var cardNumber: Long,
    @SerializedName("month")
    var expirationMonth: Int,
    @SerializedName("year")
    var expirationYear: Int,
    @SerializedName("cvv")
    var cvvCode: Int,
    @SerializedName("version")
    var sdkVersion: String,
    @SerializedName("key")
    var publicKey: String,
    @SerializedName("deviceId")
    var deviceId: String,
    @SerializedName("sessionId")
    var sessionId: String
)
