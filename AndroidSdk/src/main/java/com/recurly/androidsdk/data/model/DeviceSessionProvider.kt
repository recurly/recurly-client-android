package com.recurly.androidsdk.data.model

import android.content.Context
import androidx.core.content.edit
import java.util.UUID

/**
 * Resolves the `device_id` and `session_id` values sent alongside tokenization requests.
 *
 * These are lightweight fraud/telemetry signals (not hardware identifiers) mirroring the
 * fields recurly-client-ios sends on every tokenization request.
 */
internal object DeviceSessionProvider {

    private const val PREFS_NAME = "com.recurly.androidsdk.device"
    private const val KEY_DEVICE_ID = "device_id"

    /**
     * Returns a stable, per-install device identifier.
     *
     * When [context] is available, the identifier is persisted in SDK-private
     * [android.content.SharedPreferences] and survives across app launches, analogous to iOS's
     * `identifierForVendor`. This is a randomly generated UUID local to this app install —
     * never `ANDROID_ID` or another hardware/advertising identifier.
     *
     * When [context] is `null` (e.g. the legacy Context-free [com.recurly.androidsdk.RecurlyClient]
     * constructor), a random UUID is generated instead. Callers should resolve and cache this once
     * per SDK instance rather than calling it per-request, since without a [Context] the value
     * cannot be persisted across instances.
     */
    fun deviceId(context: Context?): String {
        if (context == null) {
            return UUID.randomUUID().toString()
        }

        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val existing = prefs.getString(KEY_DEVICE_ID, null)
        if (existing != null) {
            return existing
        }

        val generated = UUID.randomUUID().toString()
        prefs.edit { putString(KEY_DEVICE_ID, generated) }
        return generated
    }

    /**
     * Generates a new random session identifier. Callers should invoke this once per SDK
     * instantiation and reuse the value for the lifetime of that instance.
     */
    fun newSessionId(): String = UUID.randomUUID().toString()
}
