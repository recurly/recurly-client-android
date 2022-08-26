package com.recurly.androidsdk.presentation.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.recurly.androidsdk.data.model.CreditCardData
import com.recurly.androidsdk.data.model.RecurlySessionData
import com.recurly.androidsdk.data.model.tokenization.RecurlyBillingInfo
import com.recurly.androidsdk.data.model.tokenization.TokenizationRequest
import com.recurly.androidsdk.data.model.tokenization.TokenizationResponse
import com.recurly.androidsdk.domain.GetRecurlyToken
import kotlinx.coroutines.launch
import java.util.*

internal class TokenizationViewModel constructor(
    var getRecurlyToken: GetRecurlyToken
) : ViewModel() {

    /**
     * You must observe the changes on this val to obtain the tokenization response
     */
    val mutableTokenization = MutableLiveData<TokenizationResponse>()

    /**
     * When calling the tokenization it is not necessary to give the credit card values
     * because they are managed from behind. also the mandatory parameters are the first name
     * and the lastname inside the billingInfo. for the rest of the parameters you can check out our documentation at
     * https://developers.recurly.com/reference/recurly-js/index.html
     *
     * @param billingInfo This is the object with all the personal Billing information
     *
     */
    fun initCreditCardTokenization(billingInfo: RecurlyBillingInfo) {
        val request = TokenizationRequest(
            firstName = billingInfo.firstName,
            lastName = billingInfo.lastName,
            company = billingInfo.company,
            addressOne = billingInfo.addressOne,
            addressTwo = billingInfo.addressTwo,
            city = billingInfo.city,
            state = billingInfo.state,
            postalCode = billingInfo.postalCode,
            country = billingInfo.country,
            phone = billingInfo.phone,
            vatNumber = billingInfo.vatNumber,
            taxIdentifier = billingInfo.taxIdentifier,
            taxIdentifierType = billingInfo.taxIdentifierType,
            /**
             * the credit card field are obtained directly from the input data
             */
            cardNumber = CreditCardData.getCardNumber(),
            expirationMonth = CreditCardData.getExpirationMonth(),
            expirationYear = CreditCardData.getExpirationYear(),
            cvvCode = CreditCardData.getCvvCode(),

            /**
             * the session data is obtained directly from the persisted values
             */
            sdkVersion = RecurlySessionData.versionName,
            publicKey = RecurlySessionData.getPublicKey(),
            deviceId = RecurlySessionData.deviceId,
            sessionId = UUID.randomUUID().toString()
        )
        viewModelScope.launch {
            val result: TokenizationResponse = getRecurlyToken(request)
            mutableTokenization.postValue(result)
        }
    }
}