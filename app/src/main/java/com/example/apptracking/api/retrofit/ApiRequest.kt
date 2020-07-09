package com.example.apptracking.api.retrofit

import com.example.apptracking.api.response.ScriptResponse
import com.example.apptracking.api.response.UploadReponse
import com.example.apptracking.api.viewmodel.BodyUploadFile
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiRequest {


    /*
    * @param  imei : number imei of device
    * IS5502
    * */
    @GET("api/v1.5/device/script")
    fun getScripts(
        @Query("imei") imei: String
    ): Call<ScriptResponse>


    @POST("test")
    fun uploadFile(@Body body: BodyUploadFile): Call<UploadReponse>

}