package com.aura.data.container

import com.aura.data.network.AuraClient
import com.aura.data.repository.LoginRepository
import com.aura.data.repository.NetworkLoginRepository
import com.aura.ui.login.LoginViewModel
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

interface AppContainer{
    val loginRepository: LoginRepository
}

class NetworkAppContainer: AppContainer{
    private val baseUrl = "http://127.0.0.1:8080/"

    private val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
        isLenient = true
    }

    private val contentType = "application/json".toMediaType()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(json.asConverterFactory(contentType))
        .build()

    private val auraClient: AuraClient by lazy {
        retrofit.create(AuraClient::class.java)
    }

    override val loginRepository: LoginRepository by lazy {
        NetworkLoginRepository(auraClient)
    }
//    val viewModel = LoginViewModel(loginRepository)
}