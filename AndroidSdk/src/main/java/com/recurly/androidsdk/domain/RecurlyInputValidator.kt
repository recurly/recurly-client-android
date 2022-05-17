package com.recurly.androidsdk.domain

import android.content.res.ColorStateList
import com.recurly.androidsdk.data.model.CreditCardsParameters
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern
import kotlin.collections.ArrayList

internal object RecurlyInputValidator {

    /**
     * Validates the color that has been sent from the app
     *
     * @param color the color as int that has been received
     * @return true if it is a correct int color value, false if it isn´t
     */
    fun validateColor(color: Int): Boolean {
        val hexColor = java.lang.String.format("#%06X", 0x00FFFFFF and color)
        val colorPattern: Pattern = Pattern.compile("#([0-9a-f]{3}|[0-9a-f]{6}|[0-9a-f]{8})")
        val m: Matcher = colorPattern.matcher(hexColor)
        return m.matches()
    }

    /**
     * @return color as a ColorStateList
     */
    fun getColorState(color: Int): ColorStateList {
        val states = arrayOf(intArrayOf())
        val colors = intArrayOf(
            color
        )
        return ColorStateList(states, colors)
    }

    /**
     * This fun takes the number from the input and validates if it follows any credit card pattern
     * and if it does send the number to format its digits acoding tho the physical patter of
     * the credit card
     *
     * @param number the number from the input
     * @return Triple<
     * Boolean - it follows a pattern,
     * String - Credit Card Type,
     * String - Credit card number formatted
     * >
     */
    fun validateCreditCardNumber(number: String): Triple<Boolean, String, String> {
        var correct = false
        var cardType = ""
        var formattedNumber = regexSpecialCharacters(number, "0-9 ")
        var physicalPattern = ""

        CreditCardsParameters.values().forEach {
            if (Pattern.matches(it.numberPattern, formattedNumber)) {
                correct = Pattern.matches(it.numberPattern, formattedNumber)
                cardType = it.cardType
                physicalPattern = it.physicalPattern
            }
        }

        if (cardType.isNotEmpty())
            formattedNumber = separateStringOnPattern(
                physicalPattern,
                regexSpecialCharacters(number, "0-9 ")
            )

        return Triple(correct, cardType, formattedNumber)
    }

    /**
     * This fun takes the input from the expiration date field and its previous value to validate
     * if it has changed, if it follow the pattern MM/YY
     *
     * @param expirationDate the actual input from the text input field
     * @param previousValue the previous value to the change of the input field
     *
     * @return Pair< Boolean - if the input follows a correct MM/YY pattern, String - expiration date formatted >
     */
    fun validateExpirationDate(
        expirationDate: String,
        previousValue: String
    ): Pair<Boolean, String> {
        var correct = false
        var formattedDate = expirationDate

        if (previousValue.length < expirationDate.length) {
            formattedDate = verifyCharactersExpirationDate(expirationDate)
            if (formattedDate.isNotEmpty()) {
                if (formattedDate.contains("/")) {
                    val splitDate = formattedDate.split("/")
                    correct = if (splitDate.size == 2) {
                        if (splitDate[0].isEmpty() || splitDate[1].isEmpty())
                            false
                        else {
                            return Pair(verifyDate(formattedDate), formattedDate)
                        }
                    } else {
                        false
                    }
                } else {
                    if (formattedDate == "0" || formattedDate == "1") {
                        correct = true
                    } else {
                        if (formattedDate.toInt() in 2..9) {
                            correct = true
                            if (formattedDate.contains("0"))
                                formattedDate = "$formattedDate/"
                            else
                                formattedDate = "0$formattedDate/"
                        } else if (formattedDate.toInt() in 10..12 || formattedDate == "01") {
                            correct = true
                            formattedDate = "$formattedDate/"
                        }
                    }
                }
            }
        }

        return Pair(correct, formattedDate)
    }

    /**
     * This function was created to validate correct MM/YY digits distribution and not allow the
     * final user to write a date with 3 digits on the month or the year
     *
     * @param expirationDate the actual input from the text input field
     *
     * @return expiration date formatted if it applies
     */
    private fun verifyCharactersExpirationDate(expirationDate: String):String{
        var formattedDate: String  = expirationDate
        val array: Array<String> = expirationDate.toCharArray().map { it.toString() }.toTypedArray()
        var counter = 0
        if (expirationDate.contains("/")){
            val splitDate = expirationDate.split("/")
            var month = ""
            var year = ""
            if(splitDate[0].length >= 3){
                month = splitDate[0].substring(0,2)
                year = splitDate[0].substring(2) + splitDate[1]
                formattedDate = "$month/$year"
            } else if (splitDate[1].length >= 3){
                month = splitDate[0] + splitDate[1].substring(0,1)
                year = splitDate[1].substring(1)
                formattedDate = "$month/$year"
            }
        } else if(expirationDate.length >=3){
            array.forEach {
                when (counter) {
                    0 -> {
                        formattedDate = it
                    }
                    1 -> {
                        formattedDate += "$it/"
                    }
                    4 ->{
                    }
                    else -> {
                        formattedDate += it
                    }
                }
                counter++
            }
        }
        return formattedDate
    }

