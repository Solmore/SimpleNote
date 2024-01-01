package com.solmore.simplenote.ui.notesList

import android.annotation.SuppressLint
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.solmore.simplenote.ui.NotesViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.solmore.simplenote.R
import com.solmore.simplenote.model.Note
import com.solmore.simplenote.model.getDay
import com.solmore.simplenote.ui.GenericAppBar
import com.solmore.simplenote.ui.theme.SimpleNoteTheme
import com.solmore.simplenote.ui.theme.noteBGBlue
import com.solmore.simplenote.ui.theme.noteBGYellow
import com.solmore.simplenote.ui.theme.noteLightBlue
import com.solmore.simplenote.util.Constants
import com.solmore.simplenote.util.Constants.orPlaceHolderList


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter", "UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun NotesListScreen(
    navController: NavController,
    viewModel: NotesViewModel
){

    val deleteText = remember { mutableStateOf("") }
    val notesQuery = remember { mutableStateOf("") }
    val notesToDelete = remember { mutableStateOf(listOf<Note>()) }
    val openDialog = remember { mutableStateOf(false) }
    val notes = viewModel.notes.observeAsState()
    val context = LocalContext.current
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    SimpleNoteTheme{
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.primary) {
            Scaffold(
                topBar = {
                    GenericAppBar(
                        title = stringResource(R.string.simple_notes),
                        navController = navController,
                        icon = {
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.note_delete),
                                contentDescription = stringResource(R.string.delete_note),
                                tint = Color.Black
                            )
                        },
                        onIconClick = {
                            if (notes.value?.isNotEmpty() == true){
                                openDialog.value = true
                                deleteText.value = "Are you sure you want to delete all notes?"
                                notesToDelete.value = notes.value ?: emptyList()
                            } else{
                                Toast.makeText(context, "No Notes found", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        },
                        iconState = remember { mutableStateOf(true)}
                    )
                },
                floatingActionButton = {
                    NotesFab(
                        contentDescription = stringResource(R.string.create_note),
                        action = {
                            navController.navigate(Constants.NAVIGATION_NOTES_CREATE)
                        },
                        icon = R.drawable.note_add_icon
                    )
                },
                modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
            ){
                Column {
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(70.dp)
                    )
                    SearchBar(query = notesQuery)
                    NotesList(
                        notes = notes.value.orPlaceHolderList(),
                        openDialog = openDialog,
                        query = notesQuery,
                        deleteText = deleteText,
                        navController = navController,
                        notesToDelete = notesToDelete
                    )
                }

                DeleteDialog(
                    openDialog = openDialog,
                    text = deleteText,
                    action = {
                        notesToDelete.value.forEach {
                            viewModel.deleteNotes(it)
                        }
                    },
                    notesToDelete = notesToDelete)
            }
        }
    }
}


@Composable
fun SearchBar(query : MutableState<String>){
    Column (Modifier.padding(top = 12.dp, start = 12.dp, end = 12.dp, bottom = 8.dp)){
        TextField(
            value = query.value,
            placeholder = { Text(text = "Search..")},
            maxLines = 1,
            onValueChange = {query.value = it},
            modifier = Modifier
                .background(Color.White)
                .clip(RoundedCornerShape(12.dp))
                .fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = noteLightBlue,
                unfocusedContainerColor = Color.LightGray,
                focusedLabelColor = Color.Black
            ),
            trailingIcon = {
                AnimatedVisibility(
                    visible = query.value.isNotEmpty(),
                    enter =  fadeIn(),
                    exit = fadeOut()
                ) {
                    IconButton(onClick = { query.value = "" }) {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.icon_cross),
                            contentDescription = stringResource(id = R.string.clear_search))
                    }
                }
            }
        )
    }
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter", "UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun NotesList(
    notes: List<Note>,
    openDialog : MutableState<Boolean>,
    query: MutableState<String>,
    deleteText : MutableState<String>,
    navController: NavController,
    notesToDelete : MutableState<List<Note>>
){

    var previousHeader = ""
    LazyColumn(
        contentPadding = PaddingValues(12.dp),
        modifier = Modifier.background(MaterialTheme.colorScheme.primary),
    ){
        val queriedNotes = if (query.value.isEmpty()){
            notes
        } else{
            notes.filter { it.note.contains(query.value) || it.title.contains(query.value) }
        }
        itemsIndexed(queriedNotes){ index, note ->
            if(note.getDay() != previousHeader){
                Column (
                    modifier = Modifier
                        .padding(6.dp)
                        .fillMaxWidth()
                ){
                    Text(text = note.getDay(), color = Color.Black)
                }
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                )
                previousHeader = note.getDay()
            }
            NoteListItem(
                note,
                openDialog,
                deleteText = deleteText,
                navController,
                notesToDelete = notesToDelete,
                noteBackGround = if(index % 2 == 0){
                    noteBGYellow
                }else noteBGBlue
            )
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NoteListItem(
    note: Note,
    openDialog: MutableState<Boolean>,
    deleteText: MutableState<String>,
    navController: NavController,
    notesToDelete: MutableState<List<Note>>,
    noteBackGround: Color)
{
    return Box(modifier = Modifier
        .height(120.dp)
        .clip(RoundedCornerShape(12.dp))){
        Column(
            modifier = Modifier
                .background(noteBackGround)
                .fillMaxWidth()
                .height(120.dp)
                .combinedClickable(interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(bounded = false),
                    onClick = {
                        if (note.id != 0) {
                            navController.navigate(Constants.noteDetailNavigation(note.id ?: 0))
                        }
                    },
                    onLongClick = {
                        if (note.id != 0) {
                            openDialog.value = true
                            deleteText.value = "Are you sure you want to delete this note ?"
                            notesToDelete.value = mutableListOf(note)
                        }
                    }
                )
        ) {
            Row {
                if (!note.imageUri.isNullOrEmpty()){
                    // load firs image into view
                    Image(
                        painter = rememberAsyncImagePainter(
                            ImageRequest
                                .Builder(LocalContext.current)
                                .data(data = Uri.parse(note.imageUri))
                                .build()
                        ),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth(0.3f),
                        contentScale = ContentScale.Crop
                    )
                }
                Column {
                    Text(
                        text = note.title,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                    Text(
                        text = note.note,
                        color = Color.Black,
                        maxLines = 3,
                        modifier = Modifier.padding(12.dp)
                    )
                    Text(
                        text = note.dateUpdated,
                        color = Color.Black,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                }
            }

        }
    }

}

@Composable
fun NotesFab(
    contentDescription: String,
    icon: Int,
    action: () -> Unit
) {
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

@Composable
fun DeleteDialog(
    openDialog: MutableState<Boolean>,
    text: MutableState<String>,
    action: () -> Unit,
    notesToDelete: MutableState<List<Note>>
) {
    if (openDialog.value) {
        AlertDialog(
            onDismissRequest = {
                openDialog.value = false
            },
            title = {
                Text(text = "Delete Note")
            },
            text = {
                Column {
                    Text(text.value)
                }
            },
            confirmButton ={
                TextButton(
                    onClick = {
                        action.invoke()
                        openDialog.value = false
                        notesToDelete. value = mutableListOf()
                    }
                ) {
                    Text(text = "Yes", color = Color.Black)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        openDialog.value = false
                        notesToDelete.value = mutableListOf()
                    }
                ) {
                    Text(text = "No", color = Color.Black)
                }
            }
        )
    }
}

