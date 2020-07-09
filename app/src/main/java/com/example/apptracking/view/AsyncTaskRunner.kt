package com.example.apptracking.view

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.AsyncTask
import android.os.Environment
import android.os.Environment.getExternalStorageDirectory
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.apptracking.adb.AdbUtil
import com.example.apptracking.utils.converToShell
import com.example.apptracking.view.main.Shell
import java.io.DataOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.regex.Pattern


class AsyncTaskRunner() :
    AsyncTask<List<List<String>>, String, String>() {

    private var isLoop: Boolean = true
    private var isRepeat: Boolean = false
    private var scripts: List<String>? = null

    var errorMessage: Error? = null

    fun setIsLoop(isLoop: Boolean) {
        this.isRepeat = isLoop
    }

    fun getErrors(): Error? {
        return errorMessage
    }

    override fun doInBackground(vararg p0: List<List<String>>?): String {

        val result = AdbUtil.sudoForResult("getevent -tl") { isSuccess, error ->
            Log.i("shell", " async task ... :" + isSuccess + "=== error :==" + error)
        }
        Log.i("shell", " async task result ... :" + result)



        isLoop == false
        while (isLoop) {
            isLoop = isRepeat
            p0.first()?.forEach {
                it.takeIf { it1 -> return@takeIf it1.isNotEmpty() }
                    ?.forEachIndexed { index, scripts ->


                        scripts.converToShell { shell, script ->
                            Log.i("script", "=== script line := $script")
                            fun executeScript(shell: Shell?, script: String?) {
                                when (shell) {
                                    Shell.GOTO -> {
                                        script?.apply {
                                            val url = this.split(" ", ignoreCase = true).last()
                                            gotoChrome(url) { isSuccess, message ->
                                                if (!isSuccess) {
                                                    onCancelTask()
                                                    errorMessage = Error(
                                                        message = message,
                                                        nScript = this,
                                                        nGroup = ""
                                                    )
                                                }
                                            }
                                            AdbUtil.sudoForResult("start apptracking")
                                        }
                                    }
                                    Shell.CLEAR -> {
                                        script?.apply {
                                            val number = this.split(" ", ignoreCase = true).last()
                                            var scriptClear = shell.value
                                            for (i in number.toInt() downTo 0) {
                                                scriptClear += " " + 67
                                            }
                                            AdbUtil.sudoForResult(
                                                scriptClear ?: ""
                                            ) { isSuccess, error ->
                                                if (!isSuccess) {
                                                    onCancelTask()
                                                    errorMessage = Error(
                                                        message = error,
                                                        nScript = this,
                                                        nGroup = ""
                                                    )
                                                }
                                            }
                                        }
                                    }
                                    Shell.START -> {
                                        script?.split(" ", ignoreCase = true)?.lastOrNull()?.apply {
                                            AdbUtil.startApp(this) { isSuccess, error ->
                                                if (!isSuccess) {
                                                    onCancelTask()
                                                    errorMessage = Error(
                                                        message = error,
                                                        nScript = this,
                                                        nGroup = ""
                                                    )
                                                }
                                            }
                                        }
                                    }
                                    Shell.KEYBOARD -> {
                                        script?.split(" ", ignoreCase = true)?.lastOrNull()
                                            ?.takeIf {
                                                return@takeIf "on off".contains(
                                                    it,
                                                    ignoreCase = true
                                                )
                                            }?.apply {
                                                if (this.contains("on", ignoreCase = true)) {
                                                    AdbUtil.setKeyBoard(true) { isSuccess, error ->
                                                        if (!isSuccess) {
                                                            onCancelTask()
                                                            errorMessage = Error(
                                                                message = error,
                                                                nScript = this,
                                                                nGroup = ""
                                                            )
                                                        }
                                                    }
                                                } else {
                                                    AdbUtil.setKeyBoard(false) { isSuccess, error ->
                                                        if (!isSuccess) {
                                                            onCancelTask()
                                                            errorMessage = Error(
                                                                message = error,
                                                                nScript = this,
                                                                nGroup = ""
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                    }
                                    Shell.SWIPE -> {
                                        script?.apply {
                                            val pattern = """\w+""".toRegex()
                                            val words =
                                                pattern.findAll(this).map { it.value }.toList()
                                            words.takeIf { return@takeIf it.isNotEmpty() }?.apply {
                                                Log.i("===", "=== swipe size :=" + words)
                                                if (size > 7) {
                                                    var scriptNew = ""
                                                    this.forEachIndexed { index, script ->
                                                        if (index <= 6) {
                                                            scriptNew += "$script "
                                                        }
                                                    }
                                                    fun String.detectRandom(): Boolean {
                                                        return this.contains("_")
                                                    }

                                                    fun Int.repeat() {
                                                        this.apply {
                                                            for (i in this downTo 1) {
                                                                AdbUtil.sudoForResult(scriptNew) { _, _ -> }
                                                                AdbUtil.sudoForResult("sleep 1") { isSuccess, error ->
                                                                    if (!isSuccess) {
                                                                        onCancelTask()
                                                                        errorMessage = Error(
                                                                            message = error,
                                                                            nScript = script,
                                                                            nGroup = ""
                                                                        )
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                    this[7].apply {
                                                        if (detectRandom()) {
                                                            val randomsMin =
                                                                split("_").firstOrNull()?.toInt()
                                                                    ?: 0
                                                            val randomsMax =
                                                                split("_").lastOrNull()?.toInt()
                                                                    ?: 0
                                                            (randomsMin..randomsMax).random()
                                                                .repeat()
                                                        } else {
                                                            this.toInt().repeat()
                                                        }
                                                    }
                                                } else {
                                                    AdbUtil.sudoForResult(script) { isSuccess, error ->
                                                        if (!isSuccess) {
                                                            onCancelTask()
                                                            errorMessage = Error(
                                                                message = error,
                                                                nScript = script,
                                                                nGroup = ""
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    Shell.LOOP -> {
                                        isLoop = true
                                    }
                                    Shell.SCREEN_SHOT -> {
                                        script?.apply {
                                            AdbUtil.screenShot { isSuccess, error ->
                                                errorMessage = Error(
                                                    message = error,
                                                    nScript = this,
                                                    nGroup = ""
                                                )

                                            }?.apply {
                                                //save file name temporary
                                                saveBitmap(this, "temporary.png")
                                            }
                                        }
                                    }

                                    Shell.REST_API_POST -> {
                                        script?.apply {
                                            val urlServer = "http://10.0.1.15:3600/upload-avatar"
                                            // Upload file
                                            ResApi.uploadFile(urlServer) { isSuccess, error ->
                                                if (!isSuccess) {
                                                    onCancelTask()
                                                    errorMessage = Error(
                                                        message = error,
                                                        nScript = this,
                                                        nGroup = ""
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    Shell.LONG_PRESS -> {
                                        script?.apply {
                                            val pattern = """\w+""".toRegex()
                                            val words =
                                                pattern.findAll(this).map { it.value }.toList()
                                            var newScript = ""
                                            if (words.size >= 4) {
                                                newScript =
                                                    words[0] + " " + words[1] + " " + words[2] + " " + words[3] + " " + words[2] + " " + words[3] + " 3000"
                                            }
                                            Log.i(
                                                "===",
                                                "=== script new:" + script + "=== shell :" + shell + "==" + newScript
                                            )
                                            AdbUtil.sudoForResult(
                                                newScript
                                            ) { isSuccess, error ->
                                                if (!isSuccess) {
                                                    onCancelTask()
                                                    errorMessage = Error(
                                                        message = error,
                                                        nScript = "",
                                                        nGroup = ""
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    else -> {
                                        AdbUtil.sudoForResult(
                                            script ?: ""
                                        ) { isSuccess, error ->
                                            if (!isSuccess) {
                                                onCancelTask()
                                                errorMessage = Error(
                                                    message = error,
                                                    nScript = "",
                                                    nGroup = ""
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                            executeScript(shell, script)
                        }


                    }
            }
        }
        return ""
    }

    class ResApi {

        companion object {
            lateinit var result: (isSuccess: Boolean, error: String) -> Unit

            fun uploadFile(paths: String, result: (isSuccess: Boolean, error: String) -> Unit) {
                this.result = result
                uploadFile(paths)
            }


            @JvmStatic
            fun uploadFile(paths: String) {
                // val pathToOurFile = "/download/image.png" //File(pathToOurFile)


                val root = getExternalStorageDirectory()
                val dir = File(root.getAbsolutePath() + "/download/app_tracking")

                val lineEnd = "\r\n"
                val twoHyphens = "--"
                val boundary = "*****"
                var bytesRead: Int
                var bytesAvailable: Int
                var bufferSize: Int
                val buffer: ByteArray
                val maxBufferSize = 1 * 1024 * 1024
                try {
                    val fileInputStream = FileInputStream(File(dir, "temporary.png"))
                    Log.i("===", "==== size :===" + fileInputStream.available())
                    val url = URL(paths)
                    val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
                    with(connection) {
                        doInput = true
                        doOutput = true
                        useCaches = false
                        // Set HTTP method to POST.
                        requestMethod = "POST"
                        setRequestProperty("Connection", "Keep-Alive")
                        setRequestProperty("Content-Type", "multipart/form-data;boundary=$boundary")
                        connectTimeout = 5 * 60 * 1000 // set timeout : 5 minutes
                    }
                    // Allow Inputs &amp; Outputs.
                    val outputStream = DataOutputStream(connection.outputStream)
                    with(outputStream) {
                        writeBytes(twoHyphens + boundary + lineEnd)
                        writeBytes(
                            "Content-Disposition: form-data; name=\"avatar\";filename=\"${File(
                                dir,
                                "image.png"
                            )}\"$lineEnd"
                        )
                        writeBytes(lineEnd)
                    }
                    bytesAvailable = fileInputStream.available()
                    bufferSize = bytesAvailable.coerceAtMost(maxBufferSize)
                    buffer = ByteArray(bufferSize)
                    // Read file
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize)
                    while (bytesRead > 0) {
                        outputStream.write(buffer, 0, bufferSize)
                        bytesAvailable = fileInputStream.available()
                        bufferSize = bytesAvailable.coerceAtMost(maxBufferSize)
                        bytesRead = fileInputStream.read(buffer, 0, bufferSize)
                    }
                    outputStream.writeBytes(lineEnd)
                    outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd)
                    if (connection.responseCode == 200) {
                        if (this::result.isInitialized) {
                            result(true, connection.responseMessage)
                        }
                    }
                    fileInputStream.close()
                    outputStream.flush()
                    outputStream.close()
                } catch (ex: Exception) {
                    if (this::result.isInitialized) {
                        result(false, ex.message ?: "error message")
                    }
                }
            }
        }
    }


    private fun onCancelTask() {
        onCancelled()
        AdbUtil.startApp("apptracking")
    }

    override fun onPostExecute(result: String) {
        Log.i("script", "= end :=")
    }
}

class Error(var message: String = "", var nScript: String = "", nGroup: String = "")


fun gotoChrome(act: AppCompatActivity, audioLink: String) {

    fun isValid(content: String): Boolean {
        val REGEX = "\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]"
        val p = Pattern.compile(REGEX, Pattern.CASE_INSENSITIVE)
        val m = p.matcher(content)
        return m.find()
    }
    act.startActivity(
        (Intent(
            Intent.ACTION_VIEW,
            Uri.parse(audioLink)
        )).setPackage("com.android.chrome")
    )
}

fun gotoChrome(
    url: String,
    result: (isSuccess: Boolean, message: String) -> Unit
) {
    fun isValid(content: String): Boolean {
        val REGEX = "\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]"
        val p = Pattern.compile(REGEX, Pattern.CASE_INSENSITIVE)
        val m = p.matcher(content)
        return m.find()
    }

    if (isValid(url)) {
        AdbUtil.sudoForResult(
            "am start \\\n" +
                    "        -n com.android.chrome/com.google.android.apps.chrome.Main \\\n" +
                    "        -a android.intent.action.VIEW " + url, result = result
        )
    } else {
        result(false, "url invalid format")
    }
}


fun saveBitmap(finalBitmap: Bitmap, fileName: String) {
    fun writeFile() {
        val root = getExternalStorageDirectory()
        val dir = File(root.getAbsolutePath() + "/download/app_tracking")
        dir.mkdirs()
        val file = File(dir, fileName)
        if (file.exists()) {
            file.delete()
        }
        try {
            val out = FileOutputStream(file)
            finalBitmap.compress(
                Bitmap.CompressFormat.PNG,
                100,
                out
            ) // 90 refers to quality of image
            out.flush()
            out.close()
        } catch (e: Exception) {
            e.printStackTrace()
            Log.i("===", "=== error meesage :" + e.message)
        }
    }
    checkExternalMedia { _, writable ->
        if (writable) {
            writeFile()
        }
    }
}


/** Method to check whether external media available and writable. This is adapted from
http://developer.android.com/guide/topics/data/data-storage.html#filesExternal */
private fun checkExternalMedia(result: (readable: Boolean, writable: Boolean) -> Unit) {
    var mExternalStorageAvailable = false
    var mExternalStorageWriteable = false
    val state = Environment.getExternalStorageState();

    if (Environment.MEDIA_MOUNTED.equals(state)) {
        // Can read and write the media
        mExternalStorageAvailable = true
        mExternalStorageWriteable = true
    } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
        // Can only read the media
        mExternalStorageAvailable = true
        mExternalStorageWriteable = false
    } else {
        // Can't read or write
        mExternalStorageAvailable = false
        mExternalStorageWriteable = false
    }
    result(mExternalStorageAvailable, mExternalStorageWriteable)
}


