package com.example.newplaylistmaker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class TracksAdapter(
    private val items: List<Track>,
    private val onItemClickListener: OnItemClickListener? = null
) : RecyclerView.Adapter<TracksViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TracksViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_search, parent, false)
        return TracksViewHolder(view)
    }

    override fun onBindViewHolder(holder: TracksViewHolder, position: Int) {
        holder.bind(items[position])
        //if (!onItemClickListener == null)
        holder.itemView.setOnClickListener { onItemClickListener?.onItemClick(items[holder.adapterPosition]) }

    }
    override fun getItemCount() = items.size

}