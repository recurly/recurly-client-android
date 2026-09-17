package com.recurly.androidsdk.presentation.view

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.recurly.androidsdk.R
import com.recurly.androidsdk.databinding.RecurlyUnifiedCreditCardBinding
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import com.recurly.androidsdk.data.model.CreditCardsParameters
import com.recurly.androidsdk.data.model.tokenization.RecurlyCardParams
import com.recurly.androidsdk.domain.RecurlyDataFormatter
import com.recurly.androidsdk.domain.RecurlyInputValidator


class RecurlyUnifiedCreditCard @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var textColor: Int
    private var errorTextColor: Int
    private var hintColor: Int
    private var boxColor: Int
    private var errorBoxColor: Int
    private var focusedBoxColor: Int

    // A server error comes from outside this view. Keep each highlight until its field's text changes, a validateData call, or clearData.
    private var forcedNumberError = false
    private var forcedExpiryError = false
    private var forcedCvvError = false

    private var binding: RecurlyUnifiedCreditCardBinding =
        RecurlyUnifiedCreditCardBinding.inflate(LayoutInflater.from(context), this)

    /**
     * All colors are stored as Int values to simplify the color changes
     */
    init {
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        textColor = ContextCompat.getColor(context, R.color.recurly_black)
        errorTextColor = ContextCompat.getColor(context, R.color.recurly_error_red)
        boxColor = ContextCompat.getColor(context, R.color.recurly_gray_stroke)
        errorBoxColor = ContextCompat.getColor(context, R.color.recurly_error_red_blur)
        focusedBoxColor = ContextCompat.getColor(context, R.color.recurly_focused_blue)
        hintColor = ContextCompat.getColor(context, R.color.recurly_gray_stroke)
        cardNumberInputValidator()
        monthAndYearInputValidator()
        cvvInputValidator()
    }

    /**
     * Sets the placeholder texts
     * @param creditCardNumber Placeholder text for Credit Card Number field
     * @param monthAndYear Placeholder text for MM/YY field
     * @param cvv Placeholder text for CVV field
     */
    fun setPlaceholders(creditCardNumber: String, monthAndYear: String, cvv: String) {
        if (creditCardNumber.trim().isNotEmpty())
            binding.recurlyTextInputCardNumber.hint = creditCardNumber
        if (monthAndYear.trim().isNotEmpty())
            binding.recurlyTextInputCardExpiration.hint = monthAndYear
        if (cvv.trim().isNotEmpty())
            binding.recurlyTextInputCardCvv.hint = cvv
    }


    /**
     * Sets the placeholder color
     * @param color the color as an Int, for example ContextCompat.getColor(context, R.color.your-color)
     */
    fun setPlaceholderColor(color: Int) {
        if (RecurlyInputValidator.validateColor(color)) {
            hintColor = color
            val colorState: ColorStateList = RecurlyInputValidator.getColorState(color)
            binding.recurlyTextInputCardNumber.placeholderTextColor = colorState
            binding.recurlyTextInputCardExpiration.placeholderTextColor = colorState
            binding.recurlyTextInputCardCvv.placeholderTextColor = colorState
        }
    }


    /**
     * Sets the text color
     * @param color the color as an Int, for example ContextCompat.getColor(context, R.color.your-color)
     */
    fun setTextColor(color: Int) {
        if (RecurlyInputValidator.validateColor(color)) {
            textColor = color
            binding.recurlyTextEditCardNumber.setTextColor(color)
            binding.recurlyTextEditCardExpiration.setTextColor(color)
            binding.recurlyTextEditCardCvv.setTextColor(color)
        }
    }


    /**
     * Sets the error text color
     * @param color the color as an Int, for example ContextCompat.getColor(context, R.color.your-color)
     */
    fun setTextErrorColor(color: Int) {
        if (RecurlyInputValidator.validateColor(color))
            errorTextColor = color
    }

    /**
     * Sets the input fonts
     * @param newFont non null Typeface
     * @param style style as int
     */
    fun setFont(newFont: Typeface, style: Int) {
        binding.recurlyTextEditCardExpiration.setTypeface(newFont, style)
        binding.recurlyTextEditCardNumber.setTypeface(newFont, style)
        binding.recurlyTextEditCardCvv.setTypeface(newFont, style)

        binding.recurlyTextInputCardCvv.typeface = newFont
        binding.recurlyTextInputCardNumber.typeface = newFont
        binding.recurlyTextInputCardExpiration.typeface = newFont
    }

    /**
     * Validates the card number, expiration date, and CVV code.
     *
     * @return a Triple of validation results for the card number, expiration date, and CVV code. Each element is true when its field is valid
     */
    fun validateData(): Triple<Boolean, Boolean, Boolean> {
        forcedNumberError = false
        forcedExpiryError = false
        forcedCvvError = false
        val cardOk = validCardNumber(numberText())
        val expiryOk = RecurlyInputValidator.verifyDate(expirationText())
        val cvvOk = RecurlyInputValidator.verifyCVV(cvvText())
        validateAndChangeColors(false, cardOk, expiryOk, cvvOk)
        return Triple(cardOk, expiryOk, cvvOk)
    }


    /**
     * Builds a [RecurlyCardParams] snapshot of the card data currently entered into this view,
     * ready to pass to [com.recurly.androidsdk.RecurlyClient.tokenize].
     *
     * Call [validateData] first to ensure the entered card data is complete and valid.
     */
    fun cardParams(): RecurlyCardParams {
        val number = numberText()
        val expiration = expirationText()
        val cvv = cvvText()
        return RecurlyCardParams(
            cardNumber = RecurlyDataFormatter.getCardNumber(number, validCardNumber(number)),
            expirationMonth = RecurlyDataFormatter.getExpirationMonth(
                expiration, RecurlyInputValidator.verifyDate(expiration)
            ),
            expirationYear = RecurlyDataFormatter.getExpirationYear(
                expiration, RecurlyInputValidator.verifyDate(expiration)
            ),
            cvvCode = RecurlyDataFormatter.getCvvCode(cvv, RecurlyInputValidator.verifyCVV(cvv))
        )
    }


    /** Clears the entered data and the error highlight. */
    fun clearData() {
        forcedNumberError = false
        forcedExpiryError = false
        forcedCvvError = false
        binding.recurlyTextEditCardNumber.setText("")
        binding.recurlyTextEditCardExpiration.setText("")
        binding.recurlyTextEditCardCvv.setText("")
        validateAndChangeColors(false)
        if (binding.recurlyTextEditCardCvv.hasFocus())
            changeCvvIcon()
        else
            changeCardIcon()
    }

    /**
     * Marks the card number field with an error highlight. Use it for server tokenization errors or custom error states.
     * The highlight persists until that field's text changes, [validateData] runs, or [clearData] is called.
     */
    fun setCreditCardNumberError() {
        forcedNumberError = true
        validateAndChangeColors(false, cardOk = false)
    }

    /**
     * Marks the CVV field with an error highlight. Use it for server tokenization errors or custom error states.
     * The highlight persists until that field's text changes, [validateData] runs, or [clearData] is called.
     */
    fun setCvvError() {
        forcedCvvError = true
        validateAndChangeColors(false, cvvOk = false)
    }

    /**
     * Marks the expiration date field with an error highlight. Use it for server tokenization errors or custom error states.
     * The highlight persists until that field's text changes, [validateData] runs, or [clearData] is called.
     */
    fun setExpirationError() {
        forcedExpiryError = true
        validateAndChangeColors(false, expiryOk = false)
    }

    /**
     * Validates the card number input as the user types and when the focus changes.
     *
     * Text changes run the input validators, replace the text with the formatted
     * result, and repaint the colors according to the result.
     *
     * When focus changes it re-derives validity from the current text and repaints
     */
    private fun cardNumberInputValidator() {
        binding.recurlyTextEditCardNumber.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                forcedNumberError = false
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (s != null) {
                    if (s.toString().isNotEmpty()) {
                        val oldValue = s.toString()
                        val data =
                            RecurlyInputValidator.validateCreditCardNumber(
                                s.toString().replace(" ", "")
                            )
                        // Replace the text only when the validator reformatted it
                        if (oldValue != data.third) {
                            binding.recurlyTextEditCardNumber.removeTextChangedListener(this)
                            s.replace(0, oldValue.length, data.third)
                            binding.recurlyTextEditCardNumber.addTextChangedListener(this)
                        }

                        // Partial brand detection, not full Luhn verification, matches the
                        // while-typing state of the field.
                        validateAndChangeColors(true, cardOk = data.first)
                    } else {
                        validateAndChangeColors(true)
                    }
                    changeCardIcon()
                }
            }
        })

        binding.recurlyTextEditCardNumber.setOnFocusChangeListener { v, hasFocus ->
            validateAndChangeColors(hasFocus)
        }
    }

    /**
     * Validates the expiration date input as the user types and when the focus changes.
     *
     * Text changes run the input validators, replace the text with the formatted
     * result, and repaint the colors according to the result.
     *
     * When focus changes it re-derives validity from the current text and repaints
     */
    private fun monthAndYearInputValidator() {
        binding.recurlyTextEditCardExpiration.addTextChangedListener(object : TextWatcher {

            private var previousDateValue = ""

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                forcedExpiryError = false
                // The validator detects insertions by comparing lengths with the previous text
                if (s != null)
                    previousDateValue = s.toString()
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (s != null) {
                    val oldValue = s.toString()
                    val data =
                        RecurlyInputValidator.validateExpirationDate(
                            RecurlyInputValidator.regexSpecialCharacters(
                                s.toString(), "0-9/"
                            ),
                            previousDateValue
                        )
                    binding.recurlyTextEditCardExpiration.removeTextChangedListener(this)
                    s.replace(0, oldValue.length, data.second)
                    binding.recurlyTextEditCardExpiration.addTextChangedListener(this)
                    validateAndChangeColors(true, expiryOk = data.first || s.toString().isEmpty())
                }
            }
        })

        binding.recurlyTextEditCardExpiration.setOnFocusChangeListener { v, hasFocus ->
            validateAndChangeColors(hasFocus)
        }

    }

    /**
     * Validates the CVV input as the user types and when the focus changes.
     *
     * Text changes run the input validators, replace the text with the formatted
     * result, and repaint the colors according to the result.
     *
     * When focus changes it re-derives validity from the current text and repaints
     */
    private fun cvvInputValidator() {
        binding.recurlyTextEditCardCvv.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                forcedCvvError = false
                // Cap input at 4 digits. Validation, not the filter, rejects bad lengths.
                binding.recurlyTextEditCardCvv.filters =
                    arrayOf<InputFilter>(LengthFilter(4))

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (s != null) {
                    val oldValue = s.toString()
                    val formattedCVV = RecurlyInputValidator.regexSpecialCharacters(
                        s.toString(), "0-9"
                    )
                    binding.recurlyTextEditCardCvv.removeTextChangedListener(this)
                    s.replace(0, oldValue.length, formattedCVV)
                    binding.recurlyTextEditCardCvv.addTextChangedListener(this)
                    validateAndChangeColors(true)
                }
            }
        })

        binding.recurlyTextEditCardCvv.setOnFocusChangeListener { v, hasFocus ->
            //Changes the cvv icon according to the card type
            if (hasFocus) {
                changeCvvIcon()
            } else {
                changeCardIcon()
            }
            validateAndChangeColors(hasFocus)
        }
    }

    private fun numberText(): String =
        binding.recurlyTextEditCardNumber.text.toString()

    private fun expirationText(): String =
        binding.recurlyTextEditCardExpiration.text.toString()

    private fun cvvText(): String =
        binding.recurlyTextEditCardCvv.text.toString()

    private fun detectCardType(text: String): String =
        RecurlyInputValidator.validateCreditCardNumber(text).second

    private fun validCardNumber(text: String): Boolean =
        RecurlyInputValidator.verifyCardNumber(text, detectCardType(text))

    private fun lenientCardNumber(): Boolean {
        val text = numberText()
        return validCardNumber(text) || text.isEmpty()
    }

    private fun lenientExpiration(): Boolean {
        val text = expirationText()
        return RecurlyInputValidator.verifyDate(text) || text.isEmpty()
    }

    private fun lenientCvv(): Boolean {
        val text = cvvText()
        return RecurlyInputValidator.verifyCVV(text) || text.isEmpty()
    }

    /**
     * Sets the text colors and the container stroke according to the current validity
     */
    private fun validateAndChangeColors(
        focused: Boolean,
        cardOk: Boolean = lenientCardNumber() && !forcedNumberError,
        expiryOk: Boolean = lenientExpiration() && !forcedExpiryError,
        cvvOk: Boolean = lenientCvv() && !forcedCvvError
    ) {

        if (!cardOk || !expiryOk || !cvvOk) {
            binding.recurlyImageViewStrokeBackground.setImageDrawable(
                ContextCompat.getDrawable(context, R.drawable.unified_stroke_error)
            )
        } else if (focused) {
            binding.recurlyImageViewStrokeBackground.setImageDrawable(
                ContextCompat.getDrawable(context, R.drawable.unified_stroke_focused)
            )
        } else {
            binding.recurlyImageViewStrokeBackground.setImageDrawable(
                ContextCompat.getDrawable(context, R.drawable.unified_stroke_container)
            )
        }

        if (cardOk)
            binding.recurlyTextEditCardNumber.setTextColor(textColor)
        else
            binding.recurlyTextEditCardNumber.setTextColor(errorTextColor)

        if (expiryOk)
            binding.recurlyTextEditCardExpiration.setTextColor(textColor)
        else
            binding.recurlyTextEditCardExpiration.setTextColor(errorTextColor)

        if (cvvOk)
            binding.recurlyTextEditCardCvv.setTextColor(textColor)
        else
            binding.recurlyTextEditCardCvv.setTextColor(errorTextColor)
    }

    /**
     * Derives the card brand from the current number text and sets the credit card icon
     */
    private fun changeCardIcon() {
        binding.recurlyImageUnifiedCardIcon.setImageDrawable(
            RecurlyDataFormatter.changeCardIcon(context, detectCardType(numberText()))
        )
    }

    /**
     * Sets the unified icon to the CVV icon for the detected card brand
     */
    private fun changeCvvIcon() {
        if (detectCardType(numberText()) == CreditCardsParameters.AMERICAN_EXPRESS.cardType)
            binding.recurlyImageUnifiedCardIcon.setImageDrawable(
                ContextCompat.getDrawable(context, R.drawable.ic_amex_cvv)
            )
        else
            binding.recurlyImageUnifiedCardIcon.setImageDrawable(
                ContextCompat.getDrawable(context, R.drawable.ic_generic_cvv)
            )
    }
}