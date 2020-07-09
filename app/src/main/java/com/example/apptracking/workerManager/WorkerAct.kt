package com.example.apptracking.workerManager

import android.util.Log
import androidx.work.WorkManager
import com.example.apptracking.adb.AdbUtil
import com.example.apptracking.base.AbsBackActivity
import com.example.apptracking.view.AsyncTaskRunner
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader


class WorkerAct : AbsBackActivity() {
    override fun initializeBindingViewModel() {

        initWokerManage()


    }

    var tasks: AsyncTaskRunner? = null

    private fun initWokerManage() {
        val mWorkManager = WorkManager.getInstance()

        val listScript = listOf(
            listOf(
                "start hahalolo",
                "sleep 2",
                "longpress 200 420",
                "text pham54833@gmail.com",
                "sleep 1",
                "longpress 200 510",
                "text \"Lumia520\"",
                "enter",
                "enter",
                "sleep 2",
                "tap 632 1162",
                "swipe 300 700 300 400 2000 3",
                "tap 170 1170",
                "loop"
            )
        )

        /*val mUUID = NotificationWorker.requestWorker(listScript, mWorkManager)
        mWorkManager.getWorkInfoByIdLiveData(mUUID).observe(this@WorkerAct, Observer {

           Log.i("===", "=== worker manager :== " + it.state)

        })*/


        /*val result =
            AdbUtil.sudoForResult("input swipe 100 200 100 ; echo $?") { isSuccess, error ->
                Log.i("===", "=== isSuccess : " + isSuccess + " === " + error)
            }
        Log.i("====", "=== result : " + result)*/


        //Log.i("shell", "=== start task ")
        //val tasks = AsyncTaskRunner()
        //tasks.execute(listOf())


        val scriptsTap =
            listOf(listOf("tap 100 200", " tap ", " tap 100 200  ", "tap thanh thanh", " dasda"))
        val scriptsText = listOf(listOf(" text "))
        val scriptsSleep = listOf(listOf("sleep 1000", "sleep ", "sleep 100 200"))

        val scriptSwipe = listOf(
            listOf(
                "swipe 100 200 100 200",
                "swipe ",
                "swipe thanh ",
                "swipe 100 200 ",
                "swipe 100 200 200",
                "swipe 100 200 200 100_200",
                "swipe 100 200 100 200",
                "swipe 100 200 100 200 10_20",
                "swipe 100 200 100 200 10_20_"
            )
        )


        val scriptSreenshot =
            listOf(listOf("sreenshot", "sreenshot png", "sreenshot fas fasd fasdfas"))


        val scriptLongPress = listOf(
            listOf(
                "longpress 100 200 200",
                "longpress 100 200 ",

                "longpress 100 200 100 200",
                "longpress ",
                "longpress thanh ",
                "longpress 100 200 200 100_200",
                "longpress 100 200 100 200",
                "longpress 100 200 100 200 10_20",
                "longpress 100 200 100 200 10_20_"
            )
        )
        val scriptKEYBOARD = listOf(
            listOf(
                "keyboard on",
                "keyboard off ",
                "keyboard ",
                "keyboard in"
            )
        )
        val groups = listOf("group 1")

        /*checkValid(mutableListOf(listOf("", "")), groups.toMutableList()) { _isSuccess, _List ->
            Log.i(
                "===",
                "===  error : " + _isSuccess + "===" + Gson().toJson(_List) + "===" + _List?.size
            )
        }*/


        /*val script: List<String> = listOf("start hahalolo")//, "sleep 2", "swipe 100 800"
        val scripts: List<List<String>> = listOf(script)//, script2
        tasks = AsyncTaskRunner()
        executeScripts(scripts)*/

        /*Log.i(
            "===",
            "==== result :== " + AdbUtil.sudoForResult("ls") { _, _error ->
                Log.i("===", "== error :==" + _error)
            })*/


        AdbUtil


        Log.i("===", "=== shell :== " + AdbUtil.sudoForResult("ls -la") { _, _Error ->
            Log.i("===", "=== error :==" + _Error)
        })


    }

    private fun executeScripts(scripts: List<List<String>>?) {
        scripts?.takeIf { return@takeIf it.isNotEmpty() }?.apply {
            tasks?.execute(this)
        }
    }


    override fun initializeLayout() {

    }

}

fun executeCommandLine(commandLine: String?): String? {
    return try {
        val process: Process = Runtime.getRuntime().exec(commandLine)
        val reader = BufferedReader(
            InputStreamReader(process.inputStream)
        )
        var read: String? = ""
        val output = StringBuilder()
        while (reader.readLine().also({ read = it }) != null) {
            output.append(read)
            output.append("\n")
            Log.d("executed command ", output.toString())
        }
        reader.close()
        process.waitFor()
        output.toString()
    } catch (e: IOException) {
        throw RuntimeException(e)
    } catch (e: InterruptedException) {
        throw RuntimeException(e)
    }
}