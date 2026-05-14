package com.gymmanager.android.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.gymmanager.android.adapter.ExercisesAdapter
import com.gymmanager.android.databinding.ActivityExercisesBinding
import com.gymmanager.android.model.Ejercicio
import com.gymmanager.android.network.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ExercisesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityExercisesBinding
    private var rutinaId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExercisesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        rutinaId = intent.getLongExtra("RUTINA_ID", -1)
        val rutinaNombre = intent.getStringExtra("RUTINA_NOMBRE") ?: "Ejercicios"
        binding.tvRoutineTitle.text = rutinaNombre

        binding.rvExercises.layoutManager = LinearLayoutManager(this)

        loadExercises()
    }

    private fun loadExercises() {
        if (rutinaId == -1L) return
        binding.pbExercises.visibility = View.VISIBLE

        ApiClient.apiService.getEjerciciosByRutina(rutinaId).enqueue(object : Callback<List<Ejercicio>> {
            override fun onResponse(call: Call<List<Ejercicio>>, response: Response<List<Ejercicio>>) {
                binding.pbExercises.visibility = View.GONE
                if (response.isSuccessful) {
                    val exercises = response.body() ?: emptyList()
                    binding.rvExercises.adapter = ExercisesAdapter(exercises) { ex ->
                        val intent = Intent(this@ExercisesActivity, WorkoutTrackingActivity::class.java)
                        intent.putExtra("EJERCICIO_ID", ex.id)
                        intent.putExtra("EJERCICIO_NOMBRE", ex.nombre)
                        intent.putExtra("EJERCICIO_TARGET", "${ex.series}x${ex.repeticiones} (${ex.peso}kg)")
                        startActivity(intent)
                    }
                }
            }

            override fun onFailure(call: Call<List<Ejercicio>>, t: Throwable) {
                binding.pbExercises.visibility = View.GONE
                Toast.makeText(this@ExercisesActivity, "Error de red", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
