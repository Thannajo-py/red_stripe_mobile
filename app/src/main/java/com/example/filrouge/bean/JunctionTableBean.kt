package com.example.filrouge.bean

import androidx.room.Entity

@Entity(tableName = "gameMultiAddOn", primaryKeys = ["gameId", "multiAddOnId"])
data class GameMultiAddOnTableBean(
    val gameId:Long,
    val multiAddOnId: Long
)

    @Entity(tableName = "gameTag", primaryKeys = ["gameId", "tagId"])
data class GameTagTableBean(
    val gameId:Long,
    val tagId: Long
)

    @Entity(tableName = "gameTopic", primaryKeys = ["gameId", "topicId"])
data class GameTopicTableBean(
    val gameId:Long,
    val topicId: Long
)

    @Entity(tableName = "gameMechanism", primaryKeys = ["gameId", "mechanismId"])
data class GameMechanismTableBean(
    val gameId:Long,
    val mechanismId: Long
)

    @Entity(tableName = "gameDesigner", primaryKeys = ["gameId", "designerId"])
data class GameDesignerTableBean(
    val gameId:Long,
    val designerId: Long
)

    @Entity(tableName = "addOnDesigner", primaryKeys = ["addOnId", "designerId"])
data class AddOnDesignerTableBean(
    val addOnId:Long,
    val designerId: Long
)

    @Entity(tableName = "multiAddOnDesigner", primaryKeys = ["multiAddOnId", "designerId"])
data class MultiAddOnDesignerTableBean(
    val multiAddOnId:Long,
    val designerId: Long
)

    @Entity(tableName = "gameArtist", primaryKeys = ["gameId", "artistId"])
data class GameArtistTableBean(
    val gameId:Long,
    val artistId: Long
)

    @Entity(tableName = "addOnArtist", primaryKeys = ["addOnId", "artistId"])
data class AddOnArtistTableBean(
    val addOnId:Long,
    val artistId: Long
)

    @Entity(tableName = "multiAddOnArtist", primaryKeys = ["multiAddOnId", "artistId"])
data class MultiAddOnArtistTableBean(
    val multiAddOnId:Long,
    val artistId: Long
)

    @Entity(tableName = "gamePublisher", primaryKeys = ["gameId", "publisherId"])
data class GamePublisherTableBean(
    val gameId:Long,
    val publisherId: Long
)

    @Entity(tableName = "addOnPublisher", primaryKeys = ["addOnId", "publisherId"])
data class AddOnPublisherTableBean(
    val addOnId:Long,
    val publisherId: Long
)

    @Entity(tableName = "multiAddOnPublisher", primaryKeys = ["multiAddOnId", "publisherId"])
data class MultiAddOnPublisherTableBean(
    val multiAddOnId:Long,
    val publisherId: Long
)

    @Entity(tableName = "gamePlayingMod", primaryKeys = ["gameId", "playingModId"])
data class GamePlayingModTableBean(
    val gameId:Long,
    val playingModId: Long
)

    @Entity(tableName = "addOnPlayingMod", primaryKeys = ["addOnId", "playingModId"])
data class AddOnPlayingModTableBean(
    val addOnId:Long,
    val playingModId: Long
)

    @Entity(tableName = "multiAddOnPlayingMod", primaryKeys = ["multiAddOnId", "playingModId"])
data class MultiAddOnPlayingModTableBean(
    val multiAddOnId:Long,
    val playingModId: Long
)

    @Entity(tableName = "gameLanguage", primaryKeys = ["gameId", "languageId"])
data class GameLanguageTableBean(
    val gameId:Long,
    val languageId: Long
)

    @Entity(tableName = "addOnLanguage", primaryKeys = ["addOnId", "languageId"])
data class AddOnLanguageTableBean(
    val addOnId:Long,
    val languageId: Long
)

    @Entity(tableName = "multiAddOnLanguage", primaryKeys = ["multiAddOnId", "languageId"])
data class MultiAddOnLanguageTableBean(
    val multiAddOnId:Long,
    val languageId: Long
)

    