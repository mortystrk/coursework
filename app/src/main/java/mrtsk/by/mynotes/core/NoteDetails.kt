package mrtsk.by.mynotes.core

import android.arch.persistence.room.Room
import android.content.Intent
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.Snackbar
import android.util.Base64
import android.view.View
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_note_details.*
import mrtsk.by.mynotes.R
import mrtsk.by.mynotes.core.callbacks.DeleteNoteCallback
import mrtsk.by.mynotes.core.model.Const
import mrtsk.by.mynotes.core.model.Notes
import mrtsk.by.mynotes.core.preferences.PreferencesHelper
import mrtsk.by.mynotes.crypt.AESEncryptor
import mrtsk.by.mynotes.database.database.AppDatabase
import mrtsk.by.mynotes.database.entities.Note
import mrtsk.by.mynotes.database.entities.PNote
import mrtsk.by.mynotes.utils.ConvertDate
import java.text.SimpleDateFormat
import java.util.*

class NoteDetails : AppCompatActivity() {

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_edit -> {
                previousEnabledState = if(!previousEnabledState) {
                    enableWidget(true)
                    et_details_title.requestFocus()
                    true
                } else {
                    enableWidget(false)
                    false
                }
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_save -> {
                applyChanged()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_delete -> {
                showDeleteDialog()
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    private lateinit var arguments: Bundle
    private var position: Int = -1
    private lateinit var selectedNote: Note
    private lateinit var selectedPNote: PNote
    private var previousEnabledState = false
    private var isPrivate = false
    private lateinit var db: AppDatabase
    private var positionInReversedList: Int = -1
    private lateinit var e: AESEncryptor
    private lateinit var s: String
    private lateinit var preferences: PreferencesHelper
    private lateinit var decryptTitle: String
    private lateinit var decryptText: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_details)

        val bottomNavigation: BottomNavigationView = findViewById(R.id.detailsNavigationView)
        bottomNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "UserGuard"
        ).allowMainThreadQueries().build()

        preferences = PreferencesHelper(this)

        e = AESEncryptor()
        s = preferences.getS()

        val ba = Base64.decode(s, Base64.DEFAULT)
        e.init(ba)

        arguments = intent.extras
        position = arguments.getInt("position")

        isPrivate = arguments.getBoolean("private", false)

        if (isPrivate) {
            selectedPNote = if (Notes.pNotes.size == 1) {
                Notes.pNotes[position]
            } else {
                Notes.pNotes.reversed()[position]
            }

            positionInReversedList = Notes.pNotes.indexOf(selectedPNote)
        } else {
            selectedNote = if (Notes.notes.size == 1) {
                Notes.notes[position]
            } else {
                Notes.notes.reversed()[position]
            }

            positionInReversedList = Notes.notes.indexOf(selectedNote)
        }

        fillWidgets(isPrivate)
        enableWidget(false)

    }

    private fun fillWidgets(isPrivate: Boolean) {
        if (isPrivate) {
            decryptTitle = e.decrypt(selectedPNote.title!!)
            decryptText = e.decrypt(selectedPNote.text!!)
            et_details_title.setText(decryptTitle)
            et_details_text.setText(decryptText)
            iv_details_category.background = resources.getDrawable(R.drawable.b_rounded_corner, null)
            Glide.with(baseContext)
                .asBitmap()
                .load(R.drawable.baseline_lock_open_black_48dp)
                .into(iv_details_category)
            tv_detail_date.text = ConvertDate(selectedPNote.date!!)
        } else {
            et_details_title.setText(selectedNote.title)
            et_details_text.setText(selectedNote.text)
            when (selectedNote.category) {
                "Work" -> {
                    iv_details_category.background = resources.getDrawable(R.drawable.rounded_corner_work_category, null)
                    Glide.with(baseContext)
                        .asBitmap()
                        .load(R.drawable.baseline_work_white_48)
                        .into(iv_details_category)
                }
                "Education" -> {
                    iv_details_category.background = resources.getDrawable(R.drawable.rounded_corner_edu_category, null)
                    Glide.with(baseContext)
                        .asBitmap()
                        .load(R.drawable.baseline_school_white_48)
                        .into(iv_details_category)
                }
                "Home" -> {
                    iv_details_category.background = resources.getDrawable(R.drawable.rounded_corner_home_category, null)
                    Glide.with(baseContext)
                        .asBitmap()
                        .load(R.drawable.baseline_home_white_48)
                        .into(iv_details_category)
                }
                "Hobby" -> {
                    iv_details_category.background = resources.getDrawable(R.drawable.rounded_corner_hobby_category, null)
                    Glide.with(baseContext)
                        .asBitmap()
                        .load(R.drawable.baseline_palette_white_48)
                        .into(iv_details_category)
                }
                "Pet" -> {
                    iv_details_category.background = resources.getDrawable(R.drawable.rounded_corner_pet_category, null)
                    Glide.with(baseContext)
                        .asBitmap()
                        .load(R.drawable.baseline_pets_white_48)
                        .into(iv_details_category)
                }
            }
            tv_detail_date.text = ConvertDate(selectedNote.date!!)
        }
    }

