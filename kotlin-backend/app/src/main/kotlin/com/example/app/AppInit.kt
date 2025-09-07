package com.example.app

import com.example.app.generated.ApplicationCompressionConfiguration
import com.example.app.generated.ApplicationHstsConfiguration
import com.example.app.generated.models.ErrorResponse
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.UserIdPrincipal
import io.ktor.server.auth.basic
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.plugins.autohead.AutoHeadResponse
import io.ktor.server.plugins.calllogging.CallLogging
import io.ktor.server.plugins.compression.Compression
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.plugins.defaultheaders.DefaultHeaders
import io.ktor.server.plugins.hsts.HSTS
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond

abstract class NotFoundError(message: String) : Exception(message)

abstract class BadRequestError(message: String) : Exception(message)

abstract class ConflictError(message: String) : Exception(message)

fun Application.configureAll() {
    // Automatically adds a few standard HTTP headers to every response.
    install(DefaultHeaders)

    // Enables Ktor to automatically serialize/deserialize request
    // and response bodies based on the Content-Type and Accept headers.
    install(ContentNegotiation) {
        json()
    }

    // Enables Ktor to automatically handle HEAD HTTP requests for
    // any route that already handles a corresponding GET request.
    install(AutoHeadResponse)

    // Enables automatic response compression, so that your server can send
    // smaller, faster payloads to clients that support it.
    install(Compression, ApplicationCompressionConfiguration())

    // HSTS (HTTP Strict Transport Security) is a security policy that tells browsers
    // to always connect to this server using HTTPS (never HTTP).
    install(HSTS, ApplicationHstsConfiguration())

    // Enables Ktor to automatically log details about each HTTP call this server handles.
    install(CallLogging)

    // For error handling and custom HTTP responses.
    // It lets you map custom exceptions to specific HTTP responses
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respond(
                HttpStatusCode.InternalServerError,
                ErrorResponse("Internal Server Error", "The error has been logged"),
            )
            throw cause
        }
        exception<NotFoundError> { call, cause ->
            call.respond(
                HttpStatusCode.NotFound,
                ErrorResponse("Not Found", cause.localizedMessage),
            )
            throw cause
        }
        exception<BadRequestError> { call, cause ->
            call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponse("Bad Request", cause.localizedMessage),
            )
            throw cause
        }
        exception<ConflictError> { call, cause ->
            call.respond(
                HttpStatusCode.Conflict,
                ErrorResponse("Conflict", cause.localizedMessage),
            )
            throw cause
        }
    }

    // CORS (or Cross-Origin Resource Sharing) controls whether a
    // client is allowed to make cross-origin requests to this server.
    install(CORS) {
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.Authorization)
        allowMethod(HttpMethod.Get)

        // This must only be used in development!!
        allowHost(
            host = "localhost:3000",
            schemes = listOf("http"),
        )
    }

    // This enables you to secure your API routes by intercepting requests, verifying
    // credentials (like a JWT or username/password combination), providing a principal
    // to the route handler (e.g. UserIdPrincipal or JWTPrincipal) if authentication succeeds,
    // and rejecting the request with a 401 if authentication fails.
    install(Authentication) {
        basic("basicAuth") {
            validate { userPasswordCredential ->

                // Replace this with your actual validation logic. This should return a principal
                // (if the authentication was successful) or null (if the authentication failed).
                UserIdPrincipal(name = "John Doe")
            }
        }
        jwt("jwtAuth") {
            validate { jwtCredential ->
                // ...
            }
        }
    }
}
