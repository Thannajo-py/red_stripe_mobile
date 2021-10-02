package com.example.filrouge.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.filrouge.DbMethod
import com.example.filrouge.bean.*
import kotlinx.coroutines.flow.Flow


interface CommonDao<T>: CommonComponentDao<T>{
    fun getByServerId(id:Long):List<T>

}

interface CommonComponentDao<T>{
    fun getByName(searchedName:String): List<T>
}

interface CommonCustomInsert<T>: CommonComponentDao<T>{
    fun insert(newElement:String)
}


@Dao
interface GameDao: CommonDao<GameTableBean> {
    @Query("SELECT * FROM game ORDER BY name ASC")
    fun getAll(): Flow<List<GameTableBean>>
    
    @Query("SELECT * FROM game")
    fun getList(): List<GameTableBean>

    @Query("SELECT * FROM game WHERE id=:gameId")
    fun getById(gameId:Long): Flow<List<GameTableBean>>

    @Query("SELECT * FROM game WHERE id=:gameId")
    fun getObjectById(gameId:Long): List<GameTableBean>
    
    @Query("SELECT * FROM game WHERE name=:searchedName")
    override fun getByName(searchedName:String): List<GameTableBean>

    @Query("SELECT game.id as id, game.name as name, designer.name as designer FROM game LEFT JOIN gameDesigner ON gameId = game.id LEFT JOIN  designer on designerId = designer.id GROUP BY game.name")
    fun getDesignerWithGame(): LiveData<List<DesignerWithGame>>

    @Query("SELECT difficulty.id as id, difficulty.name as name FROM difficulty LEFT JOIN game ON difficultyId = difficulty.id WHERE game.id = :idGame")
    fun getDifficultyOfGame(idGame:Long): LiveData<List<DifficultyTableBean>>

    @Query("SELECT tag.id AS id, tag.name AS name FROM tag INNER JOIN gameTag ON tagId = tag.id INNER JOIN game ON gameId = :idGame GROUP BY tag.name")
    fun getTagsOfGame(idGame:Long): LiveData<List<TagTableBean>>


    @Query("SELECT topic.id AS id, topic.name AS name FROM topic INNER JOIN gameTopic ON topicId = topic.id INNER JOIN game ON gameId = :idGame GROUP BY topic.name")
    fun getTopicsOfGame(idGame:Long): LiveData<List<TopicTableBean>>


    @Query("SELECT mechanism.id AS id, mechanism.name AS name FROM mechanism INNER JOIN gameMechanism ON mechanismId = mechanism.id INNER JOIN game ON gameId = :idGame GROUP BY mechanism.name")
    fun getMechanismsOfGame(idGame:Long): LiveData<List<MechanismTableBean>>


    @Query("SELECT designer.id AS id, designer.name AS name FROM designer INNER JOIN gameDesigner ON designerId = designer.id INNER JOIN game ON gameId = :idGame GROUP BY designer.name")
    fun getDesignersOfGame(idGame:Long): LiveData<List<DesignerTableBean>>


    @Query("SELECT artist.id AS id, artist.name AS name FROM artist INNER JOIN gameArtist ON artistId = artist.id INNER JOIN game ON gameId = :idGame GROUP BY artist.name")
    fun getArtistsOfGame(idGame:Long): LiveData<List<ArtistTableBean>>


    @Query("SELECT publisher.id AS id, publisher.name AS name FROM publisher INNER JOIN gamePublisher ON publisherId = publisher.id INNER JOIN game ON gameId = :idGame GROUP BY publisher.name")
    fun getPublishersOfGame(idGame:Long): LiveData<List<PublisherTableBean>>


    @Query("SELECT playingMod.id AS id, playingMod.name AS name FROM playingMod INNER JOIN gamePlayingMod ON playingModId = playingMod.id INNER JOIN game ON gameId = :idGame GROUP BY playingMod.name")
    fun getPlayingModsOfGame(idGame:Long): LiveData<List<PlayingModTableBean>>


    @Query("SELECT language.id AS id, language.name AS name FROM language INNER JOIN gameLanguage ON languageId = language.id INNER JOIN game ON gameId = :idGame GROUP BY language.name")
    fun getLanguagesOfGame(idGame:Long): LiveData<List<LanguageTableBean>>


