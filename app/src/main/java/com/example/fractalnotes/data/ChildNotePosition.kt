package com.example.fractalnotes.data

import androidx.compose.ui.geometry.Offset
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

data class ChildNoteSerializable(val x: Float, val y: Float, val ID: Long)
data class ChildNotePosition(val position: Offset, val ID: Long)

fun ChildNotePosition.toSerializable() = ChildNoteSerializable(
    x = position.x, y = position.y, ID = ID
)

fun ChildNoteSerializable.toChildNotePosition() = ChildNotePosition(
    position = Offset(x,y), ID = ID
)

fun List<ChildNotePosition>.toIconPositionJson(): String {
    val positions = this.map { it.toSerializable() }
    return Gson().toJson(positions)
}

fun String.toChildNotePositions(): List<ChildNotePosition> {
    return try {
        val type = object : TypeToken<List<ChildNoteSerializable>>() {}.type
        val positions: List<ChildNoteSerializable> = Gson().fromJson(this, type)
        positions.map { it.toChildNotePosition() }
    } catch (e: Exception) { emptyList() }
}
