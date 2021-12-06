package com.example.filrouge.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.filrouge.bean.*
import kotlinx.coroutines.flow.Flow


interface CommonDao<T, U>: CommonComponentDao<T>{
    fun getByServerId(id:Long):List<T>
    fun getById(id:Long):Flow<List<T>>
    fun getDifficulty(idGame: Long):LiveData<List<DifficultyTableBean>>
    fun getDesigners(idGame:Long): LiveData<List<DesignerTableBean>>
    fun getDesignerObject(idGame:Long): List<DesignerTableBean>
    fun getArtists(idGame:Long): LiveData<List<ArtistTableBean>>
    fun getArtistObject(idGame:Long): List<ArtistTableBean>
    fun getPublishers(idGame:Long): LiveData<List<PublisherTableBean>>
    fun getPublisherObject(idGame:Long): List<PublisherTableBean>
    fun getPlayingMods(idGame:Long): LiveData<List<PlayingModTableBean>>
    fun getPlayingModObject(idGame:Long): List<PlayingModTableBean>
    fun getLanguages(idGame:Long): LiveData<List<LanguageTableBean>>
    fun getLanguageObject(idGame:Long): List<LanguageTableBean>
    fun getList(): List<T>
    fun getImage(name:String): List<ImageTableBean>
    fun getAllWithDesigner():LiveData<List<U>>
}


interface CommonComponentDao<T>{
    fun getByName(searchedName:String): List<T>
    fun getNameList(): List<String>
}


interface CommonCustomInsert<T>: CommonComponentDao<T>{
    fun insert(newElement:String)
    fun getDeletableNameList(): Flow<List<T>>
    fun deleteOne(id:Long)
    fun getAll(): Flow<List<T>>
}


@Dao
interface GameDao: CommonDao<GameTableBean, DesignerWithGame> {
    @Query("SELECT * FROM game ORDER BY name ASC")
    fun getAll(): Flow<List<GameTableBean>>

    @Query("SELECT game.id AS id, game.name AS name, designer.name AS designer, image.name as image FROM game LEFT JOIN gameDesigner ON gameDesigner.gameId = game.id LEFT JOIN designer ON gameDesigner.designerId = designer.id LEFT JOIN image ON image.name = game.name || 'Game' GROUP BY game.name ORDER BY game.name ASC")
    override fun getAllWithDesigner():LiveData<List<DesignerWithGame>>

    @Query("SELECT * FROM image WHERE image.name = :name || 'Game'")
    override fun getImage(name: String): List<ImageTableBean>

    @Query("SELECT * FROM game")
    override fun getList(): List<GameTableBean>

    @Query("SELECT name FROM game")
    override fun getNameList(): List<String>

    @Query("SELECT * FROM game WHERE id=:id")
    override fun getById(id:Long): Flow<List<GameTableBean>>

    @Query("SELECT * FROM game WHERE id=:gameId")
    fun getObjectById(gameId:Long): List<GameTableBean>
    
    @Query("SELECT * FROM game WHERE name=:searchedName")
    override fun getByName(searchedName:String): List<GameTableBean>

    @Query("SELECT difficulty.id as id, difficulty.name as name FROM difficulty LEFT JOIN game ON difficultyId = difficulty.id WHERE game.id = :idGame")
    override fun getDifficulty(idGame:Long): LiveData<List<DifficultyTableBean>>

    @Query("SELECT tag.id AS id, tag.name AS name FROM tag INNER JOIN gameTag ON tagId = tag.id INNER JOIN game ON gameId = :idGame GROUP BY tag.name")
    fun getTagsOfGame(idGame:Long): LiveData<List<TagTableBean>>

    @Query("SELECT tag.id AS id, tag.name AS name FROM tag INNER JOIN gameTag ON tagId = tag.id INNER JOIN game ON gameId = :idGame GROUP BY tag.name")
    fun getTagObject(idGame:Long): List<TagTableBean>

    @Query("SELECT topic.id AS id, topic.name AS name FROM topic INNER JOIN gameTopic ON topicId = topic.id INNER JOIN game ON gameId = :idGame GROUP BY topic.name")
    fun getTopicsOfGame(idGame:Long): LiveData<List<TopicTableBean>>

    @Query("SELECT topic.id AS id, topic.name AS name FROM topic INNER JOIN gameTopic ON topicId = topic.id INNER JOIN game ON gameId = :idGame GROUP BY topic.name")
    fun getTopicObject(idGame:Long): List<TopicTableBean>

    @Query("SELECT mechanism.id AS id, mechanism.name AS name FROM mechanism INNER JOIN gameMechanism ON mechanismId = mechanism.id INNER JOIN game ON gameId = :idGame GROUP BY mechanism.name")
    fun getMechanismsOfGame(idGame:Long): LiveData<List<MechanismTableBean>>

    @Query("SELECT mechanism.id AS id, mechanism.name AS name FROM mechanism INNER JOIN gameMechanism ON mechanismId = mechanism.id INNER JOIN game ON gameId = :idGame GROUP BY mechanism.name")
    fun getMechanismObject(idGame:Long): List<MechanismTableBean>

    @Query("SELECT designer.id AS id, designer.name AS name FROM designer INNER JOIN gameDesigner ON designerId = designer.id INNER JOIN game ON gameId = :idGame GROUP BY designer.name")
    override fun getDesigners(idGame:Long): LiveData<List<DesignerTableBean>>

    @Query("SELECT designer.id AS id, designer.name AS name FROM designer INNER JOIN gameDesigner ON designerId = designer.id INNER JOIN game ON gameId = :idGame GROUP BY designer.name")
    override fun getDesignerObject(idGame:Long): List<DesignerTableBean>

    @Query("SELECT artist.id AS id, artist.name AS name FROM artist INNER JOIN gameArtist ON artistId = artist.id INNER JOIN game ON gameId = :idGame GROUP BY artist.name")
    override fun getArtists(idGame:Long): LiveData<List<ArtistTableBean>>

    @Query("SELECT artist.id AS id, artist.name AS name FROM artist INNER JOIN gameArtist ON artistId = artist.id INNER JOIN game ON gameId = :idGame GROUP BY artist.name")
    override fun getArtistObject(idGame:Long): List<ArtistTableBean>

    @Query("SELECT publisher.id AS id, publisher.name AS name FROM publisher INNER JOIN gamePublisher ON publisherId = publisher.id INNER JOIN game ON gameId = :idGame GROUP BY publisher.name")
    override fun getPublishers(idGame:Long): LiveData<List<PublisherTableBean>>

    @Query("SELECT publisher.id AS id, publisher.name AS name FROM publisher INNER JOIN gamePublisher ON publisherId = publisher.id INNER JOIN game ON gameId = :idGame GROUP BY publisher.name")
    override fun getPublisherObject(idGame:Long): List<PublisherTableBean>

    @Query("SELECT playingMod.id AS id, playingMod.name AS name FROM playingMod INNER JOIN gamePlayingMod ON playingModId = playingMod.id INNER JOIN game ON gameId = :idGame GROUP BY playingMod.name")
    override fun getPlayingMods(idGame:Long): LiveData<List<PlayingModTableBean>>

    @Query("SELECT playingMod.id AS id, playingMod.name AS name FROM playingMod INNER JOIN gamePlayingMod ON playingModId = playingMod.id INNER JOIN game ON gameId = :idGame GROUP BY playingMod.name")
    override fun getPlayingModObject(idGame:Long): List<PlayingModTableBean>

    @Query("SELECT language.id AS id, language.name AS name FROM language INNER JOIN gameLanguage ON languageId = language.id INNER JOIN game ON gameId = :idGame GROUP BY language.name")
    override fun getLanguages(idGame:Long): LiveData<List<LanguageTableBean>>

    @Query("SELECT language.id AS id, language.name AS name FROM language INNER JOIN gameLanguage ON languageId = language.id INNER JOIN game ON gameId = :idGame GROUP BY language.name")
    override fun getLanguageObject(idGame:Long): List<LanguageTableBean>

