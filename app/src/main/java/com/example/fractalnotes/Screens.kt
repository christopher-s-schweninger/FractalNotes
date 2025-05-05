package com.example.fractalnotes

sealed class Screens(val route: String) {
    object NoteList : Screens("note_list")
    object NoteDisplay : Screens("note_display/{noteId}") {
        fun passId(noteId: Long) = "note_display/$noteId"
    }
}