    @Query("SELECT game.id AS id, game.name AS name, designer.name AS designer FROM game LEFT JOIN gameDesigner ON gameDesigner.gameId = game.id LEFT JOIN designer ON gameDesigner.designerId = designer.id LEFT JOIN gameTag ON gameTag.gameId = game.id LEFT JOIN tag ON gameTag.tagId = tag.id WHERE tag.id = :idTag GROUP BY game.name ORDER BY game.name ASC")
    fun getWithDesignerFromTagId(idTag:Long):LiveData<List<DesignerWithGame>>


    @Query("SELECT game.id AS id, game.name AS name, designer.name AS designer FROM game LEFT JOIN gameDesigner ON gameDesigner.gameId = game.id LEFT JOIN designer ON gameDesigner.designerId = designer.id LEFT JOIN gameTopic ON gameTopic.gameId = game.id LEFT JOIN topic ON gameTopic.topicId = topic.id WHERE topic.id = :idTopic GROUP BY game.name ORDER BY game.name ASC")
    fun getWithDesignerFromTopicId(idTopic:Long):LiveData<List<DesignerWithGame>>


    @Query("SELECT game.id AS id, game.name AS name, designer.name AS designer FROM game LEFT JOIN gameDesigner ON gameDesigner.gameId = game.id LEFT JOIN designer ON gameDesigner.designerId = designer.id LEFT JOIN gameMechanism ON gameMechanism.gameId = game.id LEFT JOIN mechanism ON gameMechanism.mechanismId = mechanism.id WHERE mechanism.id = :idMechanism GROUP BY game.name ORDER BY game.name ASC")
    fun getWithDesignerFromMechanismId(idMechanism:Long):LiveData<List<DesignerWithGame>>


    @Query("SELECT game.id AS id, game.name AS name, designer.name AS designer FROM game INNER JOIN gameDesigner ON gameDesigner.gameId = game.id INNER JOIN designer ON gameDesigner.designerId = designer.id WHERE designer.id = :idDesigner GROUP BY game.name ORDER BY game.name ASC")
    fun getWithDesignerFromDesignerId(idDesigner:Long):LiveData<List<DesignerWithGame>>


    @Query("SELECT game.id AS id, game.name AS name, designer.name AS designer FROM game LEFT JOIN gameDesigner ON gameDesigner.gameId = game.id LEFT JOIN designer ON gameDesigner.designerId = designer.id LEFT JOIN gameArtist ON gameArtist.gameId = game.id LEFT JOIN artist ON gameArtist.artistId = artist.id WHERE artist.id = :idArtist GROUP BY game.name ORDER BY game.name ASC")
    fun getWithDesignerFromArtistId(idArtist:Long):LiveData<List<DesignerWithGame>>


    @Query("SELECT game.id AS id, game.name AS name, designer.name AS designer FROM game LEFT JOIN gameDesigner ON gameDesigner.gameId = game.id LEFT JOIN designer ON gameDesigner.designerId = designer.id LEFT JOIN gamePublisher ON gamePublisher.gameId = game.id LEFT JOIN publisher ON gamePublisher.publisherId = publisher.id WHERE publisher.id = :idPublisher GROUP BY game.name ORDER BY game.name ASC")
    fun getWithDesignerFromPublisherId(idPublisher:Long):LiveData<List<DesignerWithGame>>


    @Query("SELECT game.id AS id, game.name AS name, designer.name AS designer FROM game LEFT JOIN gameDesigner ON gameDesigner.gameId = game.id LEFT JOIN designer ON gameDesigner.designerId = designer.id LEFT JOIN gamePlayingMod ON gamePlayingMod.gameId = game.id LEFT JOIN playingMod ON gamePlayingMod.playingModId = playingMod.id WHERE playingMod.id = :idPlayingMod GROUP BY game.name ORDER BY game.name ASC")
    fun getWithDesignerFromPlayingModId(idPlayingMod:Long):LiveData<List<DesignerWithGame>>


    @Query("SELECT game.id AS id, game.name AS name, designer.name AS designer FROM game LEFT JOIN gameDesigner ON gameDesigner.gameId = game.id LEFT JOIN designer ON gameDesigner.designerId = designer.id LEFT JOIN gameLanguage ON gameLanguage.gameId = game.id LEFT JOIN language ON gameLanguage.languageId = language.id WHERE language.id = :idLanguage GROUP BY game.name ORDER BY game.name ASC")
    fun getWithDesignerFromLanguageId(idLanguage:Long):LiveData<List<DesignerWithGame>>

