package com.recurly.androidsdk.presentation.view

import android.app.Activity
import android.view.ContextThemeWrapper
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.common.truth.Truth.assertThat
import com.recurly.androidsdk.R
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf
import java.util.Calendar

@RunWith(RobolectricTestRunner::class)
class RecurlyUnifiedCreditCardTest {

    private val themedContext by lazy {
        ContextThemeWrapper(RuntimeEnvironment.getApplication(), R.style.AppTheme)
    }

    private fun numberEditText(view: RecurlyUnifiedCreditCard): TextInputEditText =
        view.findViewById(R.id.recurly_text_edit_card_number)

    private fun expirationEditText(view: RecurlyUnifiedCreditCard): TextInputEditText =
        view.findViewById(R.id.recurly_text_edit_card_expiration)

    private fun cvvEditText(view: RecurlyUnifiedCreditCard): TextInputEditText =
        view.findViewById(R.id.recurly_text_edit_card_cvv)

    private fun numberInputLayout(view: RecurlyUnifiedCreditCard): TextInputLayout =
        view.findViewById(R.id.recurly_text_input_card_number)

    private fun expirationInputLayout(view: RecurlyUnifiedCreditCard): TextInputLayout =
        view.findViewById(R.id.recurly_text_input_card_expiration)

    private fun cvvInputLayout(view: RecurlyUnifiedCreditCard): TextInputLayout =
        view.findViewById(R.id.recurly_text_input_card_cvv)

    private fun cardIcon(view: RecurlyUnifiedCreditCard): ImageView =
        view.findViewById(R.id.recurly_image_unified_card_icon)

    @Test
    fun clearData_afterEnteringFullCard_resetsAllFieldsAndValidation() {
        val futureTwoDigitYear = (Calendar.getInstance().get(Calendar.YEAR) % 100) + 1
        val view = RecurlyUnifiedCreditCard(themedContext)

        numberEditText(view).setText("4111111111111111")
        expirationEditText(view).setText("12/$futureTwoDigitYear")
        cvvEditText(view).setText("123")

        view.clearData()

        assertThat(numberEditText(view).text.toString()).isEmpty()
        assertThat(expirationEditText(view).text.toString()).isEmpty()
        assertThat(cvvEditText(view).text.toString()).isEmpty()

        val params = view.cardParams()
        assertThat(params.cardNumber).isEmpty()
        assertThat(params.expirationMonth).isEqualTo(0)
        assertThat(params.expirationYear).isEqualTo(0)
        assertThat(params.cvvCode).isEmpty()

        val (numberValid, expirationValid, cvvValid) = view.validateData()
        assertThat(numberValid).isFalse()
        assertThat(expirationValid).isFalse()
        assertThat(cvvValid).isFalse()
    }


    @Test
    fun unifiedWatcher_expirationClearedToEmpty_zeroesCachedExpiry() {
        val futureTwoDigitYear = (Calendar.getInstance().get(Calendar.YEAR) % 100) + 1
        val view = RecurlyUnifiedCreditCard(themedContext)
        expirationEditText(view).setText("12/$futureTwoDigitYear")
        assertThat(view.cardParams().expirationMonth).isEqualTo(12)
        assertThat(view.cardParams().expirationYear).isEqualTo(futureTwoDigitYear)

        expirationEditText(view).setText("")

        assertThat(view.cardParams().expirationMonth).isEqualTo(0)
        assertThat(view.cardParams().expirationYear).isEqualTo(0)
    }

    @Test
    fun unifiedWatcher_cvvClearedToEmpty_clearsCachedCvv() {
        val view = RecurlyUnifiedCreditCard(themedContext)
        cvvEditText(view).setText("123")
        assertThat(view.cardParams().cvvCode).isEqualTo("123")

        cvvEditText(view).setText("")

        assertThat(view.cardParams().cvvCode).isEmpty()
    }

    @Test
    fun unifiedWatcher_numberClearedToEmpty_resetsCardNumberAndRepaints() {
        val view = RecurlyUnifiedCreditCard(themedContext)
        numberEditText(view).setText("1111111111111111")
        val stroke = view.findViewById<ImageView>(R.id.recurly_image_view_stroke_background)
        assertThat(shadowOf(stroke.drawable).createdFromResId)
            .isEqualTo(R.drawable.unified_stroke_error)

        numberEditText(view).setText("")

        assertThat(view.cardParams().cardNumber).isEmpty()
        assertThat(shadowOf(stroke.drawable).createdFromResId)
            .isEqualTo(R.drawable.unified_stroke_focused)
        assertThat(numberEditText(view).currentTextColor)
            .isEqualTo(ContextCompat.getColor(themedContext, R.color.recurly_black))
    }

