package com.recurly.androidsdk.data.network.core

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class RetrofitHelperTest {

    @Test
    fun getRetrofit_euPublicKey_routesToEuBaseUrl() {
        val retrofit = RetrofitHelper.getRetrofit("fra-1a2b3c4d5e6f")

        assertThat(retrofit.baseUrl().toString()).isEqualTo("https://api.eu.recurly.com/")
    }

    @Test
    fun getRetrofit_usPublicKey_routesToUsBaseUrl() {
        val retrofit = RetrofitHelper.getRetrofit("ewr1-4TIXlPCkR68woNJp7UYMSL")

        assertThat(retrofit.baseUrl().toString()).isEqualTo("https://api.recurly.com/")
    }

    @Test
    fun getRetrofit_emptyPublicKey_defaultsToUsBaseUrl() {
        val retrofit = RetrofitHelper.getRetrofit()

        assertThat(retrofit.baseUrl().toString()).isEqualTo("https://api.recurly.com/")
    }
}
