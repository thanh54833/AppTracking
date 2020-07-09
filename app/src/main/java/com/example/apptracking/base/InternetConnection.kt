package com.example.apptracking.base

import android.content.Context
import android.net.ConnectivityManager


object InternetConnection {
    fun checkConnection(context: Context): Boolean {
        val connMgr = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connMgr.activeNetworkInfo

        if (activeNetworkInfo != null) { // connected to the internet
            // connected to the mobile provider's data plan
            return if (activeNetworkInfo.type == ConnectivityManager.TYPE_WIFI) {
                // connected to wifi
                true
            } else
                activeNetworkInfo.type == ConnectivityManager.TYPE_MOBILE
        }
        return false
    }
}