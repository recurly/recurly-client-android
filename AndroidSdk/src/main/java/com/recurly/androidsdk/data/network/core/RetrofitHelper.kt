package com.recurly.androidsdk.data.network.core

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

internal object RetrofitHelper {

    /**
     * Builds the retrofit for its direct use
     * @return Retrofit
     */
    fun getRetrofit(): Retrofit{
        return Retrofit.Builder()
            .client(OkHttpHelper.getOkHttpClient())
            .baseUrl("https://api.recurly.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}