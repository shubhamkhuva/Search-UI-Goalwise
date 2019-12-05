package com.shubhamkhuva.searchtaskgoalwise.helper

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo

/**
 * Created by FOR WORK on 18-06-2016.
 */
class ConnectionDetector(private val _context: Context) {
    val isConnectingToInternet: Boolean
        get() {
            val connectivity =
                _context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (connectivity != null) {
                val info = connectivity.allNetworkInfo
                if (info != null) for (anInfo in info) if (anInfo.state == NetworkInfo.State.CONNECTED) {
                    return true
                }
            }
            return false
        }

}