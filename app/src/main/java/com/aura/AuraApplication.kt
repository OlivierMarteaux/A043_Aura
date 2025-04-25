package com.aura

import android.app.Application
import com.aura.data.container.AppContainer
import com.aura.data.container.NetworkAppContainer

class AuraApplication: Application() {
    /** AppContainer instance used by the rest of classes to obtain dependencies */
    lateinit var container: AppContainer
    override fun onCreate() {
        super.onCreate()
        container = NetworkAppContainer()
    }
}