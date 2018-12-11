package mrtsk.by.mynotes.database.entities

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity
data class PNote(
    @PrimaryKey(autoGenerate = true) var ID: Int,
    @ColumnInfo(name = "title") var title: String?,
    @ColumnInfo(name = "text") var text: String?,
    @ColumnInfo(name = "date") var date: String?,
    @ColumnInfo(name = "category") var category: String?
)