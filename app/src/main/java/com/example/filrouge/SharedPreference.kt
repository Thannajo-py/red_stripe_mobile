package com.example.filrouge

import android.content.Context
import android.content.SharedPreferences


class SharedPreference(val context: Context) {
    private val PREFS_NAME = "kotlincodes"
    private val sharedPref: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )

    fun save(body:String, serialKey: String){
        val editor = sharedPref.edit()
        editor.putString(serialKey, body)
        editor.apply()
    }

    fun getValueString(KEY_NAME: String): String? {
        return sharedPref.getString(KEY_NAME, null)
    }

    fun getBoolean(KEY_NAME: String): Boolean {
        return sharedPref.getBoolean(KEY_NAME, true)
    }

    fun getFloat(KEY_NAME: String) = sharedPref.getFloat(KEY_NAME, 0.0F)

    fun saveFloat(serialKey: String, value:Float) {
        val editor = sharedPref.edit()
        editor.putFloat(serialKey, value)
        editor.apply()
    }

    fun saveBoolean(serialKey: String, value:Boolean) {
        val editor = sharedPref.edit()
        editor.putBoolean(serialKey, value)
        editor.apply()
    }

    fun removeValue(KEY_NAME: String) {

        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.remove(KEY_NAME)
        editor.apply()
    }
}
