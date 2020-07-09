package com.example.apptracking.utils

import android.util.Log
import com.example.apptracking.view.main.Shell


fun checkValid(
    listScript: MutableList<List<String>>?,
    listGroup: MutableList<String>?,
    result: (isSuccess: Boolean, listError: MutableList<ScriptError>?) -> Unit
): Boolean {
    val listError: MutableList<ScriptError>? = mutableListOf()
    listScript?.takeIf { it.isNotEmpty() }?.forEachIndexed { _index, _list ->
        _list.forEach { _script ->
            _script.isValid(listGroup?.get(_index))?.apply {
                //Log.i("===", "=== error :==" + this.script)
                listError?.add(this)
            }
        }
    }
    return if (listError?.isEmpty() == true) {
        result(true, listError)
        true
    } else {
        result(false, listError)
        false
    }
}


// tap 100 200
fun String.isValid(group: String?): ScriptError? {
    var errorScript: ScriptError? = null
    //Log.i("===", "Script : $this")
    val splitScript = this.trim().split(" ", ignoreCase = true)
    when (this.getShell()) {
        Shell.TAP -> {

            if (!((splitScript.size == 3) and splitScript.isInt(1) and splitScript.isInt(2))) {
                errorScript = ScriptError(group, this)
            }
        }
        Shell.TEXT -> {
            if (splitScript.size != 2) {
                errorScript = ScriptError(group, this)
            }
        }
        Shell.SLEEP -> {
            if (!((splitScript.size == 2) and (splitScript.isInt(1)))) {
                errorScript = ScriptError(group, this)
            }
        }
        Shell.SWIPE -> {
            if (!(((splitScript.size == 5) or (splitScript.size == 6)) and splitScript.isInt(1) and splitScript.isInt(
                    2
                ) and splitScript.isInt(3) and splitScript.isInt(4) and splitScript.isRepeat(5))
            ) {
                errorScript = ScriptError(group, this)
            }
        }
        Shell.SCREEN_SHOT -> {
            if (!((splitScript.size == 2) or (splitScript.size == 1))) {
                errorScript = ScriptError(group, this)
            }
        }
        Shell.PERMISSION -> {
            if (!(splitScript.size == 2)) {
                errorScript = ScriptError(group, this)
            }
        }
        Shell.CLEAR -> {
            if (!((splitScript.size == 2) and splitScript.isInt(1))) {
                errorScript = ScriptError(group, this)
            }
        }
        Shell.LONG_PRESS -> {
            if (!(((splitScript.size == 3) or (splitScript.size == 4)) and splitScript.isInt(1) and splitScript.isInt(
                    2
                ) and splitScript.isIntOrNull(3))
            ) {
                errorScript = ScriptError(group, this)
            }
        }
        Shell.ENTER -> {
            if (!(splitScript.size == 1)) {
                errorScript = ScriptError(group, this)
            }
        }
        Shell.KEY_EVENT -> {
            if (!((splitScript.size == 2) or (splitScript.size == 3))) {
                errorScript = ScriptError(group, this)
            }
        }
        Shell.START -> {
            if (!(splitScript.size == 2)) {
                errorScript = ScriptError(group, this)
            }
        }
        Shell.KEYBOARD -> {
            if (!((splitScript.size == 2) and splitScript.isOnOrOff(1))) {
                errorScript = ScriptError(group, this)
            }
        }
        Shell.LOOP -> {
            if (!(splitScript.size == 1)) {
                errorScript = ScriptError(group, this)
            }
        }
        Shell.COPPY -> {
            if (!(splitScript.size == 1)) {
                errorScript = ScriptError(group, this)
            }

        }
        Shell.PASTE -> {
            if (!(splitScript.size == 1)) {
                errorScript = ScriptError(group, this)
            }
        }
        Shell.REST_API_POST -> {
            errorScript = ScriptError(group, this)
        }
        Shell.INPUT -> {
            errorScript = ScriptError(group, this)
        }
        else -> {
            errorScript = ScriptError(group, this)
        }
    }
    return errorScript
}


private fun List<String>?.isOnOrOff(index: Int): Boolean {
    return (this?.getOrNull(index)?.trim() == "on") or (this?.getOrNull(index)?.trim() == "off")
}

private fun List<String>?.isInt(index: Int): Boolean {
    return this?.getOrNull(index)?.toIntOrNull() != null
}

private fun List<String>?.isIntOrNull(index: Int): Boolean {
    return if (this?.getOrNull(index) != null) {
        this.getOrNull(index)?.toIntOrNull() != null
    } else {
        true
    }
}

private fun List<String>?.isRepeat(index: Int): Boolean {
    val script = this?.getOrNull(index)
    return if (script != null) {
        (script.split("_").size == 2) or (script.toIntOrNull() != null)
    } else {
        true
    }
}


private fun String.getShell(): Shell? {
    val shell = trim().split(" ", ignoreCase = true).firstOrNull() ?: ""
    Log.i("thanh", "shell = " + shell)
    return Shell.values().filter {
        return@filter it.key.equals(
            shell,
            ignoreCase = true
        )
    }.firstOrNull()
}


class ScriptError(var group: String? = "", var script: String? = "") {
    override fun toString(): String {
        return "Group : $group script : $script"
    }
}