package com.recurly.androidsdk.data.network.core

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

internal object RetrofitHelper {

    private const val US_BASE_URL = "https://api.recurly.com/"
    private const val EU_BASE_URL = "https://api.eu.recurly.com/"
    private const val EU_PUBLIC_KEY_PREFIX = "fra-"

    /**
     * Builds the retrofit for its direct use.
     *
     * Requests are routed to Recurly's EU data center when [publicKey] is an EU site's
     * public key (prefixed `fra-`), matching recurly-client-ios's zero-configuration
     * EU routing. No explicit data-center selection is required from integrators.
     *
     * @param publicKey the Recurly site public key, used to select the US/EU endpoint
     * @return Retrofit
     */
    fun getRetrofit(publicKey: String = ""): Retrofit {
        val baseUrl = if (publicKey.startsWith(EU_PUBLIC_KEY_PREFIX)) EU_BASE_URL else US_BASE_URL
        return Retrofit.Builder()
            .client(OkHttpHelper.getOkHttpClient())
            .baseUrl(baseUrl)
            .addConverterFactory(NullOnEmptyConverterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}