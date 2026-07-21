package com.recurly.androidsdk.data.model.tokenization

import com.google.gson.annotations.SerializedName

data class RecurlyBillingInfo(
    @SerializedName("first_name")
    val firstName: String,
    @SerializedName("last_name")
    val lastName: String,
    @SerializedName("company")
    val company: String = "",
    @SerializedName("address1")
    val addressOne: String = "",
    @SerializedName("address2")
    val addressTwo: String = "",
    @SerializedName("city")
    val city: String = "",
    @SerializedName("state")
    val state: String = "",
    @SerializedName("postal_code")
    val postalCode: String = "",
    @SerializedName("country")
    val country: String = "",
    @SerializedName("phone")
    val phone: String = "",
    @SerializedName("vat_number")
    val vatNumber: String = "",
    @SerializedName("tax_identifier")
    val taxIdentifier: String = "",
    @SerializedName("tax_identifier_type")
    val taxIdentifierType: String = ""
)