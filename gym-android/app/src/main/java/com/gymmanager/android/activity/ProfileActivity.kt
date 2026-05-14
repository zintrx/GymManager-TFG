package com.gymmanager.android.activity

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import android.widget.Button
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.gymmanager.android.R
import com.gymmanager.android.model.*
import com.gymmanager.android.network.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileActivity : AppCompatActivity() {

    private lateinit var ivAvatar: ImageView
    private val tabs = mutableListOf<TextView>()
    private val layouts = mutableListOf<LinearLayout>()

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val imageUri: Uri? = result.data?.data
            if (imageUri != null) {
                try {
                    val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)
                    val outputStream = java.io.ByteArrayOutputStream()
                    bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 70, outputStream)
                    val base64Image = "data:image/jpeg;base64," + android.util.Base64.encodeToString(outputStream.toByteArray(), android.util.Base64.NO_WRAP)
                    
                    val prefs = getSharedPreferences("gymmanager_prefs", MODE_PRIVATE)
                    val username = prefs.getString("username", "") ?: ""
                    
                    val request = UpdateUserRequest(
                        username = username,
                        avatarUrl = base64Image
                    )
                    
                    ApiClient.apiService.updateProfile(request).enqueue(object : Callback<LoginResponse> {
                        override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                            if (response.isSuccessful && response.body()?.success == true) {
                                val body = response.body()!!
                                getSharedPreferences("gymmanager_prefs", MODE_PRIVATE).edit()
                                    .putString("avatar_url", body.avatarUrl)
                                    .apply()
                                loadAvatar(body.avatarUrl ?: "")
                                Toast.makeText(this@ProfileActivity, "Foto sincronizada", Toast.LENGTH_SHORT).show()
                            }
                        }
                        override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                            Toast.makeText(this@ProfileActivity, "Error al sincronizar foto", Toast.LENGTH_SHORT).show()
                        }
                    })
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val prefs = getSharedPreferences("gymmanager_prefs", MODE_PRIVATE)
        val username = prefs.getString("username", "Usuario") ?: "Usuario"
        val savedAvatarUrl = prefs.getString("avatar_url", null)
        val email = prefs.getString("email", "") ?: ""
        val telefono = prefs.getString("telefono", "") ?: ""
        val dni = prefs.getString("dni", "") ?: ""
        val cuota = prefs.getFloat("cuota", 29.99f)
        val role = prefs.getString("role", "CLIENTE") ?: "CLIENTE"

        // --- Populate user info ---
        findViewById<TextView>(R.id.tvUsername).text = username
        findViewById<TextView>(R.id.tvEmail).text = if (email.isEmpty()) "sin correo" else email
        findViewById<TextView>(R.id.tvFee).text = "${String.format("%.2f", cuota)}€"
        findViewById<TextView>(R.id.tvPlan).text = role

        // --- Avatar ---
        ivAvatar = findViewById(R.id.ivAvatar)
        if (!savedAvatarUrl.isNullOrEmpty()) {
            loadAvatar(savedAvatarUrl)
        } else {
            val defaultUrl = "https://ui-avatars.com/api/?name=${Uri.encode(username)}&background=1e1e1e&color=bbff00&size=256&bold=true&rounded=true"
            loadAvatar(defaultUrl)
        }

        // Tap avatar to change photo
        findViewById<View>(R.id.cvAvatar).setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickImageLauncher.launch(intent)
        }

        // --- Settings icon ---
        findViewById<ImageView>(R.id.ivSettings).setOnClickListener {
            showSettingsDialog(username, email, telefono, dni)
        }

        // --- Plan upgrade ---
        findViewById<TextView>(R.id.tvUpgrade).setOnClickListener {
            showPlanDialog()
        }

        val tabClub = findViewById<TextView>(R.id.tabClub)
        val tabRend = findViewById<TextView>(R.id.tabRendimiento)
        val tabPub = findViewById<TextView>(R.id.tabPublicaciones)
        val tabNov = findViewById<TextView>(R.id.tabNovedades)

        val layoutClub = findViewById<LinearLayout>(R.id.layoutClub)
        val layoutRend = findViewById<LinearLayout>(R.id.layoutRendimiento)
        val layoutPub = findViewById<LinearLayout>(R.id.layoutPublicaciones)
        val layoutNov = findViewById<LinearLayout>(R.id.layoutNovedades)

        tabs.addAll(listOf(tabClub, tabRend, tabPub, tabNov))
        layouts.addAll(listOf(layoutClub, layoutRend, layoutPub, layoutNov))

        tabClub.setOnClickListener { selectTab(0) }
        tabRend.setOnClickListener { selectTab(1) }
        tabPub.setOnClickListener { selectTab(2) }
        tabNov.setOnClickListener { selectTab(3) }

        findViewById<View>(R.id.cardHorario).setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Horario del Centro")
                .setMessage("Lunes a Viernes: 06:00 - 23:00\nSábados y Domingos: 08:00 - 20:00\nFestivos: 09:00 - 15:00")
                .setPositiveButton("Cerrar", null)
                .show()
        }

        // --- Fetch Dynamic Data ---
        fetchPaymentStatus(username)
        fetchUserRoutine(username)

        val bottomNav = findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(R.id.bottomNavigation)
        bottomNav.selectedItemId = R.id.nav_profile
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, com.gymmanager.android.MainActivity::class.java))
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
                R.id.nav_profile -> true
                else -> true
            }
        }
    }

    private fun selectTab(index: Int) {
        tabs.forEachIndexed { i, tab ->
            if (i == index) {
                tab.setBackgroundResource(R.drawable.icon_circle_bg)
                tab.setTextColor(resources.getColor(R.color.white, null))
            } else {
                tab.setBackgroundResource(0)
                tab.setTextColor(resources.getColor(R.color.text_muted, null))
            }
        }
        layouts.forEachIndexed { i, layout ->
            layout.visibility = if (i == index) View.VISIBLE else View.GONE
        }
    }

    private fun loadAvatar(uri: String) {
        Glide.with(this)
            .load(uri)
            .transform(CircleCrop())
            .placeholder(android.R.drawable.ic_menu_myplaces)
            .into(ivAvatar)
    }

    private fun showSettingsDialog(username: String, currentEmail: String, currentPhone: String, currentDni: String) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_settings, null)
        val etEmail = dialogView.findViewById<EditText>(R.id.etEmail)
        val etPhone = dialogView.findViewById<EditText>(R.id.etPhone)
        val etDni = dialogView.findViewById<EditText>(R.id.etDni)
        val etPass = dialogView.findViewById<EditText>(R.id.etPassword)

        etEmail.setText(currentEmail)
        etPhone.setText(currentPhone)
        etDni.setText(currentDni)

        AlertDialog.Builder(this)
            .setView(dialogView)
            .setPositiveButton("Guardar") { _, _ ->
                val request = UpdateUserRequest(
                    username = username,
                    email = etEmail.text.toString(),
                    telefono = etPhone.text.toString(),
                    dni = etDni.text.toString(),
                    password = etPass.text.toString().ifEmpty { null }
                )
                
                ApiClient.apiService.updateProfile(request).enqueue(object : Callback<LoginResponse> {
                    override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                        if (response.isSuccessful && response.body()?.success == true) {
                            val body = response.body()!!
                            getSharedPreferences("gymmanager_prefs", MODE_PRIVATE).edit()
                                .putString("email", body.email)
                                .putString("telefono", body.telefono)
                                .putString("dni", body.dni)
                                .apply()
                            
                            findViewById<TextView>(R.id.tvEmail).text = body.email
                            Toast.makeText(this@ProfileActivity, "Ajustes guardados correctamente", Toast.LENGTH_SHORT).show()
                        }
                    }
                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                        Toast.makeText(this@ProfileActivity, "Error al guardar", Toast.LENGTH_SHORT).show()
                    }
                })
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun fetchPaymentStatus(username: String) {
        ApiClient.apiService.getPaymentStatus(username).enqueue(object : Callback<Map<String, Any>> {
            override fun onResponse(call: Call<Map<String, Any>>, response: Response<Map<String, Any>>) {
                if (response.isSuccessful) {
                    val status = response.body()
                    val isPaid = status?.get("paid") as? Boolean ?: false
                    findViewById<TextView>(R.id.tvStatus).text = if (isPaid) "AL DÍA ✅" else "PENDIENTE ⚠️"
                    findViewById<TextView>(R.id.tvNextBilling).text = "Próx: " + (status?.get("nextPayment") ?: "01/05")
                }
            }
            override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {}
        })
    }

    private fun fetchUserRoutine(username: String) {
        ApiClient.apiService.getRutinasByUsername(username).enqueue(object : Callback<List<Rutina>> {
            override fun onResponse(call: Call<List<Rutina>>, response: Response<List<Rutina>>) {
                if (response.isSuccessful && !response.body().isNullOrEmpty()) {
                    val routine = response.body()!![0]
                    fetchExercises(routine.id!!)
                }
            }
            override fun onFailure(call: Call<List<Rutina>>, t: Throwable) {}
        })
    }

    private fun fetchExercises(routineId: Long) {
        ApiClient.apiService.getEjerciciosByRutina(routineId).enqueue(object : Callback<List<Ejercicio>> {
            override fun onResponse(call: Call<List<Ejercicio>>, response: Response<List<Ejercicio>>) {
                if (response.isSuccessful) {
                    val exercises = response.body() ?: emptyList()
                    updateRendimientoUI(exercises)
                }
            }
            override fun onFailure(call: Call<List<Ejercicio>>, t: Throwable) {}
        })
    }

    private fun updateRendimientoUI(exercises: List<Ejercicio>) {
        val layout = findViewById<LinearLayout>(R.id.layoutRendimiento)
        layout.removeAllViews() // Clear mock data
        exercises.forEach { ex ->
            val card = LayoutInflater.from(this).inflate(R.layout.item_exercise_card, null)
            card.findViewById<TextView>(R.id.tvExName).text = ex.nombre
            card.findViewById<TextView>(R.id.tvExDetails).text = "${ex.series} series x ${ex.repeticiones} reps - ${ex.peso}kg"
            layout.addView(card)
        }
    }

    private fun showPlanDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_plan, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setNegativeButton("Cerrar", null)
            .create()

        val prefs = getSharedPreferences("gymmanager_prefs", MODE_PRIVATE)
        val username = prefs.getString("username", "") ?: ""

        val upgrade = { planName: String ->
            val request = UpdateUserRequest(
                username = username,
                role = planName
            )
            ApiClient.apiService.updateProfile(request).enqueue(object : Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        val body = response.body()!!
                        getSharedPreferences("gymmanager_prefs", MODE_PRIVATE).edit()
                            .putString("role", body.role)
                            .putFloat("cuota", body.cuotaMensual?.toFloat() ?: 29.99f)
                            .apply()
                        
                        findViewById<TextView>(R.id.tvPlan).text = body.role
                        findViewById<TextView>(R.id.tvFee).text = "${String.format("%.2f", body.cuotaMensual ?: 29.99)}€"
                        Toast.makeText(this@ProfileActivity, "¡Plan actualizado a ${body.role}!", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {}
            })
            dialog.dismiss()
        }

        dialogView.findViewById<Button>(R.id.btnUpgradeVip).setOnClickListener { upgrade("VIP") }
        dialogView.findViewById<Button>(R.id.btnUpgradeUltra).setOnClickListener { upgrade("ULTRA") }

        dialog.show()
    }
}