    private fun enableWidget(isEnabled: Boolean) {
        if (isEnabled) {
            et_details_text.isEnabled = true
            et_details_title.isEnabled = true
        } else {
            et_details_text.isEnabled = false
            et_details_title.isEnabled = false
        }
    }

    private fun showDeleteDialog() {
        val alertDialog = android.support.v7.app.AlertDialog.Builder(this)
        alertDialog.setTitle("Удаление")
        alertDialog.setMessage("Вы уверены, что хотите удалить заметку?")
        alertDialog.setPositiveButton("Да, удалить") { _, _ ->
            if (isPrivate) {
                db.runInTransaction {
                    db.pnoteDao().deletePNote(selectedPNote)
                }
                Notes.pNotes.remove(selectedPNote)
                et_details_title.text = null
                et_details_text.text = null
                tv_detail_date.text = null
                iv_details_category.visibility = View.INVISIBLE
                Snackbar
                    .make(details_layout, "Заметка удалена!", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Хорошо") {
                        Const.isChangeddPrivateNote = true
                        onBackPressed()
                    }.show()
                } else {
                db.runInTransaction {
                    db.noteDao().deleteNote(selectedNote)
                }
                Notes.notes.remove(selectedNote)
                et_details_title.text = null
                et_details_text.text = null
                tv_detail_date.text = null
                iv_details_category.visibility = View.INVISIBLE
                Snackbar
                    .make(details_layout, "Заметка удалена!", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Хорошо") {
                        Const.isDelete = true
                        onBackPressed()
                    }.show()
                }
            }
        alertDialog.setNegativeButton("Отмена") { _, _ ->
                Snackbar.make(details_layout, "Удаление отменено", Snackbar.LENGTH_SHORT).show()
            }
        alertDialog.setCancelable(true)
        alertDialog.setOnCancelListener {
                Snackbar.make(details_layout, "Удаление отменено", Snackbar.LENGTH_SHORT).show()
            }
        alertDialog.show()
    }

    private fun applyChanged() {
        if (isPrivate) {
            if (decryptTitle == et_details_title.text.toString()
                && decryptText == et_details_text.text.toString()) {
                Snackbar.make(details_layout, "Сохранение не требуется", Snackbar.LENGTH_SHORT).show()
            } else {
                val newTitle = et_details_title.text.toString()
                val newText = et_details_text.text.toString()
                val sdf = SimpleDateFormat("dd/M/yyyy")
                selectedPNote.date = sdf.format(Date())
                selectedPNote.title = e.encrypt(newTitle)
                selectedPNote.text = e.encrypt(newText)

                Notes.pNotes[positionInReversedList] = selectedPNote
                db.pnoteDao().updatePNote(selectedPNote)

                Snackbar
                    .make(details_layout, "Изменения сохранены", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Хорошо") {
                        Const.isChangeddPrivateNote = true
                        onBackPressed()
                    }.show()
            }
        } else {
            if (selectedNote.title!! == et_details_title.text.toString()
                && selectedNote.text!! == et_details_text.text.toString()) {
                Snackbar.make(details_layout, "Сохранение не требуется", Snackbar.LENGTH_SHORT).show()
            } else {
                selectedNote.title = et_details_title.text.toString()
                selectedNote.text = et_details_text.text.toString()
                val sdf = SimpleDateFormat("dd/M/yyyy")
                selectedNote.date = sdf.format(Date())

                Notes.notes[positionInReversedList] = selectedNote
                db.noteDao().updateNote(selectedNote)

                Snackbar
                    .make(details_layout, "Изменения сохранены", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Хорошо") {
                        Const.isChangedNote = true
                        onBackPressed()
                    }.show()
            }
        }
    }

    override fun onBackPressed() {
        if (isPrivate) {
            Const.isChangeddPrivateNote = true
            super.onBackPressed()
        } else {
            Const.isChangedNote = true
            super.onBackPressed()
        }
    }
}
