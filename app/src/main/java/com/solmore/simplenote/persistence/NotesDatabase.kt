package com.solmore.simplenote.persistence

import androidx.room.Database
import androidx.room.RoomDatabase
import com.solmore.simplenote.model.Note

@Database(entities = [Note::class],version = 1, exportSchema = false )
abstract class NotesDatabase : RoomDatabase() {
    abstract fun  NotesDao(): NotesDao
}