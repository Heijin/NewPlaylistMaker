package com.example.newplaylistmaker

import android.content.res.Resources
import java.text.SimpleDateFormat
import java.util.Locale

class CommonModule {

    fun dpToPx(dp: Int) = (dp * Resources.getSystem().displayMetrics.density).toInt()

    fun getMMSSFormat(time: String): String = SimpleDateFormat("mm:ss", Locale.getDefault()).format(time.toLong())
}