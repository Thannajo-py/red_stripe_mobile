package com.example.filrouge

import android.app.Application
import com.example.filrouge.bean.UserTableBean

lateinit var appInstance:FilRougeApp
class FilRougeApp: Application() {

    val database by lazy{FilRougeRoomDatabase.getDatabase(this)}
    val sharedPreference by lazy{SharedPreference(this)}

    override fun onCreate() {
        super.onCreate()
        appInstance = this
    }
}
