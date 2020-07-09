package com.example.apptracking.workerManager

import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader


class ShellExecute {
    companion object {
        @JvmStatic
        fun execute(command: String?): String {
            val output = StringBuffer()
            val p: Process
            //try {
                p = Runtime.getRuntime().exec(command)
                p.waitFor()
                val reader =
                    BufferedReader(InputStreamReader(p.inputStream))
                var line = ""
                while (reader.readLine().also { line = it } != null) {
                    output.append(line + "n")
                }
            //} catch (e: Exception) {
           //     e.printStackTrace()
             //   Log.i("===", "exception : " + e.message)
            //}
            return output.toString()
        }
    }
}