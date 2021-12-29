package thannajo.appli.filrouge

import thannajo.appli.filrouge.model.FilRougeRoomDatabase
import thannajo.appli.filrouge.utils.SharedPreference
import android.app.Application


lateinit var appInstance:FilRougeApp
/**
 * Contain application-wide constant
 */
class FilRougeApp: Application() {

    /**
     * database handle
     */
    val database by lazy{ FilRougeRoomDatabase.getDatabase(this)}

    /**
     * [SharedPreference] handle
     */
    val sharedPreference by lazy{ SharedPreference(this) }

    override fun onCreate() {
        super.onCreate()
        appInstance = this
    }
}
