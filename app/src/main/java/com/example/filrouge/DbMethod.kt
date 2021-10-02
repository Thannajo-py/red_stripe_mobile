package com.example.filrouge

import com.example.filrouge.bean.AddOnTableBean
import com.example.filrouge.bean.GameTableBean
import com.example.filrouge.bean.ID
import com.example.filrouge.bean.MultiAddOnTableBean

class DbMethod {

    val db = appInstance.database

    fun getGameTripleListField(it:GameBean) = arrayListOf(Triple(it.designers, db.designerDao(), db.gameDesignerDao()),
        Triple(it.artists, db.artistDao(), db.gameArtistDao()), Triple(it.publishers, db.publisherDao(), db.gamePublisherDao()),
        Triple(it.playing_mode, db.playingModDao(), db.gamePlayingModDao()), Triple(it.language, db.languageDao(), db.gameLanguageDao()),
        Triple(it.tags, db.tagDao(), db.gameTagDao()), Triple(it.topics, db.topicDao(), db.gameTopicDao()), Triple(it.mechanism, db.mechanismDao(),
        db.gameMechanismDao()))

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


    fun delete(game: ID){
        when(game){
            is GameTableBean -> db.runInTransaction { getGameTableDaoField().forEach { it.deleteWithMember1Id(game.id) } }
            is AddOnTableBean -> db.runInTransaction {  getAddOnTableDaoField().forEach { it.deleteWithMember1Id(game.id) }}
            is MultiAddOnTableBean -> db.runInTransaction { getMultiAddOnTableDaoField().forEach { it.deleteWithMember1Id(game.id) }}

        }
    }
}