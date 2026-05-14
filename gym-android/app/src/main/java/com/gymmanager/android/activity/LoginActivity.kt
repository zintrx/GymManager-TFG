package com.gymmanager.android.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.gymmanager.android.MainActivity
import com.gymmanager.android.R
import com.gymmanager.android.databinding.ActivityLoginBinding
import com.gymmanager.android.model.LoginRequest
import com.gymmanager.android.model.LoginResponse
import com.gymmanager.android.network.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Show success message if coming from registration
        if (intent.getBooleanExtra("REGISTER_SUCCESS", false)) {
            Toast.makeText(this, "¡Cuenta creada! Ahora inicia sesión.", Toast.LENGTH_LONG).show()
        }

        binding.btnLogin.setOnClickListener {
            performLogin()
        }

        // Navigate to register screen
        binding.tvGoRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun performLogin() {
        val username = binding.etUsername.text.toString()
        val password = binding.etPassword.text.toString()

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        binding.progressBar.visibility = View.VISIBLE
        binding.btnLogin.isEnabled = false

        val loginRequest = LoginRequest(username, password)
        
        ApiClient.apiService.login(loginRequest).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                binding.progressBar.visibility = View.GONE
                binding.btnLogin.isEnabled = true

                if (response.isSuccessful && response.body()?.success == true) {
                    val body = response.body()!!
                    // Persist session
                    val prefs = getSharedPreferences("gymmanager_prefs", MODE_PRIVATE)
                    prefs.edit()
                        .putLong("user_id", body.id ?: 1L)
                        .putString("username", body.username ?: username)
                        .putString("role", body.role ?: "CLIENTE")
                        .putString("email", body.email ?: "")
                        .putString("telefono", body.telefono ?: "")
                        .putString("dni", body.dni ?: "")
                        .putString("avatar_url", body.avatarUrl)
                        .putFloat("cuota", (body.cuotaMensual ?: 29.99).toFloat())
                        .apply()

                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this@LoginActivity, "Error: Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                binding.progressBar.visibility = View.GONE
                binding.btnLogin.isEnabled = true
                Toast.makeText(this@LoginActivity, "Error de conexión: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
