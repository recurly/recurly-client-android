package com.recurly.androidsdk.domain

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.recurly.androidsdk.R
import com.recurly.androidsdk.data.model.CreditCardsParameters

internal object RecurlyDataFormatter {

    internal fun getCardNumber(number: String, validInput: Boolean): String {
        val cardNumber =
            number.replace(" ", "")
        return if (cardNumber.isNotEmpty() && validInput && cardNumber.all { it.isDigit() })
            cardNumber
        else
            ""
    }

    /**
     * @param expiration expiration date as string
     * @param validInput if it is a valid expiration date
     * @return Returns the month as an Int
     */
    internal fun getExpirationMonth(expiration: String, validInput: Boolean): Int {
        val expirationDate =
            expiration.split("/")
        return if (expirationDate.size == 2 && validInput) {
            if (expirationDate[0].isNotEmpty())
                expirationDate[0].toInt()
            else
                0
        } else
            0
    }

    /**
     * @param expiration expiration date as string
     * @param validInput if it is a valid expiration date
     * @return Returns the year as an Int
     */
    internal fun getExpirationYear(expiration: String, validInput: Boolean): Int {
        val expirationDate =
            expiration.split("/")
        return if (expirationDate.size == 2 && validInput) {
            if (expirationDate[1].isNotEmpty())
                expirationDate[1].toInt()
            else
                0
        } else
            0
    }

    internal fun getCvvCode(cvv: String, validInput: Boolean): String {
        return if (cvv.isNotEmpty() && validInput && cvv.all { it.isDigit() })
            cvv
        else
            ""
    }

    /**
     * This fun get as a parameter the card type from CreditCardData to change the credit card icon
     * @param context Context
     * @param cardType the card type according to CreditCardsParameters
     * @return Returns the icon of the credit card
     */
    internal fun changeCardIcon(context: Context, cardType: String): Drawable {
        when (cardType) {
            CreditCardsParameters.HIPERCARD.cardType ->
                return ContextCompat.getDrawable(context, R.drawable.ic_hipercard_card)!!
            CreditCardsParameters.AMERICAN_EXPRESS.cardType ->
                return ContextCompat.getDrawable(context, R.drawable.ic_amex_card)!!
            CreditCardsParameters.DINERS_CLUB.cardType ->
                return ContextCompat.getDrawable(context, R.drawable.ic_diners_club_card)!!
            CreditCardsParameters.DISCOVER.cardType ->
                return ContextCompat.getDrawable(context, R.drawable.ic_discover_card)!!
            CreditCardsParameters.ELO.cardType ->
                return ContextCompat.getDrawable(context, R.drawable.ic_elo_card)!!
            CreditCardsParameters.JCB.cardType ->
                return ContextCompat.getDrawable(context, R.drawable.ic_jcb_card)!!
            CreditCardsParameters.MASTER.cardType ->
                return ContextCompat.getDrawable(context, R.drawable.ic_mastercard_card)!!
            CreditCardsParameters.TARJETA_NARANJA.cardType ->
                return ContextCompat.getDrawable(context, R.drawable.ic_tarjeta_naranja_card)!!
            CreditCardsParameters.UNION_PAY.cardType ->
                return ContextCompat.getDrawable(context, R.drawable.ic_union_pay_card)!!
            CreditCardsParameters.VISA.cardType ->
                return ContextCompat.getDrawable(context, R.drawable.ic_visa_card)!!
            else ->
                return ContextCompat.getDrawable(context, R.drawable.ic_generic_valid_card)!!
        }
    }
}