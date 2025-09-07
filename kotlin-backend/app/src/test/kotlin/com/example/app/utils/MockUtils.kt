package com.example.app.utils

import io.ktor.http.ParametersBuilder
import io.ktor.server.application.ApplicationCall
import io.mockk.every
import io.mockk.mockk

fun createMockApplicationCall(): ApplicationCall {
    val mockCall = mockk<ApplicationCall>()
    every { mockCall.request.queryParameters } returns ParametersBuilder().build()
    return mockCall
}
