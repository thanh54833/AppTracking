package com.example.apptracking.api.viewmodel

import android.app.Application
import androidx.annotation.NonNull
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.example.apptracking.api.repository.ScriptRepository
import com.example.apptracking.api.response.ScriptResponse
import com.example.apptracking.api.response.UploadReponse

class ScriptViewModel(@NonNull application: Application) : AndroidViewModel(application) {
    private val articleRepository: ScriptRepository = ScriptRepository()

    var scriptsResponseLiveData: LiveData<ScriptResponse>
    var imeiParam = MutableLiveData<String>()

    var uploadResponseLiveData: LiveData<UploadReponse>
    var bodyUpload = MutableLiveData<BodyUploadFile>()

    init {
        scriptsResponseLiveData = Transformations.switchMap(imeiParam) { body ->
            articleRepository.getScripts(body)
        }


        uploadResponseLiveData = Transformations.switchMap(bodyUpload) { body ->
            articleRepository.uploadFile(body)
        }


    }
}

class BodyUploadFile {

}
