package com.example.filrouge.bean

import androidx.room.*
import com.example.filrouge.PermissionBean


interface ID{
    val id:Long
    val name:String
}

interface CommonGame {
    val id: Long
    val name: String
    val designer: String?
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
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    val name:String
)


@Entity(tableName = "game", foreignKeys = [ForeignKey(entity = DifficultyTableBean::class,
    parentColumns = ["id"], childColumns = ["difficultyId"], onDelete = ForeignKey.NO_ACTION
)])
data class GameTableBean(
    @PrimaryKey(autoGenerate = true) override var id: Long = 0,
    val serverId: Int?,
    override val name: String,
    val player_min: Int?,
    val player_max: Int?,
    val playing_time: String?,
    val difficultyId: Long?,
    val bgg_link: String?,
    val age: Int?,
    val buying_price:Int?,
    val stock: Int?,
    val max_time: Int?,
    val external_img:String?,
    val picture:String?,
    val by_player: Boolean?,
    val hasChanged: Boolean
    ): ID

@Entity(tableName = "addOn", foreignKeys = [ForeignKey(entity = GameTableBean::class,
    parentColumns = ["id"], childColumns = ["gameId"], onDelete = ForeignKey.NO_ACTION
),ForeignKey(entity = DifficultyTableBean::class,
    parentColumns = ["id"], childColumns = ["difficultyId"], onDelete = ForeignKey.NO_ACTION
)])
data class AddOnTableBean(
    @PrimaryKey(autoGenerate = true) override var id: Long = 0,
    val serverId: Int?,
    override val name: String,
    val player_min: Int?,
    val player_max: Int?,
    val playing_time: String?,
    val difficultyId: Long?,
    val bgg_link: String?,
    val age: Int?,
    val buying_price:Int?,
    val stock: Int?,
    val max_time: Int?,
    val external_img:String?,
    val picture:String?,
    val gameId: Long?,
    val hasChanged: Boolean

): ID

@Entity(tableName = "multiAddOn", foreignKeys = [androidx.room.ForeignKey(
    entity = DifficultyTableBean::class,
    parentColumns = ["id"],
    childColumns = ["difficultyId"],
    onDelete = androidx.room.ForeignKey.NO_ACTION
)])
data class MultiAddOnTableBean(
    @PrimaryKey(autoGenerate = true) override var id: Long = 0,
    val serverId: Int?,
    override val name: String,
    val player_min: Int?,
    val player_max: Int?,
    val playing_time: String?,
    val difficultyId: Long?,
    val bgg_link: String?,
    val age: Int?,
    val buying_price:Int?,
    val stock: Int?,
    val max_time: Int?,
    val external_img:String?,
    val picture:String?,
    val hasChanged: Boolean
): ID

@Entity(tableName = "image")
data class ImageTableBean(
    @PrimaryKey(autoGenerate = true) override var id: Long = 0,
    override val name:String): ID


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
    override val designer: String?

    ):CommonGame

data class DesignerWithAddOn(
    override val id:Long,
    override val name:String,
    override val designer: String?

):CommonGame
data class DesignerWithMultiAddOn(
    override val id:Long,
    override val name:String,
    override val designer: String?

):CommonGame