    @Query("SELECT game.id AS id, game.name AS name, designer.name AS designer, image.name as image FROM game LEFT JOIN gameDesigner ON gameDesigner.gameId = game.id LEFT JOIN designer ON gameDesigner.designerId = designer.id LEFT JOIN gameArtist ON gameArtist.gameId = game.id LEFT JOIN artist ON gameArtist.artistId = artist.id LEFT JOIN gamePublisher ON gamePublisher.gameId = game.id LEFT JOIN publisher ON gamePublisher.publisherId = publisher.id LEFT JOIN gamePlayingMod ON gamePlayingMod.gameId = game.id LEFT JOIN playingMod ON gamePlayingMod.playingModId = playingMod.id LEFT JOIN gameLanguage ON gameLanguage.gameId = game.id LEFT JOIN language ON gameLanguage.languageId = language.id LEFT JOIN gameTag ON gameTag.gameId = game.id LEFT JOIN tag ON gameTag.tagId = tag.id LEFT JOIN gameTopic ON gameTopic.gameId = game.id LEFT JOIN topic ON gameTopic.topicId = topic.id LEFT JOIN gameMechanism ON gameMechanism.gameId = game.id LEFT JOIN mechanism ON gameMechanism.mechanismId = mechanism.id LEFT JOIN difficulty ON difficulty.id = game.difficultyId LEFT JOIN image ON image.name = game.name || 'Game' WHERE (game.name like '%' || :name || '%' OR :name IS NULL) AND (difficulty.name like '%' || :difficulty || '%' OR :difficulty IS NULL) AND (designer.name like '%' || :designer || '%' OR :designer IS NULL) AND (artist.name like '%' || :artist || '%' OR :artist IS NULL) AND (publisher.name like '%' || :publisher || '%' OR :publisher IS NULL) AND (playingMod.name like '%' || :playingMod || '%' OR :playingMod IS NULL) AND (language.name like '%' || :language || '%' OR :language IS NULL) AND (tag.name like '%' || :tag || '%' OR :tag IS NULL) AND (topic.name like '%' || :topic || '%' OR :topic IS NULL) AND (mechanism.name like '%' || :mechanism || '%' OR :mechanism IS NULL) AND (:playerMin BETWEEN game.player_min AND game.player_max OR :playerMin IS NULL) AND (:playerMax BETWEEN game.player_min AND game.player_max OR :playerMax IS NULL) AND (game.max_time >= :maxTime OR :maxTime IS NULL) AND (game.age >= :age OR :age IS NULL) GROUP BY game.name ORDER BY game.name ASC")
    fun getWithDesignerFromSearchQuery(name:String?, designer:String?, artist:String?, publisher:String?, playerMin:Int?, playerMax:Int?, maxTime:Int?, difficulty:String?, age:Int?, playingMod:String?, language:String?, tag:String?, topic:String?, mechanism:String?):LiveData<List<DesignerWithGame>>

    @Query("SELECT game.id AS id, game.name AS name, designer.name AS designer, image.name as image FROM game LEFT JOIN gameDesigner ON gameDesigner.gameId = game.id LEFT JOIN designer ON gameDesigner.designerId = designer.id LEFT JOIN gameTag ON gameTag.gameId = game.id LEFT JOIN tag ON gameTag.tagId = tag.id  LEFT JOIN image ON image.name = game.name || 'Game'  WHERE tag.id = :idTag GROUP BY game.name ORDER BY game.name ASC")
    fun getWithDesignerFromTagId(idTag:Long):LiveData<List<DesignerWithGame>>

    @Query("SELECT game.id AS id, game.name AS name, designer.name AS designer, image.name as image FROM game LEFT JOIN gameDesigner ON gameDesigner.gameId = game.id LEFT JOIN designer ON gameDesigner.designerId = designer.id LEFT JOIN gameTopic ON gameTopic.gameId = game.id LEFT JOIN topic ON gameTopic.topicId = topic.id  LEFT JOIN image ON image.name = game.name || 'Game'  WHERE topic.id = :idTopic GROUP BY game.name ORDER BY game.name ASC")
    fun getWithDesignerFromTopicId(idTopic:Long):LiveData<List<DesignerWithGame>>

    @Query("SELECT game.id AS id, game.name AS name, designer.name AS designer, image.name as image FROM game LEFT JOIN gameDesigner ON gameDesigner.gameId = game.id LEFT JOIN designer ON gameDesigner.designerId = designer.id LEFT JOIN gameMechanism ON gameMechanism.gameId = game.id LEFT JOIN mechanism ON gameMechanism.mechanismId = mechanism.id  LEFT JOIN image ON image.name = game.name || 'Game'  WHERE mechanism.id = :idMechanism GROUP BY game.name ORDER BY game.name ASC")
    fun getWithDesignerFromMechanismId(idMechanism:Long):LiveData<List<DesignerWithGame>>

    @Query("SELECT game.id AS id, game.name AS name, designer.name AS designer, image.name as image FROM game INNER JOIN gameDesigner ON gameDesigner.gameId = game.id INNER JOIN designer ON gameDesigner.designerId = designer.id LEFT JOIN image ON image.name = game.name || 'Game' WHERE designer.id = :idDesigner GROUP BY game.name ORDER BY game.name ASC")
    fun getWithDesignerFromDesignerId(idDesigner:Long):LiveData<List<DesignerWithGame>>

    @Query("SELECT game.id AS id, game.name AS name, designer.name AS designer, image.name as image FROM game LEFT JOIN gameDesigner ON gameDesigner.gameId = game.id LEFT JOIN designer ON gameDesigner.designerId = designer.id LEFT JOIN gameArtist ON gameArtist.gameId = game.id LEFT JOIN artist ON gameArtist.artistId = artist.id  LEFT JOIN image ON image.name = game.name || 'Game'  WHERE artist.id = :idArtist GROUP BY game.name ORDER BY game.name ASC")
    fun getWithDesignerFromArtistId(idArtist:Long):LiveData<List<DesignerWithGame>>

    @Query("SELECT game.id AS id, game.name AS name, designer.name AS designer, image.name as image FROM game LEFT JOIN gameDesigner ON gameDesigner.gameId = game.id LEFT JOIN designer ON gameDesigner.designerId = designer.id LEFT JOIN gamePublisher ON gamePublisher.gameId = game.id LEFT JOIN publisher ON gamePublisher.publisherId = publisher.id  LEFT JOIN image ON image.name = game.name || 'Game'  WHERE publisher.id = :idPublisher GROUP BY game.name ORDER BY game.name ASC")
    fun getWithDesignerFromPublisherId(idPublisher:Long):LiveData<List<DesignerWithGame>>

    @Query("SELECT game.id AS id, game.name AS name, designer.name AS designer, image.name as image FROM game LEFT JOIN gameDesigner ON gameDesigner.gameId = game.id LEFT JOIN designer ON gameDesigner.designerId = designer.id LEFT JOIN gamePlayingMod ON gamePlayingMod.gameId = game.id LEFT JOIN playingMod ON gamePlayingMod.playingModId = playingMod.id  LEFT JOIN image ON image.name = game.name || 'Game'  WHERE playingMod.id = :idPlayingMod GROUP BY game.name ORDER BY game.name ASC")
    fun getWithDesignerFromPlayingModId(idPlayingMod:Long):LiveData<List<DesignerWithGame>>

    @Query("SELECT game.id AS id, game.name AS name, designer.name AS designer, image.name as image FROM game LEFT JOIN gameDesigner ON gameDesigner.gameId = game.id LEFT JOIN designer ON gameDesigner.designerId = designer.id LEFT JOIN gameLanguage ON gameLanguage.gameId = game.id LEFT JOIN language ON gameLanguage.languageId = language.id  LEFT JOIN image ON image.name = game.name || 'Game'  WHERE language.id = :idLanguage GROUP BY game.name ORDER BY game.name ASC")
    fun getWithDesignerFromLanguageId(idLanguage:Long):LiveData<List<DesignerWithGame>>

