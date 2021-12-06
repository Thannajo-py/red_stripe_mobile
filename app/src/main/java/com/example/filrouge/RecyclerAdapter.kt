package com.example.filrouge

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.filrouge.activity.APISearchActivity
import com.example.filrouge.bean.*
import com.example.filrouge.databinding.*
import com.squareup.picasso.Picasso
import java.io.File


var currentUser: UserTableBean? = null
var API_URL:String? = null
var API_STATIC:String? = null
var addOnGame:GameBean? = null
var isLocal:Boolean = true


class UserBeanAdapter (private val client: UserListener, private val addedUserList: ArrayList<UserTableBean>)
    : ListAdapter<UserTableBean, UserBeanAdapter.ViewHolder>(UserTableBeanComparator()){

    class ViewHolder(val bind: AccountListBinding) : RecyclerView.ViewHolder(bind.root)

    class UserTableBeanComparator: DiffUtil.ItemCallback<UserTableBean>() {
        override fun areItemsTheSame(oldItem: UserTableBean, newItem: UserTableBean) =
            oldItem === newItem
        override fun areContentsTheSame(oldItem: UserTableBean, newItem: UserTableBean) =
            oldItem == newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(AccountListBinding.inflate(LayoutInflater.from(parent.context)))

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


open class GenericListAdapter(private val client: OnGenericListAdapterListener)
    : ListAdapter<CommonGame, GenericListAdapter.ViewHolder>(GameComparator()) {

    class ViewHolder(val bind:GameListBinding) : RecyclerView.ViewHolder(bind.root)

    class GameComparator<T:CommonGame>: DiffUtil.ItemCallback<T>() {
        override fun areItemsTheSame(oldItem: T, newItem: T) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: T, newItem: T) = oldItem.id == newItem.id
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(GameListBinding.inflate(LayoutInflater.from(parent.context)))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val datum = getItem(position)
        holder.bind.tvName.text = datum.name
        holder.bind.tvDesigner.text = datum?.designer?:""
        datum.image?.run{

            val file = File(holder.bind.tvDesigner.context.filesDir, this)
            val compressedBitMap = BitmapFactory.decodeByteArray(
                file.readBytes(),
                0,
                file.readBytes().size)
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


open class GenericStringListAdapter(private val client: OnGenericStringListAdapterListener, val type:String)
    : ListAdapter<ID, GenericStringListAdapter.ViewHolder>(GameComparator()) {

    class ViewHolder(val bind:NameListBinding) : RecyclerView.ViewHolder(bind.root)

    class GameComparator<T:ID>: DiffUtil.ItemCallback<T>() {
        override fun areItemsTheSame(oldItem: T, newItem: T) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: T, newItem: T) = oldItem.name == newItem.name
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(NameListBinding.inflate(LayoutInflater.from(parent.context)))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val datum = getItem(position)
        holder.bind.tvName.text = datum.name
        holder.bind.cvNameList.setOnClickListener { client.onElementClick(datum, type) }
    }
}


class GenericIDListCbAdapter (
    private val client: GenericIDCbListener,
    val type:String,
    private val addedGeneric:ArrayList<String>)
    : ListAdapter<ID, GenericIDListCbAdapter.ViewHolder>(ItemComparator()){
    class ViewHolder(val bind: NameListCbBinding) : RecyclerView.ViewHolder(bind.root)

    class ItemComparator<T:ID>: DiffUtil.ItemCallback<T>() {
        override fun areItemsTheSame(oldItem: T, newItem: T) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: T, newItem: T) = oldItem.name == newItem.name
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
    = ViewHolder(NameListCbBinding.inflate(
        LayoutInflater.from(parent.context)))


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val datum = getItem(position)
        holder.bind.tvName.text = datum.name
        holder.bind.cbName.isChecked = addedGeneric.contains(datum.name)
        holder.bind.llNameList.setOnClickListener {
            client.onGenericClick(datum.name, type, holder.bind.cbName)
        }
        holder.bind.cbName.setOnClickListener {
            client.onGenericClick(datum.name, type, holder.bind.cbName)
        }

    }
}


interface GenericIDCbListener{
    fun onGenericClick(name:String, type: String, cb:CheckBox)
}


class GenericIDListCbAdapterId (
    private val client: GenericIDCbListenerId,
    val type:String,
    private val addedGeneric:ArrayList<Long>)
    : ListAdapter<ID, GenericIDListCbAdapterId.ViewHolder>(ItemComparator()){
    class ViewHolder(val bind: NameListCbBinding) : RecyclerView.ViewHolder(bind.root)

    class ItemComparator<T:ID>: DiffUtil.ItemCallback<T>() {
        override fun areItemsTheSame(oldItem: T, newItem: T) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: T, newItem: T) = oldItem.name == newItem.name
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
            = ViewHolder(NameListCbBinding.inflate(
        LayoutInflater.from(parent.context)))


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val datum = getItem(position)
        holder.bind.tvName.text = datum.name
        holder.bind.cbName.isChecked = addedGeneric.contains(datum.id)
        holder.bind.llNameList.setOnClickListener {
            client.onGenericClick(datum.id, type, holder.bind.cbName)
        }
        holder.bind.cbName.setOnClickListener {
            client.onGenericClick(datum.id, type, holder.bind.cbName)
        }

    }
}


interface GenericIDCbListenerId{
    fun onGenericClick(id:Long, type: String, cb:CheckBox)
}


class GenericCommonGameListCbAdapter<T:CommonGame> (
    private val client: GenericCommonGameCbListener,
    private val addedGeneric:ArrayList<T>,
    val type:String
    ) : ListAdapter<CommonGame, GenericCommonGameListCbAdapter.ViewHolder>(ItemComparator()){
    class ViewHolder(val bind: GameListCbBinding) : RecyclerView.ViewHolder(bind.root)

    class ItemComparator<T:CommonGame>: DiffUtil.ItemCallback<T>() {
        override fun areItemsTheSame(oldItem: T, newItem: T) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: T, newItem: T) = oldItem.name == newItem.name
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
    = ViewHolder(GameListCbBinding.inflate(LayoutInflater.from(parent.context)))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val datum = getItem(position)
        holder.bind.tvName.text = datum.name
        holder.bind.tvDesigner.text = datum.designer
        holder.bind.cbObject.isChecked = addedGeneric.contains(datum)
        datum.image?.run{

            val file = File(holder.bind.tvDesigner.context.filesDir, this)
            val compressedBitMap = BitmapFactory.decodeByteArray(
                file.readBytes(),
                0,
                file.readBytes().size
            )
            holder.bind.ivPicture.setImageBitmap(compressedBitMap)
        }?:run{
            holder.bind.ivPicture.setImageBitmap(null)
        }
        holder.bind.rootGames.setOnClickListener {
            client.onGenericClick(datum, holder.bind.cbObject, type)
        }
        holder.bind.cbObject.setOnClickListener {
            client.onGenericClick(datum, holder.bind.cbObject, type)
        }
    }
}


