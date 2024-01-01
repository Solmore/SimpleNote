package com.solmore.simplenote

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.solmore.simplenote.ui.NotesViewModel
import com.solmore.simplenote.ui.NotesViewModelFactory
import com.solmore.simplenote.ui.noteCreate.CreateNoteScreen
import com.solmore.simplenote.ui.noteDetail.NoteDetailScreen
import com.solmore.simplenote.ui.noteEdit.NoteEditScreen
import com.solmore.simplenote.ui.notesList.NotesListScreen
import com.solmore.simplenote.util.Constants

class MainActivity : ComponentActivity() {
    private lateinit var notesViewModel: NotesViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        notesViewModel = NotesViewModelFactory(NotesApp.getDao()).create(NotesViewModel::class.java)

        setContent {
            val navController = rememberNavController()

            NavHost(navController = navController, startDestination = Constants.NAVIGATION_NOTES_LIST){
                composable(Constants.NAVIGATION_NOTES_LIST){ NotesListScreen(
                    navController = navController,
                    viewModel = notesViewModel
                )}
                composable(
                    Constants.NAVIGATION_NOTE_DETAIL,
                    arguments = listOf(navArgument(Constants.NAVIGATION_NOTE_ID_ARGUMENT){
                        type = NavType.IntType
                    })
                ){
                        navBackStackEntry ->
                    navBackStackEntry.arguments?.getInt(Constants.NAVIGATION_NOTE_ID_ARGUMENT)?.let{
                        NoteDetailScreen(noteId = it, navController = navController, viewModel = notesViewModel)
                    }
                }
                composable(
                    Constants.NAVIGATION_NOTE_EDIT,
                    arguments = listOf(navArgument(Constants.NAVIGATION_NOTE_ID_ARGUMENT){
                        type = NavType.IntType
                    })
                ){
                        navBackStackEntry ->
                    navBackStackEntry.arguments?.getInt(Constants.NAVIGATION_NOTE_ID_ARGUMENT)?.let{
                        NoteEditScreen(noteId = it, navController = navController, viewModel = notesViewModel)
                    }
                }

                composable(Constants.NAVIGATION_NOTES_CREATE){
                    CreateNoteScreen(navController = navController, viewModel = notesViewModel)
                }
            }
        }
    }
}