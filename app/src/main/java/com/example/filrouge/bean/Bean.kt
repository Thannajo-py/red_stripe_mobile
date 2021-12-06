package com.example.filrouge

import java.io.Serializable


fun listEquality(list:ArrayList<String>, otherList:ArrayList<String>) =
        list.size == otherList.size && list.all{otherList.contains(it)}


data class ApiResponse(
        val games:ArrayList<GameBean>,
        val add_ons: ArrayList<AddOnBean>,
        val multi_add_ons: ArrayList<MultiAddOnBean>
        ):Serializable


data class ApiDelete(
        val games:ArrayList<DeletedObject>,
        val add_ons: ArrayList<DeletedObject>,
        val multi_add_ons: ArrayList<DeletedObject>
        ):Serializable


data class ApiReceive(
        val games:ArrayList<GameBean>?,
        val add_ons: ArrayList<AddOnBean>?,
        val multi_add_ons: ArrayList<MultiAddOnBean>?,
        val deleted_games: ArrayList<Int>?,
        val deleted_add_ons:ArrayList<Int>?,
        val deleted_multi_add_ons:ArrayList<Int>?,
        val timestamp: Float
        )


data class DeletedObject(val id:Int)


class GameBean(
        id: Int?,
        name: String,
        player_min: Int?,
        player_max: Int?,
        playing_time: String?,
        difficulty: String?,
        designers: ArrayList<String>,
        artists: ArrayList<String>,
        publishers: ArrayList<String>,
        bgg_link: String?,
        playing_mode: ArrayList<String>,
        language: ArrayList<String>,
        age: Int?,
        buying_price:Int?,
        stock: Int?,
        max_time: Int?,
        val by_player: Boolean?,
        val tags: ArrayList<String>,
        val topics:ArrayList<String>,
        val mechanism:ArrayList<String>,
        val add_on: ArrayList<String>,
        val multi_add_on: ArrayList<String>,
        external_img: String?,
        picture: String?
): CommonBase(id,
        name,
        player_min,
        player_max,
        playing_time,
        difficulty,
        designers,
        artists,
        publishers,
        bgg_link,
        playing_mode,
        language,
        age,
        buying_price,
        stock,
        max_time,
        external_img,
        picture
),Serializable{
        override fun equals(other: Any?): Boolean {
                if(other !is GameBean){
                        return false
                }
                else{
                        return this.id == other.id && this.name == other.name &&
                                this.player_min == other.player_min &&
                                this.player_max == other.player_max &&
                                this.playing_time == other.playing_time &&
                                this.difficulty == other.difficulty &&
                                this.bgg_link == other.bgg_link &&
                                this.age == other.age &&
                                this.buying_price == other.buying_price &&
                                this.stock == other.stock &&
                                this.max_time == other.max_time &&
                                this.external_img == other.external_img &&
                                listEquality(this.designers, other.designers) &&
                                listEquality(this.artists, other.artists) &&
                                listEquality(this.publishers, other.publishers) &&
                                listEquality(this.playing_mode, other.playing_mode) &&
                                listEquality(this.language, other.language) &&
                                listEquality(this.add_on, other.add_on) &&
                                listEquality(this.multi_add_on, other.multi_add_on)
                }
        }
}


class AddOnBean(
        id: Int?,
        name: String,
        player_min: Int?,
        player_max: Int?,
        playing_time: String?,
        difficulty: String?,
        designers: ArrayList<String>,
        artists: ArrayList<String>,
        publishers: ArrayList<String>,
        bgg_link: String?,
        playing_mode: ArrayList<String>,
        language: ArrayList<String>,
        age: Int?,
        buying_price:Int?,
        stock: Int?,
        max_time: Int?,
        var game: String?,
        external_img: String?,
        picture: String?
): CommonBase(
        id,
        name,
        player_min,
        player_max,
        playing_time,
        difficulty,
        designers,
        artists,
        publishers,
        bgg_link,
        playing_mode,
        language,
        age,
        buying_price,
        stock,
        max_time,
        external_img,
        picture
),Serializable {
        override fun equals(other: Any?): Boolean {
                if(other !is AddOnBean){
                        return false
                }
                else{
                        return this.id == other.id && this.name == other.name &&
                                this.player_min == other.player_min &&
                                this.player_max == other.player_max &&
                                this.playing_time == other.playing_time &&
                                this.difficulty == other.difficulty &&
                                this.bgg_link == other.bgg_link &&
                                this.age == other.age &&
                                this.buying_price == other.buying_price &&
                                this.stock == other.stock &&
                                this.max_time == other.max_time &&
                                this.external_img == other.external_img &&
                                this.game == other.game &&
                                listEquality(this.designers, other.designers) &&
                                listEquality(this.artists, other.artists) &&
                                listEquality(this.publishers, other.publishers) &&
                                listEquality(this.playing_mode, other.playing_mode) &&
                                listEquality(this.language, other.language)
                }
        }
}


