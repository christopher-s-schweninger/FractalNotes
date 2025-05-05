package com.example.fractalnotes

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import androidx.lifecycle.asLiveData
import com.example.fractalnotes.data.Note
import com.example.fractalnotes.data.NoteDatabase
import com.example.fractalnotes.data.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class FractalNotesViewModel(application: Application) : AndroidViewModel(application) {
    private val nrepo: NoteRepository
    val notes: LiveData<List<Note>>

    init {
        val noteDao = NoteDatabase.getDatabase(application).noteDao()
        nrepo = NoteRepository(noteDao)
        notes = nrepo.notes.asLiveData()
    }

    fun addNote(title: String, originID: Long): Long {
        return runBlocking {
            nrepo.addNote(
                Note(
                    HomeNote = false,
                    OriginId = originID,
                    Title = title,
                    NoteText = ""
                )
            )
        }
    }

    fun addHomeNote(title: String, callback: (Long) -> Unit) {
        viewModelScope.launch {
            val id = nrepo.addNote(Note(HomeNote = true, OriginId = -1, Title = title, NoteText = ""))
            callback(id)
        }
    }

    fun deleteNote(note: Note) = viewModelScope.launch {
        nrepo.deleteNote(note)
    }

    fun updateNote(note: Note) {
        viewModelScope.launch {
            nrepo.updateNote(note)
            Log.d("NoteViewModel", "Note updated: ${note}")
        }
    }

    fun getNoteById(id: Long): Flow<Note?> {
        return nrepo.getNoteById(id)
    }

}