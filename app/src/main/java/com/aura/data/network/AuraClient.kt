package com.aura.data.network

import com.aura.data.model.LoginRequest
import com.aura.data.model.LoginResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface AuraClient{
    @POST("login")
    suspend fun login(@Body loginRequest: LoginRequest): LoginResponse
}