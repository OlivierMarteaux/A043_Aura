package com.aura.data.network

import com.aura.data.model.Account
import com.aura.data.model.LoginRequest
import com.aura.data.model.LoginResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * Retrofit service interface for communicating with the Aura backend API.
 *
 * This interface defines the endpoints exposed by the server and the
 * expected request/response models for each call. It uses Retrofit's
 * HTTP annotations to describe the structure of network requests.
 */
interface AuraClient{
    /**
     * Sends a login request to the server.
     *
     * This endpoint receives user credentials in the body of the request
     * and responds with a [LoginResponse] indicating whether access is granted.
     *
     * @param loginRequest The login credentials provided by the user.
     * @return A [LoginResponse] containing a `granted` flag.
     *
     * @throws retrofit2.HttpException if the server returns a non-2xx response.
     * @throws java.io.IOException if a network error occurs.
     */
    @POST("login")
    suspend fun login(@Body loginRequest: LoginRequest): LoginResponse

    @GET("accounts/{id}")
        suspend fun getAccounts(@Path("id") id: String): List<Account>

}