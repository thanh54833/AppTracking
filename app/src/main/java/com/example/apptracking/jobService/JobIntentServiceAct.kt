package com.example.apptracking.jobService

import androidx.databinding.DataBindingUtil
import com.example.apptracking.R
import com.example.apptracking.base.AbsBackActivity
import com.example.apptracking.databinding.JobIntentServiceBinding

class JobIntentServiceAct : AbsBackActivity() {

    lateinit var binding: JobIntentServiceBinding

    override fun initializeBindingViewModel() {
        binding =
            DataBindingUtil.setContentView(this@JobIntentServiceAct, R.layout.job_intent_service)
    }

    override fun initializeLayout() {
        initView()
    }

    private fun initView() {


    }
}