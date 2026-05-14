package com.gymmanager.android.activity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.gymmanager.android.databinding.ActivityWorkoutTrackingBinding
import com.gymmanager.android.model.*
import com.gymmanager.android.network.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WorkoutTrackingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWorkoutTrackingBinding
    private var ejercicioId: Long = -1
    private var userId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWorkoutTrackingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val prefs = getSharedPreferences("gymmanager_prefs", MODE_PRIVATE)
        userId = prefs.getLong("user_id", -1)

        val nombre = intent.getStringExtra("EJERCICIO_NOMBRE") ?: "Ejercicio"
        ejercicioId = intent.getLongExtra("EJERCICIO_ID", -1)
        val target = intent.getStringExtra("EJERCICIO_TARGET") ?: ""

        binding.tvExerciseTitle.text = nombre
        binding.tvTargetLabel.text = "Objetivo: $target"

        binding.btnSaveProgress.setOnClickListener {
            saveProgress()
        }
    }

    private fun saveProgress() {
        val series = binding.etSeries.text.toString().toIntOrNull() ?: 0
        val reps = binding.etReps.text.toString().toIntOrNull() ?: 0
        val weight = binding.etWeight.text.toString().toDoubleOrNull() ?: 0.0

        if (series == 0 || reps == 0) {
            Toast.makeText(this, "Por favor completa los campos", Toast.LENGTH_SHORT).show()
            return
        }

        val progreso = HistorialProgreso(
            usuario = LoginResponse(id = userId, success = true, message = "", username = null, role = null, email = null, telefono = null, dni = null, avatarUrl = null, cuotaMensual = null),
            ejercicio = Ejercicio(id = ejercicioId, nombre = "", series = 0, repeticiones = 0, peso = 0.0),
            seriesCompletadas = series,
            repeticionesRealizadas = reps,
            pesoUtilizado = weight
        )

        ApiClient.apiService.guardarProgreso(progreso).enqueue(object : Callback<HistorialProgreso> {
            override fun onResponse(call: Call<HistorialProgreso>, response: Response<HistorialProgreso>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@WorkoutTrackingActivity, "✅ Progreso guardado", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@WorkoutTrackingActivity, "❌ Error al guardar", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<HistorialProgreso>, t: Throwable) {
                Toast.makeText(this@WorkoutTrackingActivity, "Error de red", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
