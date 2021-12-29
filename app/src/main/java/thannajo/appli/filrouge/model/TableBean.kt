package thannajo.appli.filrouge.model

import androidx.room.*


interface ID{
    val id:Long
    val name:String
}


interface CommonComponent{
    val name: String
    val player_min: Int?
    val player_max: Int?
    val playing_time: String?
    val difficultyId: Long?
    val bgg_link: String?
    val age: Int?
    val buying_price:Int?
    val stock: Int?
    val max_time: Int?
    val external_img:String?
    val picture:String?
}


interface OneToOne {
    val id:Long
    val name:String
}


interface CommonGame {
    val id: Long
    val name: String
    val designer: String?
    val image:String?
}


interface Previous: ID {
    override val id: Long
    override val name: String
    val serverId: Int?
    val picture: String?
}


@Entity(tableName = "User")
data class UserTableBean(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    val login:String,
    val password:String,
    val add:Boolean,
    val change:Boolean,
    val delete:Boolean,
    val synchronize:Boolean,
    val addAccount:Boolean,
    val deleteAccount: Boolean
)


@Entity(tableName = "difficulty")
data class DifficultyTableBean(
    @PrimaryKey(autoGenerate = true) override var id: Long = 0,
    override val name:String
):ID, OneToOne


@Entity(tableName = "game", foreignKeys = [ForeignKey(entity = DifficultyTableBean::class,
    parentColumns = ["id"], childColumns = ["difficultyId"], onDelete = ForeignKey.SET_NULL
)])
data class GameTableBean(
    @PrimaryKey(autoGenerate = true) override var id: Long = 0,
    override val serverId: Int?,
    override val name: String,
    override val player_min: Int?,
    override val player_max: Int?,
    override val playing_time: String?,
    override val difficultyId: Long?,
    override val bgg_link: String?,
    override val age: Int?,
    override val buying_price:Int?,
    override val stock: Int?,
    override val max_time: Int?,
    override val external_img:String?,
    override val picture:String?,
    val by_player: Boolean?,
    val hasChanged: Boolean
    ): ID, CommonComponent, Previous


@Entity(tableName = "addOn", foreignKeys = [ForeignKey(entity = GameTableBean::class,
    parentColumns = ["id"], childColumns = ["gameId"], onDelete = ForeignKey.SET_NULL
),ForeignKey(entity = DifficultyTableBean::class,
    parentColumns = ["id"], childColumns = ["difficultyId"], onDelete = ForeignKey.SET_NULL
)])
data class AddOnTableBean(
    @PrimaryKey(autoGenerate = true) override var id: Long = 0,
    override val serverId: Int?,
    override val name: String,
    override val player_min: Int?,
    override val player_max: Int?,
    override val playing_time: String?,
    override val difficultyId: Long?,
    override val bgg_link: String?,
    override val age: Int?,
    override val buying_price:Int?,
    override val stock: Int?,
    override val max_time: Int?,
    override val external_img:String?,
    override val picture:String?,
    var gameId: Long?,
    val hasChanged: Boolean

): ID, CommonComponent, Previous


@Entity(tableName = "multiAddOn", foreignKeys = [androidx.room.ForeignKey(
    entity = DifficultyTableBean::class,
    parentColumns = ["id"],
    childColumns = ["difficultyId"],
    onDelete = ForeignKey.SET_NULL
)])
data class MultiAddOnTableBean(
    @PrimaryKey(autoGenerate = true) override var id: Long = 0,
    override val serverId: Int?,
    override val name: String,
    override val player_min: Int?,
    override val player_max: Int?,
    override val playing_time: String?,
    override val difficultyId: Long?,
    override val bgg_link: String?,
    override val age: Int?,
    override val buying_price:Int?,
    override val stock: Int?,
    override val max_time: Int?,
    override val external_img:String?,
    override val picture:String?,
    val hasChanged: Boolean
): ID, CommonComponent, Previous


@Entity(tableName = "image")
data class ImageTableBean(
    @PrimaryKey(autoGenerate = true) override var id: Long = 0,
    override val name:String,
    val gameName:String,
    val gameType: String
    ): ID


