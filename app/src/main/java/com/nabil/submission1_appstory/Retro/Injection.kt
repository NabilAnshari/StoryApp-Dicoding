package com.nabil.submission1_appstory.Retro

import android.content.Context
import com.nabil.submission1_appstory.Local.Repository

object Injection {
    fun provideRepository(context: Context): Repository {
        val apiService = ApiConfig.getApiService()
        return Repository.getInstance(apiService)
    }
}