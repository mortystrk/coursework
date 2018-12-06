package mrtsk.by.mynotes.core

import android.content.ContentValues
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.text.TextUtils
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import mrtsk.by.mynotes.R
import mrtsk.by.mynotes.database.DataContract
import mrtsk.by.mynotes.database.DatabaseHelper

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

}
