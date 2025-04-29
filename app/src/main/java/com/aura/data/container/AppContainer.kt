package com.aura.data.container

import com.aura.data.network.AuraClient
import com.aura.data.repository.CredentialsRepository
import com.aura.data.repository.LoginRepository
import com.aura.data.repository.NetworkLoginRepository
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

/**
 * A simple interface representing a container for application-level dependencies.
 *
 * This allows for a clean separation of concerns and easier testing by providing
 * a single access point for core components such as repositories.
 */
interface AppContainer{
    val loginRepository: LoginRepository
    val credentialsRepository: CredentialsRepository

}

/**
 * A concrete implementation of [AppContainer] that sets up network-based dependencies.
 *
 * This container builds a [Retrofit] client configured to communicate with
 * a REST API using [kotlinx.serialization] and JSON. The base URL depends on the
 * environment (emulator or physical device).
 *
 * This container initializes [LoginRepository] with an instance of [AuraClient]
 * using lazy loading to avoid unnecessary resource allocation.
 */
class NetworkAppContainer: AppContainer{

    /** Use 10.0.2.2 for Android emulator, or the local IP of your machine for a physical device */
    //private val baseUrl = "http://10.0.2.2:8080"
    private val baseUrl = "http://192.168.10.48:8080"

    /** JSON configuration using kotlinx.serialization */
    private val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
        isLenient = true
    }

    private val contentType = "application/json".toMediaType()

    /** Retrofit instance configured with JSON converter */
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(json.asConverterFactory(contentType))
        .build()

    /** Lazily initialized API client for network calls */
    private val auraClient: AuraClient by lazy {
        retrofit.create(AuraClient::class.java)
    }

    /** Lazily initialized repository used for login operations */
    override val loginRepository: LoginRepository by lazy {
        NetworkLoginRepository(auraClient)
    }

    override val credentialsRepository: CredentialsRepository by lazy {
        CredentialsRepository()
    }


}