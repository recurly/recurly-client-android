package com.recurly.androidsdk.presentation.viewModel

import androidx.lifecycle.LifecycleOwner
import com.recurly.androidsdk.data.model.tokenization.ErrorRecurly
import com.recurly.androidsdk.data.model.tokenization.RecurlyBillingInfo
import com.recurly.androidsdk.data.network.core.RecurlyTokenizationHandler

object RecurlyApi {

    /**
     * This function allows you to make the credit card tokenization and simplifies the management of
     * a success request or an error request simply by managing the tokenizationHandler, the credit card info
     * fills automatically
     *
     * @param lifecycleOwner The activity or the fragment where you are calling this from
     * @param billingInfo The billing information previously filled
     * @param tokenizationHandler a RecurlyTokenizationHandler that allows you to override the onSuccess and onError functions
     */
    fun creditCardTokenization(
        lifecycleOwner: LifecycleOwner, billingInfo: RecurlyBillingInfo,
        tokenizationHandler: RecurlyTokenizationHandler
    ) {
        val viewModel = TokenizationViewModelFactory().createTokenizationViewModel()
        viewModel.initCreditCardTokenization(billingInfo)
        viewModel.mutableTokenization.observe(lifecycleOwner, androidx.lifecycle.Observer {
            if (it.token.isNullOrEmpty() || it.type.isNullOrEmpty())
                tokenizationHandler.onError(error = it.error)
            else
                tokenizationHandler.onSuccess(token = it.token, type = it.type)
        })
    }

    /**
     * This function will return a filled RecurlyBillingInfo that can be used on the credit card tokenization
     *
     * the mandatory params are the firstName and the lastName, for the rest of parameters and how to fill them you can check our documentation on https://developers.recurly.com/reference/recurly-js/index.html
     */
    fun buildCreditCardBillingInfo(
        firstName: String,
        lastName: String,
        company: String = "",
        addressOne: String = "",
        addressTwo: String = "",
        city: String = "",
        state: String = "",
        postalCode: String = "",
        country: String = "",
        phone: String = "",
        vatNumber: String = "",
        taxIdentifier: String = "",
        taxIdentifierType: String = ""
    ): RecurlyBillingInfo {
        return RecurlyBillingInfo(
            firstName,
            lastName,
            company,
            addressOne,
            addressTwo,
            city,
            state,
            postalCode,
            country,
            phone,
            vatNumber,
            taxIdentifier,
            taxIdentifierType
        )
    }

    /**
     * An interface that contains the onSuccess and onError functions
     */
    interface ResponseHandler {
        fun onSuccess(token: String, type: String)

        fun onError(error: ErrorRecurly)
    }
}