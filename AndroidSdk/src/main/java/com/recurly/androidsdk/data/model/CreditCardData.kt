package com.recurly.androidsdk.data.model

/**
 *
 * The Credit Card Data is stored in this Singleton object, with the purpose to limit client access
 * to the data that the final user will introduce
 *
 */

internal object CreditCardData {

    /**
     * Every variable has its own getters and setters
     */

    private var cardNumber: String = ""
    private var expirationMonth: Int = 0
    private var expirationYear: Int = 0
    private var cvvCode: String = ""
    private var cvvLength = 3

    internal fun getCardNumber(): String {
        return cardNumber
    }

    internal fun setCardNumber(number: String) {
        cardNumber = number
    }

    internal fun getExpirationMonth(): Int{
        return expirationMonth
    }

    internal fun setExpirationMonth(month: Int){
        expirationMonth = month
    }

    internal fun getExpirationYear(): Int{
        return expirationYear
    }

    internal fun setExpirationYear(year: Int){
        expirationYear = year
    }

    internal fun getCvvCode(): String {
        return cvvCode
    }

    internal fun setCvvCode(cvv: String) {
        cvvCode = cvv
    }

    internal fun getCvvLength(): Int{
        return cvvLength
    }

    internal fun setCvvLength(length: Int){
        cvvLength = length
    }

}