    @Query("SELECT game.id AS id, game.name AS name, designer.name AS designer, image.name as image FROM game INNER JOIN gameDesigner ON gameDesigner.gameId = game.id INNER JOIN designer ON gameDesigner.designerId = designer.id  LEFT JOIN image ON image.name = game.name || 'Game' WHERE game.difficultyId = :idDifficulty GROUP BY game.name ORDER BY game.name ASC")
    fun getWithDesignerFromDifficultyId(idDifficulty:Long):LiveData<List<DesignerWithGame>>

    @Query("SELECT * FROM game WHERE serverId=:id")
    override fun getByServerId(id:Long):List<GameTableBean>

    @Query("SELECT * FROM game WHERE serverId IS NULL")
    fun getWithoutServerId():List<GameTableBean>

    @Query("SELECT * FROM game WHERE hasChanged=1")
    fun getChanged():List<GameTableBean>

    @Insert(onConflict =  OnConflictStrategy.REPLACE)
    fun insert(game: GameTableBean) : Long
    
    @Update
    fun update(game: GameTableBean)
    
    @Query("DELETE FROM game")
    fun deleteAll()
    
    @Query("DELETE FROM game WHERE id=:objectId")
    fun deleteOne(objectId:Long)
}
    
    
@Dao
interface AddOnDao: CommonDao<AddOnTableBean, DesignerWithAddOn> {
    @Query("SELECT * FROM addOn ORDER BY name ASC")
    fun getAll(): Flow<List<AddOnTableBean>>

    @Query("SELECT addOn.id AS id, addOn.name AS name, designer.name AS designer, image.name as image FROM addOn LEFT JOIN addOnDesigner ON addOnDesigner.addOnId = addOn.id LEFT JOIN designer ON addOnDesigner.designerId = designer.id LEFT JOIN image ON image.name = addOn.name || 'AddOn'  GROUP BY addOn.name ORDER BY addOn.name ASC")
    override fun getAllWithDesigner():LiveData<List<DesignerWithAddOn>>

    @Query("SELECT * FROM image WHERE image.name = :name || 'AddOn'")
    override fun getImage(name: String): List<ImageTableBean>

    @Query("SELECT * FROM addOn")
    override fun getList(): List<AddOnTableBean>

    @Query("SELECT name FROM addOn")
    override fun getNameList(): List<String>
    
    @Query("SELECT * FROM addOn WHERE name=:searchedName")
    override fun getByName(searchedName:String): List<AddOnTableBean>

    @Query("SELECT * FROM addOn WHERE id=:id")
    override fun getById(id:Long): Flow<List<AddOnTableBean>>

    @Query("SELECT * FROM addOn WHERE id=:gameId")
    fun getObjectById(gameId:Long): List<AddOnTableBean>

    @Query("SELECT difficulty.id as id, difficulty.name as name FROM difficulty LEFT JOIN addOn ON difficultyId = difficulty.id WHERE addOn.id = :idGame")
    override fun getDifficulty(idGame:Long): LiveData<List<DifficultyTableBean>>

    @Query("SELECT designer.id as id, designer.name as name FROM designer INNER JOIN addOnDesigner ON designerId = designer.id INNER JOIN addOn ON addOnId = :idGame GROUP BY designer.name")
    override fun getDesigners(idGame:Long): LiveData<List<DesignerTableBean>>

    @Query("SELECT designer.id as id, designer.name as name FROM designer INNER JOIN addOnDesigner ON designerId = designer.id INNER JOIN addOn ON addOnId = :idGame GROUP BY designer.name")
    override fun getDesignerObject(idGame:Long): List<DesignerTableBean>

    @Query("SELECT artist.id AS id, artist.name AS name FROM artist INNER JOIN addOnArtist ON artistId = artist.id INNER JOIN addOn ON addOnId = :idGame GROUP BY artist.name")
    override fun getArtists(idGame:Long): LiveData<List<ArtistTableBean>>

    @Query("SELECT artist.id AS id, artist.name AS name FROM artist INNER JOIN addOnArtist ON artistId = artist.id INNER JOIN addOn ON addOnId = :idGame GROUP BY artist.name")
    override fun getArtistObject(idGame: Long): List<ArtistTableBean>

    @Query("SELECT publisher.id AS id, publisher.name AS name FROM publisher INNER JOIN addOnPublisher ON publisherId = publisher.id INNER JOIN addOn ON addOnId = :idGame GROUP BY publisher.name")
    override fun getPublishers(idGame:Long): LiveData<List<PublisherTableBean>>

    @Query("SELECT publisher.id AS id, publisher.name AS name FROM publisher INNER JOIN addOnPublisher ON publisherId = publisher.id INNER JOIN addOn ON addOnId = :idGame GROUP BY publisher.name")
    override fun getPublisherObject(idGame: Long): List<PublisherTableBean>

    @Query("SELECT playingMod.id AS id, playingMod.name AS name FROM playingMod INNER JOIN addOnPlayingMod ON playingModId = playingMod.id INNER JOIN addOn ON addOnId = :idGame GROUP BY playingMod.name")
    override fun getPlayingMods(idGame:Long): LiveData<List<PlayingModTableBean>>

    @Query("SELECT playingMod.id AS id, playingMod.name AS name FROM playingMod INNER JOIN addOnPlayingMod ON playingModId = playingMod.id INNER JOIN addOn ON addOnId = :idGame GROUP BY playingMod.name")
    override fun getPlayingModObject(idGame: Long): List<PlayingModTableBean>

    @Query("SELECT language.id AS id, language.name AS name FROM language INNER JOIN addOnLanguage ON languageId = language.id INNER JOIN addOn ON addOnId = :idGame GROUP BY language.name")
    override fun getLanguages(idGame:Long): LiveData<List<LanguageTableBean>>

    @Query("SELECT language.id AS id, language.name AS name FROM language INNER JOIN addOnLanguage ON languageId = language.id INNER JOIN addOn ON addOnId = :idGame GROUP BY language.name")
    override fun getLanguageObject(idGame: Long): List<LanguageTableBean>

    @Query("SELECT game.id as id, game.name as name, designer.name as designer, image.name as image  FROM game  LEFT JOIN gameDesigner ON  gameDesigner.gameId = game.id LEFT JOIN designer ON designer.id = gameDesigner.designerId LEFT JOIN image on image.name = game.name || 'Game' WHERE game.id=:gameId GROUP BY game.name")
    fun getGameFromAddOn(gameId:Long): LiveData<List<DesignerWithAddOn>>

    @Query("SELECT game.id as id, game.name as name, designer.name as designer, image.name as image FROM addOn LEFT JOIN game ON addOn.gameId = game.id LEFT JOIN gameDesigner ON  gameDesigner.gameId = game.id LEFT JOIN designer ON designer.id = gameDesigner.designerId LEFT JOIN image on image.name = game.name || 'Game' WHERE addOn.gameId=:gameId GROUP BY game.name")
    fun getGameFromAddOns(gameId:Long): List<DesignerWithGame>

    @Query("SELECT addOn.id AS id, addOn.name AS name, designer.name AS designer, image.name as image FROM addOn INNER JOIN addOnDesigner ON addOnDesigner.addOnId = addOn.id INNER JOIN designer ON addOnDesigner.designerId = designer.id LEFT JOIN image ON image.name = addOn.name || 'AddOn' WHERE designer.id = :idDesigner GROUP BY addOn.name ORDER BY addOn.name ASC")
    fun getWithDesignerFromDesignerId(idDesigner:Long):LiveData<List<DesignerWithAddOn>>

