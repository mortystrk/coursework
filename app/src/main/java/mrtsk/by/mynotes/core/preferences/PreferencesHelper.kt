package mrtsk.by.mynotes.core.preferences

import android.content.Context

class PreferencesHelper(context: Context) {

    private val PREFERENCES_NAME = "AppPreferences"
    private val FIRST_ENTRY = "FirstEntry"
    private val R = "R"
    private val S = "S"

    private val preference = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    fun setFirstEntry(flag: Boolean) {
        val editor = preference.edit()
        editor.putBoolean(FIRST_ENTRY, flag)
        editor.apply()
    }

    fun isFirstEntry() : Boolean {
        return preference.getBoolean(FIRST_ENTRY, true)
    }

    fun setR(r: String) {
        val editor = preference.edit()
        editor.putString(R, r)
        editor.apply()
    }

    fun getR() : String {
        return preference.getString(R, "error")
    }

    fun setS(s: String) {
        val editor = preference.edit()
        editor.putString(S, s)
        editor.apply()
    }

    fun getS() : String {
        return preference.getString(S, "error")
    }
}