    @Query("SELECT game.id AS id, game.name AS name, designer.name AS designer FROM game INNER JOIN gameDesigner ON gameDesigner.gameId = game.id INNER JOIN designer ON gameDesigner.designerId = designer.id WHERE game.difficultyId = :idDifficulty GROUP BY game.name ORDER BY game.name ASC")
    fun getWithDesignerFromDifficultyId(idDifficulty:Long):LiveData<List<DesignerWithGame>>


    @Insert(onConflict =  OnConflictStrategy.REPLACE)
    fun insert(game: GameTableBean) : Long
    
    @Update
    fun update(game: GameTableBean)
    
    @Query("DELETE FROM game")
    fun deleteAll()
    
    @Query("DELETE FROM game WHERE id=:objectId")
    fun deleteOne(objectId:Long)
      
    @Query("SELECT * FROM game WHERE serverId=:id")
    override fun getByServerId(id:Long):List<GameTableBean>

    @Query("SELECT * FROM game WHERE serverId=NULL")
    fun getWithoutServerId():List<GameTableBean>
            
}
    
    
    
@Dao
interface AddOnDao: CommonDao<AddOnTableBean> {
    @Query("SELECT * FROM addOn ORDER BY name ASC")
    fun getAll(): Flow<List<AddOnTableBean>>
    
    @Query("SELECT * FROM addOn")
    fun getList(): List<AddOnTableBean>
    
    @Query("SELECT * FROM addOn WHERE name=:searchedName")
    override fun getByName(searchedName:String): List<AddOnTableBean>

    @Query("SELECT * FROM addOn WHERE id=:gameId")
    fun getById(gameId:Long): Flow<List<AddOnTableBean>>

    @Query("SELECT * FROM addOn WHERE id=:gameId")
    fun getObjectById(gameId:Long): List<AddOnTableBean>

    @Query("SELECT difficulty.id as id, difficulty.name as name FROM difficulty LEFT JOIN addOn ON difficultyId = difficulty.id WHERE addOn.id = :idGame")
    fun getDifficultyOfAddOn(idGame:Long): LiveData<List<DifficultyTableBean>>


    @Query("SELECT designer.id as id, designer.name as name FROM designer INNER JOIN addOnDesigner ON designerId = designer.id INNER JOIN addOn ON addOnId = :idGame")
    fun getDesignersOfAddOn(idGame:Long): LiveData<List<DesignerTableBean>>


    @Query("SELECT artist.id AS id, artist.name AS name FROM artist INNER JOIN addOnArtist ON artistId = artist.id INNER JOIN addOn ON addOnId = :idGame GROUP BY artist.name")
    fun getArtistsOfAddOn(idGame:Long): LiveData<List<ArtistTableBean>>


    @Query("SELECT publisher.id AS id, publisher.name AS name FROM publisher INNER JOIN addOnPublisher ON publisherId = publisher.id INNER JOIN addOn ON addOnId = :idGame GROUP BY publisher.name")
    fun getPublishersOfAddOn(idGame:Long): LiveData<List<PublisherTableBean>>


    @Query("SELECT playingMod.id AS id, playingMod.name AS name FROM playingMod INNER JOIN addOnPlayingMod ON playingModId = playingMod.id INNER JOIN addOn ON addOnId = :idGame GROUP BY playingMod.name")
    fun getPlayingModsOfAddOn(idGame:Long): LiveData<List<PlayingModTableBean>>


    @Query("SELECT language.id AS id, language.name AS name FROM language INNER JOIN addOnLanguage ON languageId = language.id INNER JOIN addOn ON addOnId = :idGame GROUP BY language.name")
    fun getLanguagesOfAddOn(idGame:Long): LiveData<List<LanguageTableBean>>

    @Query("SELECT game.id as id, game.name as name, designer.name as designer FROM game  LEFT JOIN gameDesigner ON  gameDesigner.gameId = game.id LEFT JOIN designer ON designer.id = gameDesigner.designerId WHERE game.id=:gameId GROUP BY game.name")
    fun getGameFromAddOn(gameId:Long): LiveData<List<DesignerWithAddOn>>

    @Query("SELECT game.id as id, game.name as name, designer.name as designer FROM addOn LEFT JOIN game ON addOn.gameId = game.id LEFT JOIN gameDesigner ON  gameDesigner.gameId = game.id LEFT JOIN designer ON designer.id = gameDesigner.designerId WHERE addOn.gameId=:gameId GROUP BY game.name")
    fun getGameFromAddOns(gameId:Long): List<DesignerWithAddOn>


