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
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_main.*
import mrtsk.by.mynotes.R
import mrtsk.by.mynotes.core.fragments.CommonNotes
import mrtsk.by.mynotes.core.fragments.CreateNote
import mrtsk.by.mynotes.core.fragments.PasswordDialog
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
import android.widget.TextView



class MainActivity : AppCompatActivity(), CreateNote.OnCreateNoteListener, CommonNotes.OnFragmentInteractionListener, PrivateNotes.OnPrivateNotesFragmentListener, PasswordDialog.PasswordListener {
    override fun onPositiveClick(password: String) {
        if (checkPassword(password)) {
            dialog.dismiss()
            val intent = Intent(this@MainActivity, NoteDetails::class.java)
            intent.putExtra("position", privateNotePosition)
            intent.putExtra("private", true)
            startActivity(intent)
        } else {
            dialog.dismiss()
            Snackbar.make(main_layout, "Пароль неверный", Snackbar.LENGTH_SHORT).show()
        }
    }

    override fun onFABPressed() {
        val intent = Intent(this@MainActivity, CreatePrivateNote::class.java)
        startActivity(intent)
    }

    override fun onDataSetIsEmptyPrivateNotes() {
        //Snackbar.make(main_layout, "Заметок пока нет. Добавьте их, и они буду отображаться здесь", Snackbar.LENGTH_SHORT).show()
    }

    override fun onItemClickPrivateNotes(position: Int) {
        numberOfBackTap = 0
        privateNotePosition = position
        dialog = PasswordDialog()
        dialog.show(supportFragmentManager, "PasswordDialog")
    }

    override fun onDataSetIsEmpty() {
        //Snackbar.make(main_layout, "Заметок пока нет. Добавьте их, и они буду отображаться здесь", Snackbar.LENGTH_SHORT).show()
    }

    override fun onItemClick(position: Int) {
        numberOfBackTap = 0
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
                targetFragment = "CommonNotes"
                commonNotesFragment = CommonNotes.newInstance("", "")
                openFragment(commonNotesFragment, true)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_private -> {
                targetFragment = "PrivateNotes"
                privateNotesFragment = PrivateNotes.newInstance("", "")
                openFragment(privateNotesFragment, true)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_create -> {
                targetFragment = "CreateNote"
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
    private lateinit var dialog: PasswordDialog
    private var privateNotePosition = -1
    private var numberOfBackTap = 0
    private lateinit var targetFragment: String

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

        targetFragment = "CommonNotes"
    }

    override fun onResume() {
        super.onResume()

        when {
            Const.isCreatedPrivateNote -> {
                Const.isCreatedPrivateNote = false

                privateNotesFragment = PrivateNotes.newInstance("", "")
                openFragment(privateNotesFragment, false)
            }
            Const.isChangeddPrivateNote -> {
                Const.isChangeddPrivateNote = false
                privateNotesFragment = PrivateNotes.newInstance("", "")
                openFragment(privateNotesFragment, false)
            }
            Const.isDelete -> {
                Const.isDelete = false
                commonNotesFragment = CommonNotes.newInstance("", "")
                openFragment(commonNotesFragment, false)
            }
            Const.isChangedNote -> {
                Const.isChangedNote = false
                commonNotesFragment = CommonNotes.newInstance("", "")
                openFragment(commonNotesFragment, false)
            }
            else -> {
                Notes.notes = db.noteDao().loadAllUsers() as ArrayList<Note>
                Notes.pNotes = db.pnoteDao().loadAllPNotes() as ArrayList<PNote>
                commonNotesFragment = CommonNotes.newInstance("", "")
                openFragment(commonNotesFragment, false)
            }
        }
    }

    private fun openFragment(fragment: Fragment, animation: Boolean) {
        numberOfBackTap = 0
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

    private fun sort() {
        if (Notes.notes.size != 0) {
            val sortedList = Notes.notes.sortedWith(compareBy { it.category })
            Notes.notes.clear()
            for (item in sortedList) {
                Notes.notes.add(item)
            }
            commonNotesFragment = CommonNotes.newInstance("","")
            openFragment(commonNotesFragment, false)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        return when (id) {
            R.id.sort -> {
                if (targetFragment == "CommonNotes") {
                    sort()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        numberOfBackTap++
        if (numberOfBackTap == 2) {
            finish()
        } else {
            Snackbar.make(main_layout, "Нажмите еще раз, чтобы выйти из приложения", Snackbar.LENGTH_SHORT).show()
        }
    }
}
