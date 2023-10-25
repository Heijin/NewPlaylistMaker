package com.example.newplaylistmaker

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.newplaylistmaker.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.idSettingsBack
            .setNavigationOnClickListener {
                finish()
            }

        binding.idSettingsShare.setOnClickListener {
            val linkToPracticum = getString(R.string.link_to_practicum)
            val sharePracticum = Intent(Intent.ACTION_SEND)
            sharePracticum.type = "text/plain"
            sharePracticum.putExtra(Intent.EXTRA_TEXT, linkToPracticum)
            startActivity(sharePracticum)
        }

        binding.idSettingsWriteTech.setOnClickListener {

            Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.my_mail)))
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.subject_for_developers))
                putExtra(Intent.EXTRA_TEXT, getString(R.string.message_for_developers))
                startActivity(this)
            }
        }

        binding.idSettingsUserLic.setOnClickListener {
            val userLicView = Intent(Intent.ACTION_VIEW)
            userLicView.data = Uri.parse(getString(R.string.practicum_offer))
            startActivity(userLicView)
        }

        binding.themeSwitcher.isChecked = (applicationContext as App).darkTheme
        binding.themeSwitcher.setOnCheckedChangeListener { _, checked ->
            (applicationContext as App).switchTheme(checked)
        }
    }
}