    @Query("SELECT addOn.id AS id, addOn.name AS name, designer.name AS designer FROM addOn INNER JOIN addOnDesigner ON addOnDesigner.addOnId = addOn.id INNER JOIN designer ON addOnDesigner.designerId = designer.id WHERE designer.id = :idDesigner GROUP BY addOn.name ORDER BY addOn.name ASC")
    fun getWithDesignerFromDesignerId(idDesigner:Long):LiveData<List<DesignerWithAddOn>>


    @Query("SELECT addOn.id AS id, addOn.name AS name, designer.name AS designer FROM addOn LEFT JOIN addOnDesigner ON addOnDesigner.addOnId = addOn.id LEFT JOIN designer ON addOnDesigner.designerId = designer.id LEFT JOIN addOnArtist ON addOnArtist.addOnId = addOn.id LEFT JOIN artist ON addOnArtist.artistId = artist.id WHERE artist.id = :idArtist GROUP BY addOn.name ORDER BY addOn.name ASC")
    fun getWithDesignerFromArtistId(idArtist:Long):LiveData<List<DesignerWithAddOn>>


    @Query("SELECT addOn.id AS id, addOn.name AS name, designer.name AS designer FROM addOn LEFT JOIN addOnDesigner ON addOnDesigner.addOnId = addOn.id LEFT JOIN designer ON addOnDesigner.designerId = designer.id LEFT JOIN addOnPublisher ON addOnPublisher.addOnId = addOn.id LEFT JOIN publisher ON addOnPublisher.publisherId = publisher.id WHERE publisher.id = :idPublisher GROUP BY addOn.name ORDER BY addOn.name ASC")
    fun getWithDesignerFromPublisherId(idPublisher:Long):LiveData<List<DesignerWithAddOn>>


    @Query("SELECT addOn.id AS id, addOn.name AS name, designer.name AS designer FROM addOn LEFT JOIN addOnDesigner ON addOnDesigner.addOnId = addOn.id LEFT JOIN designer ON addOnDesigner.designerId = designer.id LEFT JOIN addOnPlayingMod ON addOnPlayingMod.addOnId = addOn.id LEFT JOIN playingMod ON addOnPlayingMod.playingModId = playingMod.id WHERE playingMod.id = :idPlayingMod GROUP BY addOn.name ORDER BY addOn.name ASC")
    fun getWithDesignerFromPlayingModId(idPlayingMod:Long):LiveData<List<DesignerWithAddOn>>


    @Query("SELECT addOn.id AS id, addOn.name AS name, designer.name AS designer FROM addOn LEFT JOIN addOnDesigner ON addOnDesigner.addOnId = addOn.id LEFT JOIN designer ON addOnDesigner.designerId = designer.id LEFT JOIN addOnLanguage ON addOnLanguage.addOnId = addOn.id LEFT JOIN language ON addOnLanguage.languageId = language.id WHERE language.id = :idLanguage GROUP BY addOn.name ORDER BY addOn.name ASC")
    fun getWithDesignerFromLanguageId(idLanguage:Long):LiveData<List<DesignerWithAddOn>>


    @Query("SELECT addOn.id AS id, addOn.name AS name, designer.name AS designer FROM addOn INNER JOIN addOnDesigner ON addOnDesigner.addOnId = addOn.id INNER JOIN designer ON addOnDesigner.designerId = designer.id WHERE addOn.difficultyId = :idDifficulty GROUP BY addOn.name ORDER BY addOn.name ASC")
    fun getWithDesignerFromDifficultyId(idDifficulty:Long):LiveData<List<DesignerWithAddOn>>


    @Insert(onConflict =  OnConflictStrategy.REPLACE)
    fun insert(game: AddOnTableBean) : Long
    
    @Update
    fun update(game: AddOnTableBean)

    @Query("SELECT addOn.id as id, addOn.name as name, designer.name as designer FROM addOn LEFT JOIN addOnDesigner ON addOnId = addOn.id LEFT JOIN  designer on designerId = designer.id WHERE addOn.gameId=:gameId GROUP BY addOn.name")
    fun getDesignerWithAddOnOfGame(gameId:Long): LiveData<List<DesignerWithAddOn>>

    @Query("SELECT addOn.id as id, addOn.name as name, designer.name as designer FROM addOn LEFT JOIN addOnDesigner ON addOnId = addOn.id LEFT JOIN  designer on designerId = designer.id WHERE addOn.gameId=:gameId GROUP BY addOn.name")
    fun getDesignerWithAddOnOfGames(gameId:Long): List<DesignerWithAddOn>
    
