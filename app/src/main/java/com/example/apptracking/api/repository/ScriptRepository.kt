package com.example.apptracking.api.repository


import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.apptracking.api.response.ScriptResponse
import com.example.apptracking.api.response.UploadReponse
import com.example.apptracking.api.retrofit.ApiRequest
import com.example.apptracking.api.retrofit.RetrofitRequest
import com.example.apptracking.api.viewmodel.BodyUploadFile
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ScriptRepository {
    private val apiRequest: ApiRequest? =
        RetrofitRequest.retrofitInstance?.create(ApiRequest::class.java)

    fun getScripts(query: String): LiveData<ScriptResponse> {
        val data = MutableLiveData<ScriptResponse>()
        apiRequest?.getScripts(query)?.enqueue(object : Callback<ScriptResponse> {
            override fun onResponse(
                call: Call<ScriptResponse>,
                response: Response<ScriptResponse>
            ) {
                if (response.body() != null) {
                    data.value = response.body()
                }
                Log.v("https", "->" + call.request().url)
            }

            override fun onFailure(call: Call<ScriptResponse>, t: Throwable) {
                data.value = null
                Log.v("https", "Fail ->" + call.request().url)
            }
        })
        return data
    }

    fun uploadFile(body: BodyUploadFile): LiveData<UploadReponse> {
        val data = MutableLiveData<UploadReponse>()
        apiRequest?.uploadFile(body)?.enqueue(object : Callback<UploadReponse> {
            override fun onResponse(
                call: Call<UploadReponse>,
                response: Response<UploadReponse>
            ) {
                if (response.body() != null) {
                    data.value = response.body()
                }
                Log.v("https", "->" + call.request().url)
            }

            override fun onFailure(call: Call<UploadReponse>, t: Throwable) {
                data.value = null
                Log.v("https", "Fail ->" + call.request().url)
            }
        })
        return data
    }

}
