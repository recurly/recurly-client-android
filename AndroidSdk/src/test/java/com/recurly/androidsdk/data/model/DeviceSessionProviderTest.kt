package com.recurly.androidsdk.data.model

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

/**
 * Coverage for [DeviceSessionProvider]'s persistence contract: a stable per-install [deviceId]
 * when a [android.content.Context] is available, a random fallback when it is not, and a fresh
 * [DeviceSessionProvider.newSessionId] per call.
 */
@RunWith(RobolectricTestRunner::class)
class DeviceSessionProviderTest {

    @Test
    fun deviceId_nullContext_returnsRandomUuid() {
        val first = DeviceSessionProvider.deviceId(null)
        val second = DeviceSessionProvider.deviceId(null)

        assertThat(first).isNotEmpty()
        assertThat(first).isNotEqualTo(second)
    }

    @Test
    fun deviceId_withContext_persistsAndReturnsSameValueOnSubsequentCalls() {
        val context = RuntimeEnvironment.getApplication()

        val first = DeviceSessionProvider.deviceId(context)
        val second = DeviceSessionProvider.deviceId(context)

        assertThat(first).isNotEmpty()
        assertThat(second).isEqualTo(first)
    }

    @Test
    fun deviceId_withContext_survivesAcrossFreshSharedPreferencesLookup() {
        val context = RuntimeEnvironment.getApplication()
        val generated = DeviceSessionProvider.deviceId(context)

        // Simulate a new SDK instance in the same app install reading the same SDK-private prefs.
        val prefs = context.getSharedPreferences("com.recurly.androidsdk.device", android.content.Context.MODE_PRIVATE)

        assertThat(prefs.getString("device_id", null)).isEqualTo(generated)
    }

    @Test
    fun newSessionId_returnsDistinctValuesPerCall() {
        val first = DeviceSessionProvider.newSessionId()
        val second = DeviceSessionProvider.newSessionId()

        assertThat(first).isNotEmpty()
        assertThat(first).isNotEqualTo(second)
    }
}
