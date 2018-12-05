package mrtsk.by.mynotes

import android.content.ContentValues
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.text.TextUtils
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import mrtsk.by.mynotes.database.DataContract
import mrtsk.by.mynotes.database.DatabaseHelper

class MainActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private var lastRowId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbHelper = DatabaseHelper(applicationContext)
        b_add_entry.setOnClickListener { addEntry() }
        b_show_last_entry.setOnClickListener { getLastEntry() }
    }

    private fun addEntry() {
        tiet_name.error = null
        tiet_surname.error = null

        val name = tiet_name.text.toString()
        val surname = tiet_surname.text.toString()

        var cancel = false
        var focusView: View? = null

        if (TextUtils.isEmpty(name)) {
            tiet_name.error = "Enter a name"
            focusView = tiet_name
            cancel = true
        }

        if (TextUtils.isEmpty(surname)) {
            tiet_surname.error = "Enter a surname"
            focusView = tiet_surname
            cancel = true
        }

        if (cancel) {
            focusView?.requestFocus()
        } else {
            // Gets the data repository in write mode
            val db = dbHelper.writableDatabase

// Create a new map of values, where column names are the keys
            val values = ContentValues().apply {
                put(DataContract.TestEntry.COLUMN_NAME_TITLE, name)
                put(DataContract.TestEntry.COLUMN_NAME_SUBTITLE, surname)
            }

// Insert the new row, returning the primary key value of the new row
            lastRowId = db?.insert(DataContract.TestEntry.TABLE_NAME, null, values)!!

            if (lastRowId.compareTo(-1) != 0) {
                Snackbar.make(
                    main_layout, // Parent view
                    "Entry added", // Message to show
                    Snackbar.LENGTH_SHORT // How long to display the message.
                ).show()
                tiet_name.text = null
                tiet_surname.text = null
            }
        }
    }

    private fun getLastEntry() {
        val db = dbHelper.readableDatabase

        if (lastRowId.compareTo(-1) != 0) {
            val selection = "SELECT * FROM ${DataContract.TestEntry.TABLE_NAME} WHERE _ID = $lastRowId"
            val cursor = db.rawQuery(selection, null)

            with(cursor) {
                while (moveToNext()) {
                    val name = getString(getColumnIndexOrThrow(DataContract.TestEntry.COLUMN_NAME_TITLE))
                    val surname = getString(getColumnIndexOrThrow(DataContract.TestEntry.COLUMN_NAME_SUBTITLE))

                    Snackbar.make(
                        main_layout, // Parent view
                        "$name $surname", // Message to show
                        Snackbar.LENGTH_LONG // How long to display the message.
                    ).show()
                }
            }
        } else {
            Snackbar.make(
                main_layout, // Parent view
                "There are no records in the database", // Message to show
                Snackbar.LENGTH_LONG // How long to display the message.
            ).show()
        }
    }
}