interface GenericCommonGameCbListener{
    fun onGenericClick(datum:CommonGame, view: CheckBox, type:String)
}


class OneToOneListCbAdapter<T:OneToOne> (private val client: GenericOneToOneListener)
    : ListAdapter<T, OneToOneListCbAdapter.ViewHolder>(ItemComparator()){
    class ViewHolder(val bind: NameListBinding) : RecyclerView.ViewHolder(bind.root)

    class ItemComparator<T:OneToOne>: DiffUtil.ItemCallback<T>() {
        override fun areItemsTheSame(oldItem: T, newItem: T) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: T, newItem: T) = oldItem.name == newItem.name
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
    = ViewHolder(NameListBinding.inflate(LayoutInflater.from(parent.context)))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val datum = getItem(position)
        holder.bind.tvName.text = datum.name
        holder.bind.rootGames.setOnClickListener { client.onGenericClick(datum) }
    }
}


interface GenericOneToOneListener{
    fun onGenericClick(datum:OneToOne)

}


open class BgaListAdapter (private val client: APISearchActivity)
    : ListAdapter<BgaGameBean, BgaListAdapter.ViewHolder>(GameComparator()) {

    class ViewHolder(val bind: GameListBinding) : RecyclerView.ViewHolder(bind.root)

    class GameComparator : DiffUtil.ItemCallback<BgaGameBean>() {
        override fun areItemsTheSame(oldItem: BgaGameBean, newItem: BgaGameBean) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: BgaGameBean, newItem: BgaGameBean) =
            oldItem.id == newItem.id
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(GameListBinding.inflate(LayoutInflater.from(parent.context)))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val datum = getItem(position)
        holder.bind.tvName.text = datum.name
        holder.bind.tvDesigner.text = datum?.primary_designer?.name ?: ""
        datum.image_url?.run {
            Picasso.get().load(datum.image_url).into(holder.bind.ivPicture)
        } ?: run {
            holder.bind.ivPicture.setImageBitmap(null)
        }
        holder.bind.cvGameList.setOnClickListener {
            datum.image_url?.run{
                client.onElementClick(datum, datum.image_url)
            }?:run{
                client.onElementClick(datum, null)
            }
        }
    }
}
