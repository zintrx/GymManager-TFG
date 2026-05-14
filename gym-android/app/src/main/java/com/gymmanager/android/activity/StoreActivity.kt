package com.gymmanager.android.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.gymmanager.android.MainActivity
import com.gymmanager.android.R
import com.gymmanager.android.model.Compra
import com.gymmanager.android.model.CompraRequest
import com.gymmanager.android.network.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StoreActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_store)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.selectedItemId = R.id.nav_explore

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_calendar -> {
                    startActivity(Intent(this, CalendarActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_explore -> {
                    startActivity(Intent(this, ExploreActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_social -> {
                    startActivity(Intent(this, SocialActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }

        findViewById<Button>(R.id.btnBuyWhey).setOnClickListener {
            performPurchase("Proteína Whey", 29.99)
        }
        findViewById<Button>(R.id.btnBuyTowel).setOnClickListener {
            performPurchase("Toalla Gym", 12.50)
        }
        findViewById<Button>(R.id.btnBuyShaker).setOnClickListener {
            performPurchase("Shaker", 5.00)
        }
    }

    private fun performPurchase(productName: String, price: Double) {
        val prefs = getSharedPreferences("gymmanager_prefs", MODE_PRIVATE)
        val userId = prefs.getLong("user_id", 1L)

        val request = CompraRequest(productName, price, userId)

        ApiClient.apiService.realizarCompra(request).enqueue(object : Callback<Compra> {
            override fun onResponse(call: Call<Compra>, response: Response<Compra>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@StoreActivity, "¡Compra realizada: $productName!", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this@StoreActivity, "Error al procesar compra", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Compra>, t: Throwable) {
                Toast.makeText(this@StoreActivity, "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
