package com.example.filrouge.utils

import com.example.filrouge.*
import com.example.filrouge.model.*

/**
 * Contain method frequently used with database and database related object
 */
class DbMethod {

    /**
     * Database shortcut variable name
     */
    val db = appInstance.database

    /**
     * list of common field use for semantic linking
     */
    fun getCommonField() = arrayListOf(
        Type.Designer.name,
        Type.Publisher.name,
        Type.Artist.name,
        Type.PlayingMod.name,
        Type.Language.name
    )

    /**
     * list of Game specific field use for semantic linking
     */
    fun getGameSpecificField(): ArrayList<String> {
        val list = getGameCommonSpecificField()
        list.add(Type.MultiAddOn.name)
        return list
    }

    /**
     * list of Game string specific field use for semantic linking
     */
    fun getGameCommonSpecificField() = arrayListOf(
        Type.Tag.name,
        Type.Topic.name,
        Type.Mechanism.name,
    )

    /**
     * list of non-game data to be handled with [DeletedObject]
     */
    fun getDeletableList(): ArrayList<String> {
        val list = getCommonField()
        list.addAll(getGameCommonSpecificField())
        list.add(Type.Difficulty.name)
        return list
    }

    /**
     * list of all game-type object
     */
    fun getGameType() = arrayListOf(
        Type.Game.name,
        Type.AddOn.name,
        Type.MultiAddOn.name
    )

    /**
     * use to delete game object from junction Tables
     */
    fun deleteLink(game: ID, type: String){
        getCommonField().forEach {
            val dao = db.getMember("${type.highToLowCamelCase()}${it}Dao") as CommonJunctionDAo<*>
            dao.deleteWithMember1Id(game.id)
        }
        if (type == Type.Game.name){
            getGameSpecificField().forEach {
                val dao = db.getMember("${type.highToLowCamelCase()}${it}Dao") as CommonJunctionDAo<*>
                dao.deleteWithMember1Id(game.id)
            }
        }
    }

    /**
     * use to create link for game in junction Tables
     */
    fun<T: CommonDataArrayList> insertLink(game: ID, type:String, data:T, map:HashMap<String, ArrayList<String>>?=null){
        findMembers(game, getCommonField(), type, data)
        if (game is GameTableBean)findMembers(game, getGameSpecificField(), type, data, map)
    }

    /**
     * internal method get semantically linked element using Kotlin reflection and make junction tables link
     */
    private fun<T: CommonDataArrayList> findMembers(game:ID, list: ArrayList<String>, type:String, data:T, map:HashMap<String, ArrayList<String>>?=null){
        list.forEach {
            val lowercase = it.highToLowCamelCase()
            val lowerType = type.highToLowCamelCase()
            val targetList = map?.get(it)?:data.getMember(lowercase)
            targetList?.run{
                val toList = this as ArrayList<String>
                val targetDao =
                    appInstance.database.getMember("${lowercase}Dao") as CommonComponentDao<ID>
                val junctionDao =
                    appInstance.database.getMember("$lowerType${it}Dao") as CommonJunctionDAo<*>
                linkList(toList, targetDao, junctionDao, game.id)
            }
        }
    }

    /**
     * set all add-ons gameId to point toward the game Id
     */
    fun setAddOnGameLink(addOnList: ArrayList<CommonGame>, id:Long?){
        addOnList.forEach {
            val addOn = appInstance.database.addOnDao().getObjectById(it.id)
            if (addOn.isNotEmpty()) addOn.first().gameId = id
            appInstance.database.addOnDao().insert(addOn.first())
        }
    }

    /**
     * insert line into junction DAO table
     */
    private fun <T:ID, U>linkList(
        name:ArrayList<String>,
        dao:CommonComponentDao<T>,
        joinDao:CommonJunctionDAo<U>,
        id:Long){
        name.forEach {
            val list = dao.getByName(it)
            if(list.isNotEmpty()) joinDao.insertIds(id, list[0].id)
        }
    }

    /**
     * handle deletion by removing all junction first then save server Id Game object into
     * deleted list table for synchronization
     */
    fun delete(game: Previous, type:String){
        db.runInTransaction {
            val lowercase = type.highToLowCamelCase()
            deleteLink(game, type)
            game.serverId?.run{
                db.deletedContentDao().insert(DeletedContentTableBean(0, this.toLong(), type))
            }
            val dao = db.getMember("${lowercase}Dao")
            dao!!.getMember("deleteOne", game.id)
        }
    }

