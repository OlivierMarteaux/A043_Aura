package com.aura.data.repository

import com.aura.data.model.LoginRequest
import com.aura.data.network.AuraClient

interface LoginRepository{
    suspend fun login(id: String, password: String): Boolean
}

class NetworkLoginRepository(
    private val auraClient: AuraClient
): LoginRepository {
    override suspend fun login(id: String, password: String): Boolean{
        val request = LoginRequest(id, password)
        val response = auraClient.login(request)
        return response.granted
    }
}