    @Test
    fun unifiedWatcher_validNumberClearedToEmpty_clearsCachedCardNumber() {
        val view = RecurlyUnifiedCreditCard(themedContext)
        numberEditText(view).setText("4111111111111111")
        assertThat(view.cardParams().cardNumber).isEqualTo("4111111111111111")

        numberEditText(view).setText("")

        assertThat(view.cardParams().cardNumber).isEmpty()
    }
    @Test
    fun unifiedCvv_fourDigitsWithVisa_isValidAndCached() {
        val view = RecurlyUnifiedCreditCard(themedContext)
        numberEditText(view).setText("4111111111111111")
        cvvEditText(view).setText("1234")

        val (_, _, cvvValid) = view.validateData()

        assertThat(cvvValid).isTrue()
        assertThat(view.cardParams().cvvCode).isEqualTo("1234")
    }

    @Test
    fun unifiedCvv_threeDigitsWithAmex_isValidAndCached() {
        val view = RecurlyUnifiedCreditCard(themedContext)
        numberEditText(view).setText("378282246310005")
        cvvEditText(view).setText("123")

        // Pins the 3-or-4 rule: a CVV is valid with 3 or 4 digits for every brand.
        assertThat(view.cardParams().cvvCode).isEqualTo("123")
    }

    @Test
    fun unifiedCvv_twoDigits_isInvalidAndNotCached() {
        val view = RecurlyUnifiedCreditCard(themedContext)
        numberEditText(view).setText("4111111111111111")
        cvvEditText(view).setText("12")

        val (_, _, cvvValid) = view.validateData()

        assertThat(cvvValid).isFalse()
        assertThat(view.cardParams().cvvCode).isEmpty()
    }

    @Test
    fun setPlaceholders_appliesDistinctHintsPerField() {
        val view = RecurlyUnifiedCreditCard(themedContext)
        view.setPlaceholders("NumberHint", "ExpiryHint", "CvvHint")

        assertThat(numberInputLayout(view).hint.toString()).isEqualTo("NumberHint")
        assertThat(expirationInputLayout(view).hint.toString()).isEqualTo("ExpiryHint")
        assertThat(cvvInputLayout(view).hint.toString()).isEqualTo("CvvHint")
    }

    @Test
    fun setPlaceholders_blankParams_keepDefaultHints() {
        val view = RecurlyUnifiedCreditCard(themedContext)
        view.setPlaceholders("", " ", "")

        assertThat(numberInputLayout(view).hint.toString())
            .isEqualTo(themedContext.getString(R.string.hint_card_number))
        assertThat(expirationInputLayout(view).hint.toString())
            .isEqualTo(themedContext.getString(R.string.hint_month_and_year))
        assertThat(cvvInputLayout(view).hint.toString())
            .isEqualTo(themedContext.getString(R.string.hint_cvv_code))
    }

@Test
    fun setPlaceholders_blankExpirationAmongCustomParams_keepsDefaultForThatFieldOnly() {
        val view = RecurlyUnifiedCreditCard(themedContext)
        view.setPlaceholders("NumberHint", "", "CvvHint")

        assertThat(numberInputLayout(view).hint.toString()).isEqualTo("NumberHint")
        assertThat(expirationInputLayout(view).hint.toString())
            .isEqualTo(themedContext.getString(R.string.hint_month_and_year))
        assertThat(cvvInputLayout(view).hint.toString()).isEqualTo("CvvHint")
    }

    @Test
    fun unifiedWatcher_invalidPartialCvv_showsErrorStrokeImmediately() {
        val view = RecurlyUnifiedCreditCard(themedContext)
        cvvEditText(view).setText("12")

        val stroke = view.findViewById<ImageView>(R.id.recurly_image_view_stroke_background)
        assertThat(shadowOf(stroke.drawable).createdFromResId)
            .isEqualTo(R.drawable.unified_stroke_error)
    }

@Test
    fun unifiedWatcher_partialCvvThenValidCvv_clearsErrorStroke() {
        val view = RecurlyUnifiedCreditCard(themedContext)
        cvvEditText(view).setText("12")
        cvvEditText(view).setText("1234")

        val stroke = view.findViewById<ImageView>(R.id.recurly_image_view_stroke_background)
        assertThat(shadowOf(stroke.drawable).createdFromResId)
            .isEqualTo(R.drawable.unified_stroke_focused)
        assertThat(cvvEditText(view).currentTextColor)
            .isEqualTo(ContextCompat.getColor(themedContext, R.color.recurly_black))
    }

