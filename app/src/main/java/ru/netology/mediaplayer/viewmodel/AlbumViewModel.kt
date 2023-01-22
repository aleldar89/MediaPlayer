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

    init {
        loadAlbum()
    }

    fun playPause(track: Track) {
        val tracking = _data.value?.tracks.orEmpty()
            .map {
                if (it.id != track.id)
                    it.copy(play = false) // Чтобы не было одновременно несколько треков в play
                else
                    it.copy(play = !it.play)
            }
        _data.value = _data.value?.copy(tracks = tracking)
    }

    fun loadAlbum() {
        viewModelScope.launch {
            try {
                _data.value = repository.getAlbum().apply {
                    this.tracks.map {
                        it.play = false
                    }
                }
            } catch (e: Exception) {
                println("Album loading error")
            }
        }
    }

}