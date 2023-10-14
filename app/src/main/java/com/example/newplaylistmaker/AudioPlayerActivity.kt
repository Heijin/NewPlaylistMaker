package com.example.newplaylistmaker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.gson.Gson

class AudioPlayerActivity : AppCompatActivity() {

    private var trackJson = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_player)

        findViewById<ImageButton>(R.id.menu_button).setOnClickListener { finish() }

        val trackJson = intent.extras?.getString("track") ?: ""
        loadTrackInfo(trackJson)
    }

    private fun loadTrackInfo(trackJson: String) {
        val track = Gson().fromJson(trackJson, Track::class.java)
        findViewById<TextView>(R.id.trackName).text = track.trackName
        findViewById<TextView>(R.id.artistName).text = track.artistName
        findViewById<TextView>(R.id.trackTimeMills).text = CommonModule().getMMSSFormat(track.trackTime)
        findViewById<TextView>(R.id.collectionName).text = track.collectionName
        findViewById<TextView>(R.id.releaseDate).text = track.releaseDate.take(4)
        findViewById<TextView>(R.id.primaryGenreName).text = track.primaryGenreName
        findViewById<TextView>(R.id.country).text = track.country

        val itemImage = findViewById<ImageView>(R.id.item_image)

        Glide.with(itemImage)
            .load(track.artworkUrl100.replaceAfterLast('/',"512x512bb.jpg"))
            .placeholder(R.drawable.vector_placeholder)
            .fitCenter()
            .transform(RoundedCorners(CommonModule().dpToPx(8)))
            .into(itemImage)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(TRACK, trackJson)
    }
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        trackJson = savedInstanceState.getString(TRACK, "")
        loadTrackInfo(trackJson)
    }
    private companion object {
        const val TRACK = ""
    }
}