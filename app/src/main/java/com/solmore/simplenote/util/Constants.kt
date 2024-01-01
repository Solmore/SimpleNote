package com.solmore.simplenote.util

import com.solmore.simplenote.model.Note

object Constants {
    const val NAVIGATION_NOTES_LIST = "notesList"
    const val NAVIGATION_NOTES_CREATE = "notesCreated"
    const val NAVIGATION_NOTE_DETAIL = "noteDetail/{noteId}"
    const val NAVIGATION_NOTE_EDIT= "noteEdit/{noteId}"
    const val NAVIGATION_NOTE_ID_ARGUMENT = "noteId"
    const val TABLE_NAME = "notes"
    const val DATABASE_NAME = "notesDatabase"

    val noteDetailPlaceHolder = Note(
        note = "Can't find note details",
        id = 0,
        title ="Can't find note details"
    )

    fun noteDetailNavigation(noteId: Int) = "noteDetail/$noteId"
    fun noteEditNavigation(noteId: Int) = "noteEdit/$noteId"


    fun List<Note>?.orPlaceHolderList(): List<Note> {
        fun placeHolderList(): List<Note> {
            return listOf(Note(id = 0, title = "No Notes Found", note = "Please create a note.", dateUpdated = ""))
        }
        return if (!this.isNullOrEmpty()){
            this
        } else placeHolderList()
    }
}