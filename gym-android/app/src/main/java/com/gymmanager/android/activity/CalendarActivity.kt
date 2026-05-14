package com.gymmanager.android.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.gymmanager.android.R
import com.gymmanager.android.model.ClaseGym
import com.gymmanager.android.adapter.ClasesAdapter
import com.gymmanager.android.adapter.DayItem
import com.gymmanager.android.adapter.WeekAdapter
import com.gymmanager.android.databinding.ActivityCalendarBinding
import java.text.SimpleDateFormat
import java.util.*

import com.gymmanager.android.model.*
import com.gymmanager.android.network.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CalendarActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCalendarBinding
    private lateinit var classesAdapter: ClasesAdapter
    private lateinit var weekAdapter: WeekAdapter
    
    private var allActividades: List<Actividad> = emptyList()
    private var reservedActivityIds: MutableSet<Long> = mutableSetOf()
    private var userId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCalendarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val prefs = getSharedPreferences("gymmanager_prefs", MODE_PRIVATE)
        userId = prefs.getLong("user_id", -1)
        val username = prefs.getString("username", "Usuario") ?: "Usuario"
        binding.tvWelcome.text = "Hola $username,"

        setupRecyclerViews()
        setupNavigation()
        updateDateLabel(Date())
        
        loadData()
    }

    private fun loadData() {
        if (userId == -1L) return

        // Load all activities
        ApiClient.apiService.getActividades().enqueue(object : Callback<List<Actividad>> {
            override fun onResponse(call: Call<List<Actividad>>, response: Response<List<Actividad>>) {
                if (response.isSuccessful) {
                    allActividades = response.body() ?: emptyList()
                    loadUserReservations()
                }
            }
            override fun onFailure(call: Call<List<Actividad>>, t: Throwable) {}
        })
    }

    private fun loadUserReservations() {
        ApiClient.apiService.getReservasByUsuario(userId).enqueue(object : Callback<List<Reserva>> {
            override fun onResponse(call: Call<List<Reserva>>, response: Response<List<Reserva>>) {
                if (response.isSuccessful) {
                    reservedActivityIds = response.body()?.mapNotNull { it.actividad.id }?.toMutableSet() ?: mutableSetOf()
                    updateList()
                }
            }
            override fun onFailure(call: Call<List<Reserva>>, t: Throwable) {}
        })
    }

    private fun updateList() {
        classesAdapter.updateData(allActividades, reservedActivityIds)
    }

    private fun setupRecyclerViews() {
        // Classes list
        classesAdapter = ClasesAdapter(emptyList(), emptySet()) { actividad, isReserved ->
            if (isReserved) {
                cancelarReserva(actividad)
            } else {
                realizarReserva(actividad)
            }
        }
        binding.rvClasses.layoutManager = LinearLayoutManager(this)
        binding.rvClasses.adapter = classesAdapter

        // Week Strip
        val days = mutableListOf<DayItem>()
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -2)
        for (i in 0..6) {
            val isToday = i == 2
            days.add(DayItem(calendar.time, isToday))
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }
        
        weekAdapter = WeekAdapter(days) { dayItem ->
            updateDateLabel(dayItem.date)
        }
        binding.rvWeekStrip.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvWeekStrip.adapter = weekAdapter
    }

    private fun realizarReserva(actividad: Actividad) {
        val actId = actividad.id ?: return
        ApiClient.apiService.realizarReserva(actId, userId).enqueue(object : Callback<Reserva> {
            override fun onResponse(call: Call<Reserva>, response: Response<Reserva>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@CalendarActivity, "✅ ¡Clase reservada!", Toast.LENGTH_SHORT).show()
                    loadData() // Refresh
                } else {
                    Toast.makeText(this@CalendarActivity, "❌ Error: Actividad llena o ya reservada", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<Reserva>, t: Throwable) {}
        })
    }

    private fun cancelarReserva(actividad: Actividad) {
        val actId = actividad.id ?: return
        ApiClient.apiService.cancelarReserva(actId, userId).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@CalendarActivity, "❌ Reserva cancelada", Toast.LENGTH_SHORT).show()
                    loadData() // Refresh
                }
            }
            override fun onFailure(call: Call<Void>, t: Throwable) {}
        })
    }

    private fun updateDateLabel(date: Date) {
        val sdf = SimpleDateFormat("EEEE d 'de' MMMM", Locale("es", "ES"))
        binding.tvDayTitle.text = sdf.format(date).capitalize()
    }

    private fun setupNavigation() {
        binding.bottomNavigation.selectedItemId = R.id.nav_calendar
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.nav_home -> { finish(); true }
                R.id.nav_explore -> { startActivity(Intent(this, ExploreActivity::class.java)); true }
                R.id.nav_social -> { startActivity(Intent(this, SocialActivity::class.java)); true }
                R.id.nav_profile -> { startActivity(Intent(this, ProfileActivity::class.java)); true }
                else -> true
            }
        }
    }
}
