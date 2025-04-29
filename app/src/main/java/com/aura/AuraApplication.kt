package com.aura

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.aura.data.container.AppContainer
import com.aura.data.container.NetworkAppContainer
import com.aura.data.repository.UserPreferencesRepository

private const val USER_PREFERENCE_NAME = "user_preferences"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = USER_PREFERENCE_NAME
)

/**
 * Custom [Application] class for the Aura app.
 *
 * This class serves as the entry point of the application and is responsible for initializing
 * global dependencies that need to be shared across the app. Specifically, it provides an instance
 * of [AppContainer] (e.g., [NetworkAppContainer]) which holds references to repositories and other
 * core components.
 *
 * The [container] is exposed so that components like ViewModels can access the application's
 * dependencies without tightly coupling to specific implementations, supporting better modularity
 * and testability.
 *
 * @see AppContainer
 * @see NetworkAppContainer
 */
class AuraApplication: Application() {
    /**
     * The dependency container used throughout the app.
     * Initialized in [onCreate] with an instance of [NetworkAppContainer].
     */
    lateinit var container: AppContainer
    lateinit var userPreferencesRepository: UserPreferencesRepository

    override fun onCreate() {
        super.onCreate()
        container = NetworkAppContainer()
        userPreferencesRepository = UserPreferencesRepository(dataStore)
    }
}