    @Query("SELECT addOn.id AS id, addOn.name AS name, designer.name AS designer, image.name as image FROM addOn LEFT JOIN addOnDesigner ON addOnDesigner.addOnId = addOn.id LEFT JOIN designer ON addOnDesigner.designerId = designer.id LEFT JOIN addOnArtist ON addOnArtist.addOnId = addOn.id LEFT JOIN artist ON addOnArtist.artistId = artist.id  LEFT JOIN image ON image.name = addOn.name || 'AddOn'  WHERE artist.id = :idArtist GROUP BY addOn.name ORDER BY addOn.name ASC")
    fun getWithDesignerFromArtistId(idArtist:Long):LiveData<List<DesignerWithAddOn>>

    @Query("SELECT addOn.id AS id, addOn.name AS name, designer.name AS designer, image.name as image FROM addOn LEFT JOIN addOnDesigner ON addOnDesigner.addOnId = addOn.id LEFT JOIN designer ON addOnDesigner.designerId = designer.id LEFT JOIN addOnPublisher ON addOnPublisher.addOnId = addOn.id LEFT JOIN publisher ON addOnPublisher.publisherId = publisher.id  LEFT JOIN image ON image.name = addOn.name || 'AddOn'  WHERE publisher.id = :idPublisher GROUP BY addOn.name ORDER BY addOn.name ASC")
    fun getWithDesignerFromPublisherId(idPublisher:Long):LiveData<List<DesignerWithAddOn>>

    @Query("SELECT addOn.id AS id, addOn.name AS name, designer.name AS designer, image.name as image FROM addOn LEFT JOIN addOnDesigner ON addOnDesigner.addOnId = addOn.id LEFT JOIN designer ON addOnDesigner.designerId = designer.id LEFT JOIN addOnPlayingMod ON addOnPlayingMod.addOnId = addOn.id LEFT JOIN playingMod ON addOnPlayingMod.playingModId = playingMod.id  LEFT JOIN image ON image.name = addOn.name || 'AddOn'  WHERE playingMod.id = :idPlayingMod GROUP BY addOn.name ORDER BY addOn.name ASC")
    fun getWithDesignerFromPlayingModId(idPlayingMod:Long):LiveData<List<DesignerWithAddOn>>

    @Query("SELECT addOn.id AS id, addOn.name AS name, designer.name AS designer, image.name as image FROM addOn LEFT JOIN addOnDesigner ON addOnDesigner.addOnId = addOn.id LEFT JOIN designer ON addOnDesigner.designerId = designer.id LEFT JOIN addOnLanguage ON addOnLanguage.addOnId = addOn.id LEFT JOIN language ON addOnLanguage.languageId = language.id  LEFT JOIN image ON image.name = addOn.name || 'AddOn'  WHERE language.id = :idLanguage GROUP BY addOn.name ORDER BY addOn.name ASC")
    fun getWithDesignerFromLanguageId(idLanguage:Long):LiveData<List<DesignerWithAddOn>>

    @Query("SELECT addOn.id AS id, addOn.name AS name, designer.name AS designer, image.name as image FROM addOn INNER JOIN addOnDesigner ON addOnDesigner.addOnId = addOn.id INNER JOIN designer ON addOnDesigner.designerId = designer.id  LEFT JOIN image ON image.name = addOn.name || 'AddOn' WHERE addOn.difficultyId = :idDifficulty GROUP BY addOn.name ORDER BY addOn.name ASC")
    fun getWithDesignerFromDifficultyId(idDifficulty:Long):LiveData<List<DesignerWithAddOn>>

    @Query("SELECT addOn.id AS id, addOn.name AS name, designer.name AS designer, image.name as image FROM addOn LEFT JOIN addOnDesigner ON addOnDesigner.addOnId = addOn.id LEFT JOIN designer ON addOnDesigner.designerId = designer.id LEFT JOIN addOnArtist ON addOnArtist.addOnId = addOn.id LEFT JOIN artist ON addOnArtist.artistId = artist.id LEFT JOIN addOnPublisher ON addOnPublisher.addOnId = addOn.id LEFT JOIN publisher ON addOnPublisher.publisherId = publisher.id LEFT JOIN addOnPlayingMod ON addOnPlayingMod.addOnId = addOn.id LEFT JOIN playingMod ON addOnPlayingMod.playingModId = playingMod.id LEFT JOIN addOnLanguage ON addOnLanguage.addOnId = addOn.id LEFT JOIN language ON addOnLanguage.languageId = language.id LEFT JOIN difficulty ON difficulty.id = addOn.difficultyId LEFT JOIN image ON image.name = addOn.name || 'AddOn' WHERE (addOn.name like '%' || :name || '%' OR :name IS NULL) AND (difficulty.name like '%' || :difficulty || '%' OR :difficulty IS NULL) AND (designer.name like '%' || :designer || '%' OR :designer IS NULL) AND (artist.name like '%' || :artist || '%' OR :artist IS NULL) AND (publisher.name like '%' || :publisher || '%' OR :publisher IS NULL) AND (playingMod.name like '%' || :playingMod || '%' OR :playingMod IS NULL) AND (language.name like '%' || :language || '%' OR :language IS NULL) AND (:playerMin BETWEEN addOn.player_min AND addOn.player_max OR :playerMin IS NULL) AND (:playerMax BETWEEN addOn.player_min AND addOn.player_max OR :playerMax IS NULL) AND (addOn.max_time >= :maxTime OR :maxTime IS NULL) AND (addOn.age >= :age OR :age IS NULL) GROUP BY addOn.name ORDER BY addOn.name ASC\n")
    fun getWithDesignerFromSearchQuery(name:String?, designer:String?, artist:String?, publisher:String?, playerMin:Int?, playerMax:Int?, maxTime:Int?, difficulty:String?, age:Int?, playingMod:String?, language:String?):LiveData<List<DesignerWithAddOn>>

    @Query("SELECT * FROM addOn WHERE serverId=:id")
    override fun getByServerId(id:Long):List<AddOnTableBean>

    @Query("SELECT * FROM addOn WHERE serverId IS NULL")
    fun getWithoutServerId():List<AddOnTableBean>

    @Query("SELECT * FROM addOn WHERE hasChanged=1")
    fun getChanged():List<AddOnTableBean>

    @Insert(onConflict =  OnConflictStrategy.REPLACE)
    fun insert(game: AddOnTableBean) : Long
    
    @Update
    fun update(game: AddOnTableBean)

    @Query("SELECT addOn.id as id, addOn.name as name, designer.name as designer, image.name as image FROM addOn LEFT JOIN addOnDesigner ON addOnId = addOn.id LEFT JOIN  designer on designerId = designer.id LEFT JOIN image ON image.name = addOn.name || 'AddOn' WHERE addOn.gameId=:gameId GROUP BY addOn.name")
    fun getDesignerWithAddOnOfGame(gameId:Long): LiveData<List<DesignerWithAddOn>>

    @Query("SELECT addOn.id as id, addOn.name as name, designer.name as designer, image.name as image FROM addOn LEFT JOIN addOnDesigner ON addOnId = addOn.id LEFT JOIN  designer on designerId = designer.id LEFT JOIN image ON image.name = addOn.name || 'AddOn' WHERE addOn.gameId=:gameId GROUP BY addOn.name")
    fun getDesignerWithAddOnOfGames(gameId:Long): List<DesignerWithAddOn>
    
    @Query("DELETE FROM addOn")
    fun deleteAll()
    
    @Query("DELETE FROM addOn WHERE id=:objectId")
    fun deleteOne(objectId:Long)
}
    
    
@Dao
interface MultiAddOnDao: CommonDao<MultiAddOnTableBean, DesignerWithMultiAddOn> {
    @Query("SELECT * FROM multiAddOn ORDER BY name ASC")
    fun getAll(): Flow<List<MultiAddOnTableBean>>