    @Query("DELETE FROM addOn")
    fun deleteAll()
    
    @Query("DELETE FROM addOn WHERE id=:objectId")
    fun deleteOne(objectId:Long)
      
    @Query("SELECT * FROM addOn WHERE serverId=:id")
    override fun getByServerId(id:Long):List<AddOnTableBean>

    @Query("SELECT * FROM addOn WHERE serverId=NULL")
    fun getWithoutServerId():List<AddOnTableBean>
            
}
    
    
    
@Dao
interface MultiAddOnDao: CommonDao<MultiAddOnTableBean> {
    @Query("SELECT * FROM multiAddOn ORDER BY name ASC")
    fun getAll(): Flow<List<MultiAddOnTableBean>>
    
    @Query("SELECT * FROM multiAddOn")
    fun getList(): List<MultiAddOnTableBean>
    
    @Query("SELECT * FROM multiAddOn WHERE name=:searchedName")
    override fun getByName(searchedName:String): List<MultiAddOnTableBean>

    @Query("SELECT * FROM game WHERE id=:gameId")
    fun getObjectById(gameId:Long): List<GameTableBean>

    @Query("SELECT * FROM multiAddOn WHERE id=:gameId")
    fun getById(gameId:Long): Flow<List<AddOnTableBean>>

    @Query("SELECT multiAddOn.id as id, multiAddOn.name as name, designer.name as designer FROM multiAddOn LEFT JOIN multiAddOnDesigner ON multiAddOnId = multiAddOn.id LEFT JOIN  designer on designerId = designer.id GROUP BY multiAddOn.name")
    fun getDesignerWithGame(): LiveData<List<DesignerWithMultiAddOn>>

    @Query("SELECT game.id as id, game.name as name, designer.name as designer FROM game  LEFT JOIN gameDesigner ON  gameDesigner.gameId = game.id LEFT JOIN designer ON designer.id = gameDesigner.designerId LEFT JOIN gameMultiAddOn ON gameMultiAddOn.gameId = game.id LEFT JOIN multiAddOn ON gameMultiAddOn.multiAddOnId = multiAddOn.id WHERE multiAddOn.id=:gameId GROUP BY game.name")
    fun getGameFromMultiAddOn(gameId:Long): LiveData<List<DesignerWithGame>>

    @Query("SELECT multiAddOn.id as id, multiAddOn.name as name, designer.name as designer FROM multiAddOn LEFT JOIN multiAddOnDesigner ON multiAddOnDesigner.multiAddOnId = multiAddOn.id LEFT JOIN  designer on designerId = designer.id LEFT JOIN gameMultiAddOn ON gameMultiAddOn.multiAddOnId = multiAddOn.id WHERE gameMultiAddOn.gameId=:gameId GROUP BY multiAddOn.name")
    fun getDesignerWithMultiAddOnOfGame(gameId:Long): LiveData<List<DesignerWithMultiAddOn>>

    @Query("SELECT difficulty.id as id, difficulty.name as name FROM difficulty LEFT JOIN multiAddOn ON difficultyId = difficulty.id WHERE multiAddOn.id = :idGame")
    fun getDifficultyOfMultiAddOn(idGame:Long): LiveData<List<DifficultyTableBean>>


    @Query("SELECT designer.id AS id, designer.name AS name FROM designer INNER JOIN multiAddOnDesigner ON designerId = designer.id INNER JOIN multiAddOn ON multiAddOnId = :idGame GROUP BY designer.name")
    fun getDesignersOfMultiAddOn(idGame:Long): Flow<List<DesignerTableBean>>


    @Query("SELECT artist.id AS id, artist.name AS name FROM artist INNER JOIN multiAddOnArtist ON artistId = artist.id INNER JOIN multiAddOn ON multiAddOnId = :idGame GROUP BY artist.name")
    fun getArtistsOfMultiAddOn(idGame:Long): LiveData<List<ArtistTableBean>>


    @Query("SELECT publisher.id AS id, publisher.name AS name FROM publisher INNER JOIN multiAddOnPublisher ON publisherId = publisher.id INNER JOIN multiAddOn ON multiAddOnId = :idGame GROUP BY publisher.name")
    fun getPublishersOfMultiAddOn(idGame:Long): LiveData<List<PublisherTableBean>>


    @Query("SELECT playingMod.id AS id, playingMod.name AS name FROM playingMod INNER JOIN multiAddOnPlayingMod ON playingModId = playingMod.id INNER JOIN multiAddOn ON multiAddOnId = :idGame GROUP BY playingMod.name")
    fun getPlayingModsOfMultiAddOn(idGame:Long): LiveData<List<PlayingModTableBean>>


