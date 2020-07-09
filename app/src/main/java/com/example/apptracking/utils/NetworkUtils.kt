package com.example.apptracking.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import com.example.apptracking.adb.AdbUtil


object NetworkUtils {
    private const val TYPE_WIFI = 1
    private const val TYPE_MOBILE = 2
    private const val TYPE_NOT_CONNECTED = 0


    //private lateinit var result: (isSuccess: Boolean, error: String) -> Unit
    private fun getConnectivityStatus(context: Context): Int {
        val connectivityManager = context
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        if (networkInfo != null) {
            if (networkInfo.type == ConnectivityManager.TYPE_WIFI
                && networkInfo.state == NetworkInfo.State.CONNECTED
            ) {
                return TYPE_WIFI
            } else if (networkInfo.type == ConnectivityManager.TYPE_MOBILE
                && networkInfo.state == NetworkInfo.State.CONNECTED
            ) {
                return TYPE_MOBILE
            }
        }
        return TYPE_NOT_CONNECTED
    }

    fun isNetworkConnected(context: Context): Boolean {
        val networkStatus = getConnectivityStatus(context)
        var isNetWork = networkStatus == TYPE_WIFI || networkStatus == TYPE_MOBILE
        if (!isNetWork) {
            AdbUtil.sudoForResult("svc wifi enable") { isSuccess, _ ->
                isNetWork = isSuccess
            }
        }
        return isNetWork
    }

    fun isNetworkConnected(
        context: Context,
        result: (isSuccess: Boolean, error: String) -> Unit
    ): Boolean {
        val isNetwork = isNetworkConnected(context)
        if (!isNetwork) {
            AdbUtil.sudoForResult("svc wifi enable", result = result)
        }
        return isNetwork
    }
}