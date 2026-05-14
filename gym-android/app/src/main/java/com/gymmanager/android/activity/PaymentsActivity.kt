package com.gymmanager.android.activity

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.gymmanager.android.adapter.PaymentAdapter
import com.gymmanager.android.adapter.ShopAdapter
import com.gymmanager.android.databinding.ActivityPaymentsBinding
import com.gymmanager.android.model.Compra
import com.gymmanager.android.model.Pago
import com.gymmanager.android.network.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PaymentsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPaymentsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvPayments.layoutManager = LinearLayoutManager(this)
        binding.rvShopHistory.layoutManager = LinearLayoutManager(this)
        
        loadPayments()
        loadShopHistory()
    }

    private fun loadPayments() {
        binding.progressBarPayments.visibility = View.VISIBLE

        ApiClient.apiService.getPagos().enqueue(object : Callback<List<Pago>> {
            override fun onResponse(call: Call<List<Pago>>, response: Response<List<Pago>>) {
                binding.progressBarPayments.visibility = View.GONE
                if (response.isSuccessful) {
                    val payments = response.body() ?: emptyList()
                    binding.rvPayments.adapter = PaymentAdapter(payments)
                }
            }
            override fun onFailure(call: Call<List<Pago>>, t: Throwable) {
                binding.progressBarPayments.visibility = View.GONE
            }
        })
    }

    private fun loadShopHistory() {
        val prefs = getSharedPreferences("gymmanager_prefs", MODE_PRIVATE)
        val userId = prefs.getLong("user_id", 1L)

        ApiClient.apiService.getComprasByUsuario(userId).enqueue(object : Callback<List<Compra>> {
            override fun onResponse(call: Call<List<Compra>>, response: Response<List<Compra>>) {
                if (response.isSuccessful) {
                    val compras = response.body() ?: emptyList()
                    binding.rvShopHistory.adapter = ShopAdapter(compras)
                }
            }
            override fun onFailure(call: Call<List<Compra>>, t: Throwable) {
                Toast.makeText(this@PaymentsActivity, "Error al cargar tienda", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
