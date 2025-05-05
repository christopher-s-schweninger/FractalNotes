package com.example.fractalnotes.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes")
    fun getAllNotes(): Flow<List<Note>>

    @Query("SELECT * FROM notes WHERE NoteId = :id")
    fun getNoteById(id: Long): Flow<Note?>

    @Update
    suspend fun updateNote(note: Note)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: Note): Long

    @Delete
    suspend fun delete(note: Note)
}