    @Query("SELECT multiAddOn.id AS id, multiAddOn.name AS name, designer.name AS designer, image.name as image FROM multiAddOn INNER JOIN multiAddOnDesigner ON multiAddOnDesigner.multiAddOnId = multiAddOn.id INNER JOIN designer ON multiAddOnDesigner.designerId = designer.id LEFT JOIN image ON image.name = multiAddOn.name || 'MultiAddOn'  GROUP BY multiAddOn.name ORDER BY multiAddOn.name ASC")
    override fun getAllWithDesigner(): LiveData<List<DesignerWithMultiAddOn>>

    @Query("SELECT * FROM multiAddOn")
    override fun getList(): List<MultiAddOnTableBean>

    @Query("SELECT name FROM multiAddOn")
    override fun getNameList(): List<String>

    @Query("SELECT * FROM image WHERE image.name = :name || 'MultiAddOn'")
    override fun getImage(name: String): List<ImageTableBean>

    @Query("SELECT * FROM multiAddOn WHERE name=:searchedName")
    override fun getByName(searchedName: String): List<MultiAddOnTableBean>

    @Query("SELECT * FROM game WHERE id=:gameId")
    fun getObjectById(gameId: Long): List<MultiAddOnTableBean>

    @Query("SELECT * FROM multiAddOn WHERE id=:id")
    override fun getById(id: Long): Flow<List<MultiAddOnTableBean>>

    @Query("SELECT multiAddOn.id as id, multiAddOn.name as name, designer.name as designer, image.name as image FROM multiAddOn LEFT JOIN multiAddOnDesigner ON multiAddOnId = multiAddOn.id LEFT JOIN  designer on designerId = designer.id LEFT JOIN image ON image.name = multiAddOn.name || 'MultiAddOn' GROUP BY multiAddOn.name")
    fun getDesignerWithGame(): LiveData<List<DesignerWithMultiAddOn>>

    @Query("SELECT game.id as id, game.name as name, designer.name as designer, image.name as image FROM game  LEFT JOIN gameDesigner ON  gameDesigner.gameId = game.id LEFT JOIN designer ON designer.id = gameDesigner.designerId LEFT JOIN gameMultiAddOn ON gameMultiAddOn.gameId = game.id LEFT JOIN multiAddOn ON gameMultiAddOn.multiAddOnId = multiAddOn.id LEFT JOIN image ON image.name = game.name || 'Game' WHERE multiAddOn.id=:gameId GROUP BY game.name")
    fun getGameFromMultiAddOn(gameId: Long): LiveData<List<DesignerWithGame>>

    @Query("SELECT game.id as id, game.name as name, designer.name as designer, image.name as image FROM game  LEFT JOIN gameDesigner ON  gameDesigner.gameId = game.id LEFT JOIN designer ON designer.id = gameDesigner.designerId LEFT JOIN gameMultiAddOn ON gameMultiAddOn.gameId = game.id LEFT JOIN multiAddOn ON gameMultiAddOn.multiAddOnId = multiAddOn.id LEFT JOIN image ON image.name = game.name || 'Game' WHERE multiAddOn.id=:gameId GROUP BY game.name")
    fun getGameObjectFromMultiAddOn(gameId: Long): List<DesignerWithGame>

    @Query("SELECT multiAddOn.id as id, multiAddOn.name as name, designer.name as designer, image.name as image FROM multiAddOn LEFT JOIN multiAddOnDesigner ON multiAddOnDesigner.multiAddOnId = multiAddOn.id LEFT JOIN  designer on designerId = designer.id LEFT JOIN gameMultiAddOn ON gameMultiAddOn.multiAddOnId = multiAddOn.id LEFT JOIN image ON image.name = multiAddOn.name || 'MultiAddOn' WHERE gameMultiAddOn.gameId=:gameId GROUP BY multiAddOn.name")
    fun getDesignerWithMultiAddOnOfGame(gameId: Long): LiveData<List<DesignerWithMultiAddOn>>

    @Query("SELECT multiAddOn.id as id, multiAddOn.name as name, designer.name as designer, image.name as image FROM multiAddOn LEFT JOIN multiAddOnDesigner ON multiAddOnDesigner.multiAddOnId = multiAddOn.id LEFT JOIN  designer on designerId = designer.id LEFT JOIN gameMultiAddOn ON gameMultiAddOn.multiAddOnId = multiAddOn.id LEFT JOIN image ON image.name = multiAddOn.name || 'MultiAddOn' WHERE gameMultiAddOn.gameId=:gameId GROUP BY multiAddOn.name")
    fun getDesignerWithMultiAddOnObjectOfGame(gameId: Long): List<DesignerWithMultiAddOn>

    @Query("SELECT difficulty.id as id, difficulty.name as name FROM difficulty LEFT JOIN multiAddOn ON difficultyId = difficulty.id WHERE multiAddOn.id = :idGame")
    override fun getDifficulty(idGame: Long): LiveData<List<DifficultyTableBean>>

    @Query("SELECT designer.id AS id, designer.name AS name FROM designer INNER JOIN multiAddOnDesigner ON designerId = designer.id INNER JOIN multiAddOn ON multiAddOnId = :idGame GROUP BY designer.name")
    override fun getDesigners(idGame: Long): LiveData<List<DesignerTableBean>>

    @Query("SELECT designer.id AS id, designer.name AS name FROM designer INNER JOIN multiAddOnDesigner ON designerId = designer.id INNER JOIN multiAddOn ON multiAddOnId = :idGame GROUP BY designer.name")
    override fun getDesignerObject(idGame: Long): List<DesignerTableBean>


    @Query("SELECT artist.id AS id, artist.name AS name FROM artist INNER JOIN multiAddOnArtist ON artistId = artist.id INNER JOIN multiAddOn ON multiAddOnId = :idGame GROUP BY artist.name")
    override fun getArtists(idGame: Long): LiveData<List<ArtistTableBean>>

    @Query("SELECT artist.id AS id, artist.name AS name FROM artist INNER JOIN multiAddOnArtist ON artistId = artist.id INNER JOIN multiAddOn ON multiAddOnId = :idGame GROUP BY artist.name")
    override fun getArtistObject(idGame: Long): List<ArtistTableBean>

    @Query("SELECT publisher.id AS id, publisher.name AS name FROM publisher INNER JOIN multiAddOnPublisher ON publisherId = publisher.id INNER JOIN multiAddOn ON multiAddOnId = :idGame GROUP BY publisher.name")
    override fun getPublishers(idGame: Long): LiveData<List<PublisherTableBean>>

    @Query("SELECT publisher.id AS id, publisher.name AS name FROM publisher INNER JOIN multiAddOnPublisher ON publisherId = publisher.id INNER JOIN multiAddOn ON multiAddOnId = :idGame GROUP BY publisher.name")
    override fun getPublisherObject(idGame: Long): List<PublisherTableBean>

    @Query("SELECT playingMod.id AS id, playingMod.name AS name FROM playingMod INNER JOIN multiAddOnPlayingMod ON playingModId = playingMod.id INNER JOIN multiAddOn ON multiAddOnId = :idGame GROUP BY playingMod.name")
    override fun getPlayingMods(idGame: Long): LiveData<List<PlayingModTableBean>>

    @Query("SELECT playingMod.id AS id, playingMod.name AS name FROM playingMod INNER JOIN multiAddOnPlayingMod ON playingModId = playingMod.id INNER JOIN multiAddOn ON multiAddOnId = :idGame GROUP BY playingMod.name")
    override fun getPlayingModObject(idGame: Long): List<PlayingModTableBean>

    @Query("SELECT language.id AS id, language.name AS name FROM language INNER JOIN multiAddOnLanguage ON languageId = language.id INNER JOIN multiAddOn ON multiAddOnId = :idGame GROUP BY language.name")
    override fun getLanguages(idGame: Long): LiveData<List<LanguageTableBean>>

    @Query("SELECT language.id AS id, language.name AS name FROM language INNER JOIN multiAddOnLanguage ON languageId = language.id INNER JOIN multiAddOn ON multiAddOnId = :idGame GROUP BY language.name")
    override fun getLanguageObject(idGame: Long): List<LanguageTableBean>

