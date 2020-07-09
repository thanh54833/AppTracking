package com.example.apptracking.workerManager

import android.content.Context
import android.util.Log
import androidx.work.*
import com.example.apptracking.adb.AdbUtil
import com.example.apptracking.utils.converToShell
import com.example.apptracking.utils.deserializeFromJson
import com.example.apptracking.utils.serializeToJson
import com.example.apptracking.view.AsyncTaskRunner
import com.example.apptracking.view.gotoChrome
import com.example.apptracking.view.main.Shell
import com.example.apptracking.view.saveBitmap
import java.util.*


@Suppress("CAST_NEVER_SUCCEEDS")
class NotificationWorker(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {

    internal var listScript: List<List<String>> = mutableListOf()
    internal var isRetry: Boolean = false


    override fun doWork(): Result {
        isRetry = false
        //Log.i("===", "=== do work !!")
        listScript = deserializeFromJson(inputData.getString(SCRIPT) ?: "")

        //Log.i("===", "=== list script :" + Gson().toJson(listScript))

        listScript.takeIf { it.isNotEmpty() }?.forEach {
            it.forEach { scriptss ->
                scriptss.converToShell { shell, script ->
                    Log.i("===", "=== script : " + script)
                    when (shell) {
                        Shell.LOOP -> {
                            isRetry = true
                        }
                        else -> {
                            executeScript(shell, script)
                        }
                    }

                }
            }
        }

        /*AdbUtil.sudoForResult("input swipe 100 200 200 300 1000") { isSuccess, error ->
            Log.i("===", "=== excuter :==" + isSuccess + "==" + error)
        }

        AdbUtil.sudoForResult("input swipe 200 200 200 300 1000") { isSuccess, error ->
            Log.i("===", "=== excuter :==" + isSuccess + "==" + error)
        }

        AdbUtil.sudoForResult("input swipe 300 200 200 300 1000") { isSuccess, error ->
            Log.i("===", "=== excuter :==" + isSuccess + "==" + error)
        }

        AdbUtil.sudoForResult("input swipe 400 200 200 300 1000") { isSuccess, error ->
            Log.i("===", "=== excuter :==" + isSuccess + "==" + error)
        }*/


        Log.i("===","=== isretry :==="+isRetry)

        return if (isRetry) {
            Result.retry()
        } else {
            Result.success()
        }

    }

    companion object {
        var WORK_RESULT: String = "work_result"
        var MESSAGE_STATUS = "message_status"
        var SCRIPT = "SCRIPT"
        var TEXT = "TEXT"

        fun requestWorker(groups: List<List<String>>, mWorkManager: WorkManager): UUID {
            fun getData(): Data {
                val data = Data.Builder()
                data.putString(SCRIPT, serializeToJson(groups))
                data.putString(TEXT, "thanh")
                return data.build()
            }

            //Log.i("===", "=== get data () " + Gson().toJson(getData()) + "==" + groups)

            val request =
                OneTimeWorkRequest.Builder(NotificationWorker::class.java).setInputData(getData())
                    .build()
            mWorkManager.enqueue(request)
            return request.id
        }
    }

}

fun executeScript(shell: Shell?, script: String?) {

    Log.i("script", "=== script line := $script")
    when (shell) {
        Shell.GOTO -> {
            script?.apply {
                val url = this.split(" ", ignoreCase = true).last()
                gotoChrome(url) { isSuccess, message ->

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

                }
            }
        }
        Shell.START -> {
            script?.split(" ", ignoreCase = true)?.lastOrNull()?.apply {
                AdbUtil.startApp(this) { isSuccess, error ->

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

                        }
                    } else {
                        AdbUtil.setKeyBoard(false) { isSuccess, error ->

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

                        }
                    }
                }
            }
        }
        Shell.LOOP -> {
            //isLoop = true
        }
        Shell.SCREEN_SHOT -> {
            script?.apply {
                AdbUtil.screenShot { isSuccess, error ->

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
                AsyncTaskRunner.ResApi.uploadFile(urlServer) { isSuccess, error ->


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


                }
            }
        }

        else -> {
            AdbUtil.sudoForResult(
                script ?: ""
            ) { isSuccess, error ->


            }
        }

    }


}






