package mrtsk.by.mynotes.database.dao

import android.arch.persistence.room.*
import mrtsk.by.mynotes.database.entities.Guard

@Dao
interface UserDao {
    @Query("SELECT * FROM guard")
    fun getGuard(): List<Guard>

    @Insert
    fun insertGuard(guard: Guard)

    @Delete
    fun deleteGuard(guard: Guard)

    @Update
    fun updateGuard(guard: Guard)
}