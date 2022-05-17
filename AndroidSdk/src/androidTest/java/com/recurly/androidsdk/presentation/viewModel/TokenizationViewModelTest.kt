package com.recurly.androidsdk.presentation.viewModel

import android.content.Context
import androidx.lifecycle.Observer
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.recurly.androidsdk.data.model.CreditCardData
import com.recurly.androidsdk.data.model.RecurlySessionData
import junit.framework.TestCase
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class TokenizationViewModelTest: TestCase(){

    private lateinit var viewModel: TokenizationViewModel
    private lateinit var context: Context

    @Before
    public override fun setUp() {
        super.setUp()
        context = ApplicationProvider.getApplicationContext<Context>()
        viewModel = TokenizationViewModelFactory().createTokenizationViewModel()

        //Setup Credit Card and session data

        RecurlySessionData.setPublicKey("ewr1-4TIXlPCkR68woNJp7UYMSL")
        CreditCardData.setCardNumber(4111111111111111)
        CreditCardData.setCvvCode(123)
        CreditCardData.setExpirationYear(22)
        CreditCardData.setExpirationMonth(8)
    }

    @Test
    fun tokenizationTest(){
//        viewModel.initTokenization(
//            "Hugo",
//            "Flores",
//            "CH2Solutions",
//            "at my home",
//            "really at my home",
//            "La Paz",
//            "La Paz",
//            "00000",
//            "Bolivia"
//        )
//
//        assertThat(viewModel.mutableTokenization.getOrAwaitValue() != null).isEqualTo(true)
    }
}