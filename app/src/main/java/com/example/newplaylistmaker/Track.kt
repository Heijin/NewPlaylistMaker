package com.example.newplaylistmaker

import com.google.gson.annotations.SerializedName

data class Track(

    val trackName: String, // Название композиции
    val artistName: String, // Имя исполнителя
    @SerializedName("trackTimeMillis") val trackTime: String, // Продолжительность трека
    val artworkUrl100: String, // Ссылка на изображение обложки
    val trackId: String, // уникальный ID трека
    val collectionName: String, //Альбом
    val releaseDate: String, // Год выпуска
    val primaryGenreName: String, // Жанр
    val country: String, // Страна
    val previewUrl: String // Ссылка на отрывок в формате String

)
