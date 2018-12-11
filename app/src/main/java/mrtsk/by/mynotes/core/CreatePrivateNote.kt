package mrtsk.by.mynotes.core

import android.arch.persistence.room.Room
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.text.TextUtils
import android.util.Base64
import kotlinx.android.synthetic.main.activity_create_private_note.*
import mrtsk.by.mynotes.R
import mrtsk.by.mynotes.core.model.Const
import mrtsk.by.mynotes.core.model.Notes
import mrtsk.by.mynotes.core.preferences.PreferencesHelper
import mrtsk.by.mynotes.crypt.AESEncryptor
import mrtsk.by.mynotes.database.database.AppDatabase
import mrtsk.by.mynotes.database.entities.PNote
import java.text.SimpleDateFormat
import java.util.*

class CreatePrivateNote : AppCompatActivity() {

    private lateinit var preferences: PreferencesHelper
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_private_note)

        preferences = PreferencesHelper(this)
        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "UserGuard"
        ).allowMainThreadQueries().build()

        b_done_pnote.setOnClickListener {
            val text = et_note_text_pnote.text.toString()
            val title = et_add_title_pnote.text.toString()

            var isCorrectData = true

            if (TextUtils.isEmpty(text) || TextUtils.isEmpty(title)) {
                isCorrectData = false
            }

            if (!isCorrectData) {
                Snackbar.make(create_pnote_layout, "Пропущен заголовок или текст заметки", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val s = Base64.decode(preferences.getS(), Base64.DEFAULT)
            val e = AESEncryptor()
            e.init(s)

            val cipherText = e.encrypt(text)
            val cipherTitle = e.encrypt(title)
            val id = preferences.getPID() + 1

            val sdf = SimpleDateFormat("dd/M/yyyy")
            val date = sdf.format(Date())

            val pnote = PNote(id, cipherTitle, cipherText, date, "Private")

            db.pnoteDao().insertPNote(pnote)
            Notes.pNotes.add(pnote)

            preferences.setPID(id)

            Snackbar.make(create_pnote_layout, "Заметка сохранена", Snackbar.LENGTH_INDEFINITE).setAction("Хорошо") {
                Const.isCreatedPrivateNote = true
                onBackPressed()
            }.show()
        }
    }
}