class MultiAddOnBean(
        id: Int?,
        name: String,
        player_min: Int?,
        player_max: Int?,
        playing_time: String?,
        difficulty: String?,
        designers: ArrayList<String>,
        artists: ArrayList<String>,
        publishers: ArrayList<String>,
        bgg_link: String?,
        playing_mode: ArrayList<String>,
        language: ArrayList<String>,
        age: Int?,
        buying_price:Int?,
        stock: Int?,
        max_time: Int?,
        val games: ArrayList<String>,
        external_img: String?,
        picture: String?
): CommonBase(id,
        name,
        player_min,
        player_max,
        playing_time,
        difficulty,
        designers,
        artists,
        publishers,
        bgg_link,
        playing_mode,
        language,
        age,
        buying_price,
        stock,
        max_time,
        external_img,
        picture),Serializable
{
        override fun equals(other: Any?): Boolean {
                if(other !is MultiAddOnBean){
                        return false
                }
                else{
                        return this.id == other.id && this.name == other.name &&
                                this.player_min == other.player_min &&
                                this.player_max == other.player_max &&
                                this.playing_time == other.playing_time &&
                                this.difficulty == other.difficulty &&
                                this.bgg_link == other.bgg_link &&
                                this.age == other.age &&
                                this.buying_price == other.buying_price &&
                                this.stock == other.stock &&
                                this.max_time == other.max_time &&
                                this.external_img == other.external_img &&
                                listEquality(this.designers, other.designers) &&
                                listEquality(this.artists, other.artists) &&
                                listEquality(this.publishers, other.publishers) &&
                                listEquality(this.playing_mode, other.playing_mode) &&
                                listEquality(this.language, other.language) &&
                                listEquality(this.games, other.games)
                }
        }
}


open class CommonBase(
        val id: Int?,
        val name: String,
        val player_min: Int?,
        val player_max: Int?,
        val playing_time: String?,
        val difficulty: String?,
        val designers: ArrayList<String>,
        val artists: ArrayList<String>,
        val publishers: ArrayList<String>,
        val bgg_link: String?,
        val playing_mode: ArrayList<String>,
        val language: ArrayList<String>,
        val age: Int?,
        val buying_price:Int?,
        val stock: Int?,
        val max_time: Int?,
        val external_img:String?,
        val picture:String?,
):Serializable


class SendApiChange(
        val login:String,
         val password:String,
        val addedList:ApiResponse,
        val modifiedList:ApiResponse,
        val deletedList:ApiDelete,
        val timestamp:Float
        )


class ApiBody(val body:SendApiChange)


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


data class BgaApi( val games: ArrayList<BgaGameBean>? )


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


data class IdObjectBean(val id: String):Serializable


data class NamedObjectBean(val name: String):Serializable


data class NamedResultBean(val id:String, val name:String)


data class MechanicApiResult(val mechanics:ArrayList<NamedResultBean>)


data class CategoriesApiResult(val categories:ArrayList<NamedResultBean>)

data class CommonAddObject(
        val name: String,
        val player_min: Int?,
        val player_max: Int?,
        val playing_time: String?,
        val designers: ArrayList<String>,
        val artists: ArrayList<String>,
        val publishers: ArrayList<String>,
        val bgg_link: String?,
        val playing_mode: ArrayList<String>,
        val language: ArrayList<String>,
        val age: Int?,
        val buying_price: Int?,
        val stock: Int?,
        val max_time: Int?,
        val external_image: String?,
)

data class GameAddSpecific(
        val by_player: Boolean,
        val tags: ArrayList<String>,
        val topics: ArrayList<String>,
        val mechanism: ArrayList<String>,
)