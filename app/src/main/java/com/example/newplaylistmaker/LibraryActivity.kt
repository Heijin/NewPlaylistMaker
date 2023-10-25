package com.example.newplaylistmaker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.newplaylistmaker.databinding.ActivityLibraryBinding

class LibraryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityLibraryBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}