    @Query("SELECT language.id AS id, language.name AS name FROM language INNER JOIN multiAddOnLanguage ON languageId = language.id INNER JOIN multiAddOn ON multiAddOnId = :idGame GROUP BY language.name")
    fun getLanguagesOfMultiAddOn(idGame:Long): LiveData<List<LanguageTableBean>>

    @Query("SELECT multiAddOn.id AS id, multiAddOn.name AS name, designer.name AS designer FROM multiAddOn INNER JOIN multiAddOnDesigner ON multiAddOnDesigner.multiAddOnId = multiAddOn.id INNER JOIN designer ON multiAddOnDesigner.designerId = designer.id WHERE designer.id = :idDesigner GROUP BY multiAddOn.name ORDER BY multiAddOn.name ASC")
    fun getWithDesignerFromDesignerId(idDesigner:Long):LiveData<List<DesignerWithMultiAddOn>>


    @Query("SELECT multiAddOn.id AS id, multiAddOn.name AS name, designer.name AS designer FROM multiAddOn LEFT JOIN multiAddOnDesigner ON multiAddOnDesigner.multiAddOnId = multiAddOn.id LEFT JOIN designer ON multiAddOnDesigner.designerId = designer.id LEFT JOIN multiAddOnArtist ON multiAddOnArtist.multiAddOnId = multiAddOn.id LEFT JOIN artist ON multiAddOnArtist.artistId = artist.id WHERE artist.id = :idArtist GROUP BY multiAddOn.name ORDER BY multiAddOn.name ASC")
    fun getWithDesignerFromArtistId(idArtist:Long):LiveData<List<DesignerWithMultiAddOn>>


    @Query("SELECT multiAddOn.id AS id, multiAddOn.name AS name, designer.name AS designer FROM multiAddOn LEFT JOIN multiAddOnDesigner ON multiAddOnDesigner.multiAddOnId = multiAddOn.id LEFT JOIN designer ON multiAddOnDesigner.designerId = designer.id LEFT JOIN multiAddOnPublisher ON multiAddOnPublisher.multiAddOnId = multiAddOn.id LEFT JOIN publisher ON multiAddOnPublisher.publisherId = publisher.id WHERE publisher.id = :idPublisher GROUP BY multiAddOn.name ORDER BY multiAddOn.name ASC")
    fun getWithDesignerFromPublisherId(idPublisher:Long):LiveData<List<DesignerWithMultiAddOn>>


    @Query("SELECT multiAddOn.id AS id, multiAddOn.name AS name, designer.name AS designer FROM multiAddOn LEFT JOIN multiAddOnDesigner ON multiAddOnDesigner.multiAddOnId = multiAddOn.id LEFT JOIN designer ON multiAddOnDesigner.designerId = designer.id LEFT JOIN multiAddOnPlayingMod ON multiAddOnPlayingMod.multiAddOnId = multiAddOn.id LEFT JOIN playingMod ON multiAddOnPlayingMod.playingModId = playingMod.id WHERE playingMod.id = :idPlayingMod GROUP BY multiAddOn.name ORDER BY multiAddOn.name ASC")
    fun getWithDesignerFromPlayingModId(idPlayingMod:Long):LiveData<List<DesignerWithMultiAddOn>>


    @Query("SELECT multiAddOn.id AS id, multiAddOn.name AS name, designer.name AS designer FROM multiAddOn LEFT JOIN multiAddOnDesigner ON multiAddOnDesigner.multiAddOnId = multiAddOn.id LEFT JOIN designer ON multiAddOnDesigner.designerId = designer.id LEFT JOIN multiAddOnLanguage ON multiAddOnLanguage.multiAddOnId = multiAddOn.id LEFT JOIN language ON multiAddOnLanguage.languageId = language.id WHERE language.id = :idLanguage GROUP BY multiAddOn.name ORDER BY multiAddOn.name ASC")
    fun getWithDesignerFromLanguageId(idLanguage:Long):LiveData<List<DesignerWithMultiAddOn>>


    @Query("SELECT multiAddOn.id AS id, multiAddOn.name AS name, designer.name AS designer FROM multiAddOn INNER JOIN multiAddOnDesigner ON multiAddOnDesigner.multiAddOnId = multiAddOn.id INNER JOIN designer ON multiAddOnDesigner.designerId = designer.id WHERE multiAddOn.difficultyId = :idDifficulty GROUP BY multiAddOn.name ORDER BY multiAddOn.name ASC")
    fun getWithDesignerFromDifficultyId(idDifficulty:Long):LiveData<List<DesignerWithMultiAddOn>>

