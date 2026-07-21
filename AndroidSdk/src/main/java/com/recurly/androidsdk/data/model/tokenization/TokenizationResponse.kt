package com.recurly.androidsdk.data.model.tokenization

import com.google.gson.annotations.SerializedName

internal data class TokenizationResponse(
    @SerializedName("type")
    val type: String ?= "",
    @SerializedName("id")
    val token: String ?= "",
    @SerializedName("card")
    val card: TokenCardResponse? = null,
    @SerializedName("error")
    val error: ErrorRecurly
)

/**
 * Internal wire model for the card metadata returned alongside a successful
 * tokenization response. Never returns PAN or CVV; only PCI-permitted display
 * data. Mapped to the public [RecurlyTokenCard] before being surfaced to integrators.
 */
internal data class TokenCardResponse(
    @SerializedName("brand")
    val brand: String? = null,
    @SerializedName("first_six")
    val firstSix: String? = null,
    @SerializedName("last_four")
    val lastFour: String? = null,
    @SerializedName("exp_month")
    val expMonth: Int? = null,
    @SerializedName("exp_year")
    val expYear: Int? = null,
    @SerializedName("issuing_country")
    val issuingCountry: String? = null,
    @SerializedName("funding_source")
    val fundingSource: String? = null
)

internal fun TokenCardResponse.toPublic(): RecurlyTokenCard = RecurlyTokenCard(
    firstSix = firstSix,
    lastFour = lastFour,
    brand = brand,
    expMonth = expMonth,
    expYear = expYear,
    issuingCountry = issuingCountry,
    fundingSource = fundingSource
)
