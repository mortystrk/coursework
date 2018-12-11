package mrtsk.by.mynotes.core

import android.arch.persistence.room.Room
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.Snackbar
import android.view.View
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_note_details.*
import mrtsk.by.mynotes.R
import mrtsk.by.mynotes.core.model.Notes
import mrtsk.by.mynotes.database.database.AppDatabase
import mrtsk.by.mynotes.database.entities.Note
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
    private var previousEnabledState = false
    private lateinit var db: AppDatabase
    private var positionInReversedList: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_details)

        val bottomNavigation: BottomNavigationView = findViewById(R.id.detailsNavigationView)
        bottomNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "UserGuard"
        ).allowMainThreadQueries().build()

        arguments = intent.extras
        position = arguments.getInt("position")

        selectedNote = Notes.notes!!.reversed()[position]
        positionInReversedList = Notes.notes!!.indexOf(selectedNote)

        fillWidgets()
        enableWidget(false)

    }

    private fun fillWidgets() {
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
                Notes.notes!!.remove(selectedNote)
                db.noteDao().deleteNote(selectedNote)
                et_details_title.text = null
                et_details_text.text = null
                iv_details_category.visibility = View.INVISIBLE
                Snackbar
                    .make(details_layout, "Заметка удалена!", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Хорошо") {
                        val intent = Intent(this@NoteDetails, MainActivity::class.java)
                        intent.putExtra("deleted", true)
                        startActivity(intent)
                    }.show()
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
        if (selectedNote.title!! == et_details_title.text.toString()
            && selectedNote.text!! == et_details_text.text.toString()) {
            Snackbar.make(details_layout, "Сохранение не требуется", Snackbar.LENGTH_SHORT).show()
        } else {
            selectedNote.title = et_details_title.text.toString()
            selectedNote.text = et_details_text.text.toString()
            val sdf = SimpleDateFormat("dd/M/yyyy")
            selectedNote.date = sdf.format(Date())

            Notes.notes!![positionInReversedList] = selectedNote
            db.noteDao().updateNote(selectedNote)

            Snackbar
                .make(details_layout, "Изменения сохранены", Snackbar.LENGTH_INDEFINITE)
                .setAction("Хорошо") {
                    val intent = Intent(this@NoteDetails, MainActivity::class.java)
                    intent.putExtra("saved", true)
                    startActivity(intent)
                }.show()
        }
    }
}
