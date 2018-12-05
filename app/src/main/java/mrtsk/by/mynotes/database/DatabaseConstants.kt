package mrtsk.by.mynotes.database

import android.provider.BaseColumns

class DatabaseConstants {
    companion object {
        const val SQL_CREATE_ENTRIES =
            "CREATE TABLE ${DataContract.TestEntry.TABLE_NAME} (" +
                    "${BaseColumns._ID} INTEGER PRIMARY KEY," +
                    "${DataContract.TestEntry.COLUMN_NAME_TITLE} TEXT," +
                    "${DataContract.TestEntry.COLUMN_NAME_SUBTITLE} TEXT)"

        const val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS ${DataContract.TestEntry.TABLE_NAME}"
    }
}