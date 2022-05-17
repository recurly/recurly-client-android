package com.recurly.androidsdk.data.network.core

import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

internal object OkHttpHelper {

    /**
     * Builds the OkHttp client to manage retrofit calls and timeout
     * @return OkHttpClient
     */
    fun getOkHttpClient(): OkHttpClient {
        return OkHttpClient()
            .newBuilder()
            .readTimeout(20, TimeUnit.SECONDS)
            .connectTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .build();
    }

}