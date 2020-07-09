package com.example.apptracking.base

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.apptracking.config.AppConfig
import com.example.apptracking.utils.AppUtils
import com.example.apptracking.utils.NetworkUtils


abstract class AbsBackActivity : AppCompatActivity() {


    var isPermission: Boolean = false
    var isRooted: Boolean = false
    var isInternet: Boolean = false
    var isKeyBroad: Boolean = false
    var isChrome: Boolean = false

    override
    fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeBindingViewModel()
        initializeLayout()
    }

    fun getStatusDevice(result: (isPermission: Boolean, isRooted: Boolean, isInternet: Boolean, isKeyBroad: Boolean, isChrome: Boolean, error: String) -> Unit) {
        this.isRooted = AppUtils.isRootedDevice(this)
        this.isInternet = NetworkUtils.isNetworkConnected(this)

        var error: String = ""
        if (!AppConfig.checkConfig(this) { isPermission, isKeyBroad, isChrome ->
                this.isPermission = isPermission
                this.isKeyBroad = isKeyBroad
                this.isChrome = isChrome
            } or !this.isRooted or !this.isInternet) {
            if (!isPermission) {
                error += "permission -"
            }
            if (!isRooted) {
                error += "Device not rooted -"
            }
            if (!isInternet) {
                error += "No internet access -"
            }
            if (!isKeyBroad) {
                error += "hide keyboard -"
            }
            if (!isChrome) {
                error += "error kip welcome chrome -"
            }
        }
        if (TextUtils.isEmpty(error)) {
            error += "Null"
        }
        result(
            this.isPermission,
            this.isRooted,
            this.isInternet,
            this.isKeyBroad,
            this.isChrome,
            error
        )
    }

    protected abstract fun initializeBindingViewModel()
    protected abstract fun initializeLayout()

    fun gotoHome() {
        startActivity(
            (Intent(Intent.ACTION_MAIN)).addCategory(Intent.CATEGORY_HOME).setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
            )
        )
    }

    fun gotoChrome(audioLink: String) {
        startActivity(
            (Intent(
                Intent.ACTION_VIEW,
                Uri.parse(audioLink)
            )).setPackage("com.android.chrome")
        )
    }

    fun checkSuccess(isSuccess: Boolean?, statusCode: Int?): Boolean {
        val isSu = (isSuccess == true) and (statusCode == 800) || (statusCode == 801)
        if (!isSu) {
            showFail()
        }
        return isSu
    }

     fun showFail() {
        Toast.makeText(this@AbsBackActivity, "Fail request service", Toast.LENGTH_LONG).show()
    }


}