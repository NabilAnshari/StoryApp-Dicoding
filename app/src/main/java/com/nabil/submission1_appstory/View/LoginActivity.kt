package com.nabil.submission1_appstory.View

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.nabil.submission1_appstory.Data.UserModel
import com.nabil.submission1_appstory.Local.Outcome
import com.nabil.submission1_appstory.Model.MainViewModel
import com.nabil.submission1_appstory.Model.ViewModelFactory
import com.nabil.submission1_appstory.databinding.ActivityLoginBinding

@Suppress("DEPRECATION")
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private val mainViewModel by viewModels<MainViewModel> {
        ViewModelFactory.findInstance(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val btnLogin = binding.loginButton
        val lgnEmail = binding.loginEml
        val lgnPw = binding.loginPw

        isAlreadyLogin(this)

        binding.signinAct.setOnClickListener {
            val signin = Intent(this@LoginActivity, SignupActivity::class.java)
            startActivity(signin)
            finish()
        }

        if (Build.VERSION.SDK_INT >= 33) {
            val data = intent.getParcelableExtra("extra_email_username", UserModel::class.java)
            if (data != null) {
                loginUser(data.email.toString(), data.password.toString())
            }
        } else {
            val data = intent.getParcelableExtra<UserModel>("extra_email_username")
            if (data != null) {
                loginUser(data.email.toString(), data.password.toString())
            }
        }

        btnLogin.setOnClickListener {
            if (lgnPw.text?.isEmpty() == true) {
                lgnPw.error = "Mohon Untuk Di isi, Tidak Boleh Kosong"
            }

            if (lgnEmail.text?.isEmpty() == true) {
                lgnEmail.error = "Mohon isi dengan Format @gmail dan Tidak boleh kosong"
            }
            if (lgnPw.error == null && lgnEmail.error == null) {
                loginUser(lgnEmail.text.toString(), lgnPw.text.toString())
            }
        }
        playPropertyAnimation()
    }

    private fun isAlreadyLogin(context: Context){
        mainViewModel.gainPreference(context).observe(this){token ->
            if (token?.isEmpty()==false){
                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun loginUser(email: String, password: String){
        mainViewModel.loginUser(email, password).observe(this){ output ->
            if (output != null){
                when (output){
                    is Outcome.Loading ->{
                        loadingProcess()
                    }
                    is Outcome.Success ->{
                        binding.progressBar.visibility = View.GONE
                        val info = output.data
                        if(info.loginResult?.token != null){
                            mainViewModel.setPreference(info.loginResult.token, this)
                        }
                        val mainActivity = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(mainActivity)
                        finish()
                    }
                    is Outcome.Error ->{
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(this, output.error, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun playPropertyAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val emailTextView =
            ObjectAnimator.ofFloat(binding.loginEml, View.ALPHA, 1f).setDuration(100)
        val emailEditTextLayout =
            ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val passwordTextView =
            ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(100)
        val passwordEditTextLayout =
            ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val login = ObjectAnimator.ofFloat(binding.loginButton, View.ALPHA, 1f).setDuration(100)

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

    private fun loadingProcess() {
        binding.progressBar.visibility = View.VISIBLE
        binding.loginEml.isCursorVisible = false
        binding.loginPw.isCursorVisible = false
    }
}