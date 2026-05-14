package com.gymmanager.android.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import com.gymmanager.android.MainActivity
import com.gymmanager.android.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Entry Animations
        binding.txtLogo.animate()
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(1200)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .start()

        binding.txtSlogan.animate()
            .alpha(0.8f)
            .setStartDelay(600)
            .setDuration(800)
            .start()

        // Transition to Login or Main after 3 seconds
        Handler(Looper.getMainLooper()).postDelayed({
            // Here we could check if user is logged in
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }, 3000)
    }
}
