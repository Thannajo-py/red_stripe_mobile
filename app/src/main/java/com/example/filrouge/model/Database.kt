package com.example.filrouge

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.filrouge.utils.*
import com.example.filrouge.model.*
import com.example.filrouge.model.MechanismDao

/**
 * Room table declaration
 */
@Database (
    entities = [
GameTableBean::class,
AddOnTableBean::class,
MultiAddOnTableBean::class,
TagTableBean::class,
TopicTableBean::class,
MechanismTableBean::class,
UserTableBean::class,
DifficultyTableBean::class,
DesignerTableBean::class,
ArtistTableBean::class,
PublisherTableBean::class,
PlayingModTableBean::class,
LanguageTableBean::class,
GameMultiAddOnTableBean::class,
GameTagTableBean::class,
GameTopicTableBean::class,
GameMechanismTableBean::class,
GameDesignerTableBean::class,
AddOnDesignerTableBean::class,
MultiAddOnDesignerTableBean::class,
GameArtistTableBean::class,
AddOnArtistTableBean::class,
MultiAddOnArtistTableBean::class,
GamePublisherTableBean::class,
AddOnPublisherTableBean::class,
MultiAddOnPublisherTableBean::class,
GamePlayingModTableBean::class,
AddOnPlayingModTableBean::class,
MultiAddOnPlayingModTableBean::class,
GameLanguageTableBean::class,
AddOnLanguageTableBean::class,
MultiAddOnLanguageTableBean::class,
    DeletedContentTableBean::class,
    ImageTableBean::class
                      ],
    version = 10,
    exportSchema = false
)
/**
 * Room database class
 */
abstract class FilRougeRoomDatabase : RoomDatabase(){
//Dao declaration
abstract fun gameDao(): GameDao
abstract fun addOnDao(): AddOnDao
abstract fun multiAddOnDao(): MultiAddOnDao
abstract fun tagDao(): TagDao
abstract fun topicDao(): TopicDao
abstract fun mechanismDao(): MechanismDao
abstract fun userDao(): UserDao
abstract fun difficultyDao(): DifficultyDao
abstract fun designerDao(): DesignerDao
abstract fun artistDao(): ArtistDao
abstract fun publisherDao(): PublisherDao
abstract fun playingModDao(): PlayingModDao
abstract fun languageDao(): LanguageDao
abstract fun gameMultiAddOnDao(): GameMultiAddOnDao
abstract fun gameTagDao(): GameTagDao
abstract fun gameTopicDao(): GameTopicDao
abstract fun gameMechanismDao(): GameMechanismDao
abstract fun gameDesignerDao(): GameDesignerDao
abstract fun addOnDesignerDao(): AddOnDesignerDao
abstract fun multiAddOnDesignerDao(): MultiAddOnDesignerDao
abstract fun gameArtistDao(): GameArtistDao
abstract fun addOnArtistDao(): AddOnArtistDao
abstract fun multiAddOnArtistDao(): MultiAddOnArtistDao
abstract fun gamePublisherDao(): GamePublisherDao
abstract fun addOnPublisherDao(): AddOnPublisherDao
abstract fun multiAddOnPublisherDao(): MultiAddOnPublisherDao
abstract fun gamePlayingModDao(): GamePlayingModDao
abstract fun addOnPlayingModDao(): AddOnPlayingModDao
abstract fun multiAddOnPlayingModDao(): MultiAddOnPlayingModDao
abstract fun gameLanguageDao(): GameLanguageDao
abstract fun addOnLanguageDao(): AddOnLanguageDao
abstract fun multiAddOnLanguageDao(): MultiAddOnLanguageDao
abstract fun deletedContentDao(): DeletedItemDao
abstract fun imageDao(): ImageDao

    /**
     * Database connection via Singleton
     */
    companion object{
        private var INSTANCE: FilRougeRoomDatabase? = null

        fun getDatabase(context:Context) = INSTANCE ?: Room.databaseBuilder(
            context,
            FilRougeRoomDatabase::class.java,
            "room_fil_rouge_database"
        )
            .fallbackToDestructiveMigration().build().also { INSTANCE = it }
    }
}
        