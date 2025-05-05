package com.example.fractalnotes.data

import kotlinx.coroutines.flow.Flow

class NoteRepository(private val dao: NoteDao) {
    val notes: Flow<List<Note>> = dao.getAllNotes()

    suspend fun addNote(note: Note) = dao.insert(note)
    suspend fun deleteNote(note: Note) = dao.delete(note)
    suspend fun updateNote(note: Note) = dao.updateNote(note)
    fun getNoteById(id: Long) = dao.getNoteById(id)
}