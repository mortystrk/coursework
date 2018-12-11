package mrtsk.by.mynotes.database.dao

import android.arch.persistence.room.*
import mrtsk.by.mynotes.database.entities.PNote

@Dao
interface PNoteDao {
    @Query("SELECT * FROM pnote")
    fun loadAllPNotes(): List<PNote>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPNotes(vararg notes: PNote)

    @Insert
    fun insertPNote(note: PNote)

    @Update
    fun updatePNote(note: PNote)

    @Delete
    fun deletePNote(note: PNote)
}