package thannajo.appli.filrouge.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update


interface CommonJunctionDAo<T>{
    fun deleteWithMember1Id(objectId:Long)
    fun insertIds(id1:Long, id2:Long)

}

/**
 * Dao for junction table game-multi-add-on
 */
@Dao
interface GameMultiAddOnDao: CommonJunctionDAo<GameMultiAddOnTableBean> {

    @Query("SELECT * FROM gameMultiAddOn")
    fun getList(): List<GameMultiAddOnTableBean>

    @Query("INSERT INTO gameMultiAddOn(gameId, multiAddOnId) VALUES (:id1, :id2)")
    override fun insertIds(id1: Long, id2:Long)
    
    @Insert
    fun insert(line:GameMultiAddOnTableBean)

    @Update
    fun update(game: GameMultiAddOnTableBean)

    @Query("DELETE FROM gameMultiAddOn")
    fun deleteAll()

    @Query("DELETE FROM gameMultiAddOn WHERE gameId=:objectId")
    override fun deleteWithMember1Id(objectId:Long)
    
    @Query("DELETE FROM gameMultiAddOn WHERE multiAddOnId=:objectId")
    fun deleteWithMultiAddOnId(objectId:Long)
}  


@Dao
interface GameTagDao: CommonJunctionDAo<GameTagTableBean> {

    @Query("SELECT * FROM gameTag")
    fun getList(): List<GameTagTableBean>

    @Query("INSERT INTO gameTag(gameId, tagId) VALUES (:id1, :id2)")
    override fun insertIds(id1: Long, id2:Long)
    
    @Insert
    fun insert(line:GameTagTableBean)

    @Update
    fun update(game: GameTagTableBean)

    @Query("DELETE FROM gameTag")
    fun deleteAll()

    @Query("DELETE FROM gameTag WHERE gameId=:objectId")
    override fun deleteWithMember1Id(objectId:Long)
    
    @Query("DELETE FROM gameTag WHERE tagId=:objectId")
    fun deleteWithTagId(objectId:Long)
}  


@Dao
interface GameTopicDao: CommonJunctionDAo<GameTopicTableBean> {

    @Query("SELECT * FROM gameTopic")
    fun getList(): List<GameTopicTableBean>

    @Query("INSERT INTO gameTopic(gameId, topicId) VALUES (:id1, :id2)")
    override fun insertIds(id1: Long, id2:Long)
    
    @Insert
    fun insert(line:GameTopicTableBean)

    @Update
    fun update(game: GameTopicTableBean)

    @Query("DELETE FROM gameTopic")
    fun deleteAll()

    @Query("DELETE FROM gameTopic WHERE gameId=:objectId")
    override fun deleteWithMember1Id(objectId:Long)
    
    @Query("DELETE FROM gameTopic WHERE topicId=:objectId")
    fun deleteWithTopicId(objectId:Long)
}  


@Dao
interface GameMechanismDao: CommonJunctionDAo<GameMechanismTableBean> {

    @Query("SELECT * FROM gameMechanism")
    fun getList(): List<GameMechanismTableBean>

    @Query("INSERT INTO gameMechanism(gameId, mechanismId) VALUES (:id1, :id2)")
    override fun insertIds(id1: Long, id2:Long)
    
    @Insert
    fun insert(line:GameMechanismTableBean)

    @Update
    fun update(game: GameMechanismTableBean)

    @Query("DELETE FROM gameMechanism")
    fun deleteAll()

    @Query("DELETE FROM gameMechanism WHERE gameId=:objectId")
    override fun deleteWithMember1Id(objectId:Long)
    
    @Query("DELETE FROM gameMechanism WHERE mechanismId=:objectId")
    fun deleteWithMechanismId(objectId:Long)
}  


@Dao
interface GameDesignerDao: CommonJunctionDAo<GameDesignerTableBean> {

    @Query("SELECT * FROM gameDesigner")
    fun getList(): List<GameDesignerTableBean>

    @Query("INSERT INTO gameDesigner(gameId, designerId) VALUES (:id1, :id2)")
    override fun insertIds(id1: Long, id2:Long)
    
    @Insert
    fun insert(line:GameDesignerTableBean)

    @Update
    fun update(game: GameDesignerTableBean)

    @Query("DELETE FROM gameDesigner")
    fun deleteAll()

    @Query("DELETE FROM gameDesigner WHERE gameId=:objectId")
    override fun deleteWithMember1Id(objectId:Long)
    
