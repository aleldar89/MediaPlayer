package ru.netology.mediaplayer.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import ru.netology.mediaplayer.BuildConfig
import ru.netology.mediaplayer.MediaLifecycleObserver
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
                viewmodel.playPause(track)
                observer.apply {
                    mediaPlayer?.setDataSource(
                        BuildConfig.BASE_URL + track.id.toString() + ".mp3"
                    )

                    mediaPlayer?.setOnCompletionListener {
                        it.setDataSource(
                            if (track.id == viewmodel.tracks.value?.last()?.id)
                                BuildConfig.BASE_URL + "1.mp3"
                            else
                                BuildConfig.BASE_URL + (track.id + 1).toString() + ".mp3"
                        )
                    }
                }.play()
            }
        })

        binding.apply {
            albumName.text = viewmodel.data.value?.title
            artistName.text = viewmodel.data.value?.artist
            published.text = viewmodel.data.value?.published
            genre.text = viewmodel.data.value?.genre

            playAlbum.setOnClickListener {
                observer.apply {
                    mediaPlayer?.setDataSource(
                        BuildConfig.BASE_URL + "1.mp3"
                    )
                }.play()
            }
        }

        binding.list.adapter = adapter
        viewmodel.data.observe(this) {
            adapter.submitList(it.tracks)
        }

    }
}

//        val videoView = findViewById<VideoView>(R.id.videoView)
//
//        findViewById<View>(R.id.play).setOnClickListener {
//            videoView.apply {
//                setMediaController(MediaController(this@MainActivity))
//                setVideoURI(
//                    Uri.parse("https://archive.org/download/BigBuckBunny1280x720Stereo/big_buck_bunny_720_stereo.mp4")
//                )
//                setOnPreparedListener {
//                    start()
//                }
//                setOnCompletionListener {
//                    stopPlayback()
//                }
//            }
//        }