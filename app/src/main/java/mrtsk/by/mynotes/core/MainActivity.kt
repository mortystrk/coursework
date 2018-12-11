package mrtsk.by.mynotes.core

import android.annotation.SuppressLint
import android.arch.persistence.room.Room
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.NetworkInfo
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
import mrtsk.by.mynotes.crypt.AESEncryptor
import mrtsk.by.mynotes.crypt.ECParams
import mrtsk.by.mynotes.database.database.AppDatabase
import mrtsk.by.mynotes.database.entities.Note
import java.math.BigInteger
import java.time.format.DateTimeFormatter
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*
import android.util.Base64
import mrtsk.by.mynotes.core.preferences.PreferencesHelper
import mrtsk.by.mynotes.utils.random
import org.bouncycastle.crypto.InvalidCipherTextException

class MainActivity : AppCompatActivity(), CreateNote.OnCreateNoteListener, CommonNotes.OnFragmentInteractionListener {
    override fun onItemClick(position: Int) {
        val intent = Intent(this@MainActivity, NoteDetails::class.java)
        intent.putExtra("position", position)
        startActivity(intent)
    }

    override fun onDataSetIsEmpty() {
        Snackbar.make(main_layout, "Заметок пока нет. Добавьте их, и они буду отображаться здесь", Snackbar.LENGTH_LONG).show()
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
    lateinit var db: AppDatabase
    lateinit var commonNotesFragment: CommonNotes
    private var isDelete = false
    private var isSaved = false
    private lateinit var preferences: PreferencesHelper

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        preferences = PreferencesHelper(this)

        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "UserGuard"
        ).allowMainThreadQueries().build()


        if (intent.extras != null) {
            isDelete = intent.extras.getBoolean("deleted", false)
            isSaved = intent.extras.getBoolean("saved", false)
        }

        if (!isDelete && !isSaved) {
            val selectedNotes = db.noteDao().loadAllUsers()
            if (!selectedNotes.isEmpty()) {
                Notes.notes = selectedNotes as ArrayList<Note>
            }
        }

        val commonNotes = CommonNotes.newInstance("", "")
        openFragment(commonNotes)

        toolbar = supportActionBar!!
        val bottomNavigation: BottomNavigationView = findViewById(R.id.navigationView)
        bottomNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }

    private fun openFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.setCustomAnimations(R.animator.create_note_fragment_show, R.animator.create_note_fragment_hide)
        transaction.replace(R.id.container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun checkPassword(pass: String) : Boolean {
        val guardList = db.userDao().getGuard()

        val r = preferences.getR()

        val rs = pass + r
        val ba = rs.toByteArray()

        val e = AESEncryptor()
        e.init(ba)
        return  try {
            e.decrypt(guardList[0].guard!!)
            true
        } catch (e: InvalidCipherTextException) {
            false
        }
    }
}
