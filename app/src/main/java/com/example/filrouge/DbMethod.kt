package com.example.filrouge

import com.example.filrouge.bean.*
import com.example.filrouge.dao.CommonComponentDao
import com.example.filrouge.dao.CommonJunctionDAo
import com.example.filrouge.dao.GameDao
import com.example.filrouge.dao.GameMultiAddOnDao

class DbMethod {

    val db = appInstance.database

    fun getGameTripleListField(it:GameBean) = arrayListOf(Triple(it.designers, db.designerDao(), db.gameDesignerDao()),
        Triple(it.artists, db.artistDao(), db.gameArtistDao()), Triple(it.publishers, db.publisherDao(), db.gamePublisherDao()),
        Triple(it.playing_mode, db.playingModDao(), db.gamePlayingModDao()), Triple(it.language, db.languageDao(), db.gameLanguageDao()),
        Triple(it.tags, db.tagDao(), db.gameTagDao()), Triple(it.topics, db.topicDao(), db.gameTopicDao()), Triple(it.mechanism, db.mechanismDao(),
        db.gameMechanismDao()))

    fun getGameTripleListField(it:ArrayList<ArrayList<String>>) = arrayListOf(Triple(it[0], db.designerDao(), db.gameDesignerDao()),
        Triple(it[1], db.artistDao(), db.gameArtistDao()), Triple(it[2], db.publisherDao(), db.gamePublisherDao()),
        Triple(it[3], db.playingModDao(), db.gamePlayingModDao()), Triple(it[4], db.languageDao(), db.gameLanguageDao()),
        Triple(it[5], db.tagDao(), db.gameTagDao()), Triple(it[6], db.topicDao(), db.gameTopicDao()), Triple(it[7], db.mechanismDao(),
            db.gameMechanismDao()), Triple(it[8], db.multiAddOnDao(), db.gameMultiAddOnDao()) )

    fun getAddOnTripleListField(it:ArrayList<ArrayList<String>>) = arrayListOf(Triple(it[0], db.designerDao(), db.gameDesignerDao()),
        Triple(it[1], db.artistDao(), db.gameArtistDao()), Triple(it[2], db.publisherDao(), db.gamePublisherDao()),
        Triple(it[3], db.playingModDao(), db.gamePlayingModDao()), Triple(it[4], db.languageDao(), db.gameLanguageDao()))

    fun getMultiAddOnTripleListField(it:ArrayList<ArrayList<String>>) = arrayListOf(Triple(it[0], db.designerDao(), db.gameDesignerDao()),
        Triple(it[1], db.artistDao(), db.gameArtistDao()), Triple(it[2], db.publisherDao(), db.gamePublisherDao()),
        Triple(it[3], db.playingModDao(), db.gamePlayingModDao()), Triple(it[4], db.languageDao(), db.gameLanguageDao()))

    fun getAddOnTripleListField(it:AddOnBean) = arrayListOf(Triple(it.designers, db.designerDao(), db.addOnDesignerDao()),
        Triple(it.artists, db.artistDao(), db.addOnArtistDao()), Triple(it.publishers, db.publisherDao(), db.addOnPublisherDao()),
        Triple(it.playing_mode, db.playingModDao(), db.addOnPlayingModDao()), Triple(it.language, db.languageDao(), db.addOnLanguageDao()))

    fun getMultiAddOnTripleListField(it:MultiAddOnBean) = arrayListOf(Triple(it.designers, db.designerDao(), db.multiAddOnDesignerDao()),
        Triple(it.artists, db.artistDao(), db.multiAddOnArtistDao()), Triple(it.publishers, db.publisherDao(), db.multiAddOnPublisherDao()),
        Triple(it.playing_mode, db.playingModDao(), db.multiAddOnPlayingModDao()), Triple(it.language, db.languageDao(), db.multiAddOnLanguageDao()))

    private fun getGameTableDaoField() = arrayListOf(db.gameDesignerDao(),db.gameArtistDao(), db.gamePublisherDao(),
     db.gamePlayingModDao(), db.gameLanguageDao(), db.gameTagDao(), db.gameTopicDao(), db.gameMechanismDao(),
    db.gameMultiAddOnDao())

    private fun getAddOnTableDaoField() = arrayListOf(db.addOnDesignerDao(),db.addOnArtistDao(), db.addOnPublisherDao(),
        db.addOnPlayingModDao(), db.addOnLanguageDao())

    private fun getMultiAddOnTableDaoField() = arrayListOf(db.multiAddOnDesignerDao(),db.multiAddOnArtistDao(), db.multiAddOnPublisherDao(),
        db.multiAddOnPlayingModDao(), db.multiAddOnLanguageDao())