    @Query("DELETE FROM gameDesigner WHERE designerId=:objectId")
    fun deleteWithDesignerId(objectId:Long)
}  


@Dao
interface AddOnDesignerDao: CommonJunctionDAo<AddOnDesignerTableBean> {

    @Query("SELECT * FROM addOnDesigner")
    fun getList(): List<AddOnDesignerTableBean>

    @Query("INSERT INTO addOnDesigner(addOnId, designerId) VALUES (:id1, :id2)")
    override fun insertIds(id1: Long, id2:Long)
    
    @Insert
    fun insert(line:AddOnDesignerTableBean)

    @Update
    fun update(game: AddOnDesignerTableBean)

    @Query("DELETE FROM addOnDesigner")
    fun deleteAll()

    @Query("DELETE FROM addOnDesigner WHERE addOnId=:objectId")
    override fun deleteWithMember1Id(objectId:Long)
    
    @Query("DELETE FROM addOnDesigner WHERE designerId=:objectId")
    fun deleteWithDesignerId(objectId:Long)
}  


@Dao
interface MultiAddOnDesignerDao: CommonJunctionDAo<MultiAddOnDesignerTableBean> {

    @Query("SELECT * FROM multiAddOnDesigner")
    fun getList(): List<MultiAddOnDesignerTableBean>

    @Query("INSERT INTO multiAddOnDesigner(multiAddOnId, designerId) VALUES (:id1, :id2)")
    override fun insertIds(id1: Long, id2:Long)
    
    @Insert
    fun insert(line:MultiAddOnDesignerTableBean)

    @Update
    fun update(game: MultiAddOnDesignerTableBean)

    @Query("DELETE FROM multiAddOnDesigner")
    fun deleteAll()

    @Query("DELETE FROM multiAddOnDesigner WHERE multiAddOnId=:objectId")
    override fun deleteWithMember1Id(objectId:Long)
    
    @Query("DELETE FROM multiAddOnDesigner WHERE designerId=:objectId")
    fun deleteWithDesignerId(objectId:Long)
}


@Dao
interface GameArtistDao: CommonJunctionDAo<GameArtistTableBean> {

    @Query("SELECT * FROM gameArtist")
    fun getList(): List<GameArtistTableBean>

    @Query("INSERT INTO gameArtist(gameId, artistId) VALUES (:id1, :id2)")
    override fun insertIds(id1: Long, id2:Long)
    
    @Insert
    fun insert(line:GameArtistTableBean)

    @Update
    fun update(game: GameArtistTableBean)

    @Query("DELETE FROM gameArtist")
    fun deleteAll()

    @Query("DELETE FROM gameArtist WHERE gameId=:objectId")
    override fun deleteWithMember1Id(objectId:Long)
    
    @Query("DELETE FROM gameArtist WHERE artistId=:objectId")
    fun deleteWithArtistId(objectId:Long)
}  


@Dao
interface AddOnArtistDao: CommonJunctionDAo<AddOnArtistTableBean> {

    @Query("SELECT * FROM addOnArtist")
    fun getList(): List<AddOnArtistTableBean>

    @Query("INSERT INTO addOnArtist(addOnId, artistId) VALUES (:id1, :id2)")
    override fun insertIds(id1: Long, id2:Long)
    
    @Insert
    fun insert(line:AddOnArtistTableBean)

    @Update
    fun update(game: AddOnArtistTableBean)

    @Query("DELETE FROM addOnArtist")
    fun deleteAll()

    @Query("DELETE FROM addOnArtist WHERE addOnId=:objectId")
    override fun deleteWithMember1Id(objectId:Long)
    
    @Query("DELETE FROM addOnArtist WHERE artistId=:objectId")
    fun deleteWithArtistId(objectId:Long)
}  


@Dao
interface MultiAddOnArtistDao: CommonJunctionDAo<MultiAddOnArtistTableBean> {

    @Query("SELECT * FROM multiAddOnArtist")
    fun getList(): List<MultiAddOnArtistTableBean>

    @Query("INSERT INTO multiAddOnArtist(multiAddOnId, artistId) VALUES (:id1, :id2)")
    override fun insertIds(id1: Long, id2:Long)
    
    @Insert
    fun insert(line:MultiAddOnArtistTableBean)

    @Update
    fun update(game: MultiAddOnArtistTableBean)