    /**
     * This fun verifies if the credit card number follows a credit card pattern and if it
     * have the min or max digits required according to the credit card type
     *
     * @param number the credit card number
     * @param cardType the type of credit card according to CreditCardData
     *
     * @return true if it is a complete and valid credit card number, false if it is not
     */
    fun verifyCardNumber(number: String, cardType: String): Boolean {
        val numberTrim = number.replace(" ", "")
        if (numberTrim.isNotEmpty() && cardType.isNotEmpty()) {
            if ((numberTrim.length >= CreditCardsParameters.valueOf(cardType.uppercase()).minLength &&
                        numberTrim.length <= CreditCardsParameters.valueOf(cardType.uppercase()).maxLength) &&
                Pattern.matches(
                    CreditCardsParameters.valueOf(cardType.uppercase()).numberPattern,
                    numberTrim
                )
            )
                return true
        }
        return false
    }

    /**
     * This fun verifies if the cvv code is correct according to the credit card type
     *
     * @param cvvCode the cvv code from the input
     * @param cardType the card type according tho CreditCardData
     *
     * @return true if it is a valid cvv code, false if it is not
     */
    fun verifyCVV(cvvCode: String, cardType: String): Boolean {
        if (cvvCode.isNotEmpty() && cardType.isNotEmpty()) {
            return cvvCode.length == CreditCardsParameters.valueOf(cardType.uppercase()).cvvLength
        }
        return false
    }

    /**
     * This fun validates if the input follows the pattern MM/YY and is a valid date
     *
     * @param dateMMYY the input data in MM/YY format
     *
     * @return true if it is valid MM/YY date, false if it is not
     */
    fun verifyDate(dateMMYY: String): Boolean {
        if (dateMMYY.isNotEmpty()) {
            if (dateMMYY.contains("/")) {
                val splitDate = dateMMYY.split("/")
                if (splitDate.size == 2) {
                    if(splitDate[0].isNotEmpty() && splitDate[1].isNotEmpty()){
                        val year = Calendar.getInstance().get(Calendar.YEAR) % 100
                        val month = Calendar.getInstance().get(Calendar.MONTH) + 1
                        val monthVerify = (splitDate[0].toInt() in 10..12) ||
                                (splitDate[0].toInt() in 1..9 && splitDate[0].contains("0") )
                        val verifyYear = (splitDate[1].toInt() >= year) && (splitDate[1].toInt() < 100)
                        if (splitDate[1].toInt() == year)
                            if (splitDate[0].toInt() < month)
                                return false
                        return monthVerify && verifyYear
                    }
                }
            }
        }
        return false
    }

    /**
     * This fun takes the input number and give it the format of the physical pattern according
     * to the credit card type
     *
     * @param separatePattern the pattern in which the number will be formatted
     * @param cardNumber the number from the credit card input
     *
     * @return Returns the credit card number separated according to the pattern
     */
    fun separateStringOnPattern(separatePattern: String, cardNumber: String): String {
        var finalFormattedNumber = ""
        var oldNumber = cardNumber

        var charPattern: List<Int> = getPatternSeparation(separatePattern)

        charPattern.forEach {
            if (oldNumber.length > it) {
                finalFormattedNumber =
                    if (finalFormattedNumber.isNotEmpty())
                        "$finalFormattedNumber " + oldNumber.substring(0, it)
                    else
                        oldNumber.substring(0, it)
                oldNumber = oldNumber.drop(it)
            } else if (oldNumber.isNotEmpty()) {
                finalFormattedNumber =
                    if (finalFormattedNumber.isNotEmpty())
                        "$finalFormattedNumber $oldNumber"
                    else
                        oldNumber
                oldNumber = ""
            }
        }

        return finalFormattedNumber
    }

    /**
     * This fun gets the physical pattern from the credit card and transforms it to a List<Int>
     * to manage the digits from the card in an easier way
     *
     * @param separatePattern the physical pattern from the credit card according to CreditCardData
     *
     * @return Returns a List of Ints of the pattern
     */
    private fun getPatternSeparation(separatePattern: String): List<Int> {
        var patternIndex: MutableList<Int> = ArrayList()
        val entryPattern: List<String> = separatePattern.split("-")
        entryPattern.forEach {
            patternIndex.add(it.toInt())
        }
        return patternIndex
    }

    fun regexSpecialCharacters(inputData: String, charactersAccepted: String): String {
        val re = Regex("[^$charactersAccepted]")
        return re.replace(inputData, "")
    }

}