package com.recurly.androidsdk.data.network.core

import com.recurly.androidsdk.data.model.RecurlySessionData
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

internal object RetrofitHelper {

    /**
     * Builds the retrofit for its direct use
     * @return Retrofit
     */
    fun getRetrofit(): Retrofit {
        val defaultURL = "https://api.recurly.com/"
        val defaultURLEU = "https://api.eu.recurly.com/"

        val baseUrl =
            if (RecurlySessionData.getPublicKey().startsWith("fra-")) defaultURLEU else defaultURL

        return Retrofit.Builder()
            .client(OkHttpHelper.getOkHttpClient())
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}