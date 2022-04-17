package com.example.happyplacesapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.example.happyplacesapp.R
import com.example.happyplacesapp.databinding.ActivityLottieAnimationBinding

class LottieAnimationActivity : AppCompatActivity() {
    var binding: ActivityLottieAnimationBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLottieAnimationBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        Handler().postDelayed(Runnable {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        },2000)
    }
}