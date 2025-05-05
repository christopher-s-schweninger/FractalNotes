package com.example.fractalnotes

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.Absolute.SpaceEvenly
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.example.fractalnotes.data.Note
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fractalnotes.data.ChildNotePosition
import com.example.fractalnotes.data.SerializablePath
import com.example.fractalnotes.data.loadPathsFromJson
import com.example.fractalnotes.data.savePathsToJson
import com.example.fractalnotes.data.toChildNotePositions
import com.example.fractalnotes.data.toIconPositionJson
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun NoteDisplayView(
    note: Note,
    onBack: () -> Unit,
    onSaveTitle: (String) -> Unit,
    onSaveText: (String) -> Unit,
    onSaveDrawing: (String) -> Unit,
    onSaveChildren: (String) -> Unit,
    onNavigateToNewNote: (Long) -> Unit) {

    val viewModel: FractalNotesViewModel = viewModel()
    val scope = rememberCoroutineScope()
    val droppedIcons = remember { mutableStateListOf<ChildNotePosition>() }
    var dragging by remember { mutableStateOf(false) }
    var isEditing by remember { mutableStateOf(false) }
    var dragOffset by remember { mutableStateOf(Offset.Zero)}
    var layoutOffset by remember { mutableStateOf(Offset.Zero) }
    var boxWidth by remember { mutableStateOf<Float>(0.0f) }
    var boxHeight by remember { mutableStateOf<Float>(0.0f) }
    val initialPaths = remember(note.DrawImgPath) {
        if (note.DrawImgPath != null && note.DrawImgPath!!.isNotEmpty()) {
            try {
                loadPathsFromJson(note.DrawImgPath!!)
            } catch (e: Exception) {
                emptyList()
            }
        } else emptyList()
    }
    var titleInput by remember { mutableStateOf(note.Title) }
    var localText by remember { mutableStateOf(note.NoteText) }

    LaunchedEffect(note) {
        note.ChildNotes?.let {
            droppedIcons.clear()
            droppedIcons.addAll(it.toChildNotePositions())
        }
    }
    val iconPainter = painterResource(id = R.drawable.icon_note)


    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val totalHeight = maxHeight
        val topBarHeight = maxHeight * 0.1f
        val canvasAreaHeight = maxHeight * 0.8f
        val bottomBarHeight = maxHeight * 0.1f
        Column(modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(topBarHeight)
                    .padding(5.dp)
                    .background(color = Color.DarkGray),
                horizontalArrangement = SpaceEvenly
            ) {
                Button(onClick = onBack, modifier = Modifier) {
                    Text(text = "Back")
                }
                OutlinedTextField(
                    value = titleInput,
                    label = { Text(text = "Go back to save title", color = Color.White) },
                    onValueChange = { newText ->
                        titleInput = newText.trimStart { it == '0' }
                        onSaveTitle(newText.trimStart { it == '0' })
                    }
                )
            }

            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(width = 4.dp, color = Color.Green)
                    .height(canvasAreaHeight)
                    .onGloballyPositioned { coordinates ->
                        val position = coordinates.localToWindow(Offset.Zero)
                        layoutOffset = position
                    }
            ) {
                boxWidth = constraints.maxWidth.toFloat()
                boxHeight = constraints.maxHeight.toFloat()

                DrawingCanvasView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(canvasAreaHeight),
                    initialPaths = initialPaths,
                    currentColor = Color.Black,
                    currentThickness = 10f,
                    onPathsChange = { updatedPaths ->
                        val serialized = savePathsToJson(updatedPaths)
                        onSaveDrawing(serialized)
                    }
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .height(canvasAreaHeight)
                ) {
                    if (isEditing) {
                        BasicTextField(
                            value = localText,
                            onValueChange = {
                                localText = it
                                onSaveText(it)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(canvasAreaHeight)
                                .padding(8.dp)
                        )
                    } else {
                        Text(
                            text = localText,
                            color = Color.Black,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(canvasAreaHeight)
                                .padding(8.dp)
                        )
                    }
                }

                droppedIcons.forEach { icon ->
                    Image(
                        painter = iconPainter,
                        contentDescription = "Dropped Icon",
                        modifier = Modifier
                            .size(36.dp)
                            .offset {
                                IntOffset(
                                    icon.position.x.roundToInt(),
                                    icon.position.y.roundToInt()
                                )
                            }
                            .pointerInput(Unit) {
                                detectTapGestures {
                                    val iconJson = droppedIcons.toIconPositionJson()
                                    onSaveChildren(iconJson)
                                    onNavigateToNewNote(icon.ID)
                                }
                            }
                    )
                }

                if (dragging) {
                    Image(
                        painter = iconPainter,
                        contentDescription = "Dropped Icon",
                        modifier = Modifier
                            .size(36.dp)
                            .offset {
                                IntOffset(
                                    dragOffset.x.roundToInt(),
                                    dragOffset.y.roundToInt()
                                )
                            }
                    )
                }
            }

            Row(
                horizontalArrangement = SpaceEvenly,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
                    .background(color = Color.DarkGray)
                    .height(bottomBarHeight)
            ) {
                Button(
                    onClick = {
                        if (isEditing) {
                            isEditing = false
                        } else {
                            isEditing = true
                        }
                    },
                    modifier = Modifier
                ) {
                    Text(if (isEditing) "Done Editing" else "Edit Text")
                }

                Image(
                    painter = painterResource(id = R.drawable.icon_note),
                    contentDescription = "Draggable fractal note",
                    modifier = Modifier
                        .size(48.dp)
                        .pointerInput(Unit) {
                            detectDragGestures(
                                onDragStart = { offset ->
                                    dragging = true
                                    dragOffset = offset
                                },
                                onDrag = { change, dragAmount ->
                                    change.consume()
                                    val newOffset = dragOffset + dragAmount
                                    dragOffset = Offset(
                                        x = newOffset.x.coerceIn(0f, boxWidth - 48.dp.toPx()),
                                        y = newOffset.y.coerceIn(0f, boxHeight - 48.dp.toPx())
                                    )
                                },
                                onDragEnd = {
                                    dragging = false
                                    scope.launch {
                                        val newNoteId = viewModel.addNote(note.Title, note.NoteId)
                                        val newIcon = ChildNotePosition(dragOffset, newNoteId)
                                        droppedIcons.add(newIcon)
                                    }
                                }
                            )
                        }
                )

                Button(onClick = {
                    onSaveDrawing("")
                }) {
                    Text("Clear Drawing")
                }

                //            Button(onClick = {
                //                drawingViewRef?.clearDrawingBoard()
                //            }) {
                //                Text("Clear Notes")
                //            }
            }
        }
    }
}

