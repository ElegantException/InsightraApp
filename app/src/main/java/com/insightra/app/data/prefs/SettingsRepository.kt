package com.insightra.app.data.prefs

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class SavedKeyInfo(val date: String)

class SettingsRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs by lazy {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build()
        EncryptedSharedPreferences.create(
            context,
            "insightra_secure",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    suspend fun getApiKey(): String? = withContext(Dispatchers.IO) {
        prefs.getString("api_key", null)
    }

    suspend fun setApiKey(key: String) = withContext(Dispatchers.IO) {
        prefs.edit().putString("api_key", key).putString("api_key_date", System.currentTimeMillis().toString()).apply()
    }

    suspend fun getSavedInfo(): SavedKeyInfo? = withContext(Dispatchers.IO) {
        val date = prefs.getString("api_key_date", null) ?: return@withContext null
        SavedKeyInfo(date)
    }
}
