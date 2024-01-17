package com.nabil.submission1_appstory.Local

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.nabil.submission1_appstory.Data.DetailCeritaResponse
import com.nabil.submission1_appstory.Data.GetResponse
import com.nabil.submission1_appstory.Data.ListStory
import com.nabil.submission1_appstory.Data.Login
import com.nabil.submission1_appstory.Data.LoginResponse
import com.nabil.submission1_appstory.Data.Register
import com.nabil.submission1_appstory.Data.StoryResponse
import com.nabil.submission1_appstory.Retro.ApiService
import com.nabil.submission1_appstory.StoryPagingSource
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.HttpException

class Repository private constructor(
    private val apiService: ApiService
){

    suspend fun signupUser(
        nama: String,
        password: String,
        email: String
    ): Outcome<GetResponse> {
        return try {
            val response = apiService.signup(Register(nama, email, password))
            Outcome.Success(response)
        } catch (e: HttpException) {
            val error = e.response()?.errorBody()?.string()

            val jsonObject = JSONObject(error!!)
            val errorMessage = jsonObject.getString("message")
            Outcome.Error(errorMessage)
        } catch (e: Exception){
            Outcome.Error(e.message.toString())
        }
    }

    fun loginUser(email: String, password: String): LiveData<Outcome<LoginResponse>> = liveData {
        emit(Outcome.Loading)
        try {
            val response = apiService.login(Login(email, password))
            emit(Outcome.Success(response))
        } catch (e: Exception){
            emit(Outcome.Error(e.message.toString()))
        }
    }

    fun gainStory(): LiveData<PagingData<ListStory>>{
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {StoryPagingSource(apiService)}
        ).liveData
    }

    fun gainCeritaDetail(token: String, id: String): LiveData<Outcome<DetailCeritaResponse>> =
        liveData {
            emit(Outcome.Loading)
            try {
                val respon = apiService.GainCeritaDetail(token, id)
                emit(Outcome.Success(respon))
            }catch (e: Exception){
                emit(Outcome.Error(e.message.toString()))
            }
        }

    fun savePreferences(token: String, context: Context){
        val settingPreferences = SettingPref(context)
        return settingPreferences.putUser(token)
    }

    fun gainPreferences(context: Context): String?{
        val settingPreferences = SettingPref(context)
        return settingPreferences.gainUser()
    }

    suspend fun uploadStory(
        token: String,
        image: MultipartBody.Part,
        desc: RequestBody
    ): Outcome<GetResponse> {
        return try {
            val response = apiService.postStory(token, image, desc)
            Outcome.Success(response)
        }  catch (e: Exception) {
            Outcome.Error(e.message.toString())
        }
    }

    fun gainStoryLocation(token: String): LiveData<Outcome<StoryResponse>> = liveData {
        emit(Outcome.Loading)
        try {
            val response = apiService.getStoriesWithLocation(token, 1)
            emit(Outcome.Success(response))
        }catch (e:Exception){
            emit(Outcome.Error(e.message.toString()))
        }
    }


    companion object {
        @Volatile
        private var instance: Repository? = null
        fun getInstance(
            apiService: ApiService
        ): Repository =
            instance ?: synchronized(this) {
                instance ?: Repository(apiService)
            }.also { instance = it }
    }
}