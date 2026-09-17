package com.recurly.androidsdk.presentation.view

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.recurly.androidsdk.R
import com.recurly.androidsdk.databinding.RecurlyCvvCodeBinding
import com.recurly.androidsdk.domain.RecurlyDataFormatter
import com.recurly.androidsdk.domain.RecurlyInputValidator

class RecurlyCVV @JvmOverloads constructor(
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

    // A server error comes from outside this view. Keep it until the text changes, a validateData call, or clearData.
    private var forcedError = false

    private var binding: RecurlyCvvCodeBinding =
        RecurlyCvvCodeBinding.inflate(LayoutInflater.from(context), this)

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
        cvvInputValidator()
    }

    /**
     * Sets the placeholder text
     * @param cvvPlaceholder Placeholder text for cvv code field
     */
    fun setPlaceholder(cvvPlaceholder: String) {
        if (!cvvPlaceholder.trim().isEmpty())
            binding.recurlyTextInputLayoutIndividualCvvCode.hint = cvvPlaceholder
    }

    /**
     * Sets the placeholder color
     * @param color the color as an Int, for example ContextCompat.getColor(context, R.color.your-color)
     */
    fun setPlaceholderColor(color: Int) {
        if (RecurlyInputValidator.validateColor(color)) {
            hintColor = color
            var colorState: ColorStateList = RecurlyInputValidator.getColorState(color)
            binding.recurlyTextInputLayoutIndividualCvvCode.placeholderTextColor = colorState
        }
    }

    /**
     * Sets the text color
     * @param color the color as an Int, for example ContextCompat.getColor(context, R.color.your-color)
     */
    fun setTextColor(color: Int) {
        if (RecurlyInputValidator.validateColor(color)) {
            textColor = color
            binding.recurlyTextInputEditIndividualCvvCode.setTextColor(color)
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
        binding.recurlyTextInputEditIndividualCvvCode.setTypeface(newFont, style)
        binding.recurlyTextInputLayoutIndividualCvvCode.typeface = newFont
    }

    /**
     * Validates the entered CVV code
     * @return true if the input is valid, false if it is not
     */
    fun validateData(): Boolean {
        forcedError = false
        val valid = RecurlyInputValidator.verifyCVV(currentCvvText())
        changeColors(valid)
        return valid
    }

    /**
     * Marks the CVV field with an error highlight. Use it for server tokenization errors or custom error states.
     * The highlight persists until the text changes, [validateData] runs, or [clearData] is called.
     */
    fun setCvvError(){
        forcedError = true
        changeColors(false)
    }

    /**
     * Returns the currently validated CVV entered into this view. Internal visibility:
     * consumed by [com.recurly.androidsdk.data.model.tokenization.RecurlyCardParams.from] to build
     * a tokenization snapshot without exposing raw card data publicly.
     */
    internal fun getCvvCode(): String {
        val text = currentCvvText()
        return RecurlyDataFormatter.getCvvCode(text, RecurlyInputValidator.verifyCVV(text))
    }


    /** Clears the entered data and the error highlight. */
    fun clearData() {
        forcedError = false
        binding.recurlyTextInputEditIndividualCvvCode.setText("")
        changeColors()
    }

    private fun currentCvvText(): String =
        binding.recurlyTextInputEditIndividualCvvCode.text.toString()

    private fun lenientCvv(): Boolean {
        val text = currentCvvText()
        return RecurlyInputValidator.verifyCVV(text) || text.isEmpty()
    }

    /**
     * Sets the text color and the field highlight according to the current validity
     */
    private fun changeColors(ok: Boolean = lenientCvv() && !forcedError) {
        if (ok) {
            binding.recurlyTextInputLayoutIndividualCvvCode.error = null
            binding.recurlyTextInputEditIndividualCvvCode.setTextColor(textColor)
        } else {
            binding.recurlyTextInputLayoutIndividualCvvCode.error = " "
            binding.recurlyTextInputEditIndividualCvvCode.setTextColor(
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
     * When the field loses focus it re-derives validity from the current text and repaints.
     * A [setCvvError] highlight survives repaints until the text changes or [clearData] is called.
     */
    private fun cvvInputValidator() {
        binding.recurlyTextInputEditIndividualCvvCode.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                forcedError = false
                // Cap input at 4 digits. Validation, not the filter, rejects bad lengths.
                binding.recurlyTextInputEditIndividualCvvCode.filters =
                    arrayOf<InputFilter>(InputFilter.LengthFilter(4))
                binding.recurlyTextInputLayoutIndividualCvvCode.error = null
                binding.recurlyTextInputEditIndividualCvvCode.setTextColor(textColor)
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (s != null) {
                    if (s.toString().isNotEmpty()) {
                        val oldValue = s.toString()
                        val formattedCVV = RecurlyInputValidator.regexSpecialCharacters(
                            s.toString(), "0-9"
                        )
                        binding.recurlyTextInputEditIndividualCvvCode.removeTextChangedListener(this)
                        s.replace(0, oldValue.length, formattedCVV)
                        binding.recurlyTextInputEditIndividualCvvCode.addTextChangedListener(this)
                    }
                    changeColors()
                }
            }
        })

        binding.recurlyTextInputEditIndividualCvvCode.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                changeColors()
            } else {
                binding.recurlyTextInputEditIndividualCvvCode.filters =
                    arrayOf<InputFilter>(InputFilter.LengthFilter(4))
            }
        }
    }
}