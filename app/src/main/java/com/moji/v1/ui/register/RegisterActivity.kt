package com.moji.v1.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.moji.v1.database.DatabaseHelper
import com.moji.v1.databinding.ActivityRegisterBinding
import com.moji.v1.model.User

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelper(this)

        binding.btnRegister.setOnClickListener {
            handleRegister()
        }

        binding.tvGoToLogin.setOnClickListener {
            finish()
        }
    }

    private fun handleRegister() {
        val username = binding.etUsername.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val confirmPassword = binding.etConfirmPassword.text.toString().trim()

        // Validasi
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Snackbar.make(binding.root, "Semua field wajib diisi", Snackbar.LENGTH_SHORT).show()
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Snackbar.make(binding.root, "Format email tidak valid", Snackbar.LENGTH_SHORT).show()
            return
        }

        if (password.length < 6) {
            Snackbar.make(binding.root, "Password minimal 6 karakter", Snackbar.LENGTH_SHORT).show()
            return
        }

        if (password != confirmPassword) {
            Snackbar.make(binding.root, "Password tidak cocok", Snackbar.LENGTH_SHORT).show()
            return
        }

        if (dbHelper.isEmailExist(email)) {
            Snackbar.make(binding.root, "Email sudah terdaftar", Snackbar.LENGTH_SHORT).show()
            return
        }

        val user = User(username = username, email = email, password = password)
        val success = dbHelper.registerUser(user)

        if (success) {
            Snackbar.make(binding.root, "Registrasi berhasil! Silakan login 🎉", Snackbar.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        } else {
            Snackbar.make(binding.root, "Registrasi gagal, coba lagi", Snackbar.LENGTH_SHORT).show()
        }
    }
}