    fun delete_link(game: ID){
        when(game){
            is GameTableBean -> db.runInTransaction { getGameTableDaoField().forEach { it.deleteWithMember1Id(game.id) } }
            is AddOnTableBean -> db.runInTransaction {  getAddOnTableDaoField().forEach { it.deleteWithMember1Id(game.id) }}
            is MultiAddOnTableBean -> db.runInTransaction { getMultiAddOnTableDaoField().forEach { it.deleteWithMember1Id(game.id) }}

        }
    }

    fun insert_link(game: ID, list:ArrayList<ArrayList<String>>){
        when(game){
            is GameTableBean -> db.runInTransaction { getGameTripleListField(list).forEach { linkList(it.first, it.second, it.third, game.id ) } }
            is AddOnTableBean -> db.runInTransaction {  getAddOnTripleListField(list).forEach { linkList(it.first, it.second, it.third, game.id ) }}
            is MultiAddOnTableBean -> db.runInTransaction { getMultiAddOnTripleListField(list).forEach { linkList(it.first, it.second, it.third, game.id ) }}

        }
    }

    fun <T:ID, U>linkList(name:ArrayList<String>, dao:CommonComponentDao<T>, joinDao:CommonJunctionDAo<U>, id:Long){
        name.forEach {
            val list = dao.getByName(it)
            if(list.isNotEmpty()) joinDao.insert(id, list[0].id)
        }

    }
    fun delete(game: ID){
        when(game){
            is GameTableBean -> db.runInTransaction {
                getGameTableDaoField().forEach { it.deleteWithMember1Id(game.id) }
                game.serverId?.run{
                    appInstance.database.deletedItemDao().insert(DeletedContentTableBean(0,this.toLong(), Type.Game.name))
                }
                appInstance.database.gameDao().deleteOne(game.id)

            }
            is AddOnTableBean -> db.runInTransaction {
                getAddOnTableDaoField().forEach { it.deleteWithMember1Id(game.id) }
                game.serverId?.run{
                    appInstance.database.deletedItemDao().insert(DeletedContentTableBean(0,this.toLong(), Type.AddOn.name))
                }
                appInstance.database.addOnDao().deleteOne(game.id)
            }
            is MultiAddOnTableBean -> db.runInTransaction {
                getMultiAddOnTableDaoField().forEach { it.deleteWithMember1Id(game.id) }
                game.serverId?.run{
                    appInstance.database.deletedItemDao().insert(DeletedContentTableBean(0,this.toLong(), Type.MultiAddOn.name))
                }
                appInstance.database.multiAddOnDao().deleteOne(game.id)
            }

        }
    }

    fun gameMultiAddOnLinkListByMultiAddOn(name:ArrayList<String>, dao:GameDao, joinDao:GameMultiAddOnDao, id:Long){
        name.forEach {
            val list = dao.getByName(it)
            if(list.isNotEmpty()) joinDao.insert(list[0].id, id)
        }
    }

    fun convertToGameBean(game:GameTableBean):GameBean {
        val dao = appInstance.database.gameDao()
        var difficulty:String? = null
        game.difficultyId?.run{
            val difficultyL = appInstance.database.difficultyDao().getbyId(this)
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
            appInstance.database.gameDao().getTagObject(game.id).map{it.name}.toCollection(ArrayList()),
            appInstance.database.gameDao().getTopicObject(game.id).map{it.name}.toCollection(ArrayList()),
            appInstance.database.gameDao().getMechanismObject(game.id).map{it.name}.toCollection(ArrayList()),
            appInstance.database.addOnDao().getDesignerWithAddOnOfGames(game.id).map{it.name}.toCollection(ArrayList()),
            appInstance.database.multiAddOnDao().getDesignerWithMultiAddOnObjectOfGame(game.id).map{it.name}.toCollection(ArrayList()),
            game.external_img,
            game.picture)





    }

    fun convertToAddOnBean(game:AddOnTableBean):AddOnBean {
        val dao = appInstance.database.addOnDao()
        var difficulty:String? = null
        game.difficultyId?.run{
            val difficultyL = appInstance.database.difficultyDao().getbyId(this)
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

    fun convertToMultiAddOnBean(game:MultiAddOnTableBean):MultiAddOnBean {
        val dao = appInstance.database.multiAddOnDao()
        var difficulty:String? = null
        game.difficultyId?.run{
            val difficultyL = appInstance.database.difficultyDao().getbyId(this)
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
}