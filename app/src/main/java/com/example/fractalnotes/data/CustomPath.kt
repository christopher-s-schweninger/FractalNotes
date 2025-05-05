package com.example.fractalnotes.data

import androidx.compose.ui.geometry.Offset
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

data class SerializablePath(
    val points: List<Offset>,
    val color: Int,
    val brushThickness: Float,
    val alpha: Float
)

fun savePathsToJson(paths: List<SerializablePath>): String {
    val gson = Gson()
    return gson.toJson(paths)
}

fun loadPathsFromJson(json: String): List<SerializablePath> {
    val gson = Gson()
    val type = object : TypeToken<List<SerializablePath>>() {}.type
    return gson.fromJson(json, type)
}
