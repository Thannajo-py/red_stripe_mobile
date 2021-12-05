package com.example.filrouge.bean

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
    val serverId: Int?,
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
    ): ID, CommonComponent


@Entity(tableName = "addOn", foreignKeys = [ForeignKey(entity = GameTableBean::class,
    parentColumns = ["id"], childColumns = ["gameId"], onDelete = ForeignKey.SET_NULL
),ForeignKey(entity = DifficultyTableBean::class,
    parentColumns = ["id"], childColumns = ["difficultyId"], onDelete = ForeignKey.SET_NULL
)])
data class AddOnTableBean(
    @PrimaryKey(autoGenerate = true) override var id: Long = 0,
    val serverId: Int?,
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
    val gameId: Long?,
    val hasChanged: Boolean

): ID, CommonComponent


@Entity(tableName = "multiAddOn", foreignKeys = [androidx.room.ForeignKey(
    entity = DifficultyTableBean::class,
    parentColumns = ["id"],
    childColumns = ["difficultyId"],
    onDelete = androidx.room.ForeignKey.SET_NULL
)])
data class MultiAddOnTableBean(
    @PrimaryKey(autoGenerate = true) override var id: Long = 0,
    val serverId: Int?,
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
): ID, CommonComponent


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


data class DesignerTableBean2(
    override var id: Long = 0,
    override val name:String,
    val idGame:Long,
    val idAddOn:Long,
    val idMultiAddOn:Long,

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
    val game: List<GameTableBean>,
    val addOn: List<AddOnTableBean>,
    val multiAddOn: List<MultiAddOnTableBean>,
    val tag: List<TagTableBean>,
    val topic: List<TopicTableBean>,
    val mechanism: List<MechanismTableBean>,
    val user: List<UserTableBean>,
    val difficulty: List<DifficultyTableBean>,
    val designer: List<DesignerTableBean>,
    val artist: List<ArtistTableBean>,
    val publisher: List<PublisherTableBean>,
    val playingMod: List<PlayingModTableBean>,
    val language: List<LanguageTableBean>,
    val gameMultiAddOn: List<GameMultiAddOnTableBean>,
    val gameTag: List<GameTagTableBean>,
    val gameTopic: List<GameTopicTableBean>,
    val gameMechanism: List<GameMechanismTableBean>,
    val gameDesigner: List<GameDesignerTableBean>,
    val addOnDesigner: List<AddOnDesignerTableBean>,
    val multiAddOnDesigner: List<MultiAddOnDesignerTableBean>,
    val gameArtist: List<GameArtistTableBean>,
    val addOnArtist: List<AddOnArtistTableBean>,
    val multiAddOnArtist: List<MultiAddOnArtistTableBean>,
    val gamePublisher: List<GamePublisherTableBean>,
    val addOnPublisher: List<AddOnPublisherTableBean>,
    val multiAddOnPublisher: List<MultiAddOnPublisherTableBean>,
    val gamePlayingMod: List<GamePlayingModTableBean>,
    val addOnPlayingMod: List<AddOnPlayingModTableBean>,
    val multiAddOnPlayingMod: List<MultiAddOnPlayingModTableBean>,
    val gameLanguage: List<GameLanguageTableBean>,
    val addOnLanguage: List<AddOnLanguageTableBean>,
    val multiAddOnLanguage: List<MultiAddOnLanguageTableBean>,
    val deletedContent: List<DeletedContentTableBean>,
    val image: List<ImageTableBean>
)
