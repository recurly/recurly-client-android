package com.recurly.androidsdk.presentation.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.recurly.androidsdk.R
import com.recurly.androidsdk.data.model.CreditCardsParameters
import com.recurly.androidsdk.databinding.RecurlyCardBrandIconBinding
import com.recurly.androidsdk.domain.RecurlyDataFormatter

class RecurlyCardBrandIcon @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var binding: RecurlyCardBrandIconBinding =
        RecurlyCardBrandIconBinding.inflate(LayoutInflater.from(context), this)

    init {
        layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
    }

    /**
     * This fun get as a parameter the card type from CreditCardData to change the credit card icon
     * @param cardType credit card type according to CreditCardData
     */
    private fun setCardIcon(cardType: String) {
        binding.imageRecurlyCardBrand.setImageDrawable(
            RecurlyDataFormatter.changeCardIcon(context, cardType)
        )
    }

}