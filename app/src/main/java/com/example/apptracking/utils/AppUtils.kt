package com.example.apptracking.utils

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.telephony.TelephonyManager
import android.text.TextUtils
import android.util.Log
import androidx.core.app.ActivityCompat
import com.example.apptracking.adb.AdbUtil
import com.example.apptracking.view.main.Shell
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import java.io.File
import java.io.IOException

@Suppress("DEPRECATED_IDENTITY_EQUALS")
class AppUtils {
    companion object {
        @SuppressLint("HardwareIds")
        fun getIMEI(context: Context, result: (error: String) -> Unit): String? {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.READ_PHONE_STATE
                    ) !== PackageManager.PERMISSION_GRANTED
                ) {
                    var isSuccess = false
                    AdbUtil.grantPermission(
                        context,
                        "android.permission.READ_PHONE_STATE"
                    ) { success, error ->
                        if (!isSuccess) {
                            result(error)
                        }
                        isSuccess = success
                    }
                    if (isSuccess) {
                        try {
                            val telephonyManager =
                                context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                            return telephonyManager.deviceId
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                } else {
                    try {
                        val telephonyManager =
                            context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                        return telephonyManager.deviceId
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            } else {
                try {
                    val telephonyManager =
                        context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                    if (ActivityCompat.checkSelfPermission(
                            context,
                            Manifest.permission.READ_PHONE_STATE
                        ) !== PackageManager.PERMISSION_GRANTED
                    ) {
                        return null
                    }
                    return telephonyManager.deviceId
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            return null
        }


        @JvmStatic
        fun isRootedDevice(context: Context): Boolean {
            var rootedDevice = false
            val buildTags = android.os.Build.TAGS
            if (buildTags != null && buildTags.contains("test-keys")) {
                rootedDevice = true
            }
            // check if /system/app/Superuser.apk is present
            try {
                val file = File("/system/app/Superuser.apk")
                if (file.exists()) {
                    rootedDevice = true
                }
            } catch (e1: Throwable) {
                //Ignore
            }
            //check if SU command is executable or not
            try {
                Runtime.getRuntime().exec("su")
                Log.e("Root Detected", "3")
                rootedDevice = true
            } catch (localIOException: IOException) {
                //Ignore
            }
            //check weather busy box application is installed
            val packageName = "stericson.busybox" //Package for busy box app
            val pm = context.getPackageManager()
            try {
                Log.e("Root Detected", "4")
                pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
                rootedDevice = true
            } catch (e: PackageManager.NameNotFoundException) {
                //App not installed
            }
            return rootedDevice
        }




    }
}



@SuppressLint("DefaultLocale")
fun String.converToShell(result: (shell: Shell?, script: String?) -> Unit) {
    val script =
        this.split(" ", ignoreCase = true).takeIf { return@takeIf !it.isNullOrEmpty() }
            ?.first()
    val shell = Shell.values().filter {
        return@filter it.key.equals(script, ignoreCase = true)
    }.takeIf { return@takeIf !it.isNullOrEmpty() }?.first() //?.name ?: ""
    var shellValue: String? = null

    shell?.takeIf { return@takeIf !TextUtils.isEmpty(script) }?.apply {
        shellValue =
            this@converToShell.replace(script ?: "", shell.value, ignoreCase = true)
    }
    result(shell, shellValue)
}


// Serialize a single object.
fun serializeToJson(lists: List<List<String>>): String {
    val gson = Gson()
    return gson.toJson(lists)
}

// Deserialize to single object.
fun deserializeFromJson(jsonString: String): List<List<String>> {
    val gson = Gson()
    val myType = object : TypeToken<List<List<String>>>() {}.type
    return gson.fromJson(jsonString, myType)
}
