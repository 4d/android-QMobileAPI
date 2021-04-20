/*
 * Created by Quentin Marciset on 29/3/2021.
 * 4D SAS
 * Copyright (c) 2021 Quentin Marciset. All rights reserved.
 */

package com.qmobile.qmobileapi.utils

object HttpCode {

// https://en.wikipedia.org/wiki/List_of_HTTP_status_codes

    // 1xx Informational responses
    const val continue_key = 100
    const val switchingProtocols = 101
    const val processing = 102

    // 2xx Success
    const val ok = 200
    const val created = 201
    const val accepted = 202
    const val nonAuthoritativeInformation = 203
    const val noContent = 204
    const val resetContent = 205
    const val partialContent = 206

    // 3xx Redirection
    const val multipleChoices = 300
    const val movedPermanently = 301
    const val found = 302
    const val seeOther = 303
    const val notModified = 304
    const val useProxy = 305
    const val switchProxy = 306
    const val temporaryRedirect = 307
    const val permanentRedirect = 308

    // 4xx Client errors
    const val badRequest = 400
    const val unauthorized = 401
    const val paymentRequired = 402
    const val forbidden = 403
    const val notFound = 404
    const val methodNotAllowed = 405
    const val notAcceptable = 406
    const val proxyAuthenticationRequired = 407
    const val requestTimeout = 408
    const val conflict = 409
    const val gone = 410
    const val lengthRequired = 411
    const val preconditionFailed = 412
    const val requestEntityTooLarge = 413
    const val requestURITooLong = 414
    const val unsupportedMediaType = 415
    const val requestedRangeNotSatisfiable = 416
    const val expectationFailed = 417
    const val imATeapot = 418
    const val authenticationTimeout = 419
    const val enhanceYourCalm = 420
    const val unprocessableEntity = 422
    const val locked = 423
    const val failedDependency = 424
    const val preconditionRequired = 428
    const val tooManyRequests = 429
    const val requestHeaderFieldsTooLarge = 431

    // 5xx Server errors
    const val internalServerError = 500
    const val notImplemented = 501
    const val badGateway = 502
    const val serviceUnavailable = 503
    const val gatewayTimeout = 504
    const val httpVersionNotSupported = 505
    const val variantAlsoNegotiates = 506
    const val insufficientStorage = 507
    const val loopDetected = 508
    const val notExtended = 510
    const val networkAuthenticationRequired = 511

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