    @Query("DELETE FROM multiAddOnArtist")
    fun deleteAll()

    @Query("DELETE FROM multiAddOnArtist WHERE multiAddOnId=:objectId")
    override fun deleteWithMember1Id(objectId:Long)
    
    @Query("DELETE FROM multiAddOnArtist WHERE artistId=:objectId")
    fun deleteWithArtistId(objectId:Long)
}  


@Dao
interface GamePublisherDao: CommonJunctionDAo<GamePublisherTableBean> {

    @Query("SELECT * FROM gamePublisher")
    fun getList(): List<GamePublisherTableBean>

    @Query("INSERT INTO gamePublisher(gameId, publisherId) VALUES (:id1, :id2)")
    override fun insertIds(id1: Long, id2:Long)
    
    @Insert
    fun insert(line:GamePublisherTableBean)

    @Update
    fun update(game: GamePublisherTableBean)

    @Query("DELETE FROM gamePublisher")
    fun deleteAll()

    @Query("DELETE FROM gamePublisher WHERE gameId=:objectId")
    override fun deleteWithMember1Id(objectId:Long)
    
    @Query("DELETE FROM gamePublisher WHERE publisherId=:objectId")
    fun deleteWithPublisherId(objectId:Long)
}  


@Dao
interface AddOnPublisherDao: CommonJunctionDAo<AddOnPublisherTableBean> {

    @Query("SELECT * FROM addOnPublisher")
    fun getList(): List<AddOnPublisherTableBean>

    @Query("INSERT INTO addOnPublisher(addOnId, publisherId) VALUES (:id1, :id2)")
    override fun insertIds(id1: Long, id2:Long)
    
    @Insert
    fun insert(line:AddOnPublisherTableBean)

    @Update
    fun update(game: AddOnPublisherTableBean)

    @Query("DELETE FROM addOnPublisher")
    fun deleteAll()

    @Query("DELETE FROM addOnPublisher WHERE addOnId=:objectId")
    override fun deleteWithMember1Id(objectId:Long)
    
    @Query("DELETE FROM addOnPublisher WHERE publisherId=:objectId")
    fun deleteWithPublisherId(objectId:Long)
}  


@Dao
interface MultiAddOnPublisherDao: CommonJunctionDAo<MultiAddOnPublisherTableBean> {

    @Query("SELECT * FROM multiAddOnPublisher")
    fun getList(): List<MultiAddOnPublisherTableBean>

    @Query("INSERT INTO multiAddOnPublisher(multiAddOnId, publisherId) VALUES (:id1, :id2)")
    override fun insertIds(id1: Long, id2:Long)
    
    @Insert
    fun insert(line:MultiAddOnPublisherTableBean)

    @Update
    fun update(game: MultiAddOnPublisherTableBean)

    @Query("DELETE FROM multiAddOnPublisher")
    fun deleteAll()

    @Query("DELETE FROM multiAddOnPublisher WHERE multiAddOnId=:objectId")
    override fun deleteWithMember1Id(objectId:Long)
    
    @Query("DELETE FROM multiAddOnPublisher WHERE publisherId=:objectId")
    fun deleteWithPublisherId(objectId:Long)
}  


@Dao
interface GamePlayingModDao: CommonJunctionDAo<GamePlayingModTableBean> {

    @Query("SELECT * FROM gamePlayingMod")
    fun getList(): List<GamePlayingModTableBean>

    @Query("INSERT INTO gamePlayingMod(gameId, playingModId) VALUES (:id1, :id2)")
    override fun insertIds(id1: Long, id2:Long)
    
    @Insert
    fun insert(line:GamePlayingModTableBean)

    @Update
    fun update(game: GamePlayingModTableBean)

    @Query("DELETE FROM gamePlayingMod")
    fun deleteAll()

    @Query("DELETE FROM gamePlayingMod WHERE gameId=:objectId")
    override fun deleteWithMember1Id(objectId:Long)
    
    @Query("DELETE FROM gamePlayingMod WHERE playingModId=:objectId")
    fun deleteWithPlayingModId(objectId:Long)
}  


@Dao
interface AddOnPlayingModDao: CommonJunctionDAo<AddOnPlayingModTableBean> {

    @Query("SELECT * FROM addOnPlayingMod")
    fun getList(): List<AddOnPlayingModTableBean>

    @Query("INSERT INTO addOnPlayingMod(addOnId, playingModId) VALUES (:id1, :id2)")
    override fun insertIds(id1: Long, id2:Long)
    