    @Test
    fun clearData_afterSettingErrors_clearsErrorHighlights() {
        val view = RecurlyUnifiedCreditCard(themedContext)
        view.setCreditCardNumberError()
        view.setExpirationError()
        view.setCvvError()

        val errorColor = ContextCompat.getColor(themedContext, R.color.recurly_error_red)
        assertThat(numberEditText(view).currentTextColor).isEqualTo(errorColor)
        assertThat(expirationEditText(view).currentTextColor).isEqualTo(errorColor)
        assertThat(cvvEditText(view).currentTextColor).isEqualTo(errorColor)

        view.clearData()

        val normalColor = ContextCompat.getColor(themedContext, R.color.recurly_black)
        assertThat(numberEditText(view).currentTextColor).isEqualTo(normalColor)
        assertThat(expirationEditText(view).currentTextColor).isEqualTo(normalColor)
        assertThat(cvvEditText(view).currentTextColor).isEqualTo(normalColor)

        val stroke = view.findViewById<ImageView>(R.id.recurly_image_view_stroke_background)
        assertThat(shadowOf(stroke.drawable).createdFromResId)
            .isEqualTo(R.drawable.unified_stroke_container)
    }

    @Test
    fun clearData_resetsBrandIconToGeneric() {
        val view = RecurlyUnifiedCreditCard(themedContext)
        numberEditText(view).setText("4111111111111111")

        view.clearData()

        assertThat(shadowOf(cardIcon(view).drawable).createdFromResId)
            .isEqualTo(R.drawable.ic_generic_valid_card)
    }

    @Test
    fun clearData_whileCvvFocused_showsCvvIcon() {
        val activity = Robolectric.buildActivity(Activity::class.java).setup().get()
        val container = FrameLayout(activity)
        val view = RecurlyUnifiedCreditCard(themedContext)
        container.addView(view)
        activity.setContentView(container)

        numberEditText(view).setText("4111111111111111")
        cvvEditText(view).requestFocus()

        view.clearData()

        assertThat(cvvEditText(view).isFocused).isTrue()
        assertThat(shadowOf(cardIcon(view).drawable).createdFromResId)
            .isEqualTo(R.drawable.ic_generic_cvv)
    }

    @Test
    fun cvvFocus_amexBrand_showsAmexCvvIcon() {
        val activity = Robolectric.buildActivity(Activity::class.java).setup().get()
        val container = FrameLayout(activity)
        val view = RecurlyUnifiedCreditCard(themedContext)
        container.addView(view)
        activity.setContentView(container)

        numberEditText(view).setText("378282246310005")
        cvvEditText(view).requestFocus()

        assertThat(shadowOf(cardIcon(view).drawable).createdFromResId)
            .isEqualTo(R.drawable.ic_amex_cvv)
    }
    @Test
    fun clearData_doesNotStealFocus() {
        val activity = Robolectric.buildActivity(Activity::class.java).setup().get()
        val container = FrameLayout(activity)
        val otherFocusable = EditText(activity)
        val view = RecurlyUnifiedCreditCard(themedContext)
        container.addView(otherFocusable)
        container.addView(view)
        activity.setContentView(container)

        otherFocusable.requestFocus()
        numberEditText(view).setText("4111111111111111")

        view.clearData()

        assertThat(otherFocusable.isFocused).isTrue()
        assertThat(numberEditText(view).isFocused).isFalse()
        assertThat(expirationEditText(view).isFocused).isFalse()
        assertThat(cvvEditText(view).isFocused).isFalse()
    }

    @Test
    fun clearData_calledTwice_isIdempotent() {
        val view = RecurlyUnifiedCreditCard(themedContext)

        view.clearData()
        view.clearData()

        assertThat(numberEditText(view).text.toString()).isEmpty()
        val params = view.cardParams()
        assertThat(params.cardNumber).isEmpty()
        assertThat(params.cvvCode).isEmpty()
    }

    @Test
    fun clearData_onUntouchedView_isNoOp() {
        val view = RecurlyUnifiedCreditCard(themedContext)

        view.clearData()

        assertThat(numberEditText(view).text.toString()).isEmpty()
        assertThat(expirationEditText(view).text.toString()).isEmpty()
        assertThat(cvvEditText(view).text.toString()).isEmpty()
    }
}