    @Query("SELECT multiAddOn.id AS id, multiAddOn.name AS name, designer.name AS designer, image.name as image FROM multiAddOn INNER JOIN multiAddOnDesigner ON multiAddOnDesigner.multiAddOnId = multiAddOn.id INNER JOIN designer ON multiAddOnDesigner.designerId = designer.id LEFT JOIN image ON image.name = multiAddOn.name || 'MultiAddOn' WHERE designer.id = :idDesigner GROUP BY multiAddOn.name ORDER BY multiAddOn.name ASC")
    fun getWithDesignerFromDesignerId(idDesigner: Long): LiveData<List<DesignerWithMultiAddOn>>

    @Query("SELECT multiAddOn.id AS id, multiAddOn.name AS name, designer.name AS designer, image.name as image FROM multiAddOn LEFT JOIN multiAddOnDesigner ON multiAddOnDesigner.multiAddOnId = multiAddOn.id LEFT JOIN designer ON multiAddOnDesigner.designerId = designer.id LEFT JOIN multiAddOnArtist ON multiAddOnArtist.multiAddOnId = multiAddOn.id LEFT JOIN artist ON multiAddOnArtist.artistId = artist.id  LEFT JOIN image ON image.name = multiAddOn.name || 'MultiAddOn'  WHERE artist.id = :idArtist GROUP BY multiAddOn.name ORDER BY multiAddOn.name ASC")
    fun getWithDesignerFromArtistId(idArtist: Long): LiveData<List<DesignerWithMultiAddOn>>

    @Query("SELECT multiAddOn.id AS id, multiAddOn.name AS name, designer.name AS designer, image.name as image FROM multiAddOn LEFT JOIN multiAddOnDesigner ON multiAddOnDesigner.multiAddOnId = multiAddOn.id LEFT JOIN designer ON multiAddOnDesigner.designerId = designer.id LEFT JOIN multiAddOnPublisher ON multiAddOnPublisher.multiAddOnId = multiAddOn.id LEFT JOIN publisher ON multiAddOnPublisher.publisherId = publisher.id  LEFT JOIN image ON image.name = multiAddOn.name || 'MultiAddOn'  WHERE publisher.id = :idPublisher GROUP BY multiAddOn.name ORDER BY multiAddOn.name ASC")
    fun getWithDesignerFromPublisherId(idPublisher: Long): LiveData<List<DesignerWithMultiAddOn>>

    @Query("SELECT multiAddOn.id AS id, multiAddOn.name AS name, designer.name AS designer, image.name as image FROM multiAddOn LEFT JOIN multiAddOnDesigner ON multiAddOnDesigner.multiAddOnId = multiAddOn.id LEFT JOIN designer ON multiAddOnDesigner.designerId = designer.id LEFT JOIN multiAddOnPlayingMod ON multiAddOnPlayingMod.multiAddOnId = multiAddOn.id LEFT JOIN playingMod ON multiAddOnPlayingMod.playingModId = playingMod.id  LEFT JOIN image ON image.name = multiAddOn.name || 'MultiAddOn'  WHERE playingMod.id = :idPlayingMod GROUP BY multiAddOn.name ORDER BY multiAddOn.name ASC")
    fun getWithDesignerFromPlayingModId(idPlayingMod: Long): LiveData<List<DesignerWithMultiAddOn>>

    @Query("SELECT multiAddOn.id AS id, multiAddOn.name AS name, designer.name AS designer, image.name as image FROM multiAddOn LEFT JOIN multiAddOnDesigner ON multiAddOnDesigner.multiAddOnId = multiAddOn.id LEFT JOIN designer ON multiAddOnDesigner.designerId = designer.id LEFT JOIN multiAddOnLanguage ON multiAddOnLanguage.multiAddOnId = multiAddOn.id LEFT JOIN language ON multiAddOnLanguage.languageId = language.id  LEFT JOIN image ON image.name = multiAddOn.name || 'MultiAddOn'  WHERE language.id = :idLanguage GROUP BY multiAddOn.name ORDER BY multiAddOn.name ASC")
    fun getWithDesignerFromLanguageId(idLanguage: Long): LiveData<List<DesignerWithMultiAddOn>>

    @Query("SELECT multiAddOn.id AS id, multiAddOn.name AS name, designer.name AS designer, image.name as image FROM multiAddOn INNER JOIN multiAddOnDesigner ON multiAddOnDesigner.multiAddOnId = multiAddOn.id INNER JOIN designer ON multiAddOnDesigner.designerId = designer.id  LEFT JOIN image ON image.name = multiAddOn.name || 'MultiAddOn' WHERE multiAddOn.difficultyId = :idDifficulty GROUP BY multiAddOn.name ORDER BY multiAddOn.name ASC")
    fun getWithDesignerFromDifficultyId(idDifficulty: Long): LiveData<List<DesignerWithMultiAddOn>>

    @Query(
        "SELECT multiAddOn.id AS id, multiAddOn.name AS name, designer.name AS designer, image.name as image FROM multiAddOn LEFT JOIN multiAddOnDesigner ON multiAddOnDesigner.multiAddOnId = multiAddOn.id LEFT JOIN designer ON multiAddOnDesigner.designerId = designer.id LEFT JOIN multiAddOnArtist ON multiAddOnArtist.multiAddOnId = multiAddOn.id LEFT JOIN artist ON multiAddOnArtist.artistId = artist.id LEFT JOIN multiAddOnPublisher ON multiAddOnPublisher.multiAddOnId = multiAddOn.id LEFT JOIN publisher ON multiAddOnPublisher.publisherId = publisher.id LEFT JOIN multiAddOnPlayingMod ON multiAddOnPlayingMod.multiAddOnId = multiAddOn.id LEFT JOIN playingMod ON multiAddOnPlayingMod.playingModId = playingMod.id LEFT JOIN multiAddOnLanguage ON multiAddOnLanguage.multiAddOnId = multiAddOn.id LEFT JOIN language ON multiAddOnLanguage.languageId = language.id LEFT JOIN difficulty ON difficulty.id = multiAddOn.difficultyId LEFT JOIN image ON image.name = multiAddOn.name || 'MultiAddOn' WHERE (multiAddOn.name like '%' || :name || '%' OR :name IS NULL) AND (difficulty.name like '%' || :difficulty || '%' OR :difficulty IS NULL) AND (designer.name like '%' || :designer || '%' OR :designer IS NULL) AND (artist.name like '%' || :artist || '%' OR :artist IS NULL) AND (publisher.name like '%' || :publisher || '%' OR :publisher IS NULL) AND (playingMod.name like '%' || :playingMod || '%' OR :playingMod IS NULL) AND (language.name like '%' || :language || '%' OR :language IS NULL) AND (:playerMin BETWEEN multiAddOn.player_min AND multiAddOn.player_max OR :playerMin IS NULL) AND (:playerMax BETWEEN multiAddOn.player_min AND multiAddOn.player_max OR :playerMax IS NULL) AND (multiAddOn.max_time >= :maxTime OR :maxTime IS NULL) AND (multiAddOn.age >= :age OR :age IS NULL) GROUP BY multiAddOn.name ORDER BY multiAddOn.name ASC\n"
    )
    fun getWithDesignerFromSearchQuery(
        name: String?,
        designer: String?,
        artist: String?,
        publisher: String?,
        playerMin: Int?,
        playerMax: Int?,
        maxTime: Int?,
        difficulty: String?,
        age: Int?,
        playingMod: String?,
        language: String?
    ): LiveData<List<DesignerWithMultiAddOn>>

    @Query("SELECT * FROM multiAddOn WHERE serverId=:id")
    override fun getByServerId(id: Long): List<MultiAddOnTableBean>

    @Query("SELECT * FROM multiAddOn WHERE serverId IS NULL")
    fun getWithoutServerId(): List<MultiAddOnTableBean>

