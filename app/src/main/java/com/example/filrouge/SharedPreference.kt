package com.example.filrouge

import android.content.Context
import android.content.SharedPreferences


class SharedPreference(val context: Context) {
    private val PREFS_NAME = "kotlincodes"
    private val sharedPref: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun save(body:String, serialKey: String){
        val editor = sharedPref.edit()
        editor.putString(serialKey, body)
        editor.apply()
    }

    fun getValueString(KEY_NAME: String): String? {
        return sharedPref.getString(KEY_NAME, "")
    }

    fun getValueInt(KEY_NAME: String): Int? {
        return sharedPref.getInt(KEY_NAME, -1)
    }

    fun saveInt(KEY_NAME: String, body:Int){
        val editor = sharedPref.edit()
        editor.putInt(KEY_NAME, body)
        editor.apply()
    }

    fun removeValue(KEY_NAME: String) {

        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.remove(KEY_NAME)
        editor.apply()
    }
    fun clear() {
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.clear()
        editor.apply()
    }

}