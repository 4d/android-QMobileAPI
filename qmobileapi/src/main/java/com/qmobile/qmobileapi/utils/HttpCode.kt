/*
 * Created by Quentin Marciset on 29/3/2021.
 * 4D SAS
 * Copyright (c) 2021 Quentin Marciset. All rights reserved.
 */

package com.qmobile.qmobileapi.utils

import com.qmobile.qmobileapi.R

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

    @Suppress("LongMethod")
    fun message(code: Int): Int? =
        when (code) {
            continue_key -> R.string.continue_key
            switchingProtocols -> R.string.switching_protocols
            processing -> R.string.processing

            ok -> R.string.ok
            created -> R.string.created
            accepted -> R.string.accepted
            nonAuthoritativeInformation -> R.string.non_authoritative_information
            noContent -> R.string.no_content
            resetContent -> R.string.reset_content
            partialContent -> R.string.partial_content

            multipleChoices -> R.string.multiple_choices
            movedPermanently -> R.string.moved_permanently
            found -> R.string.found
            seeOther -> R.string.see_other
            notModified -> R.string.not_modified
            useProxy -> R.string.use_proxy
            switchProxy -> R.string.switch_proxy
            temporaryRedirect -> R.string.temporary_redirect
            permanentRedirect -> R.string.permanent_redirect

            badRequest -> R.string.bad_request
            unauthorized -> R.string.unauthorized
            paymentRequired -> R.string.payment_required
            forbidden -> R.string.forbidden
            notFound -> R.string.not_found
            methodNotAllowed -> R.string.method_not_allowed
            notAcceptable -> R.string.not_acceptable
            proxyAuthenticationRequired -> R.string.proxy_authentication_required
            requestTimeout -> R.string.request_timeout
            conflict -> R.string.conflict
            gone -> R.string.gone
            lengthRequired -> R.string.length_required
            preconditionFailed -> R.string.precondition_failed
            requestEntityTooLarge -> R.string.request_entity_too_large
            requestURITooLong -> R.string.request_uri_too_long
            unsupportedMediaType -> R.string.unsupported_media_type
            requestedRangeNotSatisfiable -> R.string.requested_range_not_satisfiable
            expectationFailed -> R.string.expectation_failed
            imATeapot -> R.string.im_a_teapot
            authenticationTimeout -> R.string.authentication_timeout
            enhanceYourCalm -> R.string.enhance_your_calm
            unprocessableEntity -> R.string.unprocessable_entity
            locked -> R.string.locked
            failedDependency -> R.string.failed_dependency
            preconditionRequired -> R.string.precondition_required
            tooManyRequests -> R.string.too_many_requests
            requestHeaderFieldsTooLarge -> R.string.request_header_fields_too_large

            internalServerError -> R.string.internal_server_error
            notImplemented -> R.string.not_implemented
            badGateway -> R.string.bad_gateway
            serviceUnavailable -> R.string.service_unavailable
            gatewayTimeout -> R.string.gateway_timeout
            httpVersionNotSupported -> R.string.http_version_not_supported
            variantAlsoNegotiates -> R.string.variant_also_negotiates
            insufficientStorage -> R.string.insufficient_storage
            loopDetected -> R.string.loop_detected
            notExtended -> R.string.not_extended
            networkAuthenticationRequired -> R.string.network_authentication_required
            else -> null
        }

    fun reason(code: Int): Int? =
        when (code) {
            tooManyRequests -> R.string.reason_too_many_requests
            serviceUnavailable -> R.string.reason_service_unavailable
            movedPermanently -> R.string.reason_moved_permanently
            unauthorized -> R.string.reason_unauthorized
            forbidden -> R.string.reason_forbidden
            notFound -> R.string.reason_not_found
            methodNotAllowed -> R.string.reason_method_not_allowed
            notAcceptable -> R.string.reason_not_acceptable
            requestTimeout -> R.string.reason_request_timeout
            authenticationTimeout -> R.string.reason_authentication_timeout
            locked -> R.string.reason_locked
            notImplemented -> R.string.reason_not_implemented
            gatewayTimeout -> R.string.reason_gateway_timeout
            in 500..600 -> R.string.reason_internal_error
            else -> null
        }
}
