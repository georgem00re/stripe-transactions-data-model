package com.example.app.modules

import com.example.app.generated.apis.HealthApiModule
import com.example.app.generated.models.SuccessResponse
import com.example.app.infrastructure.koin.KtorKoinComponent
import io.ktor.server.application.ApplicationCall
import org.koin.dsl.module

val healthApiModule =
    module {
        single<HealthApiModule> { HealthApi() }
    }

class HealthApi : HealthApiModule, KtorKoinComponent() {
    override suspend fun getHealth(call: ApplicationCall): SuccessResponse {
        return SuccessResponse(true)
    }
}
