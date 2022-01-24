/*
 * Created by Quentin Marciset on 29/3/2021.
 * 4D SAS
 * Copyright (c) 2021 Quentin Marciset. All rights reserved.
 */

package com.qmobile.qmobileapi.utils

//@Suppress("ComplexMethod", "LongMethod", "MagicNumber")
object HttpCode {

// https://en.wikipedia.org/wiki/List_of_HTTP_status_codes

    // 1xx Informational responses
    private const val continue_key = 100
    private const val switchingProtocols = 101
    private const val processing = 102

    // 2xx Success
    private const val ok = 200
    private const val created = 201
    private const val accepted = 202
    private const val nonAuthoritativeInformation = 203
    private const val noContent = 204
    private const val resetContent = 205
    private const val partialContent = 206

    // 3xx Redirection
    private const val multipleChoices = 300
    private const val movedPermanently = 301
    private const val found = 302
    private const val seeOther = 303
    private const val notModified = 304
    private const val useProxy = 305
    private const val switchProxy = 306
    private const val temporaryRedirect = 307
    private const val permanentRedirect = 308

    // 4xx Client errors
    private const val badRequest = 400
    private const val unauthorized = 401
    private const val paymentRequired = 402
    private const val forbidden = 403
    private const val notFound = 404
    private const val methodNotAllowed = 405
    private const val notAcceptable = 406
    private const val proxyAuthenticationRequired = 407
    const val requestTimeout = 408
    private const val conflict = 409
    private const val gone = 410
    private const val lengthRequired = 411
    private const val preconditionFailed = 412
    private const val requestEntityTooLarge = 413
    private const val requestURITooLong = 414
    private const val unsupportedMediaType = 415
    private const val requestedRangeNotSatisfiable = 416
    private const val expectationFailed = 417
    private const val imATeapot = 418
    private const val authenticationTimeout = 419
    private const val enhanceYourCalm = 420
    private const val unprocessableEntity = 422
    private const val locked = 423
    private const val failedDependency = 424
    private const val preconditionRequired = 428
    private const val tooManyRequests = 429
    private const val requestHeaderFieldsTooLarge = 431

    // 5xx Server errors
    private const val internalServerError = 500
    private const val notImplemented = 501
    private const val badGateway = 502
    private const val serviceUnavailable = 503
    private const val gatewayTimeout = 504
    private const val httpVersionNotSupported = 505
    private const val variantAlsoNegotiates = 506
    private const val insufficientStorage = 507
    private const val loopDetected = 508
    private const val notExtended = 510
    private const val networkAuthenticationRequired = 511

    fun message(code: Int): String =
        when (code) {
            continue_key -> "Continue"
            switchingProtocols -> "Switching Protocols"
            processing -> "Processing"

            ok -> "OK"
            created -> "Created"
            accepted -> "Accepted"
            nonAuthoritativeInformation -> "Non Authoritative Information"
            noContent -> "No Content"
            resetContent -> "Reset Content"
            partialContent -> "Partial Content"

            multipleChoices -> "Multiple Choices"
            movedPermanently -> "Moved Permanently"
            found -> "Found"
            seeOther -> "See Other"
            notModified -> "Not Modified"
            useProxy -> "Use Proxy"
            switchProxy -> "Switch Proxy"
            temporaryRedirect -> "Temporary Redirect"
            permanentRedirect -> "Permanent Redirect"

            badRequest -> "Bad Request"
            unauthorized -> "Unauthorized"
            paymentRequired -> "Payment Required"
            forbidden -> "Forbidden"
            notFound -> "Not Found"
            methodNotAllowed -> "Method Not Allowed"
            notAcceptable -> "Not Acceptable"
            proxyAuthenticationRequired -> "Proxy Authentication Required"
            requestTimeout -> "Request Timeout"
            conflict -> "Conflict"
            gone -> "Gone"
            lengthRequired -> "Length Required"
            preconditionFailed -> "Precondition Failed"
            requestEntityTooLarge -> "Request Entity Too Large"
            requestURITooLong -> "Request URI Too Long"
            unsupportedMediaType -> "Unsupported Media Type"
            requestedRangeNotSatisfiable -> "Requested Range Not Satisfiable"
            expectationFailed -> "Expectation Failed"
            imATeapot -> "I'm A Teapot"
            authenticationTimeout -> "Authentication Timeout"
            enhanceYourCalm -> "Enhance Your Calm"
            unprocessableEntity -> "Unprocessable Entity"
            locked -> "Locked"
            failedDependency -> "Failed Dependency"
            preconditionRequired -> "Precondition Required"
            tooManyRequests -> "Too Many Requests"
            requestHeaderFieldsTooLarge -> "Request Header Fields Too Large"

            internalServerError -> "Internal Server Error"
            notImplemented -> "Not Implemented"
            badGateway -> "Bad Gateway"
            serviceUnavailable -> "Service Unavailable"
            gatewayTimeout -> "Gateway Timeout"
            httpVersionNotSupported -> "HTTP Version Not Supported"
            variantAlsoNegotiates -> "Variant Also Negotiates"
            insufficientStorage -> "Insufficient Storage"
            loopDetected -> "Loop Detected"
            notExtended -> "Not Extended"
            networkAuthenticationRequired -> "Network Authentication Required"
            else -> ""
        }

    fun reason(code: Int): String? =
        when (code) {
            tooManyRequests -> "Too many requests send to the application server."
            serviceUnavailable -> "Service is unavailable currently."
            movedPermanently -> "Resource is moved permanently."
            unauthorized -> "You are no more authenticated. Please login."
            forbidden -> "You are no more allowed to access the resource or make this action."
            notFound -> "Some information is missing on application server."
            methodNotAllowed -> "You are not a allowed to make this request."
            notAcceptable -> "The request is not acceptable."
            requestTimeout -> "The server make too much time to respond."
            authenticationTimeout -> "Your session has expired, please re-login."
            locked -> "The resource is locked."
            notImplemented -> "Request are not available."
            gatewayTimeout -> "Gateway timeout."
            in 500..600 ->
                "The server encountered an error and was unable to complete your " +
                    "request. Please contact the server administrator."
            else -> null
        }
}
