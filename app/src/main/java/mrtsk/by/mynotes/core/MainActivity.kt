package mrtsk.by.mynotes.core

import android.annotation.SuppressLint
import android.arch.persistence.room.Room
import android.os.AsyncTask
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.app.ActionBar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_create_note.*
import mrtsk.by.mynotes.R
import mrtsk.by.mynotes.core.fragments.CommonNotes
import mrtsk.by.mynotes.core.fragments.CreateNote
import mrtsk.by.mynotes.core.model.Notes
import mrtsk.by.mynotes.database.database.AppDatabase
import mrtsk.by.mynotes.database.entities.Note
import java.time.format.DateTimeFormatter
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*

class MainActivity : AppCompatActivity(), CreateNote.OnCreateNoteListener, CommonNotes.OnFragmentInteractionListener {
    override fun onDeleteItem(note: Note, position: Int) {
        Notes.notes?.removeAt(position)
        db.noteDao().deleteNote(note)
        Snackbar.make(main_layout, "Заметка удалена", Snackbar.LENGTH_SHORT).show()
    }

    override fun onCreateNote(note: Note?, flag: Boolean) {
        if (flag) {
            db.noteDao().insertNote(note!!)
            Notes.notes!!.add(note)
            Snackbar.make(main_layout, "Заметка сохранена", Snackbar.LENGTH_SHORT).show()
        } else {
            Snackbar.make(main_layout, "Пропущен заголовок, текст или не выбрана категория", Snackbar.LENGTH_LONG).show()
        }
    }


    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_common -> {
                commonNotesFragment = CommonNotes.newInstance("", "")
                openFragment(commonNotesFragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_private -> {

                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_create -> {
                val createNote = CreateNote.newInstance("", "")
                openFragment(createNote)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    lateinit var toolbar: ActionBar
    lateinit var currentDate: String
    lateinit var db: AppDatabase
    lateinit var commonNotesFragment: CommonNotes


    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "UserGuard"
        ).allowMainThreadQueries().build()

        Notes.notes = db.noteDao().loadAllUsers() as ArrayList<Note>

        val commonNotes = CommonNotes.newInstance("", "")
        openFragment(commonNotes)

        toolbar = supportActionBar!!
        val bottomNavigation: BottomNavigationView = findViewById(R.id.navigationView)
        bottomNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        val sdf = SimpleDateFormat("dd/M/yyyy")

        //val commonNotes = CommonNotes.newInstance("", "")
        //openFragment(commonNotes)


        val dateTimeStrToLocalDateTime: (String) -> LocalDateTime = {
            LocalDateTime.parse(it, DateTimeFormatter.ofPattern("dd/M/yyyy hh:mm:ss"))
        }



        currentDate = sdf.format(Date())
      //  val snackbar = Snackbar
        //    .make(main_layout, "Текущая дата: $currentDate", Snackbar.LENGTH_INDEFINITE)
       // snackbar.show()

    }

    private fun openFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.setCustomAnimations(R.animator.create_note_fragment_show, R.animator.create_note_fragment_hide)
        transaction.replace(R.id.container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    class doAsync(val handler: () -> Unit) : AsyncTask<Void, Void, Void>() {
        override fun doInBackground(vararg params: Void?): Void? {
            handler()
            return null
        }
    }
}
