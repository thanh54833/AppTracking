package com.example.apptracking.adb

import android.graphics.Bitmap
import android.graphics.BitmapFactory

import java.io.IOException
import java.io.OutputStreamWriter

class AdbBitmap private constructor() {
    companion object {
        lateinit var result: (isSuccess: Boolean, error: String) -> Unit

    }
}