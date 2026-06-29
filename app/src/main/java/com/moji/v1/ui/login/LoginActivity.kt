package com.moji.v1.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.moji.v1.database.DatabaseHelper
import com.moji.v1.database.SessionManager
import com.moji.v1.databinding.ActivityLoginBinding
import com.moji.v1.ui.main.MainActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelper(this)
        sessionManager = SessionManager(this)

        // Kalau sudah login langsung ke MainActivity
        if (sessionManager.isLoggedIn()) {
            goToMain()
            return
        }

        binding.btnLogin.setOnClickListener {
            handleLogin()
        }

        binding.tvGoToRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun handleLogin() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Snackbar.make(binding.root, "Email dan password wajib diisi", Snackbar.LENGTH_SHORT).show()
            return
        }

        val user = dbHelper.loginUser(email, password)
        if (user != null) {
            sessionManager.saveSession(user.id, user.username, user.email)
            Snackbar.make(binding.root, "Selamat datang, ${user.username}! 👋", Snackbar.LENGTH_SHORT).show()
            goToMain()
        } else {
            Snackbar.make(binding.root, "Email atau password salah", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun goToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}