    @Insert(onConflict =  OnConflictStrategy.REPLACE)
    fun insert(game: MultiAddOnTableBean) : Long
    
    @Update
    fun update(game: MultiAddOnTableBean)
    
    @Query("DELETE FROM multiAddOn")
    fun deleteAll()
    
    @Query("DELETE FROM multiAddOn WHERE id=:objectId")
    fun deleteOne(objectId:Long)
      
    @Query("SELECT * FROM multiAddOn WHERE serverId=:id")
    override fun getByServerId(id:Long):List<MultiAddOnTableBean>

    @Query("SELECT * FROM multiAddOn WHERE serverId=NULL")
    fun getWithoutServerId():List<MultiAddOnTableBean>
            
}
    
    
    
@Dao
interface TagDao: CommonCustomInsert<TagTableBean> {
    @Query("SELECT * FROM tag ORDER BY name ASC")
    fun getAll(): Flow<List<TagTableBean>>
    
    @Query("SELECT * FROM tag")
    fun getList(): List<TagTableBean>
    
    @Query("SELECT * FROM tag WHERE name=:searchedName")
    override fun getByName(searchedName:String): List<TagTableBean>
    
    @Query("INSERT INTO tag(name) VALUES (:newElement)")
    override fun insert(newElement:String)
    
    @Update
    fun update(game: TagTableBean)
    
    @Query("DELETE FROM tag")
    fun deleteAll()
    
    @Query("DELETE FROM tag WHERE id=:objectId")
    fun deleteOne(objectId:Long)
    
}
    
    
    
@Dao
interface TopicDao: CommonCustomInsert<TopicTableBean> {
    @Query("SELECT * FROM topic ORDER BY name ASC")
    fun getAll(): Flow<List<TopicTableBean>>
    
    @Query("SELECT * FROM topic")
    fun getList(): List<TopicTableBean>
    
    @Query("SELECT * FROM topic WHERE name=:searchedName")
    override fun getByName(searchedName:String): List<TopicTableBean>

    @Query("INSERT INTO topic(name) VALUES (:newElement)")
    override fun insert(newElement:String)
    
    @Update
    fun update(game: TopicTableBean)
    
    @Query("DELETE FROM topic")
    fun deleteAll()
    
    @Query("DELETE FROM topic WHERE id=:objectId")
    fun deleteOne(objectId:Long)
    
}
    
    
    
@Dao
interface MechanismDao: CommonCustomInsert<MechanismTableBean> {
    @Query("SELECT * FROM mechanism ORDER BY name ASC")
    fun getAll(): Flow<List<MechanismTableBean>>
    
    @Query("SELECT * FROM mechanism")
    fun getList(): List<MechanismTableBean>
    
    @Query("SELECT * FROM mechanism WHERE name=:searchedName")
    override fun getByName(searchedName:String): List<MechanismTableBean>

    @Query("INSERT INTO mechanism(name) VALUES (:newElement)")
    override fun insert(newElement:String)
    
    @Update
    fun update(game: MechanismTableBean)
    
    @Query("DELETE FROM mechanism")
    fun deleteAll()
    
    @Query("DELETE FROM mechanism WHERE id=:objectId")
    fun deleteOne(objectId:Long)
    
}
    
    
    
@Dao
interface DesignerDao: CommonCustomInsert<DesignerTableBean> {
    @Query("SELECT * FROM designer ORDER BY name ASC")
    fun getAll(): Flow<List<DesignerTableBean>>
    
    @Query("SELECT * FROM designer")
    fun getList(): List<DesignerTableBean>

    @Query("SELECT game.id AS id, game.name AS name, designer.name AS designer FROM game LEFT JOIN gameDesigner ON gameDesigner.gameId = :idGame LEFT JOIN designer ON gameDesigner.designerId = designer.id GROUP BY game.name ORDER BY game.name ASC")
    fun getWithDesignerFromGameID(idGame:Long):LiveData<List<DesignerWithGame>>
    
    @Query("SELECT * FROM designer WHERE name=:searchedName")
    override fun getByName(searchedName:String): List<DesignerTableBean>

    @Query("INSERT INTO designer(name) VALUES (:newElement)")
    override fun insert(newElement:String)
    
