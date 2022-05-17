package com.recurly.androidsdk.data.model

import android.provider.Settings
import com.recurly.androidsdk.BuildConfig
import java.util.*

/**
 * This singleton class is used to store the session data from the app
 * it holds the public key which have to be set by the client from their
 * app before calling the Tokenization
 *
 * The values that here are stored are just accessible from inside the SDK
 *
 */

object RecurlySessionData {

    private var publicKey: String = ""
    internal const val versionName = "recurly-client-android; v${BuildConfig.VERSION_NAME}"
    internal const val deviceId = Settings.Secure.ANDROID_ID

    internal fun getPublicKey(): String{
        return publicKey
    }

    fun setPublicKey(key: String){
        publicKey = key
    }

}