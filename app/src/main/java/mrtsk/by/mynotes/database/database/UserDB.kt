package mrtsk.by.mynotes.database.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import mrtsk.by.mynotes.database.dao.UserDao
import mrtsk.by.mynotes.database.entities.Guard

@Database(entities = [Guard::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao() : UserDao
}