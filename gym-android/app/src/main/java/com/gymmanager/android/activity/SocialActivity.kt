package com.gymmanager.android.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.gymmanager.android.MainActivity
import com.gymmanager.android.R
import com.gymmanager.android.adapter.PostAdapter
import com.gymmanager.android.model.Publicacion
import com.gymmanager.android.network.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SocialActivity : AppCompatActivity() {

    private lateinit var rvPosts: RecyclerView
    private lateinit var postAdapter: PostAdapter
    private val postsList = mutableListOf<Publicacion>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_social)

        rvPosts = findViewById(R.id.rvPosts)
        rvPosts.layoutManager = LinearLayoutManager(this)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.selectedItemId = R.id.nav_social

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
                R.id.nav_social -> true
                R.id.nav_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }

        val tabFeed = findViewById<TextView>(R.id.tabFeed)
        val tabRanking = findViewById<TextView>(R.id.tabRanking)
        val layoutFeed = findViewById<LinearLayout>(R.id.layoutFeed)
        val layoutRanking = findViewById<LinearLayout>(R.id.layoutRanking)
        val fabAddPost = findViewById<FloatingActionButton>(R.id.fabAddPost)

        tabFeed.setOnClickListener {
            updateTabs(tabFeed, tabRanking, layoutFeed, layoutRanking, fabAddPost, true)
        }

        tabRanking.setOnClickListener {
            updateTabs(tabFeed, tabRanking, layoutFeed, layoutRanking, fabAddPost, false)
        }

        fabAddPost.setOnClickListener {
            showAddPostDialog()
        }

        loadPosts()
    }

    private fun updateTabs(t1: TextView, t2: TextView, l1: View, l2: View, fab: View, isFeed: Boolean) {
        t1.setBackgroundResource(if (isFeed) R.drawable.icon_circle_bg else 0)
        t1.setTextColor(resources.getColor(if (isFeed) R.color.white else R.color.text_muted, null))
        t2.setBackgroundResource(if (!isFeed) R.drawable.icon_circle_bg else 0)
        t2.setTextColor(resources.getColor(if (!isFeed) R.color.white else R.color.text_muted, null))
        
        l1.visibility = if (isFeed) View.VISIBLE else View.GONE
        l2.visibility = if (!isFeed) View.VISIBLE else View.GONE
        fab.visibility = if (isFeed) View.VISIBLE else View.GONE
    }

    private fun loadPosts() {
        ApiClient.apiService.getPublicaciones().enqueue(object : Callback<List<Publicacion>> {
            override fun onResponse(call: Call<List<Publicacion>>, response: Response<List<Publicacion>>) {
                if (response.isSuccessful) {
                    postsList.clear()
                    response.body()?.let { postsList.addAll(it.reversed()) }
                    postAdapter = PostAdapter(postsList)
                    rvPosts.adapter = postAdapter
                }
            }
            override fun onFailure(call: Call<List<Publicacion>>, t: Throwable) {
                Toast.makeText(this@SocialActivity, "Error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun showAddPostDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_new_post, null)
        val etContent = dialogView.findViewById<EditText>(R.id.etPostContent)
        val btnCancel = dialogView.findViewById<android.widget.Button>(R.id.btnCancelPost)
        val btnConfirm = dialogView.findViewById<android.widget.Button>(R.id.btnConfirmPost)

        val builder = AlertDialog.Builder(this)
        builder.setView(dialogView)
        val dialog = builder.create()

        btnCancel.setOnClickListener { dialog.dismiss() }
        btnConfirm.setOnClickListener {
            val content = etContent.text.toString().trim()
            if (content.isNotEmpty()) {
                createNewPost(content)
                dialog.dismiss()
            } else {
                Toast.makeText(this, "Escribe algo antes de publicar", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }

    private fun createNewPost(content: String) {
        Toast.makeText(this, "Enviando publicación...", Toast.LENGTH_SHORT).show()
        val prefs = getSharedPreferences("gymmanager_prefs", MODE_PRIVATE)
        val username = prefs.getString("username", "Usuario") ?: "Usuario"
        val userId = prefs.getLong("user_id", 1L)

        val newPost = Publicacion(contenido = content, autor = username, usuarioId = userId)
        
        ApiClient.apiService.createPublicacion(newPost).enqueue(object : Callback<Publicacion> {
            override fun onResponse(call: Call<Publicacion>, response: Response<Publicacion>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@SocialActivity, "¡Publicado!", Toast.LENGTH_SHORT).show()
                    loadPosts()
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Error desconocido"
                    Toast.makeText(this@SocialActivity, "Error al publicar: $errorMsg", Toast.LENGTH_LONG).show()
                }
            }
            override fun onFailure(call: Call<Publicacion>, t: Throwable) {
                Toast.makeText(this@SocialActivity, "Error de red: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}
