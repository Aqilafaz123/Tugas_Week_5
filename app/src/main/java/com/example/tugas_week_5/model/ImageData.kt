package com.example.tugas_week_5.model

import com.squareup.moshi.Json

data class ImageData(
    val id: String,
    @Json(name = "url") val imageUrl: String,
    val width: Int,
    val height: Int,
    val breeds: List<CatBreedData>? = null // ✅ tambahkan ini
)
