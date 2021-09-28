package com.example.filrouge.bean

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.filrouge.PermissionBean


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
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    val serverId: Int?,
    val name: String,
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
    )

@Entity(tableName = "addOn", foreignKeys = [ForeignKey(entity = GameTableBean::class,
    parentColumns = ["id"], childColumns = ["gameId"], onDelete = ForeignKey.NO_ACTION
),ForeignKey(entity = DifficultyTableBean::class,
    parentColumns = ["id"], childColumns = ["difficultyId"], onDelete = ForeignKey.NO_ACTION
)])
data class AddOnTableBean(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    val serverId: Int?,
    val name: String,
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
    val gameId: Long?

)

@Entity(tableName = "multiAddOn", foreignKeys = [androidx.room.ForeignKey(
    entity = DifficultyTableBean::class,
    parentColumns = ["id"],
    childColumns = ["difficultyId"],
    onDelete = androidx.room.ForeignKey.NO_ACTION
)])
data class MultiAddOnTableBean(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    val serverId: Int?,
    val name: String,
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
)

@Entity(tableName = "multiAddOn")
data class ImageTableBean(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    val name:String)


@Entity(tableName = "designer")
data class DesignerTableBean(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    val name:String
)

@Entity(tableName = "artist")
data class ArtistTableBean(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    val name:String
)

@Entity(tableName = "publisher")
data class PublisherTableBean(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    val name:String
)

@Entity(tableName = "playingMod")
data class PlayingModTableBean(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    val name:String
)

@Entity(tableName = "language")
data class LanguageTableBean(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    val name:String
)

@Entity(tableName = "tag")
data class TagTableBean(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    val name:String
)

@Entity(tableName = "topic")
data class TopicTableBean(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    val name:String
)

@Entity(tableName = "mechanism")
data class MechanismTableBean(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    val name:String
)