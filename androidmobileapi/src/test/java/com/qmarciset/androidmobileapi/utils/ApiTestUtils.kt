package com.qmarciset.androidmobileapi.utils

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import java.net.HttpURLConnection
import kotlin.collections.set
import okhttp3.Headers
import okhttp3.Headers.Companion.toHeaders
import okhttp3.internal.toHeaderList
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest
import org.junit.Assert
import retrofit2.Response

/**
 * Sample headers
 */
private fun getTestHeaders(): Headers {
    val headers = mutableMapOf<String, String>()
    headers["4DREST-INFO"] = "1.1"
    headers["Accept-Ranges"] = "bytes"
    headers["Connection"] = "keep-alive"
    headers["Content-Type"] = "application/json"
    headers["Server"] = "MockedServer"
    headers["Content-Length"] = "199"
    headers["Date"] = "Wed, 30 Oct 2019 09:20:49 GMT"
    headers["WAKTRANSID"] = "W832FD179F0884A02A8BA2380CC8EFEFF"
    headers["Set-Cookie"] =
        "WASID4D=962084CB4641435BA21FDB62E03AC2A1; Path=/; Max-Age=3600; HttpOnly; Version=1"
    return headers.toHeaders()
}

/**
 * Reads content from assets json files
 */
private fun readContentFromFilePath(context: Context, filename: String): String {
    return context.assets.open(filename).bufferedReader().use {
        it.readText()
    }
}

/**
 * Checks that the request returns positively
 */
fun assertRequest(request: RecordedRequest) {
    Assert.assertEquals("${request.method} ${request.path} HTTP/1.1", request.requestLine)
}

/**
 * Mocks response for unit tests
 */
fun mockResponse(filename: String): MockResponse {
    val responseCode = MockResponse().setResponseCode(HttpURLConnection.HTTP_OK)
    return responseCode.apply {
        responseCode.headers =
            getTestHeaders()
    }
        .setBody(
            readContentFromFilePath(
                ApplicationProvider.getApplicationContext(),
                filename
            )
        )
}

/**
 * Checks that the request returns positively and contains the correct headers count
 */
fun assertResponseSuccessful(response: Response<*>) {
    Assert.assertNotNull(response.body())
    Assert.assertEquals(9, response.headers().toHeaderList().size)
    Assert.assertEquals(HttpURLConnection.HTTP_OK, response.code())
    Assert.assertEquals(true, response.isSuccessful)
    Assert.assertEquals(null, response.errorBody())
}
