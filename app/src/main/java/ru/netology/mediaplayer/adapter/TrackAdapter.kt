package ru.netology.mediaplayer.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.mediaplayer.R
import ru.netology.mediaplayer.databinding.TrackItemBinding
import ru.netology.mediaplayer.dto.Track


class TrackAdapter(
    private val onInteractionListener: OnInteractionListener
) : ListAdapter<Track, TrackViewHolder>(PostDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val binding = TrackItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TrackViewHolder(binding, onInteractionListener)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val track = getItem(position)
        holder.bind(track)
    }
}

interface OnInteractionListener {
    fun onPlay(track: Track) {}
}

class TrackViewHolder(
    private val binding: TrackItemBinding,
    private val onInteractionListener: OnInteractionListener
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(track: Track) {
        binding.apply {
            trackName.text = track.file

            play.setImageResource(
                if (track.play)
                    R.drawable.ic_baseline_pause_album_36
                else
                    R.drawable.ic_baseline_play_album_36
            )

            play.setOnClickListener {
                onInteractionListener.onPlay(track)
            }
        }
    }
}

class PostDiffCallback : DiffUtil.ItemCallback<Track>() {
    override fun areItemsTheSame(oldItem: Track, newItem: Track): Boolean {
        if (oldItem::class != newItem::class) {
            return false
        }

        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Track, newItem: Track): Boolean {
        return oldItem == newItem
    }
}