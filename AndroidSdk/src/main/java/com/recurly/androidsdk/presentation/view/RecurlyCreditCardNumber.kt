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
import com.recurly.androidsdk.databinding.RecurlyCreditCardNumberBinding
import com.recurly.androidsdk.domain.RecurlyDataFormatter
import com.recurly.androidsdk.domain.RecurlyInputValidator

class RecurlyCreditCardNumber @JvmOverloads constructor(
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
    private var iconEnabled = true

    private var binding: RecurlyCreditCardNumberBinding =
        RecurlyCreditCardNumberBinding.inflate(LayoutInflater.from(context), this)

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
    }

    /**
     * Disables the credit card icon display
     */
    fun disableCardIcon() {
        iconEnabled = false
    }

    /**
     * Sets the placeholder text
     * @param creditCardNumber Placeholder text for credit card number field
     */
    fun setPlaceholder(creditCardNumber: String) {
        if (!creditCardNumber.trim().isEmpty())
            binding.recurlyTextInputLayoutIndividualCardNumber.hint = creditCardNumber
    }

    /**
     * Sets the placeholder color
     * @param color the color as an Int, for example ContextCompat.getColor(context, R.color.your-color)
     */
    fun setPlaceholderColor(color: Int) {
        if (RecurlyInputValidator.validateColor(color)) {
            hintColor = color
            val colorState: ColorStateList = RecurlyInputValidator.getColorState(color)
            binding.recurlyTextInputLayoutIndividualCardNumber.placeholderTextColor = colorState
        }
    }

    /**
     * Sets the text color
     * @param color the color as an Int, for example ContextCompat.getColor(context, R.color.your-color)
     */
    fun setTextColor(color: Int) {
        if (RecurlyInputValidator.validateColor(color)) {
            textColor = color
            binding.recurlyTextInputEditIndividualCardNumber.setTextColor(color)
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
     * Sets the input font
     * @param newFont non null Typeface
     * @param style style as int
     */
    fun setFont(newFont: Typeface, style: Int) {
        binding.recurlyTextInputEditIndividualCardNumber.setTypeface(newFont, style)
        binding.recurlyTextInputLayoutIndividualCardNumber.typeface = newFont
    }

    /**
     * Validates the entered card number
     * @return true if the input is valid, false if it is not
     */
    fun validateData(): Boolean {
        val valid = validCardNumber(currentNumberText())
        changeColors(valid)
        return valid
    }

    /**
     * Marks the card number field with an error highlight. Use it for server tokenization errors or custom error states
     */
    fun setCreditCardNumberError() {
        changeColors(false)
    }

    /**
     * Returns the currently validated card number entered into this view. Internal visibility:
     * consumed by [com.recurly.androidsdk.data.model.tokenization.RecurlyCardParams.from] to build
     * a tokenization snapshot without exposing raw card data publicly.
     */
    internal fun getCardNumber(): String {
        val text = currentNumberText()
        return RecurlyDataFormatter.getCardNumber(text, validCardNumber(text))
    }


    /** Clears the entered data and the error highlight. */
    fun clearData() {
        binding.recurlyTextInputEditIndividualCardNumber.setText("")
        changeColors()
        changeCardIcon()
    }

    private fun currentNumberText(): String =
        binding.recurlyTextInputEditIndividualCardNumber.text.toString()

    private fun detectCardType(text: String): String =
        RecurlyInputValidator.validateCreditCardNumber(text).second

    private fun validCardNumber(text: String): Boolean =
        RecurlyInputValidator.verifyCardNumber(text, detectCardType(text))

    private fun lenientCardNumber(): Boolean {
        val text = currentNumberText()
        return validCardNumber(text) || text.isEmpty()
    }

    /**
     * Sets the text color and the field highlight according to the current validity
     */
    private fun changeColors(ok: Boolean = lenientCardNumber()) {
        if (ok) {
            binding.recurlyTextInputLayoutIndividualCardNumber.error = null
            binding.recurlyTextInputEditIndividualCardNumber.setTextColor(textColor)
        } else {
            binding.recurlyTextInputLayoutIndividualCardNumber.error = " "
            binding.recurlyTextInputEditIndividualCardNumber.setTextColor(errorTextColor)
        }
    }

    /**
     * Validates the input as the user types and when the focus changes.
     *
     * Text changes run the input validators, replace the text with the formatted
     * result, and repaint the colors according to the result.
     *
     * When the field loses focus it re-derives validity from the current text and repaints
     */
    private fun cardNumberInputValidator() {
        binding.recurlyTextInputEditIndividualCardNumber.addTextChangedListener(object :
            TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
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
                        if (data.third.isNotEmpty() && oldValue != data.third) {
                            binding.recurlyTextInputEditIndividualCardNumber.removeTextChangedListener(
                                this
                            )
                            s.replace(0, oldValue.length, data.third)
                            binding.recurlyTextInputEditIndividualCardNumber.addTextChangedListener(
                                this
                            )
                        }
                        changeColors(data.first)
                    } else {
                        changeColors()
                    }
                    changeCardIcon()
                }
            }
        })

        binding.recurlyTextInputEditIndividualCardNumber.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                changeColors()
            }
        }
    }

    /**
     * Derives the card brand from the current number text and sets the credit card icon
     */
    private fun changeCardIcon() {
        binding.recurlyTextInputLayoutIndividualCardNumber.startIconDrawable =
            RecurlyDataFormatter.changeCardIcon(context, detectCardType(currentNumberText()))
    }
}