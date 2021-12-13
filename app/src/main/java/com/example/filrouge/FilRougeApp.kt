package com.example.filrouge

import android.app.Application

lateinit var appInstance:FilRougeApp
/**
 * Contain application-wide constant
 */
class FilRougeApp: Application() {

    val database by lazy{FilRougeRoomDatabase.getDatabase(this)}
    val sharedPreference by lazy{SharedPreference(this)}

    override fun onCreate() {
        super.onCreate()
        appInstance = this
    }
}
