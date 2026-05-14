package com.gymmanager.android.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.gymmanager.android.R
import com.gymmanager.android.model.LoginResponse
import com.gymmanager.android.model.RegisterRequest
import com.gymmanager.android.network.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val etUsername   = findViewById<EditText>(R.id.etUsername)
        val etEmail      = findViewById<EditText>(R.id.etEmail)
        val etPassword   = findViewById<EditText>(R.id.etPassword)
        val btnRegister  = findViewById<Button>(R.id.btnRegister)
        val progressBar  = findViewById<ProgressBar>(R.id.progressBar)
        val tvError      = findViewById<TextView>(R.id.tvError)
        val tvGoLogin    = findViewById<TextView>(R.id.tvGoLogin)

        // Navigate back to login
        tvGoLogin.setOnClickListener {
            finish()
        }

        btnRegister.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val email    = etEmail.text.toString().trim()
            val password = etPassword.text.toString()

            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                showError(tvError, "Por favor, completa todos los campos.")
                return@setOnClickListener
            }

            // Show loading
            progressBar.visibility = View.VISIBLE
            btnRegister.isEnabled  = false
            tvError.visibility     = View.GONE

            val request = RegisterRequest(username, email, password)

            ApiClient.apiService.register(request).enqueue(object : Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    progressBar.visibility = View.GONE
                    btnRegister.isEnabled  = true

                    if (response.isSuccessful && response.body()?.success == true) {
                        // Registration OK -> go to login
                        val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                        intent.putExtra("REGISTER_SUCCESS", true)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                        startActivity(intent)
                        finish()
                    } else {
                        val msg = response.body()?.message ?: "Error al registrarse."
                        showError(tvError, msg)
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    progressBar.visibility = View.GONE
                    btnRegister.isEnabled  = true
                    showError(tvError, "Error de conexión: ${t.message}")
                }
            })
        }
    }

    private fun showError(tv: TextView, msg: String) {
        tv.text       = msg
        tv.visibility = View.VISIBLE
    }
}
