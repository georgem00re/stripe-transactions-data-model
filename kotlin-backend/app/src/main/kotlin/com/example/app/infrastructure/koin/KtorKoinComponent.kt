package com.example.app.infrastructure.koin

import org.koin.core.Koin
import org.koin.core.component.KoinComponent

abstract class KtorKoinComponent : KoinComponent {
    override fun getKoin(): Koin = KoinContext.getContext()
}
