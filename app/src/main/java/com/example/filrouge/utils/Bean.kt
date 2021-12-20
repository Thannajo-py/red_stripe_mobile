package com.example.filrouge

import com.google.gson.annotations.SerializedName
import java.io.Serializable

interface CommonDataArrayList{
        val designer: ArrayList<String>
        val artist: ArrayList<String>
        val publisher: ArrayList<String>
        val playingMod: ArrayList<String>
        val language: ArrayList<String>
}


/**
 * Generic Container for modified and new Game Type object
 */
data class ApiResponse(
        @SerializedName("games")
        val game:ArrayList<GameBean>,
        @SerializedName("add_ons")
        val addOn: ArrayList<AddOnBean>,
        @SerializedName("multi_add_ons")
        val multiAddOn: ArrayList<MultiAddOnBean>
        ):Serializable

/**
 * Container for deleted item object
 */
data class ApiDelete(
        @SerializedName("games")
        val game:ArrayList<DeletedObject>,
        @SerializedName("add_ons")
        val addOn: ArrayList<DeletedObject>,
        @SerializedName("multi_add_ons")
        val multiAddOn: ArrayList<DeletedObject>
        ):Serializable


/**
 * Server API Response Bean container
 */
data class ApiReceive(
        @SerializedName("games")
        val game:ArrayList<GameBean>?,
        @SerializedName("add_ons")
        val addOn: ArrayList<AddOnBean>?,
        @SerializedName("multi_add_ons")
        val multiAddOn: ArrayList<MultiAddOnBean>?,
        @SerializedName("deleted_games")
        val deletedGame: ArrayList<Int>?,
        @SerializedName("deleted_add_ons")
        val deletedAddOn:ArrayList<Int>?,
        @SerializedName("deleted_multi_add_ons")
        val deletedMultiAddOn:ArrayList<Int>?,
        val timestamp: Float
        ){
        /**
         * Specific method to ensure answer contains at least one modification
         */
        fun isNullOrEmpty() = arrayListOf(
                        this.game,
                        this.addOn,
                        this.multiAddOn,
                        this.deletedAddOn,
                        this.deletedGame,
                        this.deletedMultiAddOn
                ).all { it.isNullOrEmpty() }
}

/**
 * Deleted object Id
 */
data class DeletedObject(val id:Int)


/**
 * Game model for Server API communication
 */
data class GameBean(
        val id: Int?,
        val name: String,
        @SerializedName("player_min")
        val playerMin: Int?,
        val player_max: Int?,
        val playing_time: String?,
        val difficulty: String?,
        override val designer: ArrayList<String>,
        override val artist: ArrayList<String>,
        override val publisher: ArrayList<String>,
        val bgg_link: String?,
        override val playingMod: ArrayList<String>,
        override val language: ArrayList<String>,
        val age: Int?,
        val buying_price:Int?,
        val stock: Int?,
        val max_time: Int?,
        val by_player: Boolean?,
        val tag: ArrayList<String>,
        val topic:ArrayList<String>,
        val mechanism:ArrayList<String>,
        @SerializedName("add_on")
        val addOn: ArrayList<String>,
        @SerializedName("multi_add_on")
        val multiAddOn: ArrayList<String>,
        val external_img:String?,
        val picture:String?,
): CommonDataArrayList, Serializable


/**
 * AddOn model for Server API communication
 */
data class AddOnBean(
        val id: Int?,
        val name: String,
        val player_min: Int?,
        val player_max: Int?,
        val playing_time: String?,
        val difficulty: String?,
        override val designer: ArrayList<String>,
        override val artist: ArrayList<String>,
        override val publisher: ArrayList<String>,
        val bgg_link: String?,
        override val playingMod: ArrayList<String>,
        override val language: ArrayList<String>,
        val age: Int?,
        val buying_price:Int?,
        val stock: Int?,
        val max_time: Int?,
        var game: String?,
        val external_img:String?,
        val picture:String?,
):CommonDataArrayList, Serializable


/**
 * MultiAddOn model for Server API communication
 */
data class MultiAddOnBean(
        val id: Int?,
        val name: String,
        val player_min: Int?,
        val player_max: Int?,
        val playing_time: String?,
        val difficulty: String?,
        override val designer: ArrayList<String>,
        override val artist: ArrayList<String>,
        override val publisher: ArrayList<String>,
        val bgg_link: String?,
        override val playingMod: ArrayList<String>,
        override val language: ArrayList<String>,
        val age: Int?,
        val buying_price:Int?,
        val stock: Int?,
        val max_time: Int?,
        val game: ArrayList<String>,
        val external_img:String?,
        val picture:String?,
): CommonDataArrayList, Serializable


/**
 * Container to send change object to server API
 */
class SendApiChange(
        val login:String,
         val password:String,
        val addedList:ApiResponse,
        val modifiedList:ApiResponse,
        val deletedList:ApiDelete,
        val timestamp:Float
        )


/**
 * Global container to send API change
 */
class ApiBody(val body:SendApiChange)


/**
 * A data class containing all searched element
 */
data class SearchQuery(
        val name:String?,
        val designer:String?,
        val artist:String?,
        val publisher:String?,
        val playerMin:Int?,
        val playerMax:Int?,
        val maxTime:Int?,
        val difficulty: String?,
        val age:Int?,
        val playingMod: String?,
        val language:String?,
        val tag:String?,
        val topic:String?,
        val mechanism:String?,
):Serializable


/**
 * Global container for BGA API
 */
data class BgaApi( val games: ArrayList<BgaGameBean>? )


/**
 * Game type representation of BGA API answer
 */
data class BgaGameBean(
     val id: String,
     val name: String,
     val url: String,
     val min_players: Int?,
     val max_players: Int?,
     val max_playtime: Int?,
     val min_age: Int?,
     val image_url: String?,
     val mechanics: ArrayList<IdObjectBean>,
     val categories: ArrayList<IdObjectBean>,
     val artists: ArrayList<String>,
     val primary_publisher: NamedObjectBean?,
     val primary_designer: NamedObjectBean?,
     val type: String?,
): Serializable


/**
 * A class representing object with id Only
 */
data class IdObjectBean(val id: String):Serializable


/**
 * A class representing object with name Only
 */
data class NamedObjectBean(val name: String):Serializable


/**
 * A class representing object with id and name
 */
data class NamedResultBean(val id:String, val name:String)


/**
 * BGA API container answer of all mechanics
 */
data class MechanicApiResult(val mechanics:ArrayList<NamedResultBean>)

/**
 * BGA API container answer of all categories
 */
data class CategoriesApiResult(val categories:ArrayList<NamedResultBean>)


/**
 * Common field between game AddOn and MultiAddOn
 */
data class CommonAddObject(
        val name: String,
        val player_min: Int?,
        val player_max: Int?,
        val playing_time: String?,
        override val designer: ArrayList<String>,
        override val artist: ArrayList<String>,
        override val publisher: ArrayList<String>,
        val bgg_link: String?,
        override val playingMod: ArrayList<String>,
        override val language: ArrayList<String>,
        val age: Int?,
        val buying_price: Int?,
        val stock: Int?,
        val max_time: Int?,
        val external_image: String?,
):CommonDataArrayList
