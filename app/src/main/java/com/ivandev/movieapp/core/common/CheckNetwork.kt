package com.ivandev.movieapp.core.common

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

object CheckNetwork {
    fun isConected(context: Context): Boolean {
        val connecivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities =
            connecivityManager.getNetworkCapabilities(connecivityManager.activeNetwork)
        capabilities.also {
            if (it != null) {
                if (it.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    return true
                } else if (it.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    return true
                }
            }
        }
        return false
    }
}