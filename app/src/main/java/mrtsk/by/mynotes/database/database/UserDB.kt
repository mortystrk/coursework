package mrtsk.by.mynotes.database.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import mrtsk.by.mynotes.database.dao.NoteDao
import mrtsk.by.mynotes.database.dao.PNoteDao
import mrtsk.by.mynotes.database.dao.UserDao
import mrtsk.by.mynotes.database.entities.Guard
import mrtsk.by.mynotes.database.entities.Note
import mrtsk.by.mynotes.database.entities.PNote

@Database(entities = [Guard::class, Note::class, PNote::class], version = 3)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao() : UserDao
    abstract fun noteDao() : NoteDao
    abstract fun pnoteDao() : PNoteDao
}