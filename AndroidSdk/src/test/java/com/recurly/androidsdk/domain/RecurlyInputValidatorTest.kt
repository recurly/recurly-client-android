package com.recurly.androidsdk.domain

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class RecurlyInputValidatorTest{

    // This type of test needs Junit 5
//    @ParameterizedTest
//    @MethodSource("validateCreditCard")
//    fun validateCardNumberWithParameters(creditCardNumber: String, expectedResponse:Boolean){
//        val verifyData = RecurlyInputValidator.validateCreditCardNumber(creditCardNumber)
//        val result = RecurlyInputValidator.verifyCardNumber(verifyData.third,verifyData.second)
//        assertThat(result && verifyData.first).isEqualTo(expectedResponse)
//    }
//
//    companion object{
//        @JvmStatic
//        fun validateCreditCard(): List<Arguments>{
//            return listOf(
//                Arguments.of("4111 1111 1111 1111",    true),
//                Arguments.of("1111 1111 1111 1111",    false)
//            )
//        }
//    }

    @Test
    fun validCreditCardInput(){
        val verifyData = RecurlyInputValidator.validateCreditCardNumber("4111111111111111")
        val result = RecurlyInputValidator.verifyCardNumber(verifyData.third,verifyData.second)
        assertThat(result && verifyData.first).isTrue()
    }

    @Test
    fun invalidCreditCardInput(){
        val verifyData = RecurlyInputValidator.validateCreditCardNumber("1111111111111111")
        val result = RecurlyInputValidator.verifyCardNumber(verifyData.third,verifyData.second)
        assertThat(result).isFalse()
    }

    @Test
    fun validExpirationDateInput(){
        val result = RecurlyInputValidator.verifyDate("08/22")
        assertThat(result).isTrue()
    }

    @Test
    fun invalidExpirationDateInput(){
        val result = RecurlyInputValidator.verifyDate("8/2")
        assertThat(result).isFalse()
    }

    @Test
    fun validCvvInput(){
        val verifyData = RecurlyInputValidator.validateCreditCardNumber("4111111111111111")
        val result = RecurlyInputValidator.verifyCVV("123",verifyData.second)
        assertThat(result && verifyData.first).isTrue()
    }

    @Test
    fun invalidCvvInput(){
        val verifyData = RecurlyInputValidator.validateCreditCardNumber("1111111111111111")
        val result = RecurlyInputValidator.verifyCardNumber("123",verifyData.second)
        assertThat(result).isFalse()
    }

    @Test
    fun creditCardFollowsPattern(){
        val result = RecurlyInputValidator.validateCreditCardNumber("4111111111111111")
        assertThat(result).isEqualTo(Triple(true,"visa","4111 1111 1111 1111"))
    }

    @Test
    fun creditCardDonNotFollowsPattern(){
        val result = RecurlyInputValidator.validateCreditCardNumber("1111111111111111")
        assertThat(result).isEqualTo(Triple(false,"",""))
    }

    @Test
    fun formatCreditCardToPattern(){
        val result = RecurlyInputValidator.separateStringOnPattern("4-4-4-4","4111111111111111")
        assertThat(result).matches("4111 1111 1111 1111")
    }

}