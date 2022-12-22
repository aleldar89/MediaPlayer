package ru.netology.mediaplayer.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.netology.mediaplayer.dto.Album
import ru.netology.mediaplayer.dto.Track
import ru.netology.mediaplayer.repository.AlbumRepository
import ru.netology.mediaplayer.repository.AlbumRepositoryImpl

private val defaultAlbum = Album(
    id = 0L,
    title = "",
    subtitle = "",
    artist = "",
    published = "",
    genre = "",
    tracks = emptyList()
)

class AlbumViewModel : ViewModel() {

    private val repository: AlbumRepository = AlbumRepositoryImpl()

    private val _data = MutableLiveData(defaultAlbum)
    val data: LiveData<Album>
        get() = _data

    private val _tracks = MutableLiveData(emptyList<Track>())
    val tracks: MutableLiveData<List<Track>>
        get() = _tracks

    init {
        loadAlbum()
    }

    fun playPause(track: Track) {
        val tracking = tracks.value?.map {
            if (it.id != track.id)
                it
            else
                it.copy(play = !it.play)
        }
        _tracks.value = tracking
    }

    fun loadAlbum() {
        viewModelScope.launch {
            try {
                _data.value = repository.getAlbum().apply {
                    this.tracks.map {
                        it.play = true
                    }
                }
                _tracks.value = data.value?.tracks
            } catch (e: Exception) {
                println("Album loading error")
            }
        }
    }

}