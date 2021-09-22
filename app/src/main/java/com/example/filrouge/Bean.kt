package com.example.filrouge

import java.io.Serializable

data class ApiResponse( val games:ArrayList<GameBean>, val add_ons: ArrayList<AddOnBean>, val multi_add_ons: ArrayList<MultiAddOnBean>):Serializable
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
        val add_on: ArrayList<AddOnBean>,
        val multi_add_on: ArrayList<MultiAddOnBean>,
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
        picture),Serializable{
        override fun equals(other: Any?): Boolean {
                if(other !is GameBean){
                        return false
                }
                else{
                        return this.id == other.id && this.name == other.name

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
        val game: String?,
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
                if(other !is AddOnBean){
                        return false
                }
                else{
                        return this.id == other.id && this.name == other.name

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
                        return this.id == other.id && this.name == other.name

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

class SendApiChange(val login:String, val password:String, val addedList:ApiResponse, val modifiedList:ApiResponse, val deletedList:ApiResponse)

class ApiBody(val body:SendApiChange)

class AllImages(val list_of_images: MutableSet<String>)

class AllUsers(val listOfUsers: ArrayList<UserBean>)

class UserBean (val login:String, val password:String, val permission:PermissionBean)

class PermissionBean ( val add:Boolean, val change:Boolean, val delete:Boolean, val synchronize:Boolean, val addAccount:Boolean, val deleteAccount: Boolean)

