package com.aura.data

import com.aura.data.model.LoginRequest
import com.aura.data.model.ServerConnection
import com.aura.data.repository.AuraRepository
import com.aura.data.model.Account

class FakeAuraRepository : AuraRepository {
    override suspend fun login(id: String, password: String): ServerConnection<Boolean> {
        val fakeUser = LoginRequest("test", "test")
        return if (id == fakeUser.id && password == fakeUser.password) {
            ServerConnection.Success(true)
        } else {
            ServerConnection.Success(false)
        }
    }
    override suspend fun getAccounts(id: String): ServerConnection<List<Account>> {
        TODO("Not yet implemented")
    }
    override suspend fun doTransfer(sender: String, recipient: String, amount: Double): ServerConnection<Boolean> {
        TODO("Not yet implemented")
    }
}