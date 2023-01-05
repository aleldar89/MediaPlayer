package ru.netology.mediaplayer.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import ru.netology.mediaplayer.BuildConfig
import ru.netology.mediaplayer.MediaLifecycleObserver
import ru.netology.mediaplayer.R
import ru.netology.mediaplayer.adapter.OnInteractionListener
import ru.netology.mediaplayer.adapter.TrackAdapter
import ru.netology.mediaplayer.databinding.ActivityMainBinding
import ru.netology.mediaplayer.dto.Track
import ru.netology.mediaplayer.viewmodel.AlbumViewModel

class MainActivity : AppCompatActivity() {

    private val observer = MediaLifecycleObserver()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewmodel: AlbumViewModel by viewModels()

        lifecycle.addObserver(observer)

        val adapter = TrackAdapter(object : OnInteractionListener {
            override fun onPlay(track: Track) {

                if (!track.play) {
                    binding.playAlbum.setImageResource(R.drawable.ic_baseline_pause_album_36)

                    /** если трек на паузе-false -> запускаем */
                    observer.apply {
                        mediaPlayer?.setDataSource(
                            BuildConfig.BASE_URL + track.id.toString() + ".mp3"
                        )

                        /** если текущая позиция трека совпадает с длительностью трека -> переход */
                        if (mediaPlayer?.currentPosition == mediaPlayer?.duration) {
                            mediaPlayer?.release()
                            mediaPlayer = null
                            mediaPlayer?.setDataSource(
                                when (track.id) {
                                    1L -> BuildConfig.BASE_URL + track.id.toString() + ".mp3"
                                    viewmodel.tracks.value?.last()?.id -> BuildConfig.BASE_URL + "1.mp3"
                                    else -> BuildConfig.BASE_URL + (track.id + 1).toString() + ".mp3"
                                }
                            )
                        }

                    }.play()
                } else {
                    binding.playAlbum.setImageResource(R.drawable.ic_baseline_play_album_36)

                    /** если трек play-true -> пауза */
                    observer.apply {
                        mediaPlayer?.pause()
                    }
                }

                /** переключаем состояние трека с паузы-false на play-true и наоборот */
                viewmodel.playPause(track)
            }
        })

        binding.list.adapter = adapter
        viewmodel.data.observe(this) {
            binding.apply {
                albumName.text = it.title
                artistName.text = it.artist
                published.text = it.published
                genre.text = it.genre
            }
            adapter.submitList(it.tracks)
        }

    }
}