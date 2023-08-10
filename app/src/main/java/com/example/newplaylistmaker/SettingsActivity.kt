package com.example.newplaylistmaker

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class SettingsActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val buttonBack = findViewById<Button>(R.id.id_settings_back)
        buttonBack.setOnClickListener {
            //Toast.makeText(this@MainActivity, "Нажали на id_button_search!", Toast.LENGTH_SHORT).show()
            //val displayIntent = Intent(this, MainActivity::class.java)
            //startActivity(displayIntent)
            finish()
            //onBackPressed() \
        }

        val buttonShare = findViewById<Button>(R.id.id_settings_share)
        buttonShare.setOnClickListener {
            val linkToPracticum = getString(R.string.link_to_practicum)
            val sharePracticum = Intent(Intent.ACTION_SEND)
            sharePracticum.type = "text/plain"
            sharePracticum.putExtra(Intent.EXTRA_TEXT, linkToPracticum)
            startActivity(sharePracticum)
        }

        val buttonWriteTech = findViewById<Button>(R.id.id_settings_write_tech)
        buttonWriteTech.setOnClickListener {

            Intent(Intent.ACTION_SENDTO).apply {
                setDataAndType(Uri.parse("mailto:"), "text/plain")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.my_mail)))
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.subject_for_developers))
                putExtra(Intent.EXTRA_TEXT, getString(R.string.message_for_developers))
                startActivity(this)
            }
        }

        val buttonUserLic = findViewById<Button>(R.id.id_settings_user_lic)
        buttonUserLic.setOnClickListener {
            val userLicView = Intent(Intent.ACTION_VIEW)
            userLicView.data = Uri.parse(getString(R.string.practicum_offer))
            startActivity(userLicView)
        }

    }
}