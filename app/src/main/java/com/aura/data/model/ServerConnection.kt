package com.aura.data.model

/**
 * Represents the result of an operation with possible loading, success, and error states.
 */
//sealed interface ServerConnection {
//    data object Loading : ServerConnection
//    data class Success(val data: Boolean) : ServerConnection
//    data class Error(val exception: Throwable) : ServerConnection
//}
sealed class ServerConnection<out T> {
    object Loading : ServerConnection<Nothing>()
    data class Success<T>(val data: T) : ServerConnection<T>()
    data class Error(val exception: Throwable) : ServerConnection<Nothing>()
}