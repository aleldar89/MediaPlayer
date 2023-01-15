package ru.netology.mediaplayer.ui

import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
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
    private var currentPos: Int? = null
    private var trackId = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewmodel: AlbumViewModel by viewModels()

        lifecycle.addObserver(observer)

        val adapter = TrackAdapter(object : OnInteractionListener {
            override fun onPlay(track: Track) {
                trackId = track.id

                observer.apply {
                    /** если играет -> пауза */
                    if (mediaPlayer?.isPlaying == true) {
                        binding.playAlbum.setImageResource(R.drawable.ic_baseline_play_album_36)
                        currentPos = mediaPlayer?.currentPosition
                        mediaPlayer?.pause()
                    } else {
                        /** если паузы нет - проигрываем с начала */
                        if (currentPos == null) {
                            binding.playAlbum.setImageResource(R.drawable.ic_baseline_pause_album_36)
                            mediaPlayer?.stop()
                            mediaPlayer?.reset()
                            mediaPlayer?.setDataSource(
                                BuildConfig.BASE_URL + track.id.toString() + ".mp3"
                            )
                            observer.play()
                        } else {
                            /** если пауза есть - продолжаем */
                            binding.playAlbum.setImageResource(R.drawable.ic_baseline_pause_album_36)
                            mediaPlayer?.seekTo(currentPos ?: 0)
                            mediaPlayer?.start()

//TODO придумать проверку
//                            if (currentTrackId == track.id) {
//                                mediaPlayer?.seekTo(currentPos ?: 0)
//                                mediaPlayer?.start()
//                            } else {
//                                mediaPlayer?.stop()
//                                mediaPlayer?.reset()
//                                mediaPlayer?.setDataSource(
//                                    BuildConfig.BASE_URL + track.id.toString() + ".mp3"
//                                )
//                                observer.play()
//                            }

                        }
                    }
                }

                /** переключаем состояние трека с паузы-false на play-true и наоборот */
                viewmodel.playPause(track)
            }
        })

        /** этот блок крашит */
//        observer.apply {
//            mediaPlayer?.setOnCompletionListener {
//                currentPos = null
//                mediaPlayer?.release()
//                mediaPlayer = null
//                mediaPlayer?.setDataSource(
//                    if (trackId == viewmodel.data.value?.tracks?.last()?.id) {
//                        BuildConfig.BASE_URL + "1.mp3"
//                    } else {
//                        BuildConfig.BASE_URL + (trackId + 1).toString() + ".mp3"
//                    }
//                )
//            }
//        }.play()

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