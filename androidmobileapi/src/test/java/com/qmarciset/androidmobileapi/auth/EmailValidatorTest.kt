/*
 * Created by Quentin Marciset on 29/5/2020.
 * 4D SAS
 * Copyright (c) 2020 Quentin Marciset. All rights reserved.
 */

package com.qmarciset.androidmobileapi.auth

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class EmailValidatorTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun testIsValidEmail() {
        val validEmailList = listOf(
            "EMAIL@example.com",
            "email@example.com",
            "firstname.lastname@example.com",
            "email@subdomain.example.com",
            "email@123.123.123.123",
            "1234567890@example.com",
            "email@example-one.com",
            "_______@example.com",
            "email@example.name",
            "email@example.museum",
            "email@example.co.jp",
            "firstname-lastname@example.com",
            ".email@example.com",
            "email.@example.com",
            "email..email@example.com",
            "Abc..123@example.com",
            "firstname+lastname@example.com"
        )
        val invalidEmailList = listOf(
            "",
            "plainaddress",
            "\n#@%^%#$@#$@#.com",
            "@example.com",
            "@@text.com",
            "ee@test..com",
            "Joe Smith <email@example.com>",
            "\"email\"@example.com",
            "email@example",
            "email@example@example.com",
            "あいうえお@example.com",
            "email@example.com (Joe Smith)",
            "”(),:;<>[\\]@example.com",
            "just”not”right@example.com",
            "\nthis\\ is\"really\"not\\allowed@example.com",
            "very.”(),:;<>[]”.VERY.”very@\\ \"very”.unusual@strange.example.com",
            "very.unusual.”@”.unusual.com@example.com",
            "much.”more\\ unusual”@example.com",
            "email@[123.123.123.123]"
        )

        for (email in validEmailList) {
            Assert.assertTrue(email.isEmailValid())
        }
        for (email in invalidEmailList) {
            Assert.assertFalse(email.isEmailValid())
        }
    }
}
