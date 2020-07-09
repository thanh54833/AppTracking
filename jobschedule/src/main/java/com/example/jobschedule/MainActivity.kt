package com.example.jobschedule

import android.Manifest.permission.READ_PHONE_STATE
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.telephony.TelephonyManager
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat


@Suppress("DEPRECATED_IDENTITY_EQUALS")
class MainActivity : AppCompatActivity() {


    companion object {
        private const val READ_PHONE_STATE_NUMBERR = 1000
    }

    @SuppressLint("HardwareIds")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /*getIMEI {

        }*/

        //Log.i("===", "== get IMEI divece :==" + getIMEI())

        /* Log.i(
             "===",
             "=== check permission :==" + (ActivityCompat.checkSelfPermission(
                 this,
                 READ_PHONE_STATE
             ) !== PackageManager.PERMISSION_GRANTED)
         )*/


        Log.i("===", "=== get pageket name :==" + applicationContext.packageName)


    }

    @SuppressLint("HardwareIds")
    fun getIMEI(result: (IMEI: String) -> Unit): String? {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val permissions = arrayOf<String>(READ_PHONE_STATE)
            if (ActivityCompat.checkSelfPermission(
                    this,
                    READ_PHONE_STATE
                ) !== PackageManager.PERMISSION_GRANTED
            ) {
                //requestPermissions(permissions, READ_PHONE_STATE_NUMBERR)
            }
        } else {
            try {
                val telephonyManager =
                    getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                if (ActivityCompat.checkSelfPermission(
                        this,
                        READ_PHONE_STATE
                    ) !== PackageManager.PERMISSION_GRANTED
                ) {
                    return null
                }
                return telephonyManager.deviceId
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return null
    }


    /* @SuppressLint("HardwareIds")
     override fun onRequestPermissionsResult(requestCode: Int, @NonNull permissions: Array<String>, @NonNull grantResults: IntArray) {
         when (requestCode) {
             READ_PHONE_STATE_NUMBERR -> {
                 if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                     getIMEI()
                 }
             }
         }
     }*/
}


/*jobScheduler = getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
    scheduleJob()*/

/* private fun scheduleJob() {
     val componentName = ComponentName(this, MyService::class.java)
     val jobInfo = JobInfo.Builder(JOB_ID, componentName)
     jobInfo.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
     val response = jobScheduler.schedule(jobInfo.build())

     if (response == JobScheduler.RESULT_FAILURE) {
         Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show()
     }
 }

 companion object {
     const val JOB_ID = 101

 }*/
