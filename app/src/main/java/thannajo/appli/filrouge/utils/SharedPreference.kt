package thannajo.appli.filrouge.utils

import android.content.Context
import android.content.SharedPreferences

/**
 * Save, load and delete variable from an xml file.
 */
class SharedPreference(val context: Context) {
    private val PREFS_NAME = "kotlincodes"
    private val sharedPref: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )


    /**
     * save [body]String data to xml file under [serialKey] name
     */
    fun saveString(body:String, serialKey: String){
        val editor = sharedPref.edit()
        editor.putString(serialKey, body)
        editor.apply()
    }

    /**
     * @return String data from xml file with [KEY_NAME] name or null
     */
    fun getValueString(KEY_NAME: String): String? {
        return sharedPref.getString(KEY_NAME, null)
    }

    /**
     * @return Boolean data from xml file with [KEY_NAME] name or null
     */
    fun getBoolean(KEY_NAME: String): Boolean {
        return sharedPref.getBoolean(KEY_NAME, true)
    }

    /**
     * @return Float data from xml file with [KEY_NAME] name or null
     */
    fun getFloat(KEY_NAME: String) = sharedPref.getFloat(KEY_NAME, 0.0F)

    /**
     * save [value]Float data to xml file under [serialKey] name
     */
    fun saveFloat(serialKey: String, value:Float) {
        val editor = sharedPref.edit()
        editor.putFloat(serialKey, value)
        editor.apply()
    }

    /**
     * save [value]Boolean data to xml file under [serialKey] name
     */
    fun saveBoolean(serialKey: String, value:Boolean) {
        val editor = sharedPref.edit()
        editor.putBoolean(serialKey, value)
        editor.apply()
    }

    /**
     * remove data under [KEY_NAME] name from xml file
     */
    fun removeValue(KEY_NAME: String) {
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.remove(KEY_NAME)
        editor.apply()
    }
}
