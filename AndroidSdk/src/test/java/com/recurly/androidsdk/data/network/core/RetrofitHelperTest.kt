package com.recurly.androidsdk.data.network.core

import com.google.common.truth.Truth.assertThat
import com.recurly.androidsdk.data.model.googlepay.GooglePayInfoResponse
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
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

    @Test
    fun getRetrofit_errorConverter_partialErrorListsDefaultToEmpty() {
        val retrofit = RetrofitHelper.getRetrofit()
        val converter = retrofit.responseBodyConverter<GooglePayInfoResponse>(
            GooglePayInfoResponse::class.java, emptyArray()
        )
        val body = """{"site_mode":"test","error":{"code":"x"}}""".toResponseBody("application/json".toMediaType())

        val parsed = converter.convert(body)

        assertThat(parsed?.error?.errorCode).isEqualTo("x")
        assertThat(parsed?.error?.fields).isEmpty()
        assertThat(parsed?.error?.details).isEmpty()
    }
}
