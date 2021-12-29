package thannajo.appli.filrouge.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "gameMultiAddOn")
data class GameMultiAddOnTableBean(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    val gameId:Long,
    val multiAddOnId: Long
)


   @Entity(tableName = "gameTag")
data class GameTagTableBean(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    val gameId:Long,
    val tagId: Long
)


@Entity(tableName = "gameTopic")
data class GameTopicTableBean(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    val gameId:Long,
    val topicId: Long
)


@Entity(tableName = "gameMechanism")
data class GameMechanismTableBean(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    val gameId:Long,
    val mechanismId: Long
)


@Entity(tableName = "gameDesigner")
data class GameDesignerTableBean(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    val gameId:Long,
    val designerId: Long
)


@Entity(tableName = "addOnDesigner")
data class AddOnDesignerTableBean(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    val addOnId:Long,
    val designerId: Long
)


@Entity(tableName = "multiAddOnDesigner")
data class MultiAddOnDesignerTableBean(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    val multiAddOnId:Long,
    val designerId: Long
)


@Entity(tableName = "gameArtist")
data class GameArtistTableBean(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    val gameId:Long,
    val artistId: Long
)


@Entity(tableName = "addOnArtist")
data class AddOnArtistTableBean(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    val addOnId:Long,
    val artistId: Long
)


@Entity(tableName = "multiAddOnArtist")
data class MultiAddOnArtistTableBean(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    val multiAddOnId:Long,
    val artistId: Long
)


@Entity(tableName = "gamePublisher")
data class GamePublisherTableBean(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    val gameId:Long,
    val publisherId: Long
)


@Entity(tableName = "addOnPublisher")
data class AddOnPublisherTableBean(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    val addOnId:Long,
    val publisherId: Long
)


@Entity(tableName = "multiAddOnPublisher")
data class MultiAddOnPublisherTableBean(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    val multiAddOnId:Long,
    val publisherId: Long
)


@Entity(tableName = "gamePlayingMod")
data class GamePlayingModTableBean(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    val gameId:Long,
    val playingModId: Long
)


@Entity(tableName = "addOnPlayingMod")
data class AddOnPlayingModTableBean(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    val addOnId:Long,
    val playingModId: Long
)


@Entity(tableName = "multiAddOnPlayingMod")
data class MultiAddOnPlayingModTableBean(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    val multiAddOnId:Long,
    val playingModId: Long
)


@Entity(tableName = "gameLanguage")
data class GameLanguageTableBean(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    val gameId:Long,
    val languageId: Long
)


@Entity(tableName = "addOnLanguage")
data class AddOnLanguageTableBean(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    val addOnId:Long,
    val languageId: Long
)


@Entity(tableName = "multiAddOnLanguage")
data class MultiAddOnLanguageTableBean(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    val multiAddOnId:Long,
    val languageId: Long
)
