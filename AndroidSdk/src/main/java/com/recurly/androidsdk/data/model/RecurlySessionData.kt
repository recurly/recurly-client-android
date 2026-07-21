package com.recurly.androidsdk.data.model

import com.recurly.androidsdk.BuildConfig

/**
 * Holds session-level data used internally by the SDK when building tokenization requests.
 */
internal object RecurlySessionData {

    internal const val versionName = "recurly-client-android; v${BuildConfig.VERSION_NAME}"

}