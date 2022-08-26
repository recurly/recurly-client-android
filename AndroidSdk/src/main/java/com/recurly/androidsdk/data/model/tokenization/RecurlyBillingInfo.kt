package com.recurly.androidsdk.data.model.tokenization

import com.google.gson.annotations.SerializedName

data class RecurlyBillingInfo(
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
    var taxIdentifierType: String
)