//fun saveViewAsImage(context: Context, view: View): String {
//    val bitmap = Bitmap.createBitmap(
//        view.width,
//        view.height,
//        Bitmap.Config.ARGB_8888
//    )
//    val canvas = Canvas(bitmap)
//    view.draw(canvas)
//
//    val filename = "drawing_${System.currentTimeMillis()}.png"
//    val file = File(context.filesDir, filename)
//    val outputStream = FileOutputStream(file)
//    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
//    outputStream.flush()
//    outputStream.close()
//    Log.d("FilePath", "Path: ${file.absolutePath}")
//    return file.absolutePath
//}

@Composable
fun DrawingCanvasView(
    modifier: Modifier = Modifier,
    initialPaths: List<SerializablePath> = emptyList(),
    currentColor: Color = Color.Black,
    currentThickness: Float = 8f,
    onPathsChange: (List<SerializablePath>) -> Unit
) {
    val paths = remember { mutableStateListOf<SerializablePath>() }
    val currentPoints = remember { mutableStateListOf<Offset>() }

    var isDrawing by remember { mutableStateOf(false) }

    // Load initial paths once
    LaunchedEffect(Unit) {
        paths.addAll(initialPaths)
    }

    Box(modifier = modifier
        .pointerInput(Unit) {
            detectDragGestures(
                onDragStart = {
                    currentPoints.clear()
                    currentPoints.add(it)
                    isDrawing = true
                },
                onDrag = { _, dragAmount ->
                    val newPoint = currentPoints.last() + dragAmount
                    currentPoints.add(newPoint)
                },
                onDragEnd = {
                    if (currentPoints.isNotEmpty()) {
                        val newPath = SerializablePath(
                            points = currentPoints.toList(),
                            color = currentColor.toArgb(),
                            brushThickness = currentThickness,
                            alpha = 1f
                        )
                        paths.add(newPath)
                        onPathsChange(paths.toList())
                    }
                    currentPoints.clear()
                    isDrawing = false
                }
            )
        }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            paths.forEach { path ->
                drawPathFromCustom(path)
            }

            if (isDrawing && currentPoints.isNotEmpty()) {
                drawPathFromCustom(
                    SerializablePath(
                        points = currentPoints,
                        color = currentColor.toArgb(),
                        brushThickness = currentThickness,
                        alpha = 1f
                    )
                )
            }
        }
    }
}

fun DrawScope.drawPathFromCustom(path: SerializablePath) {
    if (path.points.size < 2) return

    for (i in 0 until path.points.size - 1) {
        drawLine(
            color = Color(path.color).copy(alpha = path.alpha),
            start = path.points[i],
            end = path.points[i + 1],
            strokeWidth = path.brushThickness,
            cap = StrokeCap.Round
        )
    }
}