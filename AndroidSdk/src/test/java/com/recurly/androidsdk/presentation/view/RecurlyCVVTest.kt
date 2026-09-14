package com.recurly.androidsdk.presentation.view

import android.app.Activity
import android.view.ContextThemeWrapper
import android.widget.EditText
import android.widget.FrameLayout
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.common.truth.Truth.assertThat
import com.recurly.androidsdk.R
import com.recurly.androidsdk.data.model.RecurlyCardMetadata
import com.recurly.androidsdk.data.model.tokenization.RecurlyCardParams
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import java.util.Calendar

/**
 * Regression coverage for the Amex (4-digit) CVV [RecurlyCVV.validateData] bug fixed in v3.1.1:
 * `validateData()` compared the entered CVV length against a hardcoded `maxCVVLength = 3`
 * instead of [RecurlyCardMetadata.getCvvLength], so a valid 4-digit Amex CVV entered into the
 * loose/individual [RecurlyCVV] view always failed validation.
 *
 * Also backfills v3.1.0 coverage for [RecurlyCardParams.from], which reads entered data directly
 * from the loose input view instances rather than a shared singleton.
 */
@RunWith(RobolectricTestRunner::class)
class RecurlyCVVTest {

    private val themedContext by lazy {
        ContextThemeWrapper(RuntimeEnvironment.getApplication(), R.style.AppTheme)
    }

    @Before
    fun setUp() {
        RecurlyCardMetadata.setCvvLength(3)
    }

    @After
    fun tearDown() {
        RecurlyCardMetadata.setCvvLength(3)
    }

    private fun cvvEditText(cvvView: RecurlyCVV): TextInputEditText =
        cvvView.findViewById(R.id.recurly_text_input_edit_individual_cvv_code)


    private fun cvvInputLayout(cvvView: RecurlyCVV): TextInputLayout =
        cvvView.findViewById(R.id.recurly_text_input_layout_individual_cvv_code)

    @Test
    fun validateData_threeDigitCvv_defaultLength_returnsTrue() {
        val cvvView = RecurlyCVV(themedContext)
        cvvEditText(cvvView).setText("123")

        assertThat(cvvView.validateData()).isTrue()
    }

    @Test
    fun validateData_fourDigitAmexCvv_metadataLengthFour_returnsTrue() {
        RecurlyCardMetadata.setCvvLength(4)
        val cvvView = RecurlyCVV(themedContext)
        cvvEditText(cvvView).setText("1234")

        assertThat(cvvView.validateData()).isTrue()
    }

    @Test
    fun validateData_incompleteCvv_defaultThreeDigitLength_returnsFalse() {
        val cvvView = RecurlyCVV(themedContext)
        cvvEditText(cvvView).setText("12")

        assertThat(cvvView.validateData()).isFalse()
    }

    @Test
    fun from_backfillsCardParams_fromLooseInputViews() {
        val futureTwoDigitYear = (Calendar.getInstance().get(Calendar.YEAR) % 100) + 1

        val numberView = RecurlyCreditCardNumber(themedContext)
        val expirationView = RecurlyExpirationMMYY(themedContext)
        val cvvView = RecurlyCVV(themedContext)

        numberView.findViewById<TextInputEditText>(R.id.recurly_text_input_edit_individual_card_number)
            .setText("4111111111111111")
        expirationView.findViewById<TextInputEditText>(R.id.recurly_text_input_edit_individual_expiration_mmyy)
            .setText("12/$futureTwoDigitYear")
        cvvEditText(cvvView).setText("123")

        val params = RecurlyCardParams.from(numberView, expirationView, cvvView)

        assertThat(params.cardNumber).isEqualTo("4111111111111111")
        assertThat(params.expirationMonth).isEqualTo(12)
        assertThat(params.expirationYear).isEqualTo(futureTwoDigitYear)
        assertThat(params.cvvCode).isEqualTo("123")
    }


    @Test
    fun clearData_afterEnteringCvv_resetsTextAndValidation() {
        val cvvView = RecurlyCVV(themedContext)
        cvvEditText(cvvView).setText("123")
        assertThat(cvvView.getCvvCode()).isEqualTo("123")

        cvvView.clearData()

        assertThat(cvvEditText(cvvView).text.toString()).isEmpty()
        assertThat(cvvView.getCvvCode()).isEmpty()
        assertThat(cvvView.validateData()).isFalse()
    }

    @Test
    fun clearData_onUntouchedView_isNoOp() {
        val cvvView = RecurlyCVV(themedContext)

        cvvView.clearData()

        assertThat(cvvEditText(cvvView).text.toString()).isEmpty()
    }


    @Test
    fun clearData_afterSettingError_clearsErrorHighlight() {
        val cvvView = RecurlyCVV(themedContext)
        cvvView.setCvvError()
        assertThat(cvvInputLayout(cvvView).error).isNotNull()

        cvvView.clearData()

        assertThat(cvvInputLayout(cvvView).error).isNull()
    }


    @Test
    fun watcher_cvvClearedToEmpty_clearsErrorHighlight() {
        val cvvView = RecurlyCVV(themedContext)
        cvvView.setCvvError()
        assertThat(cvvInputLayout(cvvView).error).isNotNull()

        cvvEditText(cvvView).setText("")

        assertThat(cvvInputLayout(cvvView).error).isNull()
    }

    @Test
    fun clearData_doesNotStealFocus() {
        val activity = Robolectric.buildActivity(Activity::class.java).setup().get()
        val container = FrameLayout(activity)
        val otherFocusable = EditText(activity)
        val cvvView = RecurlyCVV(themedContext)
        container.addView(otherFocusable)
        container.addView(cvvView)
        activity.setContentView(container)

        otherFocusable.requestFocus()
        cvvEditText(cvvView).setText("123")

        cvvView.clearData()

        assertThat(otherFocusable.isFocused).isTrue()
        assertThat(cvvEditText(cvvView).isFocused).isFalse()
    }

    @Test
    fun clearData_calledTwice_isIdempotent() {
        val cvvView = RecurlyCVV(themedContext)

        cvvView.clearData()
        cvvView.clearData()

        assertThat(cvvEditText(cvvView).text.toString()).isEmpty()
        assertThat(cvvView.getCvvCode()).isEmpty()
        assertThat(cvvView.validateData()).isFalse()
    }
}
