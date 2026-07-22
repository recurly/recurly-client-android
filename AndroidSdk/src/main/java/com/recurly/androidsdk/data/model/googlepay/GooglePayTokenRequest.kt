package com.recurly.androidsdk.data.model.googlepay

import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName

/**
 * Body of `POST js/v1/google_pay/token`.
 *
 * Billing fields are flattened at the top level (matching the existing `/tokens` card
 * tokenization contract) alongside the full Google Pay `PaymentData` payload.
 */
internal data class GooglePayTokenRequest(
    @SerializedName("gateway_code")
    var gatewayCode: String?,
    @SerializedName("payment_data")
    var paymentData: JsonObject,
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
    @SerializedName("version")
    var sdkVersion: String,
    @SerializedName("key")
    var publicKey: String,
    @SerializedName("device_id")
    var deviceId: String,
    @SerializedName("session_id")
    var sessionId: String
)
