package com.example.fractalnotes

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresExtension
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.fractalnotes.data.Note

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@RequiresExtension(extension = Build.VERSION_CODES.S, version = 13)
@Composable
fun NoteListView(
    notes: List<Note>,
    onNoteClick: (Long) -> Unit,
    onCreate: (String) -> Unit,
    onDelete: (Note) -> Unit) {
    var input by remember { mutableStateOf("")}
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Saved Fractal Notes") })
        },
        bottomBar = {
            BottomAppBar(modifier = Modifier) {
                OutlinedTextField(
                    value = input,
                    onValueChange = { input = it },
                    placeholder = { Text("Enter title") },
                    modifier = Modifier.weight(1.0f).padding(end = 8.dp)
                )
                Text(
                    text = "Add New",
                    modifier = Modifier
                        .weight(0.3f)
                        .clickable {
                            if(input.isNotEmpty()) {
                                onCreate(input)
                                input = ""
                            }
                        }
                )
            }
        }) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
            items(notes) { note ->
                if (note.HomeNote) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNoteClick(note.NoteId) }
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(note.Title, style = MaterialTheme.typography.bodyLarge)
                        IconButton(onClick = { onDelete(note) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete")
                        }
                    }
                }
                HorizontalDivider()
            }
        }
    }
}
