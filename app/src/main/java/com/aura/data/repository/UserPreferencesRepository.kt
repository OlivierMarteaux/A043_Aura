package com.aura.data.repository

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

/**
 * Repository for managing user preferences using Jetpack DataStore.
 *
 * This class provides methods to persist and retrieve simple user input
 * as a string value, using a `Preferences`-based `DataStore`.
 *
 * @property dataStore The DataStore instance used to read and write user preferences.
 */
class UserPreferencesRepository(
    private val dataStore:DataStore<Preferences>
) {
    private companion object {
        val USER_INPUT = stringPreferencesKey("user_input")
        const val TAG = "UserPreferencesRepo"
    }

    suspend fun saveUserInput(userInput: String) {
        dataStore.edit { preferences ->
            preferences[USER_INPUT] = userInput
        }
    }

    val userInput: Flow<String> = dataStore.data
        .map { preferences -> preferences[USER_INPUT] ?: ""}
}