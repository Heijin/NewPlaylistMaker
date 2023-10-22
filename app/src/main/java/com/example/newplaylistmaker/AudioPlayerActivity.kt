package com.example.newplaylistmaker

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.newplaylistmaker.databinding.ActivityAudioPlayerBinding
import com.google.gson.Gson

class AudioPlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAudioPlayerBinding
    private val handler = Handler(Looper.getMainLooper())
    private var playerState = STATE_DEFAULT
    private var mediaPlayer = MediaPlayer()
    private var trackJson = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAudioPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener {
            finish()
            mediaPlayer.stop()
        }

        val trackJson = intent.extras?.getString("track") ?: ""
        loadTrackInfo(trackJson)

        binding.playPause.setOnClickListener{
            when (playerState) {
                STATE_PREPARED, STATE_PAUSED -> startPlayer()
                STATE_PLAYING -> pausePlayer()
            }
        }
    }

    private fun startTrackTimer() {
        handler.post(createUpdateTimerTask())
    }

    private fun createUpdateTimerTask(): Runnable {
        return object : Runnable {
            override fun run() {
                if (mediaPlayer.isPlaying) {
                    val currentPosition = mediaPlayer.currentPosition
                    // Сделаем синхронизацию на старте, т.к. наш таймер срабатывает чуть раньше, чем фактическое время игры трека.
                    binding.itemTrackTime.text =
                        CommonModule().getMMSSFormat(currentPosition.toString())

                    if (currentPosition < 1000L)
                        handler.postDelayed(this, DELAY - currentPosition + 50L)
                    else
                        handler.postDelayed(this, DELAY)
                }
            }
        }
    }

    override fun onStop() {
        pausePlayer()
        super.onStop()
    }
    private fun loadTrackInfo(trackJson: String) {
        val track = Gson().fromJson(trackJson, Track::class.java)
        binding.trackName.text = track.trackName
        binding.artistName.text = track.artistName
        binding.trackTimeMills.text = CommonModule().getMMSSFormat(track.trackTime)
        binding.collectionName.text = track.collectionName
        binding.releaseDate.text = track.releaseDate.take(4)
        binding.primaryGenreName.text = track.primaryGenreName
        binding.country.text = track.country

        val itemImage = findViewById<ImageView>(R.id.item_image)

        Glide.with(itemImage)
            .load(track.artworkUrl100.replaceAfterLast('/',"512x512bb.jpg"))
            .placeholder(R.drawable.vector_placeholder)
            .fitCenter()
            .transform(RoundedCorners(CommonModule().dpToPx(8)))
            .into(itemImage)

        mediaPlayer.setDataSource(track.previewUrl)
        mediaPlayer.prepareAsync()

        mediaPlayer.setOnPreparedListener {
            binding.playPause.isClickable = true
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            binding.playPause.setImageResource(R.drawable.vector_button_play)
            binding.itemTrackTime.setText(R.string.zeroTime)
            playerState = STATE_PREPARED
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        playerState = STATE_PLAYING
        binding.playPause.setImageResource(R.drawable.vector_button_pause)
        startTrackTimer()
    }
    private fun pausePlayer () {
        mediaPlayer.pause()
        playerState = STATE_PAUSED
        binding.playPause.setImageResource(R.drawable.vector_button_play)
        binding.itemTrackTime.text = CommonModule().getMMSSFormat(mediaPlayer.currentPosition.toString())
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
        private const val DELAY = 1000L
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
    }
}