@Entity(tableName = "designer")
data class DesignerTableBean(
    @PrimaryKey(autoGenerate = true) override var id: Long = 0,
    override val name:String
): ID

@Entity(tableName = "artist")
data class ArtistTableBean(
    @PrimaryKey(autoGenerate = true) override var id: Long = 0,
    override val name:String
): ID


@Entity(tableName = "publisher")
data class PublisherTableBean(
    @PrimaryKey(autoGenerate = true) override var id: Long = 0,
    override val name:String
): ID


@Entity(tableName = "playingMod")
data class PlayingModTableBean(
    @PrimaryKey(autoGenerate = true) override var id: Long = 0,
    override val name:String
): ID


@Entity(tableName = "language")
data class LanguageTableBean(
    @PrimaryKey(autoGenerate = true) override var id: Long = 0,
    override val name:String
): ID


@Entity(tableName = "tag")
data class TagTableBean(
    @PrimaryKey(autoGenerate = true) override var id: Long = 0,
    override val name:String
): ID


@Entity(tableName = "topic")
data class TopicTableBean(
    @PrimaryKey(autoGenerate = true) override var id: Long = 0,
    override val name:String
): ID


@Entity(tableName = "mechanism")
data class MechanismTableBean(
    @PrimaryKey(autoGenerate = true) override var id: Long = 0,
    override val name:String
): ID


data class DesignerWithGame(
    override val id:Long,
    override val name:String,
    override val designer: String?,
    override val image:String?
    ):CommonGame, OneToOne


data class DesignerWithAddOn(
    override val id:Long,
    override val name:String,
    override val designer: String?,
    override val image:String?
):CommonGame


data class DesignerWithMultiAddOn(
    override val id:Long,
    override val name:String,
    override val designer: String?,
    override val image:String?

):CommonGame


@Entity(tableName = "deletedItems")
data class DeletedContentTableBean(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    val idContent: Long,
    val type:String
)


data class SavedDatabase(
    var game: List<GameTableBean>?=null,
    var addOn: List<AddOnTableBean>?=null,
    var multiAddOn: List<MultiAddOnTableBean>?=null,
    var tag: List<TagTableBean>?=null,
    var topic: List<TopicTableBean>?=null,
    var mechanism: List<MechanismTableBean>?=null,
    var user: List<UserTableBean>?=null,
    var difficulty: List<DifficultyTableBean>?=null,
    var designer: List<DesignerTableBean>?=null,
    var artist: List<ArtistTableBean>?=null,
    var publisher: List<PublisherTableBean>?=null,
    var playingMod: List<PlayingModTableBean>?=null,
    var language: List<LanguageTableBean>?=null,
    var gameMultiAddOn: List<GameMultiAddOnTableBean>?=null,
    var gameTag: List<GameTagTableBean>?=null,
    var gameTopic: List<GameTopicTableBean>?=null,
    var gameMechanism: List<GameMechanismTableBean>?=null,
    var gameDesigner: List<GameDesignerTableBean>?=null,
    var addOnDesigner: List<AddOnDesignerTableBean>?=null,
    var multiAddOnDesigner: List<MultiAddOnDesignerTableBean>?=null,
    var gameArtist: List<GameArtistTableBean>?=null,
    var addOnArtist: List<AddOnArtistTableBean>?=null,
    var multiAddOnArtist: List<MultiAddOnArtistTableBean>?=null,
    var gamePublisher: List<GamePublisherTableBean>?=null,
    var addOnPublisher: List<AddOnPublisherTableBean>?=null,
    var multiAddOnPublisher: List<MultiAddOnPublisherTableBean>?=null,
    var gamePlayingMod: List<GamePlayingModTableBean>?=null,
    var addOnPlayingMod: List<AddOnPlayingModTableBean>?=null,
    var multiAddOnPlayingMod: List<MultiAddOnPlayingModTableBean>?=null,
    var gameLanguage: List<GameLanguageTableBean>?=null,
    var addOnLanguage: List<AddOnLanguageTableBean>?=null,
    var multiAddOnLanguage: List<MultiAddOnLanguageTableBean>?=null,
    var deletedContent: List<DeletedContentTableBean>?=null,
    var image: List<ImageTableBean>?=null
)
