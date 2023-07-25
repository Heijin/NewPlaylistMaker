package com.example.newplaylistmaker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        //val imageClickListener: View.OnClickListener = object : View.OnClickListener { override fun onClick(v: View?) {
            //Toast.makeText(this@MainActivity, "Нажали на id_button_search!", Toast.LENGTH_SHORT).show()
        //} }

        //button_search.setOnClickListener(imageClickListener)


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