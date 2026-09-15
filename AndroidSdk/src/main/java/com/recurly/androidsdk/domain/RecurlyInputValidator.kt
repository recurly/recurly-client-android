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

    fun validateCreditCardNumber(number: String): Triple<Boolean, String, String> {
        val digits = regexSpecialCharacters(number, "0-9")
        // Partial detection must win, or maestro's 12-15 digit group claims truncated 6-prefixed PANs.
        val partialMatch = detectBrand(digits, partial = true)
        val detected = if (partialMatch != "unknown") partialMatch else detectBrand(digits, partial = false)
        val cardType = if (detected == "unknown") "" else detected
        val brand = CreditCardsParameters.entries.firstOrNull { it.cardType == cardType }
        val maxDigits = brand?.groups?.flatMap { it.lengths }?.maxOrNull() ?: 19
        val gaps = brand?.gaps ?: setOf(4, 8, 12)
        val formattedNumber = formatWithGaps(digits.take(maxDigits), gaps)

        return Triple(cardType.isNotEmpty(), cardType, formattedNumber)
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

    fun verifyCardNumber(number: String, cardType: String): Boolean {
        val digits = regexSpecialCharacters(number, "0-9")
        if (digits.isEmpty() || cardType.isEmpty()) return false
        return detectBrand(digits, partial = false) == cardType && isLuhnValid(digits)
    }

    /**
     * Verifies if the cvv code has a valid length. Brand-agnostic: recurly-js
     * accepts 3 or 4 digits for every brand.
     *
     * @param cvvCode the cvv code from the input
     *
     * @return true if it is a valid cvv code, false if it is not
     */
    fun verifyCVV(cvvCode: String): Boolean {
        return cvvCode.length in 3..4
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

    private fun detectBrand(digits: String, partial: Boolean): String {
        if (digits.isEmpty()) return "unknown"
        val compareLength = minOf(digits.length, 6)
        // Low end pads with 0 and high end with 9 to compare unequal BIN widths.
        val compareValue = buildCompareValue(digits, compareLength, '0') ?: return "unknown"

        val matches = CreditCardsParameters.entries.filter { brand ->
            // Maestro's ranges overlap other brands, so it needs the full length.
            if (partial && brand == CreditCardsParameters.MAESTRO) return@filter false
            brand.groups.any { group ->
                (partial || digits.length in group.lengths) &&
                    group.ranges.any { (start, end) ->
                        val rangeStart = buildCompareValue(start.toString(), compareLength, '0')
                        val rangeEnd = buildCompareValue(end.toString(), compareLength, '9')
                        rangeStart != null && rangeEnd != null && compareValue in rangeStart..rangeEnd
                    }
            }
        }

        // Elo BINs sit inside discover's span, so elo wins the overlap.
        if (matches.size == 2 &&
            matches.any { it == CreditCardsParameters.ELO } &&
            matches.any { it == CreditCardsParameters.DISCOVER }
        ) {
            return CreditCardsParameters.ELO.cardType
        }

        return if (matches.size == 1) matches[0].cardType else "unknown"
    }

    private fun buildCompareValue(source: String, length: Int, terminator: Char): Long? {
        var result = source.take(length)
        while (result.length < length) {
            result += terminator
        }
        return result.toLongOrNull()
    }

    private fun isLuhnValid(digits: String): Boolean {
        if (digits.length !in 12..19 || !digits.all { it in '0'..'9' }) return false
        var sum = 0
        var alternate = false
        for (i in digits.length - 1 downTo 0) {
            var n = digits[i] - '0'
            if (alternate) {
                n *= 2
                if (n > 9) n -= 9
            }
            sum += n
            alternate = !alternate
        }
        // Reject all-zero PANs, whose checksum is trivially valid.
        return sum % 10 == 0 && sum > 0
    }

    private fun formatWithGaps(digits: String, gaps: Set<Int>): String {
        if (digits.isEmpty()) return digits
        val builder = StringBuilder()
        digits.forEachIndexed { index, char ->
            if (index > 0 && index in gaps) builder.append(' ')
            builder.append(char)
        }
        return builder.toString()
    }

    fun regexSpecialCharacters(inputData: String, charactersAccepted: String): String {
        val re = Regex("[^$charactersAccepted]")
        return re.replace(inputData, "")
    }

}