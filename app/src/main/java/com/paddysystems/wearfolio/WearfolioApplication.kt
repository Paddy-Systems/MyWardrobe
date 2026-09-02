package com.paddysystems.wearfolio

import android.app.Application

class WearfolioApplication : Application() {
    lateinit var appContainer: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()

        appContainer = AppContainer(this)
    }
}