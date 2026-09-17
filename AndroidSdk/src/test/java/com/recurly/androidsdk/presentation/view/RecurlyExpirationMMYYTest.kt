package com.recurly.androidsdk.presentation.view

import android.app.Activity
import android.view.ContextThemeWrapper
import android.widget.EditText
import android.widget.FrameLayout
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
import java.util.Calendar

@RunWith(RobolectricTestRunner::class)
class RecurlyExpirationMMYYTest {

    private val themedContext by lazy {
        ContextThemeWrapper(RuntimeEnvironment.getApplication(), R.style.AppTheme)
    }

    private fun expirationEditText(expirationView: RecurlyExpirationMMYY): TextInputEditText =
        expirationView.findViewById(R.id.recurly_text_input_edit_individual_expiration_mmyy)

    private fun expirationInputLayout(expirationView: RecurlyExpirationMMYY): TextInputLayout =
        expirationView.findViewById(R.id.recurly_text_input_layout_individual_expiration_mmyy)

    @Test
    fun clearData_afterEnteringExpiration_resetsTextAndValidation() {
        val futureTwoDigitYear = (Calendar.getInstance().get(Calendar.YEAR) % 100) + 1
        val expirationView = RecurlyExpirationMMYY(themedContext)
        expirationEditText(expirationView).setText("12/$futureTwoDigitYear")
        assertThat(expirationView.getExpirationMonth()).isEqualTo(12)
        assertThat(expirationView.getExpirationYear()).isEqualTo(futureTwoDigitYear)

        expirationView.clearData()

        assertThat(expirationEditText(expirationView).text.toString()).isEmpty()
        assertThat(expirationView.getExpirationMonth()).isEqualTo(0)
        assertThat(expirationView.getExpirationYear()).isEqualTo(0)
        assertThat(expirationView.validateData()).isFalse()
    }

    @Test
    fun clearData_afterSettingError_clearsErrorHighlight() {
        val expirationView = RecurlyExpirationMMYY(themedContext)
        expirationView.setExpirationError()
        assertThat(expirationInputLayout(expirationView).error).isNotNull()

        expirationView.clearData()

        assertThat(expirationInputLayout(expirationView).error).isNull()
    }

    @Test
    fun clearData_doesNotStealFocus() {
        val activity = Robolectric.buildActivity(Activity::class.java).setup().get()
        val container = FrameLayout(activity)
        val otherFocusable = EditText(activity)
        val expirationView = RecurlyExpirationMMYY(themedContext)
        container.addView(otherFocusable)
        container.addView(expirationView)
        activity.setContentView(container)

        otherFocusable.requestFocus()
        val futureTwoDigitYear = (Calendar.getInstance().get(Calendar.YEAR) % 100) + 1
        expirationEditText(expirationView).setText("12/$futureTwoDigitYear")

        expirationView.clearData()

        assertThat(otherFocusable.isFocused).isTrue()
        assertThat(expirationEditText(expirationView).isFocused).isFalse()
    }

    @Test
    fun clearData_calledTwice_isIdempotent() {
        val expirationView = RecurlyExpirationMMYY(themedContext)

        expirationView.clearData()
        expirationView.clearData()

        assertThat(expirationEditText(expirationView).text.toString()).isEmpty()
        assertThat(expirationView.getExpirationMonth()).isEqualTo(0)
        assertThat(expirationView.getExpirationYear()).isEqualTo(0)
        assertThat(expirationView.validateData()).isFalse()
    }

    @Test
    fun clearData_onUntouchedView_isNoOp() {
        val expirationView = RecurlyExpirationMMYY(themedContext)

        expirationView.clearData()

        assertThat(expirationEditText(expirationView).text.toString()).isEmpty()
    }

@Test
    fun setExpirationError_errorHighlightPersistsThroughFocusChange() {
        val activity = Robolectric.buildActivity(Activity::class.java).setup().get()
        val container = FrameLayout(activity)
        val otherFocusable = EditText(activity)
        val expirationView = RecurlyExpirationMMYY(themedContext)
        container.addView(otherFocusable)
        container.addView(expirationView)
        activity.setContentView(container)

        val futureTwoDigitYear = (Calendar.getInstance().get(Calendar.YEAR) % 100) + 1
        expirationEditText(expirationView).setText("12/$futureTwoDigitYear")
        expirationEditText(expirationView).requestFocus()
        assertThat(expirationEditText(expirationView).isFocused).isTrue()
        expirationView.setExpirationError()
        val errorColor = ContextCompat.getColor(themedContext, R.color.recurly_error_red)
        assertThat(expirationInputLayout(expirationView).error).isNotNull()
        assertThat(expirationEditText(expirationView).currentTextColor).isEqualTo(errorColor)

        otherFocusable.requestFocus()
        assertThat(expirationEditText(expirationView).isFocused).isFalse()

        assertThat(expirationInputLayout(expirationView).error).isNotNull()
        assertThat(expirationEditText(expirationView).currentTextColor).isEqualTo(errorColor)
    }

    @Test
    fun validateData_afterSetExpirationError_retiresErrorHighlight() {
        val activity = Robolectric.buildActivity(Activity::class.java).setup().get()
        val container = FrameLayout(activity)
        val otherFocusable = EditText(activity)
        val expirationView = RecurlyExpirationMMYY(themedContext)
        container.addView(otherFocusable)
        container.addView(expirationView)
        activity.setContentView(container)

        val futureTwoDigitYear = (Calendar.getInstance().get(Calendar.YEAR) % 100) + 1
        expirationEditText(expirationView).setText("12/$futureTwoDigitYear")
        expirationEditText(expirationView).requestFocus()
        assertThat(expirationEditText(expirationView).isFocused).isTrue()
        expirationView.setExpirationError()
        assertThat(expirationInputLayout(expirationView).error).isNotNull()

        assertThat(expirationView.validateData()).isTrue()
        assertThat(expirationInputLayout(expirationView).error).isNull()

        otherFocusable.requestFocus()
        assertThat(expirationEditText(expirationView).isFocused).isFalse()

        assertThat(expirationInputLayout(expirationView).error).isNull()
    }

    @Test
    fun setExpirationError_errorHighlightClearsWhenTextChanges() {
        val expirationView = RecurlyExpirationMMYY(themedContext)
        expirationView.setExpirationError()
        assertThat(expirationInputLayout(expirationView).error).isNotNull()

        val futureTwoDigitYear = (Calendar.getInstance().get(Calendar.YEAR) % 100) + 1
        expirationEditText(expirationView).setText("12/$futureTwoDigitYear")

        assertThat(expirationInputLayout(expirationView).error).isNull()
    }

@Test
    fun setExpirationError_errorHighlightClearsWhenTextClearedToEmpty() {
        val expirationView = RecurlyExpirationMMYY(themedContext)
        expirationView.setExpirationError()
        assertThat(expirationInputLayout(expirationView).error).isNotNull()

        expirationEditText(expirationView).setText("")

        assertThat(expirationInputLayout(expirationView).error).isNull()
    }
}
