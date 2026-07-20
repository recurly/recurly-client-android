package com.recurly.androidsdk.data.network.core

import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

/**
 * Retrofit's Gson converter throws an [java.io.EOFException] instead of returning null when a
 * response body is empty (e.g. a 200 with no content). This factory intercepts empty bodies
 * before they reach the delegate converter so `Response.body()` can be safely null-checked,
 * as the rest of the network layer expects.
 */
internal class NullOnEmptyConverterFactory : Converter.Factory() {

    override fun responseBodyConverter(
        type: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *> {
        val delegate = retrofit.nextResponseBodyConverter<Any>(this, type, annotations)
        return Converter<ResponseBody, Any?> { body ->
            if (body.contentLength() == 0L) null else delegate.convert(body)
        }
    }
}
