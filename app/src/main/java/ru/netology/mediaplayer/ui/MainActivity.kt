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
                observer.apply {
                    mediaPlayer?.setOnCompletionListener {
                        val tracks = viewmodel.data.value?.tracks.orEmpty()
                        val nextIndex = tracks.indexOfFirst {
                            it.id == track.id
                        }.takeIf {
                            it >= 0
                        }?.inc()
                            .takeIf {
                                it in tracks.indices
                            } ?: 0

                        val nextTrack = tracks[nextIndex]

                        mediaPlayer?.stop()
                        mediaPlayer?.release()
                        mediaPlayer?.setDataSource(
                            "${BuildConfig.BASE_URL}${nextTrack.id}.mp3"
                        )
                        observer.play()

                        trackId = nextTrack.id

                        viewmodel.playPause(nextTrack)
                    }

                    if (trackId != track.id) {
                        mediaPlayer?.stop()
                        mediaPlayer?.reset()
                        mediaPlayer?.setDataSource(
                            "${BuildConfig.BASE_URL}${track.id}.mp3"
                        )
                        observer.play()

                        trackId = track.id

                        viewmodel.playPause(track)
                        return
                    }

                    trackId = track.id

                    /** ???????? ???????????? - ?????????? */
                    if (mediaPlayer?.isPlaying == true) {
                        binding.playAlbum.setImageResource(R.drawable.ic_baseline_play_album_36)
                        currentPos = mediaPlayer?.currentPosition
                        mediaPlayer?.pause()
                    } else {
                        /** ???????? ?????????? ?????? - ?????????????????????? ?? ???????????? */
                        if (currentPos == null) {
                            binding.playAlbum.setImageResource(R.drawable.ic_baseline_pause_album_36)
                            mediaPlayer?.stop()
                            mediaPlayer?.reset()
                            mediaPlayer?.setDataSource(
                                "${BuildConfig.BASE_URL}${track.id}.mp3"
                            )
                            observer.play()
                        } else {
                            /** ???????? ?????????? ???????? - ???????????????????? */
                            binding.playAlbum.setImageResource(R.drawable.ic_baseline_pause_album_36)
                            mediaPlayer?.seekTo(currentPos ?: 0)
                            mediaPlayer?.start()
                        }
                    }
                }

                /** ?????????????????????? ?????????????????? ?????????? ?? ??????????-false ???? play-true ?? ???????????????? */
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