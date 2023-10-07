package com.example.newplaylistmaker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val buttonSearch = findViewById<Button>(R.id.id_button_search)
        buttonSearch.setOnClickListener {
            //Toast.makeText(this@MainActivity, "Нажали на id_button_search!", Toast.LENGTH_SHORT).show()
            val displayIntent = Intent(this, SearchActivity::class.java)
            startActivity(displayIntent)
        }


        val buttonLibrary = findViewById<Button>(R.id.id_button_library)
        buttonLibrary.setOnClickListener {
           // Toast.makeText(this@MainActivity, "Нажали на id_button_library!", Toast.LENGTH_SHORT).show()
            val displayIntent = Intent(this, LibraryActivity::class.java)
            startActivity(displayIntent)
        }

        val buttonSettings = findViewById<Button>(R.id.id_button_settings)
        buttonSettings.setOnClickListener {
           // Toast.makeText(this@MainActivity, "Нажали на id_button_settings!", Toast.LENGTH_SHORT).show()
            val displayIntent = Intent(this, SettingsActivity::class.java)
            startActivity(displayIntent)
        }

    }
}