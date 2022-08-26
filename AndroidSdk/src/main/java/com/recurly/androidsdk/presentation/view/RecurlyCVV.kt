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
import com.recurly.androidsdk.data.model.CreditCardData
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
    private var correctCVVInput = true

    private val maxCVVLength: Int = 3

    private var binding: RecurlyCvvCodeBinding =
        RecurlyCvvCodeBinding.inflate(LayoutInflater.from(context), this)

    /**
     * All the color are initialized as Int, this is to make ir easier to handle the texts colors change
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
     * This fun changes the placeholder text according to the parameter received
     * @param cvvPlaceholder Placeholder text for cvv code field
     */
    fun setPlaceholder(cvvPlaceholder: String) {
        if (!cvvPlaceholder.trim().isEmpty())
            binding.recurlyTextInputLayoutIndividualCvvCode.hint = cvvPlaceholder
    }

    /**
     * This fun changes the placeholder color according to the parameter received
     * @param color you should sent the color like ContextCompat.getColor(context, R.color.your-color)
     */
    fun setPlaceholderColor(color: Int) {
        if (RecurlyInputValidator.validateColor(color)) {
            hintColor = color
            var colorState: ColorStateList = RecurlyInputValidator.getColorState(color)
            binding.recurlyTextInputLayoutIndividualCvvCode.placeholderTextColor = colorState
        }
    }

    /**
     * This fun changes the text color according to the parameter received
     * @param color you should sent the color like ContextCompat.getColor(context, R.color.your-color)
     */
    fun setTextColor(color: Int) {
        if (RecurlyInputValidator.validateColor(color)) {
            textColor = color
            binding.recurlyTextInputEditIndividualCvvCode.setTextColor(color)
        }
    }

    /**
     * This fun changes the text color according to the parameter received
     * @param color you should sent the color like ContextCompat.getColor(context, R.color.your-color)
     */
    fun setTextErrorColor(color: Int) {
        if (RecurlyInputValidator.validateColor(color))
            errorTextColor = color
    }

    /**
     * This fun changes the font of the input field according to the parameter received
     * @param newFont non null Typeface
     * @param style style as int
     */
    fun setFont(newFont: Typeface, style: Int) {
        binding.recurlyTextInputEditIndividualCvvCode.setTypeface(newFont, style)
        binding.recurlyTextInputLayoutIndividualCvvCode.typeface = newFont
    }

    /**
     * This fun validates if the input is complete and is valid, this means it follows a cvv code digits length
     * @return true if the input is correctly filled, false if it is not
     */
    fun validateData(): Boolean {
        correctCVVInput =
            binding.recurlyTextInputEditIndividualCvvCode.text.toString().length == maxCVVLength
        changeColors()
        return correctCVVInput
    }

    /**
     * This fun will highlight the CVV as it have an error, you can use this
     * for tokenization validations or if you need to highlight this field with an error
     */
    fun setCvvError(){
        correctCVVInput = false
        changeColors()
    }

    /**
     * This fun changes the text color and the field highlight according at if it is correct or not
     */
    private fun changeColors() {
        if (correctCVVInput) {
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
     *This is an internal fun that validates the input as it is introduced, it is separated in two parts:
     * If it is focused or not, and if it has text changes.
     *
     * When it has text changes calls to different input validators from RecurlyInputValidator
     * and according to the response of the input validator it replaces the text and
     * changes text color according if has errors or not
     *
     * When it changes the focus of the view it validates if the field is correctly filled, and then
     * saves the input data
     */
    private fun cvvInputValidator() {
        binding.recurlyTextInputEditIndividualCvvCode.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                binding.recurlyTextInputEditIndividualCvvCode.filters =
                    arrayOf<InputFilter>(InputFilter.LengthFilter(CreditCardData.getCvvLength()))
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
                        correctCVVInput = formattedCVV.length == CreditCardData.getCvvLength()
                        CreditCardData.setCvvCode(
                            RecurlyDataFormatter.getCvvCode(
                                formattedCVV, correctCVVInput
                            )
                        )
                    } else {
                        correctCVVInput = true
                        changeColors()
                    }
                }
            }
        })

        binding.recurlyTextInputEditIndividualCvvCode.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                CreditCardData.setCvvCode(
                    RecurlyDataFormatter.getCvvCode(
                        binding.recurlyTextInputEditIndividualCvvCode.text.toString(),
                        correctCVVInput
                    )
                )
                changeColors()
            } else {
                binding.recurlyTextInputEditIndividualCvvCode.filters =
                    arrayOf<InputFilter>(InputFilter.LengthFilter(CreditCardData.getCvvLength()))
            }
        }
    }
}