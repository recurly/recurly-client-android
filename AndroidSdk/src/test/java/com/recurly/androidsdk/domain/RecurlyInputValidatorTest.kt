package com.recurly.androidsdk.domain

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.util.Calendar

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
        val futureYear = (Calendar.getInstance().get(Calendar.YEAR) % 100) + 1
        val result = RecurlyInputValidator.verifyDate("12/$futureYear")
        assertThat(result).isTrue()
    }

    @Test
    fun invalidExpirationDateInput(){
        val result = RecurlyInputValidator.verifyDate("8/2")
        assertThat(result).isFalse()
    }

    @Test
    fun validCvvInput(){
        val result = RecurlyInputValidator.verifyCVV("123")
        assertThat(result).isTrue()
    }

    @Test
    fun invalidCardNumberInput(){
        val verifyData = RecurlyInputValidator.validateCreditCardNumber("1111111111111111")
        val result = RecurlyInputValidator.verifyCardNumber("123",verifyData.second)
        assertThat(result).isFalse()
    }

    @Test
    fun validFourDigitCvvInput(){
        val result = RecurlyInputValidator.verifyCVV("1234")
        assertThat(result).isTrue()
    }

    @Test
    fun shortCvvInput(){
        val result = RecurlyInputValidator.verifyCVV("12")
        assertThat(result).isFalse()
    }

    @Test
    fun longCvvInput(){
        val result = RecurlyInputValidator.verifyCVV("12345")
        assertThat(result).isFalse()
    }

    @Test
    fun emptyCvvInput(){
        val result = RecurlyInputValidator.verifyCVV("")
        assertThat(result).isFalse()
    }

    @Test
    fun creditCardFollowsPattern(){
        val result = RecurlyInputValidator.validateCreditCardNumber("4111111111111111")
        assertThat(result).isEqualTo(Triple(true,"visa","4111 1111 1111 1111"))
    }

    @Test
    fun creditCardDonNotFollowsPattern(){
        // Unknown brands still format, matching Stripe and Braintree.
        val result = RecurlyInputValidator.validateCreditCardNumber("1111111111111111")
        assertThat(result).isEqualTo(Triple(false,"","1111 1111 1111 1111"))
    }

    @Test
    fun mastercard2SeriesIsDetected(){
        // Live since 2016 (canon range 2221-2720), unmatched by the old ^5[1-5] regex
        val result = RecurlyInputValidator.validateCreditCardNumber("2221000000000009")
        assertThat(result.second).isEqualTo("master")
    }

    @Test
    fun eloRange451416ResolvesToEloNotVisa(){
        // recurly-js canon explicitly carves 451416 out of visa's 400000-499999 span for elo
        val result = RecurlyInputValidator.validateCreditCardNumber("4514160000000000")
        assertThat(result.second).isEqualTo("elo")
    }

    @Test
    fun hipercardMatchesAllFourCanonicalLengths(){
        assertThat(RecurlyInputValidator.validateCreditCardNumber("6062820000000000").second)
            .isEqualTo("hipercard") // 16
        assertThat(RecurlyInputValidator.validateCreditCardNumber("60628200000000000").second)
            .isEqualTo("hipercard") // 17
        assertThat(RecurlyInputValidator.validateCreditCardNumber("606282000000000000").second)
            .isEqualTo("hipercard") // 18
        assertThat(RecurlyInputValidator.validateCreditCardNumber("6062820000000000000").second)
            .isEqualTo("hipercard") // 19, only reachable via the first BIN group
    }

    @Test
    fun unionPayRange8100IsDetected(){
        // canon 8100-8171 is missing from the old ^6[2] regex entirely
        val result = RecurlyInputValidator.validateCreditCardNumber("8100000000000000")
        assertThat(result.second).isEqualTo("union_pay")
    }

    @Test
    fun discoverStartsAt6440(){
        val result = RecurlyInputValidator.validateCreditCardNumber("6440000000000000")
        assertThat(result.second).isEqualTo("discover")
    }

    @Test
    fun maestroIsDetectedEndToEndOnACompleteNumber(){
        // Partial detection excludes maestro, so this needs the complete-length match.
        val result = RecurlyInputValidator.validateCreditCardNumber("6759649826438453")
        assertThat(result.second).isEqualTo("maestro")
        assertThat(RecurlyInputValidator.verifyCardNumber("6759649826438453", "maestro")).isTrue()
    }

    @Test
    fun typingAMaestroRangePrefixStaysUnknownWhileIncomplete(){
        val result = RecurlyInputValidator.validateCreditCardNumber("67")
        assertThat(result.second).isEqualTo("")
    }

    @Test
    fun sixZeroZeroZeroRangeStillResolvesInsteadOfBecomingUnenterable(){
        // Canon moves 6000-6010 to maestro, which only the complete-length match reaches.
        val result = RecurlyInputValidator.validateCreditCardNumber("6000000000000000")
        assertThat(result.second).isEqualTo("maestro")
    }

    @Test
    fun luhnInvalidNumberFailsVerificationDespiteMatchingBrand(){
        val verifyData = RecurlyInputValidator.validateCreditCardNumber("4111111111111112")
        assertThat(verifyData.second).isEqualTo("visa")
        assertThat(RecurlyInputValidator.verifyCardNumber("4111111111111112", "visa")).isFalse()
    }

    @Test
    fun nineteenDigitNumberIsFormattedFourFourFourSeven(){
        // gaps are {4,8,12}: no gap at 16, so the trailing 7 digits of a 19-digit PAN stay together.
        val result = RecurlyInputValidator.validateCreditCardNumber("6440000000000000000")
        assertThat(result.third).isEqualTo("6440 0000 0000 0000000")
    }

    @Test
    fun amexAndDinersUseTheFourTenGap(){
        // 4-6-5 and 4-6-4 respectively, both from Braintree's {4,10} gap set
        val amex = RecurlyInputValidator.validateCreditCardNumber("371449635398431")
        assertThat(amex).isEqualTo(Triple(true, "american_express", "3714 496353 98431"))

        val diners = RecurlyInputValidator.validateCreditCardNumber("30569309025904")
        assertThat(diners).isEqualTo(Triple(true, "diners_club", "3056 930902 5904"))
    }

    @Test
    fun eloWinsTheDiscoverTieBreakAtSixFiveZeroZero(){
        val result = RecurlyInputValidator.validateCreditCardNumber("6500")
        assertThat(result.second).isEqualTo("elo")
    }

    @Test
    fun ambiguousPrefixResolvesToUnknownNotFirstMatch(){
        val result = RecurlyInputValidator.validateCreditCardNumber("5")
        assertThat(result.second).isEqualTo("")
    }

    @Test
    fun jcbRangeIsDetected(){
        val result = RecurlyInputValidator.validateCreditCardNumber("3528000000000007")
        assertThat(result.second).isEqualTo("jcb")
    }

    @Test
    fun tarjetaNaranjaRangeIsDetected(){
        val result = RecurlyInputValidator.validateCreditCardNumber("5895620000000000")
        assertThat(result.second).isEqualTo("tarjeta_naranja")
    }

    @Test
    fun visaRangeEndpointsAroundTheEloCarveOutAreDetected(){
        assertThat(RecurlyInputValidator.validateCreditCardNumber("4514150000000000").second)
            .isEqualTo("visa")
        assertThat(RecurlyInputValidator.validateCreditCardNumber("4514170000000000").second)
            .isEqualTo("visa")
    }

    @Test
    fun visaThirteenDigitLengthIsDetected(){
        val result = RecurlyInputValidator.validateCreditCardNumber("4222222222222")
        assertThat(result.second).isEqualTo("visa")
    }

    @Test
    fun maestroTwelveDigitLengthIsDetected(){
        val result = RecurlyInputValidator.validateCreditCardNumber("675964982643")
        assertThat(result.second).isEqualTo("maestro")
    }

    @Test
    fun truncatedDiscoverPrefixDoesNotFailOpenAsMaestro(){
        // A Luhn-valid 12-digit 6011 prefix is a complete maestro match, so it must not verify.
        val partial = RecurlyInputValidator.validateCreditCardNumber("601100000004")
        assertThat(partial.second).isEqualTo("discover")
        assertThat(RecurlyInputValidator.verifyCardNumber("601100000004", "discover")).isFalse()
    }

}