package com.recurly.androidsdk.presentation.view

import android.app.Activity
import android.view.ContextThemeWrapper
import android.widget.EditText
import android.widget.FrameLayout
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

@RunWith(RobolectricTestRunner::class)
class RecurlyCreditCardNumberTest {

    private val themedContext by lazy {
        ContextThemeWrapper(RuntimeEnvironment.getApplication(), R.style.AppTheme)
    }

    private fun numberEditText(numberView: RecurlyCreditCardNumber): TextInputEditText =
        numberView.findViewById(R.id.recurly_text_input_edit_individual_card_number)

    private fun numberInputLayout(numberView: RecurlyCreditCardNumber): TextInputLayout =
        numberView.findViewById(R.id.recurly_text_input_layout_individual_card_number)

    @Test
    fun clearData_afterEnteringCardNumber_resetsTextAndValidation() {
        val numberView = RecurlyCreditCardNumber(themedContext)
        numberEditText(numberView).setText("4111111111111111")
        assertThat(numberView.getCardNumber()).isEqualTo("4111111111111111")

        numberView.clearData()

        assertThat(numberEditText(numberView).text.toString()).isEmpty()
        assertThat(numberView.getCardNumber()).isEmpty()
        assertThat(numberView.validateData()).isFalse()
    }

    @Test
    fun clearData_afterSettingError_clearsErrorHighlight() {
        val numberView = RecurlyCreditCardNumber(themedContext)
        numberView.setCreditCardNumberError()
        assertThat(numberInputLayout(numberView).error).isNotNull()

        numberView.clearData()

        assertThat(numberInputLayout(numberView).error).isNull()
    }

    @Test
    fun clearData_resetsBrandIconToGeneric() {
        val numberView = RecurlyCreditCardNumber(themedContext)
        numberEditText(numberView).setText("4111111111111111")

        numberView.clearData()

        val icon = numberInputLayout(numberView).startIconDrawable
        assertThat(shadowOf(icon).createdFromResId).isEqualTo(R.drawable.ic_generic_valid_card)
    }


    @Test
    fun watcher_numberClearedToEmpty_resetsBrandIcon() {
        val numberView = RecurlyCreditCardNumber(themedContext)
        numberEditText(numberView).setText("4111111111111111")

        numberEditText(numberView).setText("")

        val icon = numberInputLayout(numberView).startIconDrawable
        assertThat(shadowOf(icon).createdFromResId).isEqualTo(R.drawable.ic_generic_valid_card)
    }

    @Test
    fun watcher_numberClearedToEmpty_resetsCachedCardNumber() {
        val numberView = RecurlyCreditCardNumber(themedContext)
        numberEditText(numberView).setText("4111111111111111")
        assertThat(numberView.getCardNumber()).isEqualTo("4111111111111111")

        numberEditText(numberView).setText("")

        assertThat(numberView.getCardNumber()).isEmpty()
    }

    @Test
    fun setPlaceholder_appliesHintToInputLayout() {
        val numberView = RecurlyCreditCardNumber(themedContext)
        numberView.setPlaceholder("Card Number Hint")

        assertThat(numberInputLayout(numberView).hint.toString()).isEqualTo("Card Number Hint")
    }

    @Test
    fun clearData_doesNotStealFocus() {
        val activity = Robolectric.buildActivity(Activity::class.java).setup().get()
        val container = FrameLayout(activity)
        val otherFocusable = EditText(activity)
        val numberView = RecurlyCreditCardNumber(themedContext)
        container.addView(otherFocusable)
        container.addView(numberView)
        activity.setContentView(container)

        otherFocusable.requestFocus()
        numberEditText(numberView).setText("4111111111111111")

        numberView.clearData()

        assertThat(otherFocusable.isFocused).isTrue()
        assertThat(numberEditText(numberView).isFocused).isFalse()
    }

    @Test
    fun clearData_calledTwice_isIdempotent() {
        val numberView = RecurlyCreditCardNumber(themedContext)

        numberView.clearData()
        numberView.clearData()

        assertThat(numberEditText(numberView).text.toString()).isEmpty()
        assertThat(numberView.getCardNumber()).isEmpty()
        assertThat(numberView.validateData()).isFalse()
    }
}
