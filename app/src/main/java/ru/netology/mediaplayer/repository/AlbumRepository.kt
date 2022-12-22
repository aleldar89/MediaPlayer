package ru.netology.mediaplayer.repository

import ru.netology.mediaplayer.dto.Album

interface AlbumRepository {
    suspend fun getAlbum(): Album
}