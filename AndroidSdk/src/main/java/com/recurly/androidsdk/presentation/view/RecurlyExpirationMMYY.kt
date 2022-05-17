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
import com.recurly.androidsdk.data.model.CreditCardData
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
    private var correctExpirationInput = true

    private var previousDateValue = ""

    private var binding: RecurlyExpirationMmyyBinding =
        RecurlyExpirationMmyyBinding.inflate(LayoutInflater.from(context), this)

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
        monthAndYearInputValidator()
    }

    /**
     * This fun changes the placeholder text according to the parameter received
     * @param expirationDatePlaceholder Placeholder text for MM/YY field
     */
    fun setPlaceholder(expirationDatePlaceholder: String) {
        if (!expirationDatePlaceholder.trim().isEmpty())
            binding.recurlyTextInputLayoutIndividualExpirationMmyy.hint = expirationDatePlaceholder
    }

    /**
     * This fun changes the placeholder color according to the parameter received
     * @param color you should sent the color like ContextCompat.getColor(context, R.color.your-color)
     */
    fun setPlaceholderColor(color: Int) {
        if (RecurlyInputValidator.validateColor(color)) {
            hintColor = color
            var colorState: ColorStateList = RecurlyInputValidator.getColorState(color)
            binding.recurlyTextInputLayoutIndividualExpirationMmyy.placeholderTextColor = colorState
        }
    }

    /**
     * This fun changes the text color according to the parameter received
     * @param color you should sent the color like ContextCompat.getColor(context, R.color.your-color)
     */
    fun setTextColor(color: Int) {
        if (RecurlyInputValidator.validateColor(color)) {
            textColor = color
            binding.recurlyTextInputEditIndividualExpirationMmyy.setTextColor(color)
        }
    }

    /**
     * This fun changes the error text color according to the parameter received
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
        binding.recurlyTextInputEditIndividualExpirationMmyy.setTypeface(newFont, style)
        binding.recurlyTextInputLayoutIndividualExpirationMmyy.typeface = newFont
    }

    /**
     * This fun validates if the input is complete and is valid, this means it follows a MM/YY
     * valid date pattern
     * @return true if the input is correctly filled, false if it is not
     */
    fun validateData(): Boolean {
        correctExpirationInput =
            RecurlyInputValidator.verifyDate(binding.recurlyTextInputEditIndividualExpirationMmyy.text.toString())
        changeColors()
        return correctExpirationInput
    }

    /**
     * This fun will highlight the Expiration Date MM/YY as it have an error, you can use this
     * for tokenization validations or if you need to highlight this field with an error
     */
    fun setExpirationError() {
        correctExpirationInput = false
        changeColors()
    }

    /**
     * This fun changes the text color and the field highlight according at if it is correct or not
     */
    private fun changeColors() {
        if (correctExpirationInput) {
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
    private fun monthAndYearInputValidator() {
        binding.recurlyTextInputEditIndividualExpirationMmyy.addTextChangedListener(object :
            TextWatcher {

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
                        correctExpirationInput = data.first
                        binding.recurlyTextInputEditIndividualExpirationMmyy.removeTextChangedListener(
                            this
                        )
                        s.replace(0, oldValue.length, data.second)
                        binding.recurlyTextInputEditIndividualExpirationMmyy.addTextChangedListener(
                            this
                        )
                        changeColors()
                        CreditCardData.setExpirationMonth(
                            RecurlyDataFormatter.getExpirationMonth(
                                s.toString(), correctExpirationInput
                            )
                        )
                        CreditCardData.setExpirationYear(
                            RecurlyDataFormatter.getExpirationYear(
                                s.toString(), correctExpirationInput
                            )
                        )
                    } else {
                        correctExpirationInput = true
                        changeColors()
                    }
                }
            }
        })

        binding.recurlyTextInputEditIndividualExpirationMmyy.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                correctExpirationInput = RecurlyInputValidator.verifyDate(
                    binding.recurlyTextInputEditIndividualExpirationMmyy.text.toString()
                ) || binding.recurlyTextInputEditIndividualExpirationMmyy.text.toString().isEmpty()
                CreditCardData.setExpirationMonth(
                    RecurlyDataFormatter.getExpirationMonth(
                        binding.recurlyTextInputEditIndividualExpirationMmyy.text.toString(),
                        correctExpirationInput
                    )
                )
                CreditCardData.setExpirationYear(
                    RecurlyDataFormatter.getExpirationYear(
                        binding.recurlyTextInputEditIndividualExpirationMmyy.text.toString(),
                        correctExpirationInput
                    )
                )
                changeColors()
            }
        }

    }
}