package com.solmore.simplenote.ui.noteDetail

import android.annotation.SuppressLint
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.solmore.simplenote.R
import com.solmore.simplenote.ui.GenericAppBar
import com.solmore.simplenote.ui.NotesViewModel
import com.solmore.simplenote.ui.theme.SimpleNoteTheme
import com.solmore.simplenote.util.Constants
import com.solmore.simplenote.util.Constants.noteDetailPlaceHolder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter", "UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun NoteDetailScreen(
    noteId: Int,
    navController: NavController,
    viewModel: NotesViewModel
){
    val scope = rememberCoroutineScope()
    val note = remember{
        mutableStateOf(noteDetailPlaceHolder)
    }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val scrollState = rememberScrollState()

    LaunchedEffect(true){
        scope.launch(Dispatchers.IO) {
            note.value = viewModel.getNote(noteId) ?: noteDetailPlaceHolder
        }
    }

    SimpleNoteTheme {
        Surface(modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
            color = colorScheme.background) {
            Scaffold(
                topBar = {
                    GenericAppBar(
                        title = note.value.title,
                        navController = navController,
                        onIconClick = {
                            navController.navigate(Constants.noteEditNavigation(note.value.id ?: 0))
                        },
                        icon = {
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.edit_note),
                                contentDescription = stringResource(R.string.edit_note),
                                tint = Color.Black
                            )
                        },
                        iconState = remember { mutableStateOf(true) }
                    )
                },
                modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
                ){
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.LightGray)
                        //.verticalScroll(rememberScrollState())
                        //.nestedScroll(scrollBehavior.nestedScrollConnection)
                ) {
                    if( note.value.imageUri != null && note.value.imageUri!!.isNotEmpty() ){
                        Image(
                            painter = rememberAsyncImagePainter(
                                ImageRequest
                                .Builder(LocalContext.current)
                                .data(data = Uri.parse(note.value.imageUri))
                                .build()
                            ),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxHeight(0.5f)
                                .fillMaxWidth()
                                .padding(6.dp),
                            contentScale = ContentScale.Crop
                        )
                    }
                    Text(
                        text = note.value.title,
                        modifier = Modifier.padding(top = 24.dp, start = 12.dp, end = 24.dp),
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(text = note.value.dateUpdated, Modifier.padding(12.dp), color = Color.Gray)
                    Box(modifier = Modifier.verticalScroll(scrollState)
                    ){
                        Text(text = note.value.note, modifier = Modifier.padding(12.dp))
                    }
                }
            }

        }
    }
}