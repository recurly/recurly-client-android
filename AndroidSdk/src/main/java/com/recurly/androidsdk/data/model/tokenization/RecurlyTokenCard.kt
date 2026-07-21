package com.recurly.androidsdk.data.model.tokenization

/**
 * Metadata about the card that was tokenized. The PAN and CVV are never returned;
 * only PCI-permitted display data is included (BIN/first six digits, last four
 * digits, brand, expiration, funding source, and issuing country).
 *
 * @property firstSix the first six digits of the card number (the BIN)
 * @property lastFour the last four digits of the card number
 * @property brand the card brand, e.g. "visa"
 * @property expMonth the card's expiration month
 * @property expYear the card's expiration year
 * @property issuingCountry the ISO country code of the issuing bank, or "ZZ" if unknown
 * @property fundingSource the card's funding source, e.g. "credit", "debit", or "prepaid"
 */
data class RecurlyTokenCard(
    val firstSix: String?,
    val lastFour: String?,
    val brand: String?,
    val expMonth: Int?,
    val expYear: Int?,
    val issuingCountry: String?,
    val fundingSource: String?
)
