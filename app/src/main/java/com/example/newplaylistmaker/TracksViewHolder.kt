package com.example.newplaylistmaker

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class TracksViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val itemTrackName: TextView = itemView.findViewById(R.id.item_trackName)
    private val itemArtistName: TextView = itemView.findViewById(R.id.item_artistName)
    private val itemTrackTime: TextView = itemView.findViewById(R.id.item_trackTime)
    private val itemImage: ImageView = itemView.findViewById(R.id.item_image)

    fun bind(item: Track) {
        Glide.with(itemView)
            .load(item.artworkUrl100)
            .placeholder(R.drawable.vector_placeholder)
            .fitCenter()
            .transform(RoundedCorners(Utils.dpToPx(2)))
            .into(itemImage)
        itemTrackName.text = item.trackName.trim()
        itemArtistName.text = item.artistName.trim()
        itemTrackTime.text = Utils.getMMSSFormat(item.trackTime)
    }
}