    /**
     * handle game multi-add-on junction table
      */
    fun gameMultiAddOnLinkListByMultiAddOn(
        name:ArrayList<String>,
        dao:GameDao,
        joinDao:GameMultiAddOnDao,
        id:Long
    ){
        name.forEach {
            val list = dao.getByName(it)
            if(list.isNotEmpty()) joinDao.insertIds(list[0].id, id)
        }
    }

    /**
     * convert [GameTableBean] from local database to [GameBean] for server exchange
     */
    fun convertToBean(game:GameTableBean): GameBean {
        val dao = appInstance.database.gameDao()
        var difficulty:String? = null
        game.difficultyId?.run{
            val difficultyL = appInstance.database.difficultyDao().getById(this)
            if (difficultyL.isNotEmpty()) difficulty = difficultyL[0].name
        }
        return GameBean(
            game.serverId,
            game.name,
            game.player_min,
            game.player_max,
            game.playing_time,
            difficulty,
            dao.getDesignerObject(game.id).map{it.name}.toCollection(ArrayList()),
            dao.getArtistObject(game.id).map{it.name}.toCollection(ArrayList()),
            dao.getPublisherObject(game.id).map{it.name}.toCollection(ArrayList()),
            game.bgg_link,
            dao.getPlayingModObject(game.id).map{it.name}.toCollection(ArrayList()),
            dao.getLanguageObject(game.id).map{it.name}.toCollection(ArrayList()),
            game.age,
            game.buying_price,
            game.stock,
            game.max_time,
            game.by_player,
            appInstance.database.gameDao().getTagObject(game.id).map{
                it.name
            }.toCollection(ArrayList()),
            appInstance.database.gameDao().getTopicObject(game.id).map{
                it.name
            }.toCollection(ArrayList()),
            appInstance.database.gameDao().getMechanismObject(game.id).map{
                it.name
            }.toCollection(ArrayList()),
            appInstance.database.addOnDao().getDesignerWithAddOnOfGames(game.id).map{
                it.name
            }.toCollection(ArrayList()),
            appInstance.database.multiAddOnDao().getDesignerWithMultiAddOnObjectOfGame(game.id).map{
                it.name
            }.toCollection(ArrayList()),
            game.external_img,
            game.picture)
    }

    /**
     * convert [AddOnTableBean] from local database to [AddOnBean] for server exchange
     */
    fun convertToBean(game:AddOnTableBean): AddOnBean {
        val dao = appInstance.database.addOnDao()
        var difficulty:String? = null
        game.difficultyId?.run{
            val difficultyL = appInstance.database.difficultyDao().getById(this)
            if (difficultyL.isNotEmpty()) difficulty = difficultyL[0].name
        }
        var linkGame:String? = null
        game.gameId?.run{
            val gameL = appInstance.database.gameDao().getObjectById(this)
            if (gameL.isNotEmpty()) linkGame = gameL[0].name
        }
        return AddOnBean(
            game.serverId,
            game.name,
            game.player_min,
            game.player_max,
            game.playing_time,
            difficulty,
            dao.getDesignerObject(game.id).map{it.name}.toCollection(ArrayList()),
            dao.getArtistObject(game.id).map{it.name}.toCollection(ArrayList()),
            dao.getPublisherObject(game.id).map{it.name}.toCollection(ArrayList()),
            game.bgg_link,
            dao.getPlayingModObject(game.id).map{it.name}.toCollection(ArrayList()),
            dao.getLanguageObject(game.id).map{it.name}.toCollection(ArrayList()),
            game.age,
            game.buying_price,
            game.stock,
            game.max_time,
            linkGame,
            game.external_img,
            game.picture)
    }

    /**
     * convert [MultiAddOnTableBean] from local database to [MultiAddOnBean] for server exchange
     */
    fun convertToBean(game:MultiAddOnTableBean): MultiAddOnBean {
        val dao = appInstance.database.multiAddOnDao()
        var difficulty:String? = null
        game.difficultyId?.run{
            val difficultyL = appInstance.database.difficultyDao().getById(this)
            if (difficultyL.isNotEmpty()) difficulty = difficultyL[0].name
        }
        return MultiAddOnBean(
            game.serverId,
            game.name,
            game.player_min,
            game.player_max,
            game.playing_time,
            difficulty,
            dao.getDesignerObject(game.id).map{it.name}.toCollection(ArrayList()),
            dao.getArtistObject(game.id).map{it.name}.toCollection(ArrayList()),
            dao.getPublisherObject(game.id).map{it.name}.toCollection(ArrayList()),
            game.bgg_link,
            dao.getPlayingModObject(game.id).map{it.name}.toCollection(ArrayList()),
            dao.getLanguageObject(game.id).map{it.name}.toCollection(ArrayList()),
            game.age,
            game.buying_price,
            game.stock,
            game.max_time,
            dao.getGameObjectFromMultiAddOn(game.id).map{it.name}.toCollection(ArrayList()),
            game.external_img,
            game.picture)
    }

