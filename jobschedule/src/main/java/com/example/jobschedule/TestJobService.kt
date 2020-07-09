package com.example.jobschedule

import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi

/**
 * JobService to be scheduled by the JobScheduler.
 * start another service
 */
/*class TestJobService : JobService() {

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onStartJob(params: JobParameters): Boolean {
        val service = Intent(applicationContext, LocalWordService::class.java)
        applicationContext.startService(service)
        Util.scheduleJob(applicationContext) // reschedule the job
        return true
    }

    override fun onStopJob(params: JobParameters): Boolean {
        return true
    }

    companion object {
        private val TAG = "SyncService"
    }

}*/