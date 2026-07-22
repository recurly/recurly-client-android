package com.recurly.androidsdk.presentation.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.google.android.gms.wallet.button.ButtonConstants
import com.google.android.gms.wallet.button.ButtonOptions
import com.google.android.gms.wallet.button.PayButton
import com.recurly.androidsdk.data.model.googlepay.RecurlyGooglePayMethod
import com.recurly.androidsdk.domain.GooglePayRequestBuilder

/**
 * A Google Pay button following Google's branding guidelines, ready to be wired to
 * [com.recurly.androidsdk.RecurlyClient.googlePay]/[RecurlyGooglePayHandler].
 *
 * Wraps Google's [PayButton] via composition (it is a `final` class, so it cannot be extended)
 * to keep this class's public surface free of the internal Google Pay wire-format models.
 *
 * Call [configure] with the [RecurlyGooglePayMethod] fetched via
 * [RecurlyGooglePayHandler.getPaymentMethod] before the button becomes visible/clickable.
 */
class RecurlyGooglePayButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val payButton = PayButton(context, attrs, defStyleAttr)

    init {
        addView(payButton, LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT))
    }

    /**
     * @param method the server-driven Google Pay configuration used to render the button's
     * allowed payment methods (never hardcoded)
     * @param theme [ButtonConstants.ButtonTheme.DARK] or [ButtonConstants.ButtonTheme.LIGHT]
     * @param buttonType the button label, e.g. [ButtonConstants.ButtonType.PAY]
     * @param cornerRadius the button's corner radius in pixels
     */
    fun configure(
        method: RecurlyGooglePayMethod,
        theme: Int = ButtonConstants.ButtonTheme.DARK,
        buttonType: Int = ButtonConstants.ButtonType.PAY,
        cornerRadius: Int = 100
    ) {
        payButton.initialize(
            ButtonOptions.newBuilder()
                .setButtonTheme(theme)
                .setButtonType(buttonType)
                .setCornerRadius(cornerRadius)
                .setAllowedPaymentMethods(GooglePayRequestBuilder.buildAllowedPaymentMethodsJson(method))
                .build()
        )
    }

    /**
     * Forwards the click listener to the underlying Google [PayButton] so its native
     * touch/ripple feedback still renders correctly.
     */
    override fun setOnClickListener(listener: View.OnClickListener?) {
        payButton.setOnClickListener(listener)
    }
}
