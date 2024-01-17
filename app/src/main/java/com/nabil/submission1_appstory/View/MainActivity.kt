package com.nabil.submission1_appstory.View

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.nabil.submission1_appstory.Adapt.ShowAdapter
import com.nabil.submission1_appstory.Data.GlobalVariabel
import com.nabil.submission1_appstory.Model.MainViewModel
import com.nabil.submission1_appstory.Model.ViewModelFactory
import com.nabil.submission1_appstory.R
import com.nabil.submission1_appstory.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val mainViewModel by viewModels<MainViewModel>{
        ViewModelFactory.findInstance(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val layoutManager = LinearLayoutManager(this)
        binding.rvStory.layoutManager = layoutManager

        val token = takeToken()
        GlobalVariabel.token = "Bearer $token"

        displayStory(mainViewModel)

        binding.btnAdd.setOnClickListener {
            val intent = Intent(this@MainActivity, NewStoryActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        displayStory(mainViewModel)
    }

    private fun displayStory(mainViewModel: MainViewModel) {
        val adapter = ShowAdapter()
        binding.rvStory.adapter = adapter

        mainViewModel.story.observe(this, {
            adapter.submitData(lifecycle, it)
        })
        adapter.setOnItemClickListener { story ->
            val intent = Intent(this@MainActivity, StoryDescripActivity::class.java)
            intent.putExtra("id", story.id)
            startActivity(intent)
        }
    }

    private fun takeToken(): String?{
        return mainViewModel.gainPreference(this).value
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_option, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.mapsMenu -> {
                val intent = Intent(this, MapsActivity::class.java)
                startActivity(intent)
            }
            R.id.logoutMenu -> {
                mainViewModel.setPreference("", this)
                val intent = Intent(this@MainActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}

