package ru.netology.mediaplayer.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.OkHttpClient
import okhttp3.Request
import ru.netology.mediaplayer.BuildConfig
import ru.netology.mediaplayer.dto.Album
import java.util.concurrent.TimeUnit

class AlbumRepositoryImpl: AlbumRepository  {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .build()

    private val gson = Gson()
    private val typeToken = object : TypeToken<Album>() {}

    companion object {
        private const val BASE_URL = "${BuildConfig.BASE_URL}album.json"
    }

    override suspend fun getAlbum(): Album {
        val request: Request = Request.Builder()
            .url(BASE_URL)
            .build()

        return client.newCall(request)
            .execute()
            .let {
                it.body?.string() ?: throw RuntimeException("body is null")
            }
            .let {
                gson.fromJson(it, typeToken.type)
            }
    }

}