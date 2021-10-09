package com.example.filrouge

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.filrouge.bean.*
import com.example.filrouge.databinding.*
import java.io.File





var currentUser: UserTableBean? = null

var API_URL:String? = null
var API_STATIC:String? = null

var addOnGame:GameBean? = null

var isLocal:Boolean = true




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





open class GenericListAdapter<T:CommonGame> (val client: OnGenericListAdapterListener) : ListAdapter<CommonGame, GenericListAdapter.ViewHolder>(GameComparator()) {


    class ViewHolder(val bind:GameListBinding) : RecyclerView.ViewHolder(bind.root)

    class GameComparator<T:CommonGame>: DiffUtil.ItemCallback<T>() {
        override fun areItemsTheSame(oldItem: T, newItem: T) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: T, newItem: T) = oldItem.id == newItem.id
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(GameListBinding.inflate(LayoutInflater.from(parent.context)))

    override fun onBindViewHolder(holder: GenericListAdapter.ViewHolder, position: Int) {
        val datum = getItem(position)
        holder.bind.tvName.text = datum.name
        holder.bind.tvDesigner.text = datum?.designer?:""
        datum.image?.run{

            val file = File(holder.bind.tvDesigner.context.filesDir, this)
            val compressedBitMap = BitmapFactory.decodeByteArray(file.readBytes(),0,file.readBytes().size)
            holder.bind.ivPicture.setImageBitmap(compressedBitMap)
        }?:run{
            holder.bind.ivPicture.setImageBitmap(null)
        }
        holder.bind.cvGameList.setOnClickListener { client.onElementClick(datum) }
    }





}

interface OnGenericListAdapterListener{
    fun onElementClick(datum:CommonGame)
}

interface OnGenericStringListAdapterListener{
    fun onElementClick(datum:ID, type:String)
}

open class GenericStringListAdapter<T: ID> (val client: OnGenericStringListAdapterListener, val type:String) : ListAdapter<ID, GenericStringListAdapter.ViewHolder>(GameComparator()) {


    class ViewHolder(val bind:NameListBinding) : RecyclerView.ViewHolder(bind.root)

    class GameComparator<T:ID>: DiffUtil.ItemCallback<T>() {
        override fun areItemsTheSame(oldItem: T, newItem: T) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: T, newItem: T) = oldItem.name == newItem.name
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(NameListBinding.inflate(LayoutInflater.from(parent.context)))

    override fun onBindViewHolder(holder: GenericStringListAdapter.ViewHolder, position: Int) {
        val datum = getItem(position)
        holder.bind.tvName.text = datum.name
        holder.bind.cvNameList.setOnClickListener { client.onElementClick(datum, type) }
    }

}


class GenericIDListCbAdapter<T:ID> (val client: GenericIDCbListener, val type:String,
                            val addedGeneric:ArrayList<String>) : ListAdapter<ID, GenericIDListCbAdapter.ViewHolder>(ItemComparator()){
    class ViewHolder(val bind: NameListCbBinding) : RecyclerView.ViewHolder(bind.root)

    class ItemComparator<T:ID>: DiffUtil.ItemCallback<T>() {
        override fun areItemsTheSame(oldItem: T, newItem: T) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: T, newItem: T) = oldItem.name == newItem.name
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(NameListCbBinding.inflate(
        LayoutInflater.from(parent.context)))


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val datum = getItem(position)
        holder.bind.tvName.text = datum.name
        holder.bind.cbName.isChecked = addedGeneric.contains(datum.name)
        holder.bind.llNameList.setOnClickListener { client.onGenericClick(datum.name, type, holder.bind.cbName) }
        holder.bind.cbName.setOnClickListener { client.onGenericClick(datum.name, type, holder.bind.cbName) }

    }



}

interface GenericIDCbListener{
    fun onGenericClick(name:String, type: String, cb:CheckBox)

}

class GenericCommonGameListCbAdapter<T:CommonGame> (val client: GenericCommonGameCbListener,
                                    val addedGeneric:ArrayList<T>) : ListAdapter<CommonGame, GenericCommonGameListCbAdapter.ViewHolder>(ItemComparator()){
    class ViewHolder(val bind: GameListCbBinding) : RecyclerView.ViewHolder(bind.root)

    class ItemComparator<T:CommonGame>: DiffUtil.ItemCallback<T>() {
        override fun areItemsTheSame(oldItem: T, newItem: T) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: T, newItem: T) = oldItem.name == newItem.name
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(GameListCbBinding.inflate(
        LayoutInflater.from(parent.context)))


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val datum = getItem(position)
        holder.bind.tvName.text = datum.name
        holder.bind.tvDesigner.text = datum.designer
        holder.bind.cbObject.isChecked = addedGeneric.contains(datum)
        datum.image?.run{

            val file = File(holder.bind.tvDesigner.context.filesDir, this)
            val compressedBitMap = BitmapFactory.decodeByteArray(file.readBytes(),0,file.readBytes().size)
            holder.bind.ivPicture.setImageBitmap(compressedBitMap)
        }?:run{
            holder.bind.ivPicture.setImageBitmap(null)
        }
        holder.bind.rootGames.setOnClickListener { client.onGenericClick(datum, holder.bind.cbObject) }
        holder.bind.cbObject.setOnClickListener { client.onGenericClick(datum, holder.bind.cbObject) }
    }



}

interface GenericCommonGameCbListener{
    fun onGenericClick(datum:CommonGame, view: CheckBox)

}

class OneToOneListCbAdapter<T:OneToOne> (val client: GenericOneToOneListener,
                             var addedGame:T?) : ListAdapter<T, OneToOneListCbAdapter.ViewHolder>(ItemComparator()){
    class ViewHolder(val bind: NameListBinding) : RecyclerView.ViewHolder(bind.root)

    class ItemComparator<T:OneToOne>: DiffUtil.ItemCallback<T>() {
        override fun areItemsTheSame(oldItem: T, newItem: T) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: T, newItem: T) = oldItem.name == newItem.name
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(NameListBinding.inflate(
        LayoutInflater.from(parent.context)))


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val datum = getItem(position)
        holder.bind.tvName.text = datum.name


        holder.bind.rootGames.setOnClickListener { client.onGenericClick(datum) }

    }



}

interface GenericOneToOneListener{
    fun onGenericClick(datum:OneToOne)

}