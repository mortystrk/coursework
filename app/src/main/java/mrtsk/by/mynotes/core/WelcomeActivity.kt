package mrtsk.by.mynotes.core

import android.arch.persistence.room.Room
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.text.TextUtils
import android.view.View
import kotlinx.android.synthetic.main.activity_welcome.*
import mrtsk.by.mynotes.R
import mrtsk.by.mynotes.core.preferences.PreferencesHelper
import mrtsk.by.mynotes.database.database.AppDatabase
import mrtsk.by.mynotes.database.entities.Guard

class WelcomeActivity : AppCompatActivity() {

    private lateinit var preferences: PreferencesHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        preferences = PreferencesHelper(this)
        if (!preferences.isFirstEntry()){
            val intent = Intent(this@WelcomeActivity, MainActivity::class.java)
            startActivity(intent)
        }

        b_start.setOnClickListener { getPasswordAndNextActivity() }
    }

    private fun getPasswordAndNextActivity() {
        et_pass.error = null
        et_repeat_pass.error = null

        val password = et_pass.text.toString()
        val repeatPassword = et_repeat_pass.text.toString()

        var isPasswordCorrect = true
        var cancel = false
        var focusView: View? = null

        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            et_pass.error = "Минимум 6 символов в пароле!"
            focusView = et_pass
            isPasswordCorrect = false
            cancel = true
        }

        if (TextUtils.isEmpty(password)) {
            et_pass.error = "Поле обязательно!"
            focusView = et_pass
            isPasswordCorrect = false
            cancel = true
        }

        if (!TextUtils.isEmpty(repeatPassword) && !isPasswordEqual(password, repeatPassword)) {
            et_repeat_pass.error = "Пароли не совпадают!"
            if (isPasswordCorrect) {
                focusView = et_repeat_pass
            }
            cancel = true
        }

        if (TextUtils.isEmpty(repeatPassword)) {
            et_repeat_pass.error = "Повторите пароль!"
            if (isPasswordCorrect) {
                focusView = et_repeat_pass
            }
            cancel = true
        }

        if (cancel) {
            focusView?.requestFocus()
        } else {
            //позже тут будет шифрование
            val db = Room.databaseBuilder(
                applicationContext,
                AppDatabase::class.java, "UserGuard"
            ).allowMainThreadQueries().build()

            db.userDao().insertGuard(Guard(0, password))

            preferences.setFirstEntry(false)

            val snackbar = Snackbar
                .make(welcome_screen, "Пароль сохранен!", Snackbar.LENGTH_INDEFINITE)
                .setAction("Хорошо") {
                    val intent = Intent(this@WelcomeActivity, MainActivity::class.java)
                    startActivity(intent)
                }
            snackbar.show()
        }
    }

    private fun isPasswordValid(passwordStr: String) : Boolean {
        return passwordStr.length > 5
    }

    private fun isPasswordEqual(password: String, repeatPassword: String) : Boolean {
        return password == repeatPassword
    }
}
