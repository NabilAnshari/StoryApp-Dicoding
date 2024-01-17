package com.nabil.submission1_appstory.View

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.nabil.submission1_appstory.Data.UserModel
import com.nabil.submission1_appstory.Local.Outcome
import com.nabil.submission1_appstory.Model.MainViewModel
import com.nabil.submission1_appstory.Model.ViewModelFactory
import com.nabil.submission1_appstory.databinding.ActivitySignupBinding

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding

    private val mainViewModel by viewModels<MainViewModel> {
        ViewModelFactory.findInstance(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val valName = binding.signupName.text
        val valEmail = binding.signupEmail.text
        val valPassword = binding.signupPw.text

        binding.signupButton.setOnClickListener {
            val signupPassword = binding.signupPw.text

            if (signupPassword?.length!! <8){
                binding.signupButton.error = "Minimal harus 8 karakter"
            }else{
                loadingCorrect()
                mainViewModel.signupNewUser(
                    valName.toString(),
                    valPassword.toString(),
                    valEmail.toString()
                ).observe(this){ output ->
                    when (output){
                        is Outcome.Loading -> {}
                        is Outcome.Success -> {
                            binding.progressBar.visibility = View.GONE
                            val response = output.data
                            Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()
                            sendToLogin(
                                UserModel(
                                    valEmail.toString(),
                                    valPassword.toString()
                                )
                            )
                        }
                        is Outcome.Error -> {
                            val Message = output.error
                            incorrectData()
                            Toast.makeText(this, Message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
        playPropertyAnimation()
    }


    @SuppressLint("MissingSuperCall")
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    private fun incorrectData(){
        binding.progressBar.visibility = View.GONE
        binding.signupName.isCursorVisible = true
        binding.signupEmail.isCursorVisible = true
        binding.signupPw.isCursorVisible = true
    }

    private fun loadingCorrect(){
        binding.progressBar.visibility = View.GONE
        binding.signupName.isCursorVisible = false
        binding.signupEmail.isCursorVisible = false
        binding.signupPw.isCursorVisible = false
    }

    private fun sendToLogin(data: UserModel){
        val intent = Intent(this@SignupActivity, LoginActivity::class.java)
        intent.putExtra("extra_email_username", data)
        startActivity(intent)
        finish()
    }

    private fun playPropertyAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val emailTextView =
            ObjectAnimator.ofFloat(binding.signupEmail, View.ALPHA, 1f).setDuration(100)
        val emailEditTextLayout =
            ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val passwordTextView =
            ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(100)
        val passwordEditTextLayout =
            ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val login = ObjectAnimator.ofFloat(binding.signupButton, View.ALPHA, 1f).setDuration(100)

        AnimatorSet().apply {
            playSequentially(
                emailTextView,
                emailEditTextLayout,
                passwordTextView,
                passwordEditTextLayout,
                login
            )
            startDelay = 100
        }.start()
    }
}