    @Insert
    fun insert(line:AddOnPlayingModTableBean)

    @Update
    fun update(game: AddOnPlayingModTableBean)

    @Query("DELETE FROM addOnPlayingMod")
    fun deleteAll()

    @Query("DELETE FROM addOnPlayingMod WHERE addOnId=:objectId")
    override fun deleteWithMember1Id(objectId:Long)
    
    @Query("DELETE FROM addOnPlayingMod WHERE playingModId=:objectId")
    fun deleteWithPlayingModId(objectId:Long)
}  


@Dao
interface MultiAddOnPlayingModDao: CommonJunctionDAo<MultiAddOnPlayingModTableBean> {

    @Query("SELECT * FROM multiAddOnPlayingMod")
    fun getList(): List<MultiAddOnPlayingModTableBean>

    @Query("INSERT INTO multiAddOnPlayingMod(multiAddOnId, playingModId) VALUES (:id1, :id2)")
    override fun insertIds(id1: Long, id2:Long)
    
    @Insert
    fun insert(line:MultiAddOnPlayingModTableBean)

    @Update
    fun update(game: MultiAddOnPlayingModTableBean)

    @Query("DELETE FROM multiAddOnPlayingMod")
    fun deleteAll()

    @Query("DELETE FROM multiAddOnPlayingMod WHERE multiAddOnId=:objectId")
    override fun deleteWithMember1Id(objectId:Long)
    
    @Query("DELETE FROM multiAddOnPlayingMod WHERE playingModId=:objectId")
    fun deleteWithPlayingModId(objectId:Long)
}  


@Dao
interface GameLanguageDao: CommonJunctionDAo<GameLanguageTableBean> {

    @Query("SELECT * FROM gameLanguage")
    fun getList(): List<GameLanguageTableBean>

    @Query("INSERT INTO gameLanguage(gameId, languageId) VALUES (:id1, :id2)")
    override fun insertIds(id1: Long, id2:Long)
    
    @Insert
    fun insert(line:GameLanguageTableBean)

    @Update
    fun update(game: GameLanguageTableBean)

    @Query("DELETE FROM gameLanguage")
    fun deleteAll()

    @Query("DELETE FROM gameLanguage WHERE gameId=:objectId")
    override fun deleteWithMember1Id(objectId:Long)
    
    @Query("DELETE FROM gameLanguage WHERE languageId=:objectId")
    fun deleteWithLanguageId(objectId:Long)
}  


@Dao
interface AddOnLanguageDao: CommonJunctionDAo<AddOnLanguageTableBean> {

    @Query("SELECT * FROM addOnLanguage")
    fun getList(): List<AddOnLanguageTableBean>

    @Query("INSERT INTO addOnLanguage(addOnId, languageId) VALUES (:id1, :id2)")
    override fun insertIds(id1: Long, id2:Long)
    
    @Insert
    fun insert(line:AddOnLanguageTableBean)

    @Update
    fun update(game: AddOnLanguageTableBean)

    @Query("DELETE FROM addOnLanguage")
    fun deleteAll()

    @Query("DELETE FROM addOnLanguage WHERE addOnId=:objectId")
    override fun deleteWithMember1Id(objectId:Long)
    
    @Query("DELETE FROM addOnLanguage WHERE languageId=:objectId")
    fun deleteWithLanguageId(objectId:Long)
}  


@Dao
interface MultiAddOnLanguageDao: CommonJunctionDAo<MultiAddOnLanguageTableBean> {

    @Query("SELECT * FROM multiAddOnLanguage")
    fun getList(): List<MultiAddOnLanguageTableBean>

    @Query("INSERT INTO multiAddOnLanguage(multiAddOnId, languageId) VALUES (:id1, :id2)")
    override fun insertIds(id1: Long, id2:Long)
    
    @Insert
    fun insert(line:MultiAddOnLanguageTableBean)

    @Update
    fun update(game: MultiAddOnLanguageTableBean)

    @Query("DELETE FROM multiAddOnLanguage")
    fun deleteAll()

    @Query("DELETE FROM multiAddOnLanguage WHERE multiAddOnId=:objectId")
    override fun deleteWithMember1Id(objectId:Long)
    
    @Query("DELETE FROM multiAddOnLanguage WHERE languageId=:objectId")
    fun deleteWithLanguageId(objectId:Long)
}  
