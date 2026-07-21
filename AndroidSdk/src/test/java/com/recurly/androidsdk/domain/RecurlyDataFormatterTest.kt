package com.recurly.androidsdk.domain

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class RecurlyDataFormatterTest {

    // getCardNumber

    @Test
    fun getCardNumber_validInput_returnsStrippedNumber() {
        val result = RecurlyDataFormatter.getCardNumber("4111 1111 1111 1111", true)
        assertThat(result).isEqualTo("4111111111111111")
    }

    @Test
    fun getCardNumber_invalidInput_returnsEmpty() {
        val result = RecurlyDataFormatter.getCardNumber("4111 1111 1111 1111", false)
        assertThat(result).isEmpty()
    }

    @Test
    fun getCardNumber_emptyString_returnsEmpty() {
        val result = RecurlyDataFormatter.getCardNumber("", true)
        assertThat(result).isEmpty()
    }

    @Test
    fun getCardNumber_leadingZeroPreserved() {
        val result = RecurlyDataFormatter.getCardNumber("0123456789012345", true)
        assertThat(result).isEqualTo("0123456789012345")
    }

    @Test
    fun getCardNumber_nonNumericInput_returnsEmpty() {
        val result = RecurlyDataFormatter.getCardNumber("4111abcd11111111", true)
        assertThat(result).isEmpty()
    }

    // getCvvCode

    @Test
    fun getCvvCode_validInput_returnsString() {
        val result = RecurlyDataFormatter.getCvvCode("123", true)
        assertThat(result).isEqualTo("123")
    }

    @Test
    fun getCvvCode_leadingZeroPreserved() {
        val result = RecurlyDataFormatter.getCvvCode("012", true)
        assertThat(result).isEqualTo("012")
    }

    @Test
    fun getCvvCode_invalidInput_returnsEmpty() {
        val result = RecurlyDataFormatter.getCvvCode("123", false)
        assertThat(result).isEmpty()
    }

    @Test
    fun getCvvCode_nonNumericInput_returnsEmpty() {
        val result = RecurlyDataFormatter.getCvvCode("12a", true)
        assertThat(result).isEmpty()
    }

    @Test
    fun getCvvCode_emptyString_returnsEmpty() {
        val result = RecurlyDataFormatter.getCvvCode("", true)
        assertThat(result).isEmpty()
    }
}
