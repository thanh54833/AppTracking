package com.example.apptracking.config

import android.content.Context
import android.util.Log
import com.example.apptracking.adb.AdbUtil
import com.example.apptracking.config.AppConfig.KEY.FILE_SHAREDGREFS
import com.example.apptracking.config.AppConfig.KEY.HIDE_KEYBOARD
import com.example.apptracking.config.AppConfig.KEY.PASS_CHROME
import com.example.apptracking.config.AppConfig.KEY.REQUEST_PERMISSION

class AppConfig {
    companion object {

        var isPassChrome: Boolean = false // pass wellcom chrome
        var isHideKeyboard: Boolean = false
        var isRequestPermissions: Boolean = false
        private var permissions = listOf(
            "android.permission.READ_PHONE_STATE",
            "android.permission.WRITE_EXTERNAL_STORAGE",
            "android.permission.READ_EXTERNAL_STORAGE"
        )

        @JvmStatic
        fun checkConfig(
            context: Context,
            result: (isPermission: Boolean, isKeyBroad: Boolean, isChrome: Boolean) -> Unit
        ): Boolean {
            fun checkSetting(): Boolean {
                var isSuccessSetting: Boolean = true
                val sharedPrefs =
                    context.getSharedPreferences(FILE_SHAREDGREFS, Context.MODE_PRIVATE)
                with(sharedPrefs) {
                    isPassChrome = getBoolean(PASS_CHROME, false)
                    isHideKeyboard = getBoolean(HIDE_KEYBOARD, false)
                    isRequestPermissions = getBoolean(REQUEST_PERMISSION, false)
                }
                if (!isPassChrome) {
                    AdbUtil.passWellcomeChrome { isSuccess, _ ->
                        if (!isSuccess) {
                            isSuccessSetting = isSuccess
                        }
                        with(sharedPrefs.edit()) {
                            putBoolean(PASS_CHROME, isSuccess)
                            commit()
                        }
                        isPassChrome = isSuccess
                        Log.i("config", "pass chrome : $isSuccess")
                    }
                }
                if (!isHideKeyboard) {
                    AdbUtil.setKeyBoard(false) { isSuccess, _ ->
                        with(sharedPrefs.edit()) {
                            putBoolean(HIDE_KEYBOARD, isSuccess)
                            commit()
                        }
                        if (!isSuccess) {
                            isSuccessSetting = isSuccess
                        }
                        isHideKeyboard = isSuccess
                        Log.i("config", "hide keyboard : $isSuccess")
                    }
                }
                if (!isRequestPermissions) {
                    var isRePer = true
                    permissions.forEach { permission ->
                        AdbUtil.grantPermission(context, permission) { isSuccess, error ->
                            if (!isSuccess) {
                                isRePer = isSuccess
                            }
                            isRequestPermissions = isSuccess
                        }
                    }
                    with(sharedPrefs.edit()) {
                        putBoolean(REQUEST_PERMISSION, isRePer)
                        commit()
                    }
                    if (!isRePer) {
                        isSuccessSetting = isRePer
                    }
                    Log.i("config", "request permision : $isRePer")
                }
                return isSuccessSetting
            }

            val isSetting: Boolean = checkSetting()
            result(isRequestPermissions, isHideKeyboard, isPassChrome)
            return isSetting
        }
    }

    object KEY {
        const val PASS_CHROME = "PASS_CHROME"
        const val HIDE_KEYBOARD = "HIDE_KEYBOARD"
        const val REQUEST_PERMISSION = "REQUES_PERMISSION"
        const val FILE_SHAREDGREFS = "FILE_CONFIG"
    }

}
