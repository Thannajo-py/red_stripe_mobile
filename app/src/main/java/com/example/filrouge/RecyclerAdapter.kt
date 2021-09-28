package com.example.filrouge

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.filrouge.bean.UserTableBean
import com.example.filrouge.databinding.*
import java.io.File


val allGames = ArrayList<GameBean>()

val allAddOns = ArrayList<AddOnBean>()

val allMultiAddOns = ArrayList<MultiAddOnBean>()

val allImages = AllImages(mutableSetOf())

var currentUser: UserTableBean? = null

var API_URL:String? = null
var API_STATIC:String? = null

var addOnGame:GameBean? = null

var isLocal:Boolean = true




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


}


interface GenericListener{
    fun onGenericClick(datum:String, type: String)

}


class UserBeanAdapter (val client: UserListener, val addedUserList: ArrayList<UserTableBean>) : ListAdapter<UserTableBean, UserBeanAdapter.ViewHolder>(UserTableBeanComparator()){
    class ViewHolder(val bind: AccountListBinding) : RecyclerView.ViewHolder(bind.root)

    class UserTableBeanComparator: DiffUtil.ItemCallback<UserTableBean>() {
        override fun areItemsTheSame(oldItem: UserTableBean, newItem: UserTableBean) = oldItem === newItem
        override fun areContentsTheSame(oldItem: UserTableBean, newItem: UserTableBean) = oldItem == newItem
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(AccountListBinding.inflate(
        LayoutInflater.from(parent.context)))


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val datum = getItem(position)
        holder.bind.cbSelect.text = datum.login
        holder.bind.cbSelect.isChecked = addedUserList.contains(datum)
        holder.bind.cbSelect.setOnClickListener { client.onUserClick(datum, position)
           }
    }



}

interface UserListener{
    fun onUserClick(datum:UserTableBean, position:Int)

}


open class GenericAdapterWithCheckBox<T:CommonBase> (val data: ArrayList<T>, val client: OnGenericCbListListener,
                                                     val addedObject:ArrayList<T>)
    : RecyclerView.Adapter<GenericAdapterWithCheckBox.ViewHolder>() {


    class ViewHolder(val bind:GameListCbBinding) : RecyclerView.ViewHolder(bind.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(GameListCbBinding.inflate(
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
        holder.bind.cbObject.isChecked = addedObject.contains(datum)
        holder.bind.cvGameListCb.setOnClickListener { client.onElementClick(datum, position) }
        holder.bind.cbObject.setOnClickListener { client.onElementClick(datum, position) }
    }

    override fun getItemCount() = data.size



}
interface OnGenericCbListListener{
    fun onElementClick(datum:CommonBase?, position:Int)
}


class GenericTypeCbAdapter (val data: ArrayList<String>, val client: GenericCbListener, val type:String,
val addedGeneric:ArrayList<String>) : RecyclerView.Adapter<GenericTypeCbAdapter.ViewHolder>(){
    class ViewHolder(val bind: NameListCbBinding) : RecyclerView.ViewHolder(bind.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(NameListCbBinding.inflate(
        LayoutInflater.from(parent.context)))


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val datum = data[position]
        holder.bind.tvName.text = datum
        holder.bind.cbName.isChecked = addedGeneric.contains(datum)
        holder.bind.llNameList.setOnClickListener { client.onGenericClick(datum, type, position) }
        holder.bind.cbName.setOnClickListener { client.onGenericClick(datum, type, position) }
    }


    override fun getItemCount() = data.size


}

interface GenericCbListener{
    fun onGenericClick(datum:String, type: String, poisition:Int)

}

open class GameAddOnAdapterWithCheckBox(val data: ArrayList<GameBean>, val client: OnGenericCbListListener)
    : RecyclerView.Adapter<GameAddOnAdapterWithCheckBox.ViewHolder>() {


    class ViewHolder(val bind:GameListCbBinding) : RecyclerView.ViewHolder(bind.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(GameListCbBinding.inflate(
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
        holder.bind.cbObject.isChecked = addOnGame == datum
        holder.bind.cvGameListCb.setOnClickListener { client.onElementClick(datum, position) }
        holder.bind.cbObject.setOnClickListener { client.onElementClick(datum, position) }
    }

    override fun getItemCount() = data.size



}