    /**
     * convert [GameBean] from server exchange  to [GameTableBean] for local database
     */
    fun convertToTableBean(it: GameBean):GameTableBean{
        var gameId = 0L
        val gameInDb = db.gameDao().getByServerId(it.id?.toLong() ?: 0L)
        var gameDifficulty: Long? = null
        it.difficulty?.run {
            val listGameDifficulty = db.difficultyDao().getByName(this)
            gameDifficulty = if (listGameDifficulty.isNotEmpty()) {
                listGameDifficulty[0].id
            } else {
                val id = db.difficultyDao()
                    .insert(DifficultyTableBean(0, this))
                id
            }
        }
        if (gameInDb.isNotEmpty()) {
            gameId = gameInDb[0].id
        }
        return GameTableBean(
                gameId,
                it.id,
                it.name,
                it.playerMin,
                it.player_max,
                it.playing_time,
                gameDifficulty,
                it.bgg_link,
                it.age,
                it.buying_price,
                it.stock,
                it.max_time,
                it.external_img,
                it.picture,
                it.by_player,
                false
            )
    }

    /**
     * convert [AddOnBean] from server exchange  to [AddOnTableBean] for local database
     */
    fun convertToTableBean(it: AddOnBean):AddOnTableBean{
        val gameInDb = db.addOnDao().getByServerId(it.id?.toLong() ?: 0L)
        var addOnId = 0L
        var gameDifficulty: Long? = null
        it.difficulty?.run {
            val listGameDifficulty = db.difficultyDao().getByName(this)
            if (listGameDifficulty.isNotEmpty()) gameDifficulty =
                listGameDifficulty[0].id
        }
        var gameId: Long? = null
        it.game?.run {
            val listGame = appInstance.database.gameDao().getByName(this)
            if (listGame.isNotEmpty()) gameId = listGame[0].id
        }
        if (gameInDb.isNotEmpty()) addOnId = gameInDb[0].id
        return AddOnTableBean(
                addOnId,
                it.id,
                it.name,
                it.player_min,
                it.player_max,
                it.playing_time,
                gameDifficulty,
                it.bgg_link,
                it.age,
                it.buying_price,
                it.stock,
                it.max_time,
                it.external_img,
                it.picture,
                gameId,
                false
            )
    }

    /**
     * convert [MultiAddOnBean] from server exchange  to [MultiAddOnTableBean] for local database
     */
    fun convertToTableBean(it: MultiAddOnBean):MultiAddOnTableBean{
        var gameId = 0L
        val gameInDb = db.multiAddOnDao().getByServerId(it.id?.toLong() ?: 0L)
        var gameDifficulty: Long? = null
        it.difficulty?.run {
            val listGameDifficulty = db.difficultyDao().getByName(this)
            if (listGameDifficulty.isNotEmpty()) gameDifficulty =
                listGameDifficulty[0].id
        }
        if (gameInDb.isNotEmpty()) gameId = gameInDb[0].id
        return MultiAddOnTableBean(
                gameId,
                it.id,
                it.name,
                it.player_min,
                it.player_max,
                it.playing_time,
                gameDifficulty,
                it.bgg_link,
                it.age,
                it.buying_price,
                it.stock,
                it.max_time,
                it.external_img,
                it.picture,
            false
        )
    }

    /**
     * Convert [String] of Add-on name list to ArrayList of AddOns
     */
    fun convertStringListToAddOnTableList(list:ArrayList<String>): ArrayList<CommonGame>{
        val addOnList = ArrayList<CommonGame>()
        list.forEach {
            val addOnReturnList = db.addOnDao().getByNameWithDesigner(it)
            if (addOnReturnList.isNotEmpty()) addOnList.add(addOnReturnList.first())
        }
        return addOnList
    }
}
