package com.example.app.infrastructure.koin

import org.koin.core.Koin
import org.koin.core.KoinApplication
import org.koin.mp.KoinPlatformTools

object KoinContext {
    private var koin: Koin? = null

    fun setContext(application: KoinApplication) {
        koin = application.koin
    }

    fun getContext(): Koin {
        return koin ?: KoinPlatformTools.defaultContext().get()
    }
}
