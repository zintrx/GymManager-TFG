package com.gymmanager.android.activity

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.gymmanager.android.adapter.RoutineAdapter
import com.gymmanager.android.databinding.ActivityRoutinesBinding
import com.gymmanager.android.model.Rutina
import com.gymmanager.android.network.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

import androidx.lifecycle.lifecycleScope
import com.gymmanager.android.db.AppDatabase
import com.gymmanager.android.db.RutinaEntity
import kotlinx.coroutines.launch

class RoutinesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRoutinesBinding
    private lateinit var database: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRoutinesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = AppDatabase.getDatabase(this)
        binding.rvRoutines.layoutManager = LinearLayoutManager(this)
        
        val prefs = getSharedPreferences("gymmanager_prefs", MODE_PRIVATE)
        val clienteId = prefs.getLong("clienteId", 1L)
        loadRoutines(clienteId)
    }

    private fun loadRoutines(clienteId: Long) {
        binding.progressBarRoutines.visibility = View.VISIBLE

        ApiClient.apiService.getRutinasByCliente(clienteId).enqueue(object : Callback<List<Rutina>> {
            override fun onResponse(call: Call<List<Rutina>>, response: Response<List<Rutina>>) {
                binding.progressBarRoutines.visibility = View.GONE
                
                if (response.isSuccessful) {
                    val routines = response.body() ?: emptyList()
                    saveRoutinesOffline(routines)
                    displayRoutines(routines)
                } else {
                    loadRoutinesOffline()
                }
            }

            override fun onFailure(call: Call<List<Rutina>>, t: Throwable) {
                binding.progressBarRoutines.visibility = View.GONE
                loadRoutinesOffline()
                Toast.makeText(this@RoutinesActivity, "Modo Offline: Cargando caché", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun displayRoutines(routines: List<Rutina>) {
        binding.rvRoutines.adapter = RoutineAdapter(routines, 
            onDelete = { routine ->
                deleteRoutine(routine)
            },
            onClick = { routine ->
                val intent = android.content.Intent(this@RoutinesActivity, ExercisesActivity::class.java)
                intent.putExtra("RUTINA_ID", routine.id)
                intent.putExtra("RUTINA_NOMBRE", routine.nombreRutina)
                startActivity(intent)
            }
        )
    }

    private fun deleteRoutine(routine: Rutina) {
        val routineId = routine.id ?: return
        
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Eliminar Rutina")
            .setMessage("¿Estás seguro de que quieres eliminar esta rutina?")
            .setPositiveButton("Eliminar") { _, _ ->
                ApiClient.apiService.deleteRutina(routineId).enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if (response.isSuccessful) {
                            Toast.makeText(this@RoutinesActivity, "Rutina eliminada", Toast.LENGTH_SHORT).show()
                            val prefs = getSharedPreferences("gymmanager_prefs", MODE_PRIVATE)
                            loadRoutines(prefs.getLong("clienteId", 1L))
                        } else {
                            Toast.makeText(this@RoutinesActivity, "Error al eliminar", Toast.LENGTH_SHORT).show()
                        }
                    }
                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Toast.makeText(this@RoutinesActivity, "Error de red", Toast.LENGTH_SHORT).show()
                    }
                })
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun saveRoutinesOffline(routines: List<Rutina>) {
        lifecycleScope.launch {
            val entities = routines.map { 
                RutinaEntity(it.id ?: 0, it.nombreRutina, it.descripcion ?: "", it.fechaAsignacion?.time) 
            }
            database.routineDao().clearAll()
            database.routineDao().insertRutinas(entities)
        }
    }

    private fun loadRoutinesOffline() {
        lifecycleScope.launch {
            val entities = database.routineDao().getAllRutinas()
            val routines = entities.map { 
                Rutina(it.id, it.nombre, it.descripcion, if (it.fechaAsignacion != null) java.util.Date(it.fechaAsignacion) else null) 
            }
            displayRoutines(routines)
        }
    }
}
