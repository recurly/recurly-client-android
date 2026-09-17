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
import com.recurly.androidsdk.databinding.RecurlyExpirationMmyyBinding
import com.recurly.androidsdk.domain.RecurlyDataFormatter
import com.recurly.androidsdk.domain.RecurlyInputValidator

class RecurlyExpirationMMYY @JvmOverloads constructor(
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
    private var binding: RecurlyExpirationMmyyBinding =
        RecurlyExpirationMmyyBinding.inflate(LayoutInflater.from(context), this)

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
        monthAndYearInputValidator()
    }

    /**
     * Sets the placeholder text
     * @param expirationDatePlaceholder Placeholder text for MM/YY field
     */
    fun setPlaceholder(expirationDatePlaceholder: String) {
        if (!expirationDatePlaceholder.trim().isEmpty())
            binding.recurlyTextInputLayoutIndividualExpirationMmyy.hint = expirationDatePlaceholder
    }

    /**
     * Sets the placeholder color
     * @param color the color as an Int, for example ContextCompat.getColor(context, R.color.your-color)
     */
    fun setPlaceholderColor(color: Int) {
        if (RecurlyInputValidator.validateColor(color)) {
            hintColor = color
            var colorState: ColorStateList = RecurlyInputValidator.getColorState(color)
            binding.recurlyTextInputLayoutIndividualExpirationMmyy.placeholderTextColor = colorState
        }
    }

    /**
     * Sets the text color
     * @param color the color as an Int, for example ContextCompat.getColor(context, R.color.your-color)
     */
    fun setTextColor(color: Int) {
        if (RecurlyInputValidator.validateColor(color)) {
            textColor = color
            binding.recurlyTextInputEditIndividualExpirationMmyy.setTextColor(color)
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
        binding.recurlyTextInputEditIndividualExpirationMmyy.setTypeface(newFont, style)
        binding.recurlyTextInputLayoutIndividualExpirationMmyy.typeface = newFont
    }

    /**
     * Validates the entered expiration date
     * @return true if the input is valid, false if it is not
     */
    fun validateData(): Boolean {
        val valid = RecurlyInputValidator.verifyDate(currentExpirationText())
        changeColors(valid)
        return valid
    }

    /**
     * Marks the expiration date field with an error highlight. Use it for server tokenization errors or custom error states
     */
    fun setExpirationError() {
        changeColors(false)
    }

    /**
     * Returns the currently validated expiration month entered into this view. Internal
     * visibility: consumed by [com.recurly.androidsdk.data.model.tokenization.RecurlyCardParams.from]
     * to build a tokenization snapshot without exposing raw card data publicly.
     */
    internal fun getExpirationMonth(): Int =
        RecurlyDataFormatter.getExpirationMonth(
            currentExpirationText(), RecurlyInputValidator.verifyDate(currentExpirationText())
        )

    /**
     * Returns the currently validated expiration year entered into this view. Internal
     * visibility: consumed by [com.recurly.androidsdk.data.model.tokenization.RecurlyCardParams.from]
     * to build a tokenization snapshot without exposing raw card data publicly.
     */
    internal fun getExpirationYear(): Int =
        RecurlyDataFormatter.getExpirationYear(
            currentExpirationText(), RecurlyInputValidator.verifyDate(currentExpirationText())
        )


    /** Clears the entered data and the error highlight. */
    fun clearData() {
        binding.recurlyTextInputEditIndividualExpirationMmyy.setText("")
        changeColors()
    }

    private fun currentExpirationText(): String =
        binding.recurlyTextInputEditIndividualExpirationMmyy.text.toString()

    private fun lenientExpiration(): Boolean {
        val text = currentExpirationText()
        return RecurlyInputValidator.verifyDate(text) || text.isEmpty()
    }

    /**
     * Sets the text color and the field highlight according to the current validity
     */
    private fun changeColors(ok: Boolean = lenientExpiration()) {
        if (ok) {
            binding.recurlyTextInputLayoutIndividualExpirationMmyy.error = null
            binding.recurlyTextInputEditIndividualExpirationMmyy.setTextColor(
                textColor
            )
        } else {
            binding.recurlyTextInputLayoutIndividualExpirationMmyy.error = " "
            binding.recurlyTextInputEditIndividualExpirationMmyy.setTextColor(
                errorTextColor
            )
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
    private fun monthAndYearInputValidator() {
        binding.recurlyTextInputEditIndividualExpirationMmyy.addTextChangedListener(object :
            TextWatcher {

            private var previousDateValue = ""

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                if (s != null)
                    previousDateValue = s.toString()
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (s != null) {
                    if (s.toString().isNotEmpty()) {
                        val oldValue = s.toString()
                        val data =
                            RecurlyInputValidator.validateExpirationDate(
                                RecurlyInputValidator.regexSpecialCharacters(
                                    s.toString(), "0-9/"
                                ),
                                previousDateValue
                            )
                        binding.recurlyTextInputEditIndividualExpirationMmyy.removeTextChangedListener(
                            this
                        )
                        s.replace(0, oldValue.length, data.second)
                        binding.recurlyTextInputEditIndividualExpirationMmyy.addTextChangedListener(
                            this
                        )
                        changeColors(data.first)
                    } else {
                        changeColors()
                    }
                }
            }
        })

        binding.recurlyTextInputEditIndividualExpirationMmyy.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                changeColors()
            }
        }

    }
}