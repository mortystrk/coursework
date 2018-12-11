package mrtsk.by.mynotes.core

import android.annotation.SuppressLint
import android.arch.persistence.room.Room
import android.content.Intent
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.app.ActionBar
import android.util.Base64
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_main.*
import mrtsk.by.mynotes.R
import mrtsk.by.mynotes.core.fragments.CommonNotes
import mrtsk.by.mynotes.core.fragments.CreateNote
import mrtsk.by.mynotes.core.fragments.PrivateNotes
import mrtsk.by.mynotes.core.model.Notes
import mrtsk.by.mynotes.crypt.AESEncryptor
import mrtsk.by.mynotes.database.database.AppDatabase
import mrtsk.by.mynotes.database.entities.Note
import mrtsk.by.mynotes.core.model.Const
import mrtsk.by.mynotes.core.preferences.PreferencesHelper
import mrtsk.by.mynotes.database.entities.PNote
import org.bouncycastle.crypto.InvalidCipherTextException
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity(), CreateNote.OnCreateNoteListener, CommonNotes.OnFragmentInteractionListener, PrivateNotes.OnPrivateNotesFragmentListener {
    override fun onFABPressed() {
        val intent = Intent(this@MainActivity, CreatePrivateNote::class.java)
        startActivity(intent)
    }

    override fun onDataSetIsEmptyPrivateNotes() {
        Snackbar.make(main_layout, "Заметок пока нет. Добавьте их, и они буду отображаться здесь", Snackbar.LENGTH_SHORT).show()
    }

    override fun onItemClickPrivateNotes(position: Int) {
        val intent = Intent(this@MainActivity, NoteDetails::class.java)
        intent.putExtra("position", position)
        intent.putExtra("private", true)
        startActivity(intent)
    }

    override fun onDataSetIsEmpty() {
        Snackbar.make(main_layout, "Заметок пока нет. Добавьте их, и они буду отображаться здесь", Snackbar.LENGTH_SHORT).show()
    }

    override fun onItemClick(position: Int) {
        val intent = Intent(this@MainActivity, NoteDetails::class.java)
        intent.putExtra("position", position)
        startActivity(intent)
    }

    override fun onCreateNote(note: Note?, flag: Boolean) {
        if (flag) {
            db.noteDao().insertNote(note!!)
            Notes.notes.add(note)
            Snackbar.make(main_layout, "Заметка сохранена", Snackbar.LENGTH_SHORT).show()
            navigationView.selectedItemId = R.id.navigation_common
        } else {
            Snackbar.make(main_layout, "Пропущен заголовок, текст или не выбрана категория", Snackbar.LENGTH_LONG).show()
        }
    }


    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_common -> {
                commonNotesFragment = CommonNotes.newInstance("", "")
                openFragment(commonNotesFragment, true)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_private -> {
                privateNotesFragment = PrivateNotes.newInstance("", "")
                openFragment(privateNotesFragment, true)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_create -> {
                val createNote = CreateNote.newInstance("", "")
                openFragment(createNote, true)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    lateinit var toolbar: ActionBar
    lateinit var db: AppDatabase
    lateinit var commonNotesFragment: CommonNotes
    lateinit var privateNotesFragment: PrivateNotes
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

        toolbar = supportActionBar!!
        val bottomNavigation: BottomNavigationView = findViewById(R.id.navigationView)
        bottomNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }

    override fun onResume() {
        super.onResume()

        Notes.notes.clear()
        Notes.notes = db.noteDao().loadAllUsers() as ArrayList<Note>

        Notes.pNotes = db.pnoteDao().loadAllPNotes() as ArrayList<PNote>
        /*val pnote = Notes.pNotes[0]

        val s = Base64.decode(preferences.getS(), Base64.DEFAULT)
        val e = AESEncryptor()
        e.init(s)

        try {
            val title = e.decrypt(pnote.title!!)
            val text = e.decrypt(pnote.text!!)

            Snackbar.make(main_layout, "$title $text", Snackbar.LENGTH_SHORT).show()
        } catch (e: InvalidCipherTextException) {
            Snackbar.make(main_layout, "Error", Snackbar.LENGTH_SHORT).show()
        }*/
        val commonNotes = CommonNotes.newInstance("", "")

        if (!Const.isDelete && !isSaved) {
            openFragment(commonNotes, true)
        } else {
            openFragment(commonNotes, false)
        }
    }

    private fun openFragment(fragment: Fragment, animation: Boolean) {
        val transaction = supportFragmentManager.beginTransaction()
        if (animation) {
            transaction.setCustomAnimations(R.animator.create_note_fragment_show, R.animator.create_note_fragment_hide)
        }
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
