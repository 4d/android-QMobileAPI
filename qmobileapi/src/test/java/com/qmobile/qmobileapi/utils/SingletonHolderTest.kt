/*
 * Created by Quentin Marciset on 4/11/2020.
 * 4D SAS
 * Copyright (c) 2020 Quentin Marciset. All rights reserved.
 */

package com.qmobile.qmobileapi.utils

import android.os.Build
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class SingletonHolderTest {

    @Test
    fun `verify really is a singleton`() {
        val authInfoHelperSingleton1 =
            SharedPreferencesHolder.getInstance(ApplicationProvider.getApplicationContext())
        val authInfoHelperSingleton2 =
            SharedPreferencesHolder.getInstance(ApplicationProvider.getApplicationContext())
        val authInfoHelper1 = SharedPreferencesHolder(ApplicationProvider.getApplicationContext())
        val authInfoHelper2 = SharedPreferencesHolder(ApplicationProvider.getApplicationContext())

        Assert.assertTrue(authInfoHelperSingleton1 === authInfoHelperSingleton2)
        Assert.assertFalse(authInfoHelperSingleton1 === authInfoHelper1)
        Assert.assertFalse(authInfoHelper1 === authInfoHelper2)
    }
}
