package com.example.filrouge

import android.content.Context
import android.content.Intent
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

    fun removeValue(KEY_NAME: String) {

        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.remove(KEY_NAME)
        editor.apply()
    }

}

fun refreshedSavedData(sharedPreference:SharedPreference){
    sharedPreference.save(gson.toJson(ApiResponse(allGames, allAddOns, allMultiAddOns)),SerialKey.APIStorage.name)

}