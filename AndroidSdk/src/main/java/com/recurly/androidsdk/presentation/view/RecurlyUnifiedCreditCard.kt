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
import com.recurly.androidsdk.data.model.CreditCardData
import com.recurly.androidsdk.data.model.CreditCardsParameters
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

    private var cardType: String = ""
    private var correctCardInput = true
    private var correctExpirationInput = true
    private var correctCVVInput = true
    private var maxCVVLength: Int = 3

    private var previousDateValue = ""

    private var binding: RecurlyUnifiedCreditCardBinding =
        RecurlyUnifiedCreditCardBinding.inflate(LayoutInflater.from(context), this)

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
        cardNumberInputValidator()
        monthAndYearInputValidator()
        cvvInputValidator()
    }

    /**
     * This fun changes the placeholders texts according to the parameters received
     * @param creditCardNumber Placeholder text for Credit Card Number field
     * @param monthAndYear Placeholder text for MM/YY field
     * @param cvv Placeholder text for CVV field
     */
    fun setPlaceholders(creditCardNumber: String, monthAndYear: String, cvv: String) {
        if (creditCardNumber.trim().isNotEmpty())
            binding.recurlyTextEditCardNumber.hint = creditCardNumber
        if (monthAndYear.trim().isNotEmpty())
            binding.recurlyTextEditCardExpiration.hint = creditCardNumber
        if (cvv.trim().isNotEmpty())
            binding.recurlyTextEditCardCvv.hint = creditCardNumber
    }


    /**
     * This fun changes the placeholder color according to the parameter received
     * @param color you should sent the color like ContextCompat.getColor(context, R.color.your-color)
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
     * This fun changes the text color according to the parameter received
     * @param color you should sent the color like ContextCompat.getColor(context, R.color.your-color)
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
     * This fun changes the error text color according to the parameter received
     * @param color you should sent the color like ContextCompat.getColor(context, R.color.your-color)
     */
    fun setTextErrorColor(color: Int) {
        if (RecurlyInputValidator.validateColor(color))
            errorTextColor = color
    }

    /**
     * This fun changes the font of the input fields according to the parameter received
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
     * This fun validates if all the input are complete and are valid, this means it follows a credit card
     * patter and respect the min and max digits value, it follows a MM/YY
     * valid date pattern, and it follows a cvv code digits length, if there are any errors the fields
     * will be highlighted red
     * @return Triple<Boolean = number validation,Boolean = expiration validation,Boolean = cvv validation>true if the inputs are correctly filled, false if they are not
     */
    fun validateData(): Triple<Boolean, Boolean, Boolean> {
        correctCardInput = RecurlyInputValidator.verifyCardNumber(
            binding.recurlyTextEditCardNumber.text.toString(),
            cardType
        )
        correctExpirationInput =
            RecurlyInputValidator.verifyDate(binding.recurlyTextEditCardExpiration.text.toString())
        correctCVVInput = RecurlyInputValidator.verifyCVV(
            binding.recurlyTextEditCardCvv.text.toString(),
            cardType
        )
        validateAndChangeColors(false)
        return Triple(correctCardInput, correctExpirationInput, correctCVVInput)
    }

    /**
     * This fun will highlight the Credit Card Number as it have an error, you can use this
     * for tokenization validations or if you need to highlight this field with an error
     */
    fun setCreditCardNumberError() {
        correctCardInput = false
        validateAndChangeColors(false)
    }

    /**
     * This fun will highlight the CVV as it have an error, you can use this
     * for tokenization validations or if you need to highlight this field with an error
     */
    fun setCvvError() {
        correctCVVInput = false
        validateAndChangeColors(false)
    }

    /**
     * This fun will highlight the Expiration Date MM/YY as it have an error, you can use this
     * for tokenization validations or if you need to highlight this field with an error
     */
    fun setExpirationError() {
        correctExpirationInput = false
        validateAndChangeColors(false)
    }

    /**
     *This is an internal fun that validates the input as it is introduced for credit car number field,
     * it is separated in two parts:
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
        binding.recurlyTextEditCardNumber.addTextChangedListener(object : TextWatcher {

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
                        // We validate if the return info is not empty and if it is different from what
                        // we send to the input validator so this way we make secure if the text is
                        // a valid and has changed
                        if (oldValue != data.third) {
                            binding.recurlyTextEditCardNumber.removeTextChangedListener(this)
                            s.replace(0, oldValue.length, data.third)
                            binding.recurlyTextEditCardNumber.addTextChangedListener(this)
                            if (cardType.isNotEmpty())
                                maxCVVLength =
                                    CreditCardsParameters.valueOf(cardType.uppercase()).cvvLength
                        }
                        CreditCardData.setCardNumber(
                            RecurlyDataFormatter.getCardNumber(
                                s.toString(), correctCardInput
                            )
                        )
                        validateAndChangeColors(true)
                    } else {
                        cardType = ""
                        maxCVVLength = 3
                        correctCardInput = true
                    }
                    changeCardIcon()
                }
            }
        })

        binding.recurlyTextEditCardNumber.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {//We only save the value of the field if it is valid and complete
                correctCardInput = RecurlyInputValidator.verifyCardNumber(
                    binding.recurlyTextEditCardNumber.text.toString(), cardType
                ) || binding.recurlyTextEditCardNumber.text.toString().isEmpty()
                CreditCardData.setCardNumber(
                    RecurlyDataFormatter.getCardNumber(
                        binding.recurlyTextEditCardNumber.text.toString(), correctCardInput
                    )
                )
            }
            validateAndChangeColors(hasFocus)
        }
    }

    /**
     * This is an internal fun that validates the input as it is introduced for the expiration date field,
     * it is separated in two parts:
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
        binding.recurlyTextEditCardExpiration.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // we save the previous input to validate if it has changed and if it is correct
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
                    CreditCardData.setExpirationMonth(
                        RecurlyDataFormatter.getExpirationMonth(
                            s.toString(), data.first
                        )
                    )
                    CreditCardData.setExpirationYear(
                        RecurlyDataFormatter.getExpirationYear(
                            s.toString(), data.first
                        )
                    )
                    correctExpirationInput = data.first || s.toString().isEmpty()
                    validateAndChangeColors(true)
                }
            }
        })

        binding.recurlyTextEditCardExpiration.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                // We save the data from month and year separately
                correctExpirationInput = RecurlyInputValidator.verifyDate(
                    binding.recurlyTextEditCardExpiration.text.toString()
                ) || binding.recurlyTextEditCardExpiration.text.toString().isEmpty()
                CreditCardData.setExpirationMonth(
                    RecurlyDataFormatter.getExpirationMonth(
                        binding.recurlyTextEditCardExpiration.text.toString(),
                        correctExpirationInput
                    )
                )
                CreditCardData.setExpirationYear(
                    RecurlyDataFormatter.getExpirationYear(
                        binding.recurlyTextEditCardExpiration.text.toString(),
                        correctExpirationInput
                    )
                )
            }
            validateAndChangeColors(hasFocus)
        }

    }

    /**
     * This is an internal fun that validates the input as it is introduced in CVV code field,
     * it is separated in two parts:
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
        binding.recurlyTextEditCardCvv.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //According to the card type we change the max length of the cvv code
                if (cardType.isEmpty())
                    binding.recurlyTextEditCardCvv.filters =
                        arrayOf<InputFilter>(LengthFilter(maxCVVLength))
                else
                    binding.recurlyTextEditCardCvv.filters =
                        arrayOf<InputFilter>(
                            LengthFilter(
                                CreditCardsParameters.valueOf(cardType.uppercase()).cvvLength
                            )
                        )

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (s != null) {
                    val oldValue = s.toString()
                    correctCVVInput = true
                    val formattedCVV = RecurlyInputValidator.regexSpecialCharacters(
                        s.toString(), "0-9"
                    )
                    binding.recurlyTextEditCardCvv.removeTextChangedListener(this)
                    s.replace(0, oldValue.length, formattedCVV)
                    binding.recurlyTextEditCardCvv.addTextChangedListener(this)
                    validateAndChangeColors(true)
                    correctCVVInput =
                        formattedCVV.length == maxCVVLength
                                || formattedCVV.isEmpty()
                    CreditCardData.setCvvCode(
                        RecurlyDataFormatter.getCvvCode(
                            formattedCVV, correctCVVInput
                        )
                    )
                }
            }
        })

        binding.recurlyTextEditCardCvv.setOnFocusChangeListener { v, hasFocus ->
            //Changes the cvv icon according to the card type
            if (hasFocus) {
                if (cardType == CreditCardsParameters.AMERICAN_EXPRESS.cardType)
                    binding.recurlyImageUnifiedCardIcon.setImageDrawable(
                        ContextCompat.getDrawable(context, R.drawable.ic_amex_cvv)
                    )
                else
                    binding.recurlyImageUnifiedCardIcon.setImageDrawable(
                        ContextCompat.getDrawable(context, R.drawable.ic_generic_cvv)
                    )
            } else {
                correctCVVInput = RecurlyInputValidator.verifyCVV(
                    binding.recurlyTextEditCardCvv.text.toString(), cardType
                ) || binding.recurlyTextEditCardCvv.text.toString().isEmpty()
                CreditCardData.setCvvCode(
                    RecurlyDataFormatter.getCvvCode(
                        binding.recurlyTextEditCardCvv.text.toString(), correctCVVInput
                    )
                )
                changeCardIcon()
            }
            validateAndChangeColors(hasFocus)
        }
    }

    /**
     * This fun changes the colors of the text input field or the background according to if there
     * are some kind of error or not
     */
    private fun validateAndChangeColors(focused: Boolean) {

        if (!correctCardInput || !correctExpirationInput || !correctCVVInput) {
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

        if (correctCardInput)
            binding.recurlyTextEditCardNumber.setTextColor(textColor)
        else
            binding.recurlyTextEditCardNumber.setTextColor(errorTextColor)

        if (correctExpirationInput)
            binding.recurlyTextEditCardExpiration.setTextColor(textColor)
        else
            binding.recurlyTextEditCardExpiration.setTextColor(errorTextColor)

        if (correctCVVInput)
            binding.recurlyTextEditCardCvv.setTextColor(textColor)
        else
            binding.recurlyTextEditCardCvv.setTextColor(errorTextColor)
    }

    /**
     * This fun get as a parameter the card type from CreditCardData to change the credit card icon
     */
    private fun changeCardIcon() {
        binding.recurlyImageUnifiedCardIcon.setImageDrawable(
            RecurlyDataFormatter.changeCardIcon(context, cardType)
        )
    }
}