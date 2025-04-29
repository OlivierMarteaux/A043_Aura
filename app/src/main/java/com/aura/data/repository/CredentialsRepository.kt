package com.aura.data.repository

import android.provider.Telephony.Carriers.PASSWORD
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey

class CredentialsRepository (
    private val dataStore: DataStore<Preferences>
){
    private companion object {
        val IDENTIFIER = stringPreferencesKey("identifier")
        val PASSWORD = stringPreferencesKey("password")

    }

    suspend fun saveCredentials(identifier: String, password: String) {
        dataStore.edit { preferences ->
            preferences[IDENTIFIER] = identifier
            preferences[PASSWORD] = password
        }
    }


}