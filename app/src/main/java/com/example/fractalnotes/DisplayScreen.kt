package com.example.fractalnotes

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresExtension
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.fractalnotes.data.Note

@RequiresExtension(extension = Build.VERSION_CODES.S, version = 13)
@Composable
fun DisplayScreen(viewModel: FractalNotesViewModel) {
    val navController = rememberNavController()

    val notes by viewModel.notes.observeAsState(emptyList())

    NavHost(navController = navController, startDestination = Screens.NoteList.route) {
        composable(Screens.NoteList.route) {
            NoteListView(
                notes = notes,
                onNoteClick = { noteId ->
                    Log.d("Navigation", "Selected note: $noteId")
                    navController.navigate(Screens.NoteDisplay.passId(noteId))
                },
                onCreate = {
                    viewModel.addHomeNote(it) { id ->
                        navController.navigate(Screens.NoteDisplay.passId(id))
                    }
                },
                onDelete = {
                    viewModel.deleteNote(it)
                }
            )
        }
        composable(
            route = Screens.NoteDisplay.route,
            arguments = listOf(navArgument("noteId") { type = NavType.LongType})
        ) { backStackEntry ->
            val noteId = backStackEntry.arguments?.getLong("noteId") ?: return@composable
            Log.d("NoteDebug", "selectedNoteID = $noteId")
            val noteState = remember { mutableStateOf<Note?>(null) }
            LaunchedEffect(noteId) {
                viewModel.getNoteById(noteId).collect { note ->
                    noteState.value = note
                }
            }
            val selNote = noteState.value
            Log.d("NoteDebug", "selectedNote = $selNote")

            selNote?.let { note ->
                NoteDisplayView(
                    note = note,
                    onBack = {
                        if (note.HomeNote) {
                            navController.popBackStack()
                            navController.navigate(Screens.NoteList.route)
                        } else {
                            navController.navigate(Screens.NoteDisplay.passId(note.OriginId))
                        }
                    },
                    onSaveTitle = { title ->
                        val updatedNote = note.copy(Title = title)
                        viewModel.updateNote(updatedNote)
                    },
                    onSaveText = { text ->
                        val updatedNote = note.copy(NoteText = text)
                        viewModel.updateNote(updatedNote)
                    },
                    onSaveDrawing = { pathsJson ->
                        val updatedNote = note.copy(DrawImgPath = pathsJson)
                        viewModel.updateNote(updatedNote)

                    },
                    onSaveChildren = { iconJson ->
                        val updatedNote = note.copy(ChildNotes = iconJson)
                        viewModel.updateNote(updatedNote)
                    },
                    onNavigateToNewNote = { ID ->
                        navController.navigate(Screens.NoteDisplay.passId(ID))
                    }
                )
            }
        }
    }
}