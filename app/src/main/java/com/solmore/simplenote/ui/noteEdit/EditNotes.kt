package com.solmore.simplenote.ui.noteEdit

import android.annotation.SuppressLint
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.solmore.simplenote.util.Constants
import com.solmore.simplenote.NotesApp
import com.solmore.simplenote.R
import com.solmore.simplenote.model.Note
import com.solmore.simplenote.ui.GenericAppBar
import com.solmore.simplenote.ui.NotesViewModel
import com.solmore.simplenote.ui.theme.SimpleNoteTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter", "UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun NoteEditScreen(
    noteId: Int,
    navController: NavController,
    viewModel: NotesViewModel
){
    val scope = rememberCoroutineScope()
    val note = remember{ mutableStateOf(Constants.noteDetailPlaceHolder)}
    val currentNote = remember { mutableStateOf(note.value.note) }
    val currentTitle = remember { mutableStateOf(note.value.title) }
    val currentPhoto = remember { mutableStateOf(note.value.imageUri) }
    val saveButtonState = remember { mutableStateOf(false) }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    val getImageRequest = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->

        if (uri != null) {
            NotesApp.getUriPermission(uri)
        }
        currentPhoto.value = uri.toString()
        if (currentPhoto.value != note.value.imageUri) {
            saveButtonState.value = true
        }
    }

    LaunchedEffect(true){
        scope.launch(Dispatchers.IO) {
            note.value = viewModel.getNote(noteId) ?: Constants.noteDetailPlaceHolder
            currentNote.value = note.value.note
            currentTitle.value = note.value.title
            currentPhoto.value = note.value.imageUri
        }
    }

    SimpleNoteTheme{
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.primary) {
            Scaffold(
                topBar = {
                    GenericAppBar(
                        title = "Edit Note",
                        navController = navController,
                        icon = {
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.save),
                                contentDescription = stringResource(R.string.save_note),
                                tint = Color.Black
                            )
                        },
                        onIconClick = {
                            viewModel.updateNote(
                                Note(
                                    id = note.value.id,
                                    note = currentNote.value,
                                    title = currentTitle.value,
                                    imageUri = currentPhoto.value
                                )
                            )
                            navController.popBackStack()
                        },
                        iconState = saveButtonState
                    )
                },
                floatingActionButton = {
                    NotesFab(
                        contentDescription = stringResource(R.string.add_photo),
                        action = {
                            getImageRequest.launch(arrayOf("image/*"))
                        },
                        icon = R.drawable.camera
                    )
                },
                content = {
                    Column(
                        Modifier
                            .padding(12.dp)
                            .fillMaxSize()
                    ) {
                        if (currentPhoto.value != null && currentPhoto.value!!.isNotEmpty()){
                            Image(
                                painter = rememberAsyncImagePainter(
                                    ImageRequest
                                        .Builder(LocalContext.current)
                                        .data(data = Uri.parse(currentPhoto.value))
                                        .build()
                                ),
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight(0.5f)
                                    .padding(6.dp),
                                contentScale = ContentScale.Crop
                            )
                        }else {
                            Spacer(modifier = Modifier.padding(50.dp))
                        }
                        TextField(
                            value = currentTitle.value,
                            colors = TextFieldDefaults.colors(
                                cursorColor = Color.Black,
                                focusedContainerColor = Color.LightGray,
                                unfocusedContainerColor = Color.LightGray,
                                focusedLabelColor = Color.Black
                            ),
                            onValueChange = { value ->
                                currentTitle.value = value
                                if (currentTitle.value != note.value.title){
                                    saveButtonState.value = true
                                } else if (currentNote.value == note.value.note &&
                                    currentTitle.value == note.value.title
                                ){
                                    saveButtonState.value = false
                                }
                            },
                            label = { Text(text = "Title")}
                        )
                        Spacer(modifier = Modifier.padding(12.dp))
                        TextField(
                            value = currentNote.value,
                            colors = TextFieldDefaults.colors(
                                cursorColor = Color.Black,
                                focusedContainerColor = Color.LightGray,
                                unfocusedContainerColor = Color.LightGray,
                                focusedLabelColor = Color.Black,
                            ),
                            onValueChange = { value ->
                                currentNote.value = value
                                if (currentNote.value != note.value.note) {
                                    saveButtonState.value = true
                                } else if (currentNote.value == note.value.note &&
                                    currentTitle.value == note.value.title
                                ) {
                                    saveButtonState.value = false
                                }
                            },
                            label = { Text(text = "Body")}
                        )
                    }
                },
                modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
            )
        }
    }
}

@Composable
fun NotesFab(contentDescription: String, icon: Int, action: () -> Unit) {
    return FloatingActionButton(
        onClick = { action.invoke() },
        containerColor = MaterialTheme.colorScheme.primary
    ) {
        Icon(
            ImageVector.vectorResource(id = icon),
            contentDescription = contentDescription,
            tint = Color.Black
        )

    }
}