/*
 * Created by Quentin Marciset on 7/2/2020.
 * 4D SAS
 * Copyright (c) 2020 Quentin Marciset. All rights reserved.
 */

package com.qmobile.qmobileapi.connectivity

import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket

class ServerAccessibility {

    var disposable: CompositeDisposable = CompositeDisposable()

    /**
     * Pings server
     */
    fun pingServer(
        hostname: String,
        port: Int,
        timeout: Int,
        onResult: (isAccessible: Boolean?, throwable: Throwable?) -> Unit
    ) {
        disposable.add(
            Single.fromCallable {
                try {
                    val socket = Socket()
                    val socketAddress = InetSocketAddress(hostname, port)

                    socket.connect(socketAddress, timeout)
                    socket.close()

                    Timber.d("Server ping successful to $hostname:$port")
                    true
                } catch (e: IOException) {
                    Timber.d("Server ping unsuccessful")
                    Timber.e(e.localizedMessage)
                    false
                }
            }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { isServerAccessible, error ->
                    onResult(isServerAccessible, error)
                }
        )
    }
}
