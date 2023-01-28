package com.example.study_apt

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.study_apt.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.goToRepository.setOnClickListener {

        }

        binding.goToUserActivity.setOnClickListener {

        }
    }

}