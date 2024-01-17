package com.nabil.submission1_appstory.Retro

import com.nabil.submission1_appstory.Data.DetailCeritaResponse
import com.nabil.submission1_appstory.Data.Login
import com.nabil.submission1_appstory.Data.LoginResponse
import com.nabil.submission1_appstory.Data.GetResponse
import com.nabil.submission1_appstory.Data.Register
import com.nabil.submission1_appstory.Data.StoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @POST("register")
    suspend fun signup(
        @Body requestBody: Register
    ): GetResponse

    @POST("login")
    suspend fun login(
        @Body requestBody: Login
    ): LoginResponse

    @Multipart
    @POST("stories")
    suspend fun postStory(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody
    ): GetResponse

    @GET("stories")
    suspend fun gainStrories(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("size") size: Int,
    ): StoryResponse

    @GET("stories/{id}")
    suspend fun GainCeritaDetail(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): DetailCeritaResponse

    @GET("stories")
    suspend fun getStoriesWithLocation(
        @Header("Authorization") token: String,
        @Query("location") location : Int = 1,
    ): StoryResponse
}