    @Query("SELECT * FROM multiAddOn WHERE hasChanged=1")
    fun getChanged(): List<MultiAddOnTableBean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(game: MultiAddOnTableBean): Long

    @Update
    fun update(game: MultiAddOnTableBean)

    @Query("DELETE FROM multiAddOn")
    fun deleteAll()

    @Query("DELETE FROM multiAddOn WHERE id=:objectId")
    fun deleteOne(objectId: Long)
}
    
    
@Dao
interface TagDao: CommonCustomInsert<TagTableBean> {
    @Query("SELECT * FROM tag ORDER BY name ASC")
    override fun getAll(): Flow<List<TagTableBean>>
    
    @Query("SELECT * FROM tag")
    fun getList(): List<TagTableBean>

    @Query("SELECT tag.id, tag.name FROM tag LEFT JOIN gameTag ON gameTag.tagId = tag.id WHERE gameTag.tagId IS NULL")
    override fun getDeletableNameList(): Flow<List<TagTableBean>>

    @Query("SELECT name FROM tag")
    override fun getNameList(): List<String>
    
    @Query("SELECT * FROM tag WHERE name=:searchedName")
    override fun getByName(searchedName:String): List<TagTableBean>
    
    @Query("INSERT INTO tag(name) VALUES (:newElement)")
    override fun insert(newElement:String)

    @Insert
    fun insert(newElement:TagTableBean)
    
    @Update
    fun update(game: TagTableBean)
    
    @Query("DELETE FROM tag")
    fun deleteAll()
    
    @Query("DELETE FROM tag WHERE id=:id")
    override fun deleteOne(id:Long)
    
}
    

@Dao
interface TopicDao: CommonCustomInsert<TopicTableBean> {
    @Query("SELECT * FROM topic ORDER BY name ASC")
    override fun getAll(): Flow<List<TopicTableBean>>
    
    @Query("SELECT * FROM topic")
    fun getList(): List<TopicTableBean>

    @Query("SELECT topic.id, topic.name FROM topic LEFT JOIN gameTopic ON gameTopic.topicId = topic.id WHERE gameTopic.topicId IS NULL")
    override fun getDeletableNameList(): Flow<List<TopicTableBean>>

    @Query("SELECT name FROM topic")
    override fun getNameList(): List<String>
    
    @Query("SELECT * FROM topic WHERE name=:searchedName")
    override fun getByName(searchedName:String): List<TopicTableBean>

    @Query("INSERT INTO topic(name) VALUES (:newElement)")
    override fun insert(newElement:String)

    @Insert
    fun insert(newElement:TopicTableBean)
    
    @Update
    fun update(game: TopicTableBean)
    
    @Query("DELETE FROM topic")
    fun deleteAll()
    
    @Query("DELETE FROM topic WHERE id=:id")
    override fun deleteOne(id:Long)
}

    
@Dao
interface MechanismDao: CommonCustomInsert<MechanismTableBean> {
    @Query("SELECT * FROM mechanism ORDER BY name ASC")
    override fun getAll(): Flow<List<MechanismTableBean>>
    
    @Query("SELECT * FROM mechanism")
    fun getList(): List<MechanismTableBean>

    @Query("SELECT mechanism.id, mechanism.name FROM mechanism LEFT JOIN gameMechanism ON gameMechanism.mechanismId = mechanism.id WHERE gameMechanism.mechanismId IS NULL")
    override fun getDeletableNameList(): Flow<List<MechanismTableBean>>

    @Query("SELECT name FROM mechanism")
    override fun getNameList(): List<String>
    
    @Query("SELECT * FROM mechanism WHERE name=:searchedName")
    override fun getByName(searchedName:String): List<MechanismTableBean>

    @Query("INSERT INTO mechanism(name) VALUES (:newElement)")
    override fun insert(newElement:String)

    @Insert
    fun insert(newElement:MechanismTableBean)
    
    @Update
    fun update(game: MechanismTableBean)
    
    @Query("DELETE FROM mechanism")
    fun deleteAll()
    
    @Query("DELETE FROM mechanism WHERE id=:id")
    override fun deleteOne(id:Long)
}

    
@Dao
interface DesignerDao: CommonCustomInsert<DesignerTableBean> {
    @Query("SELECT * FROM designer ORDER BY name ASC")
    override fun getAll(): Flow<List<DesignerTableBean>>
    
    @Query("SELECT * FROM designer")
    fun getList(): List<DesignerTableBean>

    @Query("SELECT designer.id, designer.name FROM designer LEFT JOIN gameDesigner ON gameDesigner.designerId = designer.id LEFT JOIN addOnDesigner ON addOnDesigner.designerId = designer.id LEFT JOIN multiAddOnDesigner ON multiAddOnDesigner.designerId = designer.id WHERE gameDesigner.designerId IS NULL AND addOnDesigner.designerId IS NULL AND multiAddOnDesigner.designerId IS NULL")
    override fun getDeletableNameList(): Flow<List<DesignerTableBean>>

    @Query("SELECT name FROM designer")
    override fun getNameList(): List<String>

    @Query("SELECT game.id AS id, game.name AS name, designer.name AS designer, image.name as image FROM game LEFT JOIN gameDesigner ON gameDesigner.gameId = :idGame LEFT JOIN designer ON gameDesigner.designerId = designer.id LEFT JOIN image ON image.name = game.name || 'Game' GROUP BY game.name ORDER BY game.name ASC")
    fun getWithDesignerFromGameID(idGame:Long):LiveData<List<DesignerWithGame>>
    
    @Query("SELECT * FROM designer WHERE name=:searchedName")
    override fun getByName(searchedName:String): List<DesignerTableBean>

    @Query("INSERT INTO designer(name) VALUES (:newElement)")
    override fun insert(newElement:String)

    @Insert
    fun insert(newElement:DesignerTableBean)
    
    @Update
    fun update(game: DesignerTableBean)
    
    @Query("DELETE FROM designer")
    fun deleteAll()
    
    @Query("DELETE FROM designer WHERE id=:id")
    override fun deleteOne(id:Long)
}
    
    
@Dao
interface ArtistDao: CommonCustomInsert<ArtistTableBean> {
    @Query("SELECT * FROM artist ORDER BY name ASC")
    override fun getAll(): Flow<List<ArtistTableBean>>
    
    @Query("SELECT * FROM artist")
    fun getList(): List<ArtistTableBean>

    @Query("SELECT artist.id, artist.name FROM artist LEFT JOIN gameArtist ON gameArtist.artistId = artist.id LEFT JOIN addOnArtist ON addOnArtist.artistId = artist.id LEFT JOIN multiAddOnArtist ON multiAddOnArtist.artistId = artist.id WHERE gameArtist.artistId IS NULL AND addOnArtist.artistId IS NULL AND multiAddOnArtist.artistId IS NULL")
    override fun getDeletableNameList(): Flow<List<ArtistTableBean>>

    @Query("SELECT name FROM artist")
    override fun getNameList(): List<String>
    
    @Query("SELECT * FROM artist WHERE name=:searchedName")
    override fun getByName(searchedName:String): List<ArtistTableBean>

    @Query("INSERT INTO artist(name) VALUES (:newElement)")
    override fun insert(newElement:String)

    @Insert
    fun insert(newElement:ArtistTableBean)

    @Update
    fun update(game: ArtistTableBean)
    
    @Query("DELETE FROM artist")
    fun deleteAll()
    
    @Query("DELETE FROM artist WHERE id=:id")
    override fun deleteOne(id:Long)
}

    
@Dao
interface PublisherDao: CommonCustomInsert<PublisherTableBean> {
    @Query("SELECT * FROM publisher ORDER BY name ASC")
    override fun getAll(): Flow<List<PublisherTableBean>>
    
    @Query("SELECT * FROM publisher")
    fun getList(): List<PublisherTableBean>

