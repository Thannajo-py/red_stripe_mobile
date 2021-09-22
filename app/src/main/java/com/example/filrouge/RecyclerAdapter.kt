package com.example.filrouge

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.example.filrouge.databinding.AccountListBinding
import com.example.filrouge.databinding.GameListBinding
import com.example.filrouge.databinding.NameListBinding
import java.io.File


val allGames = ArrayList<GameBean>()
val addedGames = ArrayList<GameBean>()
val modifiedGames = ArrayList<GameBean>()
val deletedGames = ArrayList<GameBean>()

val allAddOns = ArrayList<AddOnBean>()
val addedAddOns = ArrayList<AddOnBean>()
val modifiedAddOns = ArrayList<AddOnBean>()
val deletedAddOns = ArrayList<AddOnBean>()

val allMultiAddOns = ArrayList<MultiAddOnBean>()
val addedMultiAddOns = ArrayList<MultiAddOnBean>()
val modifiedMultiAddOns = ArrayList<MultiAddOnBean>()
val deletedMultiAddOns = ArrayList<MultiAddOnBean>()

val allImages = AllImages(mutableSetOf())
val allUsers = AllUsers(ArrayList())

var currentUser:UserBean? = null

var API_URL:String? = null
var API_STATIC:String? = null




open class GenericAdapter<T:CommonBase> (val data: ArrayList<T>, val client: OnGenericListListener) : RecyclerView.Adapter<GenericAdapter.ViewHolder>() {


    class ViewHolder(val bind:GameListBinding) : RecyclerView.ViewHolder(bind.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(GameListBinding.inflate(
        LayoutInflater.from(parent.context)))


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val datum = data[position]
        holder.bind.tvName.text = datum.name
        holder.bind.tvDesigner.text = if (datum.designers.size > 0) datum.designers[0] else ""
        if (allImages.list_of_images.contains(datum.name)){
            val file = File(holder.bind.tvDesigner.context.filesDir, datum.name)
            val compressedBitMap = BitmapFactory.decodeByteArray(file.readBytes(),0,file.readBytes().size)
            holder.bind.ivPicture.setImageBitmap(compressedBitMap)
        }else{
            holder.bind.ivPicture.setImageBitmap(null)
        }
        holder.bind.cvGameList.setOnClickListener { client.onElementClick(datum) }
    }

    override fun getItemCount() = data.size



}
interface OnGenericListListener{
    fun onElementClick(datum:CommonBase?)
}





class MultiAddOnGameAdapter (val data: ArrayList<String>, val client: OnGenericListListener) : RecyclerView.Adapter<MultiAddOnGameAdapter.ViewHolder>(){
    class ViewHolder(val bind:GameListBinding) : RecyclerView.ViewHolder(bind.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(GameListBinding.inflate(
        LayoutInflater.from(parent.context)))


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val datum = data[position]
        holder.bind.tvName.text = datum
        val gameNameTry = allGames.filter{it.name == datum}
        val gameName = if (gameNameTry.size == 1) gameNameTry[0] else null
        if (gameName != null && allImages.list_of_images.contains(gameName.name)){
            val file = File(holder.bind.tvDesigner.context.filesDir, gameName.name)
            val compressedBitMap = BitmapFactory.decodeByteArray(file.readBytes(),0,file.readBytes().size)
            holder.bind.ivPicture.setImageBitmap(compressedBitMap)
        }else{
            holder.bind.ivPicture.setImageBitmap(null)
        }

        holder.bind.cvGameList.setOnClickListener { client.onElementClick(gameName) }
    }


    override fun getItemCount() = data.size


}


class GenericTypeAdapter (val data: ArrayList<String>, val client: CommonType, val type:String) : RecyclerView.Adapter<GenericTypeAdapter.ViewHolder>(){
    class ViewHolder(val bind: NameListBinding) : RecyclerView.ViewHolder(bind.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(NameListBinding.inflate(
        LayoutInflater.from(parent.context)))


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val datum = data[position]
        holder.bind.tvName.text = datum
        holder.bind.rootGames.setOnClickListener { client.onGenericClick(datum, type) }
    }


    override fun getItemCount() = data.size

    interface GenericListener{
        fun onGenericClick(datum:String, type: String)

    }
}

class UserBeanAdapter (val data: ArrayList<UserBean>, val client: UserListener, val addedUserList: ArrayList<UserBean>) : RecyclerView.Adapter<UserBeanAdapter.ViewHolder>(){
    class ViewHolder(val bind: AccountListBinding) : RecyclerView.ViewHolder(bind.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(AccountListBinding.inflate(
        LayoutInflater.from(parent.context)))


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val datum = data[position]
        holder.bind.cbSelect.text = datum.login
        holder.bind.cbSelect.isChecked = addedUserList.contains(datum)
        holder.bind.cbSelect.setOnClickListener { client.onUserClick(datum, position)
           }
    }


    override fun getItemCount() = data.size


}

interface UserListener{
    fun onUserClick(datum:UserBean, position:Int)

}
