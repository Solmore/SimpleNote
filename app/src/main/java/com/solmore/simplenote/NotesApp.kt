package com.solmore.simplenote

import android.app.Application
import android.content.Intent
import android.net.Uri
import androidx.room.Room
import com.solmore.simplenote.persistence.NotesDao
import com.solmore.simplenote.persistence.NotesDatabase
import com.solmore.simplenote.util.Constants


class NotesApp: Application() {

    private var db: NotesDatabase? = null

    init{
        instance = this
    }

    private fun getDb(): NotesDatabase {
        return if (db != null){
            db!!
        } else{
            db = Room.databaseBuilder(
                instance!!.applicationContext,
                NotesDatabase::class.java, Constants.DATABASE_NAME
            ).fallbackToDestructiveMigration().build()

            db!!
        }
    }

    companion object{
        private var instance: NotesApp? = null

        fun getDao(): NotesDao {
            return instance!!.getDb().NotesDao()
        }

        fun getUriPermission(uri: Uri){
            instance!!.applicationContext.contentResolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        }
    }
}