package com.example.filrouge

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.filrouge.bean.*
import com.example.filrouge.dao.*
import com.example.filrouge.dao.MechanismDao


@Database (entities = [
    DifficultyTableBean::class,
    GameTableBean::class,
    AddOnTableBean::class,
    MultiAddOnTableBean::class,
    TagTableBean::class,
    TopicTableBean::class,
    MechanismTableBean::class,
    DesignerTableBean::class,
    ArtistTableBean::class,
    PublisherTableBean::class,
    PlayingModTableBean::class,
    LanguageTableBean::class,
    UserTableBean::class], version = 1, exportSchema = false)
abstract class FilRougeRoomDatabase : RoomDatabase(){
abstract fun gameDao(): GameDao
abstract fun addOnDao(): AddOnDao
abstract fun multiAddOnDao(): MultiAddOnDao
abstract fun tagDao(): TagDao
abstract fun topicDao(): TopicDao
abstract fun mechanismDao(): MechanismDao
abstract fun designerDao(): DesignerDao
abstract fun artistDao(): ArtistDao
abstract fun publisherDao(): PublisherDao
abstract fun playingModDao(): PlayingModDao
abstract fun languageDao(): LanguageDao
abstract fun userDao(): UserDao

    companion object{
        private var INSTANCE: FilRougeRoomDatabase? = null

        fun getDatabase(context:Context) = INSTANCE ?: Room.databaseBuilder(context, FilRougeRoomDatabase::class.java, "room_fil_rouge_database")
            .fallbackToDestructiveMigration().build().also { INSTANCE = it }
    }
}