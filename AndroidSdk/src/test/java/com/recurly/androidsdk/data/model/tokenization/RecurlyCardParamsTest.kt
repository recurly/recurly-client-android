package com.recurly.androidsdk.data.model.tokenization

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class RecurlyCardParamsTest {

    @Test
    fun toString_doesNotExposeRawCardData() {
        // A data class conversion would auto-generate a toString() that dumps PAN and CVV.
        val params = RecurlyCardParams.testInstance("4111111111111111", 12, 27, "123")

        val dumped = params.toString()

        assertThat(dumped).doesNotContain("cardNumber=")
        assertThat(dumped).doesNotContain("cvvCode=")
        assertThat(dumped).doesNotContain("4111111111111111")
    }
}