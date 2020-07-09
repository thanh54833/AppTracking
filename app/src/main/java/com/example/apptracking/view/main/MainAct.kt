package com.example.apptracking.view.main

import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.apptracking.R
import com.example.apptracking.api.viewmodel.ScriptViewModel
import com.example.apptracking.base.AbsBackActivity
import com.example.apptracking.databinding.MainActBinding
import com.example.apptracking.utils.AppUtils
import com.example.apptracking.view.AsyncTaskRunner


class MainAct : AbsBackActivity() {

    lateinit var binding: MainActBinding
    lateinit var viewModel: ScriptViewModel
    private var scripts: List<List<String>>? = null

    private var tasks: AsyncTaskRunner? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val builder: StrictMode.VmPolicy.Builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build());
        if (Build.VERSION.SDK_INT > 23) {
            builder.detectFileUriExposure();
        }
    }


    override fun initializeBindingViewModel() {
        binding = DataBindingUtil.setContentView(this@MainAct, R.layout.main_act)
        viewModel = ViewModelProviders.of(this).get(ScriptViewModel::class.java)
    }

    override fun initializeLayout() {
        initView()
        initObserver()
        initAction()
    }

    private fun initView() {

        getStatusDevice { isPermission, isRooted, isInternet, isKeyBroad, isChrome, error ->
            binding.permissionTv.setStatus(isPermission)
            binding.rootedTv.setStatus(isRooted)
            binding.internetTv.setStatus(isInternet)
            binding.keyboardTv.setStatus(isKeyBroad)
            binding.skipsChromeTv.setStatus(isChrome)
            binding.errorTv.setStatus(!(isPermission and isRooted and isInternet and isKeyBroad and isChrome))
            binding.messageTv.text = error
        }


    }


    fun AppCompatTextView.setStatus(isSuccess: Boolean) {
        fun styleError() {
            this.text = "False"
            this.setTextColor(ContextCompat.getColorStateList(context, R.color.status_error))
        }

        fun styleSuccess() {
            this.text = "True"
            this.setTextColor(ContextCompat.getColorStateList(context, R.color.status_success))
        }
        if (isSuccess) {
            styleSuccess()
        } else {
            styleError()
        }
    }

    private fun initAction() {
        binding.startAction.setOnClickListener {
            viewModel.imeiParam.value = AppUtils.getIMEI(context = this) {}//"IS5502"
        }

        binding.cancelLoop.setOnClickListener {
            tasks?.setIsLoop(false)
            tasks?.cancel(true)
        }
    }

    private fun initObserver() {

        viewModel.scriptsResponseLiveData.observe(this, Observer {
            it?.apply {
                if (checkSuccess(success, statusCode)) {
                    data?.scripts?.apply {
                        tasks = AsyncTaskRunner()
                        executeScripts(this)
                    }
                }
            } ?: showFail()
        })


        /*val script: List<String> = listOf(
            "start hahalolo",
            "sleep 4",
            "tap 200 400",
            "text pham54833@gmail.com",
            "tap 200 500",
            "text Lumia520",
            "enter",
            "enter",
            "sleep 4",
            "tap 150 77",
            "sleep 2",
            "text \"Tour DaLat\"",
            "sleep 3",
            "tap 180 170",
            "sleep 3",
            "swipe 350 1050 350 520 1000 4",
            "tap 100 756",
            "sleep 2",
            "tap 677 1166",
            "sleep 1",
            "tap 677 1166",
            "sleep 1",
            "tap 50 727",
            "text 0358380646",
            "tap 33 975",
            "tap 677 1166",
            "sleep 2",
            "swipe 350 1050 350 520 1000",
            "tap 70 591",
            "text \"ho chieu\"",
            "tap 70 688",
            "sleep 1",
            "tap 558 817",
            "sleep 1",
            "tap 70 775",
            "sleep 1",
            "tap 224 336",
            "tap 677 166",
            "sleep 1",
            "tap 677 166",
            "sleep 1",
            "tap 677 1166",
            "sleep 1",
            "tap 82 830 ",
            "sleep 2",
            "tap 150 331",
            "sleep 1",
            "tap 82 910",
            "text \"Dia chi\"",
            "tap 82 1010",
            "text \"thanh pho\"",
            "tap 677 1166",
            "sleep 2",
            "tap 450 835",
            "sleep 2",
            "tap 72 410",
            "text \"thanh\"",
            "tap 93 528",
            "text 4242424242424242",
            "sleep 2",
            "tap 68 637",
            "sleep 2",
            "tap 68 905",
            "text \"dia chi\"",
            "tap 603 975",
            "text \"Ho chieu\"",
            "tap 677 1166",
            "sleep 2",
            "tap 440 840",
            "sleep 2",
            "tap 70 413",
            "text thanh",
            "tap 70 520",
            "text 4242424242424242",
            "tap 72 650",
            "sleep 1",
            "tap 65 905",
            "tap 604 977",
            "tap 400 640",
            "text 123",
            "tap 677 1166",
            "sleep 2",
            "tap 548 688",
            "sleep 2",
            "tap 390 583",
            "sleep 3",
            "tap 628 1165",
            "sleep 3",
            "swipe 350 1050 350 520 1000 2",
            "tap 168 1167",
            "loop"
        )*/

        //val script: List<String> = listOf("goto http://google.com", "swipe 100 200 200 300 200 2_5")

        /*val script: List<String> = listOf(
            "goto https://test-newsfeed.hahalolo.com/auth/signin",
            "sleep 4",
            "swipe 181 434 181 434 1000",
            "sleep 2",
            "text pham54833@gmailss.com"
        )

        //"goto https://google.com","sleep 4",//"goto https://test-newsfeed.hahalolo.com/auth/signin","sleep 4", //,"text pham54833@gmail.com","longpress 126 523","text Lima520","enter","enter"
        //"longpress 200 430"
        //"sleep 4","loop"
        //val script2: List<String> = listOf("start hahalolo", "sleep 2", "swipe 100 800")

        val scripts: List<List<String>> = listOf(script)//, script2
        tasks = AsyncTaskRunner()
        executeScripts(scripts)*/

        //Log.i("===", "=== script :==" + AdbUtil.sudoForResult("input text thanh"))//fasdf ffasd
        // AdbUtil.sudoForResult("input text ss")




    }

    private fun executeScripts(scripts: List<List<String>>?) {
        scripts?.takeIf { return@takeIf it.isNotEmpty() }?.apply {
            tasks?.execute(this)
        }
    }


}


//key -> value
enum class Shell(var key: String, var value: String) {
    TAP("tap", "input tap"), // test v1
    TEXT("text", "input text"),
    SLEEP("sleep", "sleep"),
    SWIPE("swipe", "input swipe"),
    SCREEN_SHOT("sreenshot", "screenshot"),
    GOTO("goto", "gotoUrl "),
    PERMISSION("permission", "permission"),
    CLEAR("clear", "input keyevent "),
    LONG_PRESS("longpress", "input swipe"),
    ENTER("enter", "input keyevent 66"),
    KEY_EVENT("keyevent", "input keyevent"),
    START("start", "start"),
    KEYBOARD("keyboard", "keyboard"),
    LOOP("loop", "loop"),
    COPPY("copy", "input keyevent 278"),
    PASTE("paste", "input keyevent 279"),
    REST_API_POST("post", "post"),
    INPUT("", "");
}

