package com.solmore.simplenote.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.solmore.simplenote.model.Note
import com.solmore.simplenote.persistence.NotesDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotesViewModel(
    private val db: NotesDao
) :  ViewModel() {

    val notes: LiveData<List<Note>> = db.getNotes()

    fun deleteNotes(note: Note){
        viewModelScope.launch(Dispatchers.IO ) {
            db.deleteNote(note)
        }
    }

    fun updateNote(note: Note){
        viewModelScope.launch(Dispatchers.IO ) {
            db.updateNote(note)
        }
    }

    fun createNote(title: String, note: String, image: String? = null){
        viewModelScope.launch(Dispatchers.IO) {
            db.insertNote(Note(title = title, note = note, imageUri = image))
        }
    }

    suspend fun getNote(noteId: Int): Note?{
        return db.getNoteById(noteId)
    }
}

@Suppress("UNCHECKED_CAST")
class NotesViewModelFactory(
    private val db: NotesDao
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return  NotesViewModel(db = db) as T
    }

}
