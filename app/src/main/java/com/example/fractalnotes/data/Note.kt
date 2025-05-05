package com.example.fractalnotes.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class Note(
    // The ID of the note
    @PrimaryKey(autoGenerate = true) val NoteId: Long = 0,
    // Boolean confirmation of Home Note
    val HomeNote: Boolean,
    // The ID of the note that we just came from. If Home Note, Origin is -1
    val OriginId: Long,
    // The title of the note
    var Title: String,
    // This is the 'drawn' image of the note
    var DrawImgPath: String? = null,
    // This is the text that exists within a note
    val NoteText: String,
    // Idea to have drag and drop text interactables
    // var TextInteractables: List<Pair<String, Pair<Int, Int>>>,
    // This is all of the child notes and their position on this note
    var ChildNotes: String? = null

)
