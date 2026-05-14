package com.gymmanager.android

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.gymmanager.android.activity.*
import com.gymmanager.android.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupGridListeners()
        setupBottomNavigation()
        startEntryAnimations()
    }

    private fun startEntryAnimations() {
        val views = listOf(
            binding.btnAccess, binding.btnClasses, binding.btnRoutines,
            binding.btnStore, binding.btnProgress
        )
        
        views.forEachIndexed { index, view ->
            view.alpha = 0f
            view.translationY = 50f
            view.animate()
                .alpha(1f)
                .translationY(0f)
                .setStartDelay(100L * index)
                .setDuration(500)
                .start()
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.nav_home -> true
                R.id.nav_calendar -> {
                    startActivity(Intent(this, CalendarActivity::class.java))
                    true
                }
                R.id.nav_explore -> {
                    startActivity(Intent(this, ExploreActivity::class.java))
                    true
                }
                R.id.nav_social -> {
                    startActivity(Intent(this, SocialActivity::class.java))
                    true
                }
                R.id.nav_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    private fun setupGridListeners() {
        binding.btnRoutines.setOnClickListener {
            startActivity(Intent(this, RoutinesActivity::class.java))
        }

        binding.btnAccess.setOnClickListener {
            startActivity(Intent(this, QrAccessActivity::class.java))
        }

        binding.btnStore.setOnClickListener {
            startActivity(Intent(this, StoreActivity::class.java))
        }

        binding.btnProgress.setOnClickListener {
            Toast.makeText(this, "Registrando pesaje...", Toast.LENGTH_SHORT).show()
        }
        
        binding.btnClasses.setOnClickListener {
            startActivity(Intent(this, CalendarActivity::class.java))
        }
    }
}
