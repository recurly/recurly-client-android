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

    private var cardNumber: Long = 0
    private var expirationMonth: Int = 0
    private var expirationYear: Int = 0
    private var cvvCode: Int = 0
    private var cvvLength = 3

    internal fun getCardNumber(): Long{
        return cardNumber
    }

    internal fun setCardNumber(number: Long){
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

    internal fun getCvvCode(): Int{
        return cvvCode
    }

    internal fun setCvvCode(cvv: Int){
        cvvCode = cvv
    }

    internal fun getCvvLength(): Int{
        return cvvLength
    }

    internal fun setCvvLength(length: Int){
        cvvLength = length
    }

}