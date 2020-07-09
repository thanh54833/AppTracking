package com.example.apptracking.adb

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import java.io.*
import java.net.DatagramSocket
import java.net.Socket


class AdbUtil {
    companion object {
        lateinit var result: (isSuccess: Boolean, error: String) -> Unit
        @JvmStatic
        fun sudoForResult(
            vararg strings: String,
            result: (isSuccess: Boolean, error: String) -> Unit
        ): String {
            this.result = result
            return sudoForResult(*strings)
        }

        @JvmStatic
        fun sudoForResult(vararg strings: String): String {
            var res = ""
            var outputStream: DataOutputStream? = null
            var response: InputStream? = null
            try {
                val su = Runtime.getRuntime().exec(strings)//"su"
                outputStream = DataOutputStream(su.outputStream)
                response = su.inputStream
                for (s in strings) {
                    outputStream.writeBytes(s + "\n")
                    outputStream.flush()
                }
                outputStream.writeBytes("exit\n")
                outputStream.flush()
                try {
                    su.waitFor()
                } catch (e: InterruptedException) {
                    if (this::result.isInitialized) {
                        result(false, "error message : " + e.message)
                    }
                    e.printStackTrace()
                }
                res = readFully(response)
            } catch (e: IOException) {
                if (this::result.isInitialized) {
                    result(false, "error message : " + e.message)
                }
                e.printStackTrace()
            } finally {
                outputStream?.let { it1 ->
                    response?.let {
                        closeSilently(it1, it)
                    }
                }
            }
            return res
        }

        @Throws(IOException::class)
        private fun readFully(ip: InputStream): String {
            val baos = ByteArrayOutputStream()
            val buffer = ByteArray(4096)
            ip.use { input ->
                baos.use { fileOut ->
                    while (true) {
                        val length: Int = input.read(buffer)
                        if (length <= 0) {
                            break
                        }
                        Log.i("shell", "= buffer :" + buffer)
                        fileOut.write(buffer, 0, length)
                    }
                }
            }
            return baos.toString("UTF-8")
        }

        private fun closeSilently(vararg xs: Any) {
            // Note: on Android API levels prior to 19 Socket does not implement Closeable
            for (x in xs) {
                try {
                    when (x) {
                        is Closeable -> x.close()
                        is Socket -> x.close()
                        is DatagramSocket -> x.close()
                        else -> {
                            if (this::result.isInitialized) {
                                result(false, "cannot close : $x")
                            }
                        }
                    }
                } catch (e: Throwable) {
                    if (this::result.isInitialized) {
                        result(false, "error message : " + e.message)
                    }
                }
            }
            if (this::result.isInitialized) {
                result(true, "is success")
            }
        }

/*
* Get list permission by : pm list permissions -g  with
* -g : get with group
* Ex : android.permission.READ_PHONE_STATE
* using : AppUtils.getIMEI(this)
*
* */

        fun grantPermission(context: Context, packageName: String) {
            sudoForResult("pm grant ${context.packageName} $packageName")
        }

        fun grantPermission(
            context: Context,
            packageName: String,
            result: (isSuccess: Boolean, error: String) -> Unit
        ) {
            //Log.i("===", "=====")
            sudoForResult("pm grant ${context.packageName} $packageName", result = result)
        }


        /*
        * Start app with your name app
        * name : name app
        *
        * ex : hahalolo
        *
        *
        * */

        @SuppressLint("DefaultLocale")
        fun startApp(name: String) {
            var listPackage =
                sudoForResult("ls -a /data/data/ | grep $name").split("\n", ignoreCase = true)
            if (listPackage.isEmpty()) {
                listPackage =
                    sudoForResult("ls -a /data/data/ | grep ${name.capitalize()}").split(
                        "\n",
                        ignoreCase = true
                    )
            }
            listPackage.takeIf { return@takeIf !it.isNullOrEmpty() }?.first()?.apply {
                sudoForResult("monkey -p $this -c android.intent.category.LAUNCHER 1")
            }
        }


        fun startApp(name: String, result: (isSuccess: Boolean, error: String) -> Unit) {
            val listPackage =
                sudoForResult("ls -a /data/data/ | grep $name").split("\n", ignoreCase = true)
            listPackage.takeIf { return@takeIf !it.isNullOrEmpty() }?.first()?.apply {
                sudoForResult(
                    "monkey -p $this -c android.intent.category.LAUNCHER 1",
                    result = result
                )
            } ?: result(false, "App name not exist !")
        }

        fun setKeyBoard(value: Boolean) {
            fun show() {
                val listMethodInputs =
                    sudoForResult("ime list -s -a").split("\n", ignoreCase = true)
                listMethodInputs.takeIf { return@takeIf !it.isNullOrEmpty() }?.forEach {
                    it.takeIf { return@takeIf it.contains("HindiInputMethodService") }?.apply {
                        sudoForResult("ime set $this")
                    }
                }
            }

            fun hide() {
                val listMethodInputs =
                    sudoForResult("ime list -s -a").split("\n", ignoreCase = true)
                listMethodInputs.takeIf { return@takeIf !it.isNullOrEmpty() }?.forEach {
                    it.takeIf { return@takeIf it.contains("HindiInputMethodService") }?.apply {
                        sudoForResult("ime set $this")
                    }
                }
            }
            if (value) {
                show()
            } else {
                hide()
            }
        }

        fun setKeyBoard(value: Boolean, result: (isSuccess: Boolean, error: String) -> Unit) {
            fun show() {
                val listMethodInputs =
                    sudoForResult("ime list -s -a", result = result).split("\n", ignoreCase = true)
                var methodInputDefault: String = ""
                listMethodInputs.takeIf { return@takeIf !it.isNullOrEmpty() }?.forEach {
                    sudoForResult("ime enable $this", result = result)
                    it.takeIf { return@takeIf it.contains("LatinIME") }?.apply {
                        methodInputDefault = this
                    }
                }
                sudoForResult("ime enable $methodInputDefault", result = result)
            }

            fun hide() {
                val listMethodInputs =
                    sudoForResult("ime list -s -a", result = result).split("\n", ignoreCase = true)
                listMethodInputs.takeIf { return@takeIf !it.isNullOrEmpty() }?.forEach { method ->
                    sudoForResult("ime disable $method", result = result)
                }
            }
            if (value) {
                show()
            } else {
                hide()
            }
        }

        fun passWellcomeChrome(result: (isSuccess: Boolean, error: String) -> Unit) {
            sudoForResult(
                "adb shell 'echo \"chrome --disable-fre --no-default-browser-check --no-first-run\" > /data/local/tmp/chrome-command-line'",
                result = result
            )
        }

        fun passWellcomeChrome() {
            sudoForResult("adb shell 'echo \"chrome --disable-fre --no-default-browser-check --no-first-run\" > /data/local/tmp/chrome-command-line'")
        }


        /*
        *
        * */
        @JvmStatic
        fun screenShot(result: (isSuccess: Boolean, error: String) -> Unit): Bitmap? {
            this.result = result
            return screenShot()
        }

        @JvmStatic
        fun screenShot(): Bitmap? {
            try {
                val process = Runtime.getRuntime().exec("su")
                val outputStream = OutputStreamWriter(process.outputStream)
                outputStream.write("/system/bin/screencap -p\n")
                outputStream.flush()
                val screen = BitmapFactory.decodeStream(process.inputStream)
                outputStream.write("exit\n")
                outputStream.flush()
                outputStream.close()

                if (this::result.isInitialized) {
                    result(true, "is success")
                }

                return screen
            } catch (e: IOException) {
                if (this::result.isInitialized) {
                    result(true, "error message : " + e.message)
                }
                e.printStackTrace()
                return null
            }
        }


    }
}




