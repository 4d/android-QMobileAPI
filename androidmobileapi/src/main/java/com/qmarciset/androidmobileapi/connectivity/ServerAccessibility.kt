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

                    Timber.d("Server ping successful")
                    true
                } catch (e: IOException) {
                    Timber.e("Server ping unsuccessful")
                    false
                }
            }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { hasInternet ->
                    onResult(hasInternet)
                }
        )
    }
}
