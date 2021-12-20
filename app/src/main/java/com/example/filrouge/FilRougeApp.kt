package com.example.filrouge

import android.app.Application
import com.example.filrouge.utils.SharedPreference

lateinit var appInstance:FilRougeApp
/**
 * Contain application-wide constant
 */
class FilRougeApp: Application() {

    /**
     * database handle
     */
    val database by lazy{FilRougeRoomDatabase.getDatabase(this)}

    /**
     * [SharedPreference] handle
     */
    val sharedPreference by lazy{ SharedPreference(this) }

    override fun onCreate() {
        super.onCreate()
        appInstance = this
    }
}
