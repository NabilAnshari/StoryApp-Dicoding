package com.nabil.submission1_appstory.View

import androidx.lifecycle.ViewModel
import com.nabil.submission1_appstory.Local.Repository

class MapsViewModel(private val mainRepository: Repository) : ViewModel() {
    fun gainStories(token: String) = mainRepository.gainStoryLocation(token)
}