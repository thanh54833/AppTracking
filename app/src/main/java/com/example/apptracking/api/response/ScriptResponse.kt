package com.example.apptracking.api.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class ScriptResponse {

    @SerializedName("success")
    @Expose
    val success: Boolean? = null
    @SerializedName("message")
    @Expose
    val message: String? = null
    @SerializedName("status_code")
    @Expose
    val statusCode: Int? = null
    @SerializedName("jsonapi")
    @Expose
    val jsonapi: Jsonapi? = null
    @SerializedName("data")
    @Expose
    val data: Data? = null
    @SerializedName("meta")
    @Expose
    val meta: Meta? = null
}


class Data {
    @SerializedName("list_group")
    @Expose
    var list_group: List<String>? = null

    @SerializedName("list_script")
    @Expose
    var list_script: List<String>? = null

    @SerializedName("scripts")
    @Expose
    var scripts: List<List<String>>? = null
}

class Jsonapi {
    @SerializedName("version")
    @Expose
    val version: String? = null
}

class Meta {
    @SerializedName("copyright")
    @Expose
    val copyright: String? = null
    @SerializedName("authors")
    @Expose
    val authors: List<Any>? = null

}

class UploadReponse {
    @SerializedName("message")
    @Expose
    var message: String? = null
}