    @Update
    fun update(game: DesignerTableBean)
    
    @Query("DELETE FROM designer")
    fun deleteAll()
    
    @Query("DELETE FROM designer WHERE id=:objectId")
    fun deleteOne(objectId:Long)


    
}
    
    
    
@Dao
interface ArtistDao: CommonCustomInsert<ArtistTableBean> {
    @Query("SELECT * FROM artist ORDER BY name ASC")
    fun getAll(): Flow<List<ArtistTableBean>>
    
    @Query("SELECT * FROM artist")
    fun getList(): List<ArtistTableBean>
    
    @Query("SELECT * FROM artist WHERE name=:searchedName")
    override fun getByName(searchedName:String): List<ArtistTableBean>

    @Query("INSERT INTO artist(name) VALUES (:newElement)")
    override fun insert(newElement:String)
    
    @Update
    fun update(game: ArtistTableBean)
    
    @Query("DELETE FROM artist")
    fun deleteAll()
    
    @Query("DELETE FROM artist WHERE id=:objectId")
    fun deleteOne(objectId:Long)
    
}
    
    
    
@Dao
interface PublisherDao: CommonCustomInsert<PublisherTableBean> {
    @Query("SELECT * FROM publisher ORDER BY name ASC")
    fun getAll(): Flow<List<PublisherTableBean>>
    
    @Query("SELECT * FROM publisher")
    fun getList(): List<PublisherTableBean>
    
    @Query("SELECT * FROM publisher WHERE name=:searchedName")
    override fun getByName(searchedName:String): List<PublisherTableBean>

    @Query("INSERT INTO publisher(name) VALUES (:newElement)")
    override fun insert(newElement:String)
    
    @Update
    fun update(game: PublisherTableBean)
    
    @Query("DELETE FROM publisher")
    fun deleteAll()
    
    @Query("DELETE FROM publisher WHERE id=:objectId")
    fun deleteOne(objectId:Long)
    
}
    
    
    
@Dao
interface PlayingModDao: CommonCustomInsert<PlayingModTableBean> {
    @Query("SELECT * FROM playingMod ORDER BY name ASC")
    fun getAll(): Flow<List<PlayingModTableBean>>
    
    @Query("SELECT * FROM playingMod")
    fun getList(): List<PlayingModTableBean>
    
    @Query("SELECT * FROM playingMod WHERE name=:searchedName")
    override fun getByName(searchedName:String): List<PlayingModTableBean>

    @Query("INSERT INTO playingMod(name) VALUES (:newElement)")
    override fun insert(newElement:String)
    
    @Update
    fun update(game: PlayingModTableBean)
    
    @Query("DELETE FROM playingMod")
    fun deleteAll()
    
    @Query("DELETE FROM playingMod WHERE id=:objectId")
    fun deleteOne(objectId:Long)
    
}
    
    
    
@Dao
interface LanguageDao: CommonCustomInsert<LanguageTableBean> {
    @Query("SELECT * FROM language ORDER BY name ASC")
    fun getAll(): Flow<List<LanguageTableBean>>
    
    @Query("SELECT * FROM language")
    fun getList(): List<LanguageTableBean>
    
    @Query("SELECT * FROM language WHERE name=:searchedName")
    override fun getByName(searchedName:String): List<LanguageTableBean>

    @Query("INSERT INTO language(name) VALUES (:newElement)")
    override fun insert(newElement:String)
    
    @Update
    fun update(game: LanguageTableBean)
    
    @Query("DELETE FROM language")
    fun deleteAll()
    
    @Query("DELETE FROM language WHERE id=:objectId")
    fun deleteOne(objectId:Long)
    
}
    
    
    
@Dao
interface DifficultyDao: CommonComponentDao<DifficultyTableBean> {
    @Query("SELECT * FROM difficulty ORDER BY name ASC")
    fun getAll(): Flow<List<DifficultyTableBean>>
    
    @Query("SELECT * FROM difficulty")
    fun getList(): List<DifficultyTableBean>
    
    @Query("SELECT * FROM difficulty WHERE name=:searchedName")
    override fun getByName(searchedName:String): List<DifficultyTableBean>
    
    @Insert
    fun insert(game: DifficultyTableBean) : Long
    
    @Update
    fun update(game: DifficultyTableBean)
    
    @Query("DELETE FROM difficulty")
    fun deleteAll()
    
    @Query("DELETE FROM difficulty WHERE id=:objectId")
    fun deleteOne(objectId:Long)
    
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
