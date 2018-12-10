package mrtsk.by.mynotes.core

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_note_details.*
import mrtsk.by.mynotes.R
import mrtsk.by.mynotes.core.model.Notes
import mrtsk.by.mynotes.database.entities.Note

class NoteDetails : AppCompatActivity() {

    private lateinit var arguments: Bundle
    private var position: Int = -1
    private lateinit var selectedNote: Note

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_details)

        arguments = intent.extras
        position = arguments.getInt("position")

        selectedNote = Notes.notes!!.reversed()[position]

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
}
