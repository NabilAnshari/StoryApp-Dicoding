package com.nabil.submission1_appstory.View

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.nabil.submission1_appstory.Component.reduceFilePhoto
import com.nabil.submission1_appstory.Component.rotatePhoto
import com.nabil.submission1_appstory.Component.uriToFile
import com.nabil.submission1_appstory.Model.MainViewModel
import com.nabil.submission1_appstory.Model.ViewModelFactory
import com.nabil.submission1_appstory.databinding.ActivityNewStoryBinding
import com.nabil.submission1_appstory.Local.Outcome
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class NewStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewStoryBinding
    private var findFile : File?=null

    private val mainViewModel by viewModels<MainViewModel> {
        ViewModelFactory.findInstance(application)
    }

    private val reqPermissionLaunch =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(this, "Permission granted", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_LONG).show()
            }
        }

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(
            this,
            REQUIRED_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!allPermissionsGranted()) {
            reqPermissionLaunch.launch(REQUIRED_PERMISSION)
        }

        val tokenSend = mainViewModel.gainPreference(this).value
        val prevDesc = binding.prevDesc

        binding.btngaleri.setOnClickListener { startGallery() }
        binding.btncamera.setOnClickListener { startCamera() }
        binding.btnupload.setOnClickListener {
            if (tokenSend != null) {
                uploadImage(prevDesc, tokenSend)
            }
        }
    }

    private fun startCamera() {
        val move = Intent(this, OpenCamActivity::class.java)
        launchIntentCamera.launch(move)
    }

    private val launchIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERA_X_RESULT) {
            val tempFile = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                it.data?.getSerializableExtra("picture", File::class.java)
            } else {
                @Suppress("DEPRECATION")
                it.data?.getSerializableExtra("picture")
            } as? File

            val backCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean

            tempFile?.let { file ->
                rotatePhoto(file, backCamera)
                findFile= file
                binding.prevImg.setImageBitmap(BitmapFactory.decodeFile(file.path))
            }
        }
    }

    private fun startGallery(){
        val gallery = Intent()
        gallery.action = Intent.ACTION_GET_CONTENT
        gallery.type = "image/*"
        val intent = Intent.createChooser(gallery,"Pick photo")
        launchGallery.launch(intent)
    }

    private val launchGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { output ->
        if (output.resultCode == RESULT_OK){
            val pickedImg = output.data?.data as Uri
            pickedImg.let {uri ->
                val tempFile = uriToFile(uri, this@NewStoryActivity)
                findFile = tempFile
                binding.prevImg.setImageURI(uri)
            }
        } else{
            Log.d("Photo", "No photo was selected")
        }
    }

    private fun uploadImage(findDesc: EditText, token: String) {
        if (findFile != null) {
            val file = reduceFilePhoto(findFile as File)

            val desc = findDesc.text.toString().toRequestBody("text/plain".toMediaType())
            val requestImageFile = file.asRequestBody("image/jpeg".toMediaType())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo",
                file.name,
                requestImageFile
            )
            mainViewModel.uploadStory("Bearer $token", imageMultipart, desc)
                .observe(this) { result ->
                    when (result) {
                        is Outcome.Loading-> {
                            loadingProcess()
                        }
                        is Outcome.Success -> {
                            val response = result.data
                            val intent = Intent(this@NewStoryActivity, MainActivity::class.java)
                            startActivity(intent)
                            Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()
                            finish()
                        }
                        is Outcome.Error -> {
                            val errorMessage = result.error
                            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                        }
                    }
                }


        } else {
            Toast.makeText(
                this@NewStoryActivity,
                "input file gambar lebih dulu",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun loadingProcess() {
        binding.progressBar.visibility = View.VISIBLE
        binding.prevDesc.isCursorVisible = false
    }

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
        const val CAMERA_X_RESULT = 200
    }
}