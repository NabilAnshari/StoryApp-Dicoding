package com.nabil.submission1_appstory.View

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import com.bumptech.glide.Glide
import com.nabil.submission1_appstory.Data.Cerita
import com.nabil.submission1_appstory.Local.Outcome
import com.nabil.submission1_appstory.Model.MainViewModel
import com.nabil.submission1_appstory.Model.ViewModelFactory
import com.nabil.submission1_appstory.databinding.ActivityStoryDescripBinding

class StoryDescripActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStoryDescripBinding

    private val mainViewModel by viewModels<MainViewModel> {
        ViewModelFactory.findInstance(application)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoryDescripBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= 33){
            val id = intent.getStringExtra("id")
            if (id!=null) settingStory(mainViewModel, id)
        }else{
            val id = intent.getStringExtra("id") as String
            settingStory(mainViewModel,id)
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun settingStory(mainViewModel: MainViewModel, id: String) {
        val token = findToken()
        if (token != null) {
            mainViewModel.getCeritaDetail("Bearer $token", id)
                .observe(this@StoryDescripActivity) { desc ->
                    if (desc != null) {
                        when (desc) {
                            is Outcome.Loading -> {

                            }
                            is Outcome.Success -> {
                                val info = desc.data.story
                                if (info != null) {
                                    setData(info)
                                }
                            }
                            is Outcome.Error -> {
                                // Handle the error
                            }
                        }
                    }
                }
        }
    }

    private fun findToken(): String? {
        return mainViewModel.gainPreference(this).value
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setData(data: Cerita) {
        Glide.with(this)
            .load(data.photoUrl)
            .into(binding.photoDetail)
        binding.nameDetail.text = data.name
        binding.descDetail.text = data.description
    }
}