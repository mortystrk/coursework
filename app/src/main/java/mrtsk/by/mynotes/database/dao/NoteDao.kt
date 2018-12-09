package mrtsk.by.mynotes.database.dao

import android.arch.persistence.room.*
import mrtsk.by.mynotes.database.entities.Note

@Dao
interface NoteDao {
    @Query("SELECT * FROM note")
    fun loadAllUsers(): List<Note>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNotes(vararg notes: Note)

    @Insert
    fun insertNote(note: Note)

    @Update
    fun updateNote(note: Note)

    @Delete
    fun deleteNote(note: Note)
}