/*
 * Created by Quentin Marciset on 7/2/2020.
 * 4D SAS
 * Copyright (c) 2020 Quentin Marciset. All rights reserved.
 */

package com.qmarciset.androidmobileapi.connectivity

import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket
import timber.log.Timber

class ServerAccessibility {

    var disposable: CompositeDisposable = CompositeDisposable()

    /**
     * Pings server
     */
    fun pingServer(
        hostname: String,
        port: Int,
        timeout: Int,
        onResult: (isAccessible: Boolean) -> Unit
    ) {
        disposable.add(
            Single.fromCallable {
                try {
                    val socket = Socket()
                    val socketAddress = InetSocketAddress(hostname, port)

                    socket.connect(socketAddress, timeout)
                    socket.close()

                    Timber.i("Server ping successful")
                    true
                } catch (e: IOException) {
                    Timber.i("Server ping unsuccessful")
                    false
                }
            }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { isServerAccessible ->
                    onResult(isServerAccessible)
                }
        )
    }
}