    @Query("SELECT publisher.id, publisher.name FROM publisher LEFT JOIN gamePublisher ON gamePublisher.publisherId = publisher.id LEFT JOIN addOnPublisher ON addOnPublisher.publisherId = publisher.id LEFT JOIN multiAddOnPublisher ON multiAddOnPublisher.publisherId = publisher.id WHERE gamePublisher.publisherId IS NULL AND addOnPublisher.publisherId IS NULL AND multiAddOnPublisher.publisherId IS NULL")
    override fun getDeletableNameList(): Flow<List<PublisherTableBean>>

    @Query("SELECT name FROM publisher")
    override fun getNameList(): List<String>
    
    @Query("SELECT * FROM publisher WHERE name=:searchedName")
    override fun getByName(searchedName:String): List<PublisherTableBean>

    @Query("INSERT INTO publisher(name) VALUES (:newElement)")
    override fun insert(newElement:String)

    @Insert
    fun insert(newElement:PublisherTableBean)
    
    @Update
    fun update(game: PublisherTableBean)
    
    @Query("DELETE FROM publisher")
    fun deleteAll()
    
    @Query("DELETE FROM publisher WHERE id=:id")
    override fun deleteOne(id:Long)
}
    

@Dao
interface PlayingModDao: CommonCustomInsert<PlayingModTableBean> {
    @Query("SELECT * FROM playingMod ORDER BY name ASC")
    override fun getAll(): Flow<List<PlayingModTableBean>>
    
    @Query("SELECT * FROM playingMod")
    fun getList(): List<PlayingModTableBean>

    @Query("SELECT playingMod.id, playingMod.name FROM playingMod LEFT JOIN gamePlayingMod ON gamePlayingMod.playingModId = playingMod.id LEFT JOIN addOnPlayingMod ON addOnPlayingMod.playingModId = playingMod.id LEFT JOIN multiAddOnPlayingMod ON multiAddOnPlayingMod.playingModId = playingMod.id WHERE gamePlayingMod.playingModId IS NULL AND addOnPlayingMod.playingModId IS NULL AND multiAddOnPlayingMod.playingModId IS NULL")
    override fun getDeletableNameList(): Flow<List<PlayingModTableBean>>

    @Query("SELECT playingMod.id, playingMod.name FROM playingMod LEFT JOIN gamePlayingMod ON gamePlayingMod.playingModId = playingMod.id LEFT JOIN addOnPlayingMod ON addOnPlayingMod.playingModId = playingMod.id LEFT JOIN multiAddOnPlayingMod ON multiAddOnPlayingMod.playingModId = playingMod.id WHERE gamePlayingMod.playingModId IS NULL AND addOnPlayingMod.playingModId IS NULL AND multiAddOnPlayingMod.playingModId IS NULL")
    fun getDeletableNameList2(): List<PlayingModTableBean>

    @Query("SELECT name FROM playingMod")
    override fun getNameList(): List<String>
    
    @Query("SELECT * FROM playingMod WHERE name=:searchedName")
    override fun getByName(searchedName:String): List<PlayingModTableBean>

    @Query("INSERT INTO playingMod(name) VALUES (:newElement)")
    override fun insert(newElement:String)

    @Insert
    fun insert(newElement:PlayingModTableBean)
    
    @Update
    fun update(game: PlayingModTableBean)
    
    @Query("DELETE FROM playingMod")
    fun deleteAll()
    
    @Query("DELETE FROM playingMod WHERE id=:id")
    override fun deleteOne(id:Long)
}

    
@Dao
interface LanguageDao: CommonCustomInsert<LanguageTableBean> {
    @Query("SELECT * FROM language ORDER BY name ASC")
    override fun getAll(): Flow<List<LanguageTableBean>>
    
    @Query("SELECT * FROM language")
    fun getList(): List<LanguageTableBean>

    @Query("SELECT language.id, language.name FROM language LEFT JOIN gameLanguage ON gameLanguage.languageId = language.id LEFT JOIN addOnLanguage ON addOnLanguage.languageId = language.id LEFT JOIN multiAddOnLanguage ON multiAddOnLanguage.languageId = language.id WHERE gameLanguage.languageId IS NULL AND addOnLanguage.languageId IS NULL AND multiAddOnLanguage.languageId IS NULL")
    override fun getDeletableNameList(): Flow<List<LanguageTableBean>>

    @Query("SELECT name FROM language")
    override fun getNameList(): List<String>
    
    @Query("SELECT * FROM language WHERE name=:searchedName")
    override fun getByName(searchedName:String): List<LanguageTableBean>

    @Query("INSERT INTO language(name) VALUES (:newElement)")
    override fun insert(newElement:String)

    @Insert
    fun insert(newElement:LanguageTableBean)
    
    @Update
    fun update(game: LanguageTableBean)
    
    @Query("DELETE FROM language")
    fun deleteAll()
    
    @Query("DELETE FROM language WHERE id=:id")
    override fun deleteOne(id:Long)
}
    

@Dao
interface DifficultyDao: CommonCustomInsert<DifficultyTableBean> {
    @Query("SELECT * FROM difficulty ORDER BY name ASC")
    override fun getAll(): Flow<List<DifficultyTableBean>>

    @Query("SELECT * FROM difficulty WHERE id=:id")
    fun getById(id:Long): List<DifficultyTableBean>

    @Query("SELECT name FROM difficulty")
    override fun getNameList(): List<String>
    
    @Query("SELECT * FROM difficulty")
    fun getList(): List<DifficultyTableBean>

    @Query("SELECT difficulty.id, difficulty.name FROM difficulty LEFT JOIN game ON game.difficultyId = difficulty.id WHERE game.difficultyId IS NULL")
    override fun getDeletableNameList(): Flow<List<DifficultyTableBean>>
    
    @Query("SELECT * FROM difficulty WHERE name=:searchedName")
    override fun getByName(searchedName:String): List<DifficultyTableBean>
    
    @Insert
    fun insert(game: DifficultyTableBean) : Long

    @Query("INSERT INTO difficulty(name) VALUES (:newElement)")
    override fun insert(newElement:String)
    
    @Update
    fun update(game: DifficultyTableBean)
    
    @Query("DELETE FROM difficulty")
    fun deleteAll()
    
    @Query("DELETE FROM difficulty WHERE id=:id")
    override fun deleteOne(id:Long)
}


@Dao
interface UserDao {
    @Query("SELECT * FROM user ORDER BY login ASC")
    fun getAll(): Flow<List<UserTableBean>>

    @Query("SELECT * FROM user ORDER BY login ASC")
    fun getList():List<UserTableBean>

    @Query("SELECT * FROM user ORDER BY login ASC")
    fun checkEmpty(): List<UserTableBean>

    @Insert
    fun insert(user: UserTableBean) : Long

    @Query("SELECT * FROM user WHERE login=:name ORDER BY login ASC")
    fun getUser(name:String): List<UserTableBean>

    @Query("DELETE FROM user WHERE id=:userId")
    fun deleteUser(userId:Long)
}


@Dao
interface DeletedItemDao {
    @Query("SELECT * FROM deletedItems")
    fun getAll(): List<DeletedContentTableBean>

    @Query("SELECT * FROM deletedItems WHERE type=:type")
    fun getByType(type:String): List<DeletedContentTableBean>


    @Insert
    fun insert(user: DeletedContentTableBean) : Long


    @Query("DELETE FROM deletedItems")
    fun deleteAll()
}


@Dao
interface ImageDao {

    @Query("SELECT * FROM image WHERE name=:name")
    fun getByName(name:String):List<ImageTableBean>

    @Query("SELECT * FROM image")
    fun getAll(): List<ImageTableBean>

    @Insert
    fun insert(user: ImageTableBean) : Long


    @Query("DELETE FROM image")
    fun deleteAll()

    @Query("DELETE FROM image WHERE name=:name")
    fun deleteByName(name:String)
}
