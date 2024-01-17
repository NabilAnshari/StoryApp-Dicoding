package com.nabil.submission1_appstory.Model

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.nabil.submission1_appstory.Data.GetResponse
import com.nabil.submission1_appstory.Data.ListStory
import com.nabil.submission1_appstory.Local.Repository
import kotlinx.coroutines.launch
import com.nabil.submission1_appstory.Local.Outcome
import okhttp3.MultipartBody
import okhttp3.RequestBody

class MainViewModel(private val mainRepository: Repository) : ViewModel(){

    private val liveDataOutcome = MutableLiveData<Outcome<GetResponse>>()
    private val token = MutableLiveData<String?>()

    fun signupNewUser(
        nama: String,
        password: String,
        email: String
    ): LiveData<Outcome<GetResponse>> {
        viewModelScope.launch {
            val result = mainRepository.signupUser(nama, password, email)
            liveDataOutcome.value = result
        }
        return liveDataOutcome
    }

    fun loginUser(
        email: String, password: String
    ) = mainRepository.loginUser(email, password)

    val story : LiveData<PagingData<ListStory>> = mainRepository.gainStory().cachedIn(viewModelScope)

    fun gainPreference(
        context: Context
    ): LiveData<String?> {
        val DataToken = mainRepository.gainPreferences(context)
        token.value = DataToken
        return token
    }

    fun setPreference(
        token: String, context: Context
    ) = mainRepository.savePreferences(token, context)

    fun getCeritaDetail(
        token: String, id: String
    ) = mainRepository.gainCeritaDetail(token, id)

    fun uploadStory(
        token: String,
        image: MultipartBody.Part,
        desc: RequestBody
    ) : LiveData<Outcome<GetResponse>> {
        viewModelScope.launch {
            val output = mainRepository.uploadStory(token, image, desc)
            liveDataOutcome.value = output
        }
        return liveDataOutcome
    }
}