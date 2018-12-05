package mrtsk.by.mynotes.database

import android.provider.BaseColumns

object DataContract {

    object TestEntry : BaseColumns {
        const val TABLE_NAME = "entry"
        const val COLUMN_NAME_TITLE = "title"
        const val COLUMN_NAME_SUBTITLE = "subtitle"
    }
}