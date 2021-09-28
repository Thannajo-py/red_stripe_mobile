package com.example.filrouge.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.filrouge.bean.*
import kotlinx.coroutines.flow.Flow


@Dao
interface GameDao {
@Query("SELECT * FROM game ORDER BY name ASC")
fun getAll(): Flow<List<GameTableBean>>
@Insert
fun insert(game: GameTableBean) : Long
@Query("DELETE FROM game")
fun deleteAll()

}


@Dao
interface AddOnDao {
@Query("SELECT * FROM addOn ORDER BY name ASC")
fun getAll(): Flow<List<AddOnTableBean>>
@Insert
fun insert(addOn: AddOnTableBean) : Long
@Query("DELETE FROM addOn")
fun deleteAll()

}


@Dao
interface MultiAddOnDao {
@Query("SELECT * FROM multiAddOn ORDER BY name ASC")
fun getAll(): Flow<List<MultiAddOnTableBean>>
@Insert
fun insert(multiAddOn: MultiAddOnTableBean) : Long
@Query("DELETE FROM multiAddOn")
fun deleteAll()

}


@Dao
interface TagDao {
@Query("SELECT * FROM tag ORDER BY name ASC")
fun getAll(): Flow<List<TagTableBean>>
@Insert
fun insert(tag: TagTableBean) : Long
@Query("DELETE FROM tag")
fun deleteAll()

}


@Dao
interface TopicDao {
@Query("SELECT * FROM topic ORDER BY name ASC")
fun getAll(): Flow<List<TopicTableBean>>
@Insert
fun insert(topic: TopicTableBean) : Long
@Query("DELETE FROM topic")
fun deleteAll()

}


@Dao
interface MechanismDao {
@Query("SELECT * FROM mechanism ORDER BY name ASC")
fun getAll(): Flow<List<MechanismTableBean>>
@Insert
fun insert(mechanism: MechanismTableBean) : Long
@Query("DELETE FROM mechanism")
fun deleteAll()

}


@Dao
interface DesignerDao {
@Query("SELECT * FROM designer ORDER BY name ASC")
fun getAll(): Flow<List<DesignerTableBean>>
@Insert
fun insert(designer: DesignerTableBean) : Long
@Query("DELETE FROM designer")
fun deleteAll()

}


@Dao
interface ArtistDao {
@Query("SELECT * FROM artist ORDER BY name ASC")
fun getAll(): Flow<List<ArtistTableBean>>
@Insert
fun insert(artist: ArtistTableBean) : Long
@Query("DELETE FROM artist")
fun deleteAll()

}


@Dao
interface PublisherDao {
@Query("SELECT * FROM publisher ORDER BY name ASC")
fun getAll(): Flow<List<PublisherTableBean>>
@Insert
fun insert(publisher: PublisherTableBean) : Long
@Query("DELETE FROM publisher")
fun deleteAll()

}


@Dao
interface PlayingModDao {
@Query("SELECT * FROM playingMod ORDER BY name ASC")
fun getAll(): Flow<List<PlayingModTableBean>>
@Insert
fun insert(playingMod: PlayingModTableBean) : Long
@Query("DELETE FROM playingMod")
fun deleteAll()

}


@Dao
interface LanguageDao {
@Query("SELECT * FROM language ORDER BY name ASC")
fun getAll(): Flow<List<LanguageTableBean>>
@Insert
fun insert(language: LanguageTableBean) : Long
@Query("DELETE FROM language")
fun deleteAll()

}


@Dao
interface UserDao {
    @Query("SELECT * FROM user ORDER BY login ASC")
    fun getAll(): Flow<List<UserTableBean>>

    @Query("SELECT * FROM user ORDER BY login ASC")
    fun checkEmpty(): List<UserTableBean>

    @Insert
    fun insert(user: UserTableBean) : Long

    @Query("SELECT * FROM user WHERE login=:name ORDER BY login ASC")
    fun getUser(name:String): List<UserTableBean>

    @Query("DELETE FROM user WHERE id=:userId")
    fun deleteUser(userId:Long)
}

    
    