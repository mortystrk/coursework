package mrtsk.by.mynotes.database.entities

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity
data class Guard(
    @PrimaryKey(autoGenerate = true) var ID: Int = 0,
    @ColumnInfo(name = "guard") var guard: String?
)