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
import com.recurly.androidsdk.data.model.CreditCardsParameters
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
    private var cardType = ""
    private var correctCardInput = true
    private var iconEnabled = true

    private var binding: RecurlyCreditCardNumberBinding =
        RecurlyCreditCardNumberBinding.inflate(LayoutInflater.from(context), this)

    /**
     * All the color are initialized as Int, to make it easier to handle the texts colors change
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
     * When you call this fun the credit card icon gets disabled and will not show
     */
    fun disableCardIcon() {
        iconEnabled = false
    }

    /**
     * This fun changes the placeholder text according to the parameter received
     * @param creditCardNumber Placeholder text for credit card number field
     */
    fun setPlaceholder(creditCardNumber: String) {
        if (!creditCardNumber.trim().isEmpty())
            binding.recurlyTextInputEditIndividualCardNumber.hint = creditCardNumber
    }

    /**
     * This fun changes the placeholder color according to the parameter received
     * @param color you should sent the color like ContextCompat.getColor(context, R.color.your-color)
     */
    fun setPlaceholderColor(color: Int) {
        if (RecurlyInputValidator.validateColor(color)) {
            hintColor = color
            val colorState: ColorStateList = RecurlyInputValidator.getColorState(color)
            binding.recurlyTextInputLayoutIndividualCardNumber.placeholderTextColor = colorState
        }
    }

    /**
     * This fun changes the text color according to the parameter received
     * @param color you should sent the color like ContextCompat.getColor(context, R.color.your-color)
     */
    fun setTextColor(color: Int) {
        if (RecurlyInputValidator.validateColor(color)) {
            textColor = color
            binding.recurlyTextInputEditIndividualCardNumber.setTextColor(color)
        }
    }

    /**
     * This fun changes the error text color according to the parameter received
     * @param color you should sent the color like ContextCompat.getColor(context, R.color.yor-color)
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
        binding.recurlyTextInputEditIndividualCardNumber.setTypeface(newFont, style)
        binding.recurlyTextInputLayoutIndividualCardNumber.typeface = newFont
    }

    /**
     * This fun validates if the input is complete and is valid, this means it follows a credit card
     * patter and respect the min and max digits value
     * @return true if the input is correctly filled, false if it is not
     */
    fun validateData(): Boolean {
        correctCardInput = RecurlyInputValidator.verifyCardNumber(
            binding.recurlyTextInputEditIndividualCardNumber.text.toString(),
            cardType
        )
        changeColors()
        return correctCardInput
    }

    /**
     * This fun will highlight the Credit Card Number as it have an error, you can use this
     * for tokenization validations or if you need to highlight this field with an error
     */
    fun setCreditCardNumberError() {
        correctCardInput = false
        changeColors()
    }

    /**
     * This fun changes the text color and the field highlight according at if it is correct or not
     */
    private fun changeColors() {
        if (correctCardInput) {
            binding.recurlyTextInputLayoutIndividualCardNumber.error = null
            binding.recurlyTextInputEditIndividualCardNumber.setTextColor(textColor)
        } else {
            binding.recurlyTextInputLayoutIndividualCardNumber.error = " "
            binding.recurlyTextInputEditIndividualCardNumber.setTextColor(errorTextColor)
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
                        correctCardInput = data.first
                        cardType = data.second
                        if (data.third.isNotEmpty() && oldValue != data.third) {
                            binding.recurlyTextInputEditIndividualCardNumber.removeTextChangedListener(
                                this
                            )
                            s.replace(0, oldValue.length, data.third)
                            binding.recurlyTextInputEditIndividualCardNumber.addTextChangedListener(
                                this
                            )
                        }
                        CreditCardData.setCardNumber(
                            RecurlyDataFormatter.getCardNumber(
                                s.toString(), correctCardInput
                            )
                        )
                        if (cardType.isNotEmpty())
                            CreditCardData.setCvvLength(
                                CreditCardsParameters.valueOf(cardType.uppercase()).cvvLength
                            )
                        else
                            CreditCardData.setCvvLength(3)
                    } else {
                        cardType = ""
                        CreditCardData.setCvvLength(3)
                        correctCardInput = true
                    }
                    changeCardIcon()
                    changeColors()
                }
            }
        })

        binding.recurlyTextInputEditIndividualCardNumber.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                correctCardInput = RecurlyInputValidator.verifyCardNumber(
                    binding.recurlyTextInputEditIndividualCardNumber.text.toString(), cardType
                ) || binding.recurlyTextInputEditIndividualCardNumber.text.toString().isEmpty()
                changeColors()
                CreditCardData.setCardNumber(
                    RecurlyDataFormatter.getCardNumber(
                        binding.recurlyTextInputEditIndividualCardNumber.text.toString(),
                        correctCardInput
                    )
                )
            }
        }
    }

    /**
     * This fun get as a parameter the card type from CreditCardData to change the credit card icon
     */
    private fun changeCardIcon() {
        binding.recurlyTextInputLayoutIndividualCardNumber.startIconDrawable =
            RecurlyDataFormatter.changeCardIcon(context, cardType)
    }
}