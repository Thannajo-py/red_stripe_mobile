package thannajo.appli.filrouge.utils

import thannajo.appli.filrouge.databinding.*
import thannajo.appli.filrouge.model.*
import thannajo.appli.filrouge.view.APISearchActivity
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import java.io.File

/**
 * current application user or null if unregistered
 */
var currentUser: UserTableBean? = null

/**
 * Url of the server
 */
var API_URL:String? = null

/**
 * Url of the image file of the server
 */
var API_STATIC:String? = null


/**
 * Determine if the application use server or not
 */
var isLocal:Boolean = true


/**
 * A [RecyclerView] adapter for an user list implement[UserListener]
 * use binding  [AccountListBinding] showing name and check box,
 * checked if user present in addedUserList
 * use checkbox which is checked when list member is included in [addedUserList]
 */
class UserBeanAdapter (
    private val client: UserListener,
    private val addedUserList: ArrayList<UserTableBean>
    ) : ListAdapter<UserTableBean, UserBeanAdapter.ViewHolder>(UserTableBeanComparator()){

    /**
     * define the bind variable for accessing binding element
     */
    class ViewHolder(val bind: AccountListBinding) : RecyclerView.ViewHolder(bind.root)

    /**
     * comparative rules for detecting change
     */
    class UserTableBeanComparator: DiffUtil.ItemCallback<UserTableBean>() {
        /**
         * @return true if both item are the same
         */
        override fun areItemsTheSame(oldItem: UserTableBean, newItem: UserTableBean) =
            oldItem === newItem

        /**
         * @return true if both item have the same content
         */
        override fun areContentsTheSame(oldItem: UserTableBean, newItem: UserTableBean) =
            oldItem == newItem
    }

    /**
     * call at view holder creation
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(AccountListBinding.inflate(LayoutInflater.from(parent.context)))

    /**
     * use to fill the viewHolder
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val datum = getItem(position)
        holder.bind.cbSelect.text = datum.login
        holder.bind.cbSelect.isChecked = addedUserList.contains(datum)
        holder.bind.cbSelect.setOnClickListener { client.onUserClick(datum, position) }
    }
}


/**
 * on click interface of [UserBeanAdapter]
 */
interface UserListener{
    fun onUserClick(datum:UserTableBean, position:Int)
}


/**
 * A [RecyclerView] adapter for [CommonGame]
 * use [GameListBinding]
 * show name, designer(one if any) and picture(if any)
 * implement [OnGenericListAdapterListener] on click listener
 */
open class GenericListAdapter(private val client: OnGenericListAdapterListener)
    : ListAdapter<CommonGame, GenericListAdapter.ViewHolder>(GameComparator()) {

    /**
     * define the bind variable for accessing binding element
     */
    class ViewHolder(val bind: GameListBinding) : RecyclerView.ViewHolder(bind.root)

    /**
     * comparative rules for detecting change
     */
    class GameComparator<T:CommonGame>: DiffUtil.ItemCallback<T>() {
        /**
         * @return true if both item are the same
         */
        override fun areItemsTheSame(oldItem: T, newItem: T) = oldItem === newItem
        /**
         * @return true if both item have the same content
         */
        override fun areContentsTheSame(oldItem: T, newItem: T) = oldItem.name == newItem.name
    }

    /**
     * action taken at view holder creation
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(GameListBinding.inflate(LayoutInflater.from(parent.context)))

    /**
     * use to fill the viewHolder
     */
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

/**
 * on click interface of [UserBeanAdapter]
 */
interface OnGenericListAdapterListener{
    fun onElementClick(datum:CommonGame)
}


/**
 * on click interface of [GenericStringListAdapter]
 */
interface OnGenericStringListAdapterListener{
    fun onElementClick(datum: ID, type:String)
}


/**
 * A [RecyclerView] adapter for all non game database feature
 * show name only
 * implement [OnGenericStringListAdapterListener] on click listener
 */
open class GenericStringListAdapter(private val client: OnGenericStringListAdapterListener, val type:String)
    : ListAdapter<ID, GenericStringListAdapter.ViewHolder>(GameComparator()) {

    /**
     * define the bind variable for accessing binding element
     */
    class ViewHolder(val bind: NameListBinding) : RecyclerView.ViewHolder(bind.root)

    /**
     * comparative rules for detecting change
     */
    class GameComparator<T:ID>: DiffUtil.ItemCallback<T>() {
        /**
         * @return true if both item are the same
         */
        override fun areItemsTheSame(oldItem: T, newItem: T) = oldItem === newItem
        /**
         * @return true if both item have the same content
         */
        override fun areContentsTheSame(oldItem: T, newItem: T) = oldItem.name == newItem.name
    }

    /**
     * call at view holder creation
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(NameListBinding.inflate(LayoutInflater.from(parent.context)))

    /**
     * use to fill the viewHolder
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val datum = getItem(position)
        holder.bind.tvName.text = datum.name
        holder.bind.cvNameList.setOnClickListener { client.onElementClick(datum, type) }
    }
}


/**
 * A [RecyclerView] adapter for [CommonGame]
 * use [GenericIDCbListener]
 * show name and checkbox
 * use checkbox which is checked when list member is included in [addedGeneric]
 * implement [OnGenericListAdapterListener] on click listener
 */
class GenericIDListCbAdapter (
    private val client: GenericIDCbListener,
    val type:String,
    private val addedGeneric:ArrayList<String>)
    : ListAdapter<ID, GenericIDListCbAdapter.ViewHolder>(ItemComparator()){

    /**
     * define the bind variable for accessing binding element
     */
    class ViewHolder(val bind: NameListCbBinding) : RecyclerView.ViewHolder(bind.root)

    /**
     * comparative rules for detecting change
     */
    class ItemComparator<T:ID>: DiffUtil.ItemCallback<T>() {
        /**
         * @return true if both item are the same
         */
        override fun areItemsTheSame(oldItem: T, newItem: T) = oldItem === newItem
        /**
         * @return true if both item have the same content
         */
        override fun areContentsTheSame(oldItem: T, newItem: T) = oldItem.name == newItem.name
    }

    /**
     * call at view holder creation
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
    = ViewHolder(NameListCbBinding.inflate(
        LayoutInflater.from(parent.context)))

    /**
     * use to fill the viewHolder
     */
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


/**
 * on click interface of [GenericIDListCbAdapter]
 */
interface GenericIDCbListener{
    fun onGenericClick(name:String, type: String, cb:CheckBox)
}


/**
 * A [RecyclerView] adapter for [CommonGame]
 * use [GenericIDCbListenerId]
 * show name and checkbox
 * use checkbox which is checked when list member is included in [addedGeneric]
 * implement [GenericIDCbListenerId] on click listener
 */
class GenericIDListCbAdapterId (
    private val client: GenericIDCbListenerId,
    val type:String,
    private val addedGeneric:ArrayList<Long>)
    : ListAdapter<ID, GenericIDListCbAdapterId.ViewHolder>(ItemComparator()){

    /**
     * define the bind variable for accessing binding element
     */
    class ViewHolder(val bind: NameListCbBinding) : RecyclerView.ViewHolder(bind.root)

    /**
     * comparative rules for detecting change
     */
    class ItemComparator<T:ID>: DiffUtil.ItemCallback<T>() {
        /**
         * @return true if both item are the same
         */
        override fun areItemsTheSame(oldItem: T, newItem: T) = oldItem === newItem
        /**
         * @return true if both item have the same content
         */
        override fun areContentsTheSame(oldItem: T, newItem: T) = oldItem.name == newItem.name
    }

    /**
     * call at view holder creation
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
            = ViewHolder(NameListCbBinding.inflate(
        LayoutInflater.from(parent.context)))

    /**
     * use to fill the viewHolder
     */
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


/**
 * on click interface of [GenericIDListCbAdapterId]
 */
interface GenericIDCbListenerId{
    fun onGenericClick(id:Long, type: String, cb:CheckBox)
}


/**
 * A [RecyclerView] adapter for [CommonGame]
 * use [GenericCommonGameCbListener]
 * show name, designer(one if any) and picture(if any)
 * use checkbox which is checked when list member is included in [addedGeneric]
 * implement [GenericCommonGameCbListener] on click listener
 */
class GenericCommonGameListCbAdapter<T:CommonGame> (
    private val client: GenericCommonGameCbListener,
    private val addedGeneric:ArrayList<T>,
    val type:String
    ) : ListAdapter<CommonGame, GenericCommonGameListCbAdapter.ViewHolder>(ItemComparator()){

    /**
     * define the bind variable for accessing binding element
     */
    class ViewHolder(val bind: GameListCbBinding) : RecyclerView.ViewHolder(bind.root)

    /**
     * comparative rules for detecting change
     */
    class ItemComparator<T:CommonGame>: DiffUtil.ItemCallback<T>() {
        /**
         * @return true if both item are the same
         */
        override fun areItemsTheSame(oldItem: T, newItem: T) = oldItem === newItem
        /**
         * @return true if both item have the same content
         */
        override fun areContentsTheSame(oldItem: T, newItem: T) = oldItem.name == newItem.name
    }

    /**
     * call at view holder creation
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
    = ViewHolder(GameListCbBinding.inflate(LayoutInflater.from(parent.context)))

    /**
     * use to fill the viewHolder
     */
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


/**
 * on click interface of [GenericCommonGameListCbAdapter]
 */
interface GenericCommonGameCbListener{
    fun onGenericClick(datum:CommonGame, view: CheckBox, type:String)
}


/**
 * A [RecyclerView] adapter for [OneToOne]
 * use [GenericOneToOneListener]
 * show name
 * implement [GenericOneToOneListener] on click listener
 */
class OneToOneListAdapter<T: OneToOne> (private val client: GenericOneToOneListener)
    : ListAdapter<T, OneToOneListAdapter.ViewHolder>(ItemComparator()){

    /**
     * define the bind variable for accessing binding element
     */
    class ViewHolder(val bind: NameListBinding) : RecyclerView.ViewHolder(bind.root)

    /**
     * comparative rules for detecting change
     */
    class ItemComparator<T:OneToOne>: DiffUtil.ItemCallback<T>() {
        /**
         * @return true if both item are the same
         */
        override fun areItemsTheSame(oldItem: T, newItem: T) = oldItem === newItem
        /**
         * @return true if both item have the same content
         */
        override fun areContentsTheSame(oldItem: T, newItem: T) = oldItem.name == newItem.name
    }

    /**
     * call at view holder creation
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
    = ViewHolder(NameListBinding.inflate(LayoutInflater.from(parent.context)))

    /**
     * use to fill the viewHolder
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val datum = getItem(position)
        holder.bind.tvName.text = datum.name
        holder.bind.rootGames.setOnClickListener { client.onGenericClick(datum) }
    }
}

/**
 * on click interface of [OneToOneListAdapter] and [AddOnGameListAdapter]
 */
interface GenericOneToOneListener{
    fun onGenericClick(datum:OneToOne)
}


/**
 * A [RecyclerView] adapter for [DesignerWithGame]
 * use [GenericOneToOneListener]
 * use for linking add on  with game (one to many relationship)
 * show name, designer(if any), and picture(if any)
 * implement [GenericOneToOneListener] on click listener
 */
class AddOnGameListAdapter (private val client: GenericOneToOneListener)
    : ListAdapter<DesignerWithGame, AddOnGameListAdapter.ViewHolder>(ItemComparator()){

    /**
     * define the bind variable for accessing binding element
     */
    class ViewHolder(val bind: GameListBinding) : RecyclerView.ViewHolder(bind.root)

    /**
     * comparative rules for detecting change
     */
    class ItemComparator: DiffUtil.ItemCallback<DesignerWithGame>() {
        /**
         * @return true if both item are the same
         */
        override fun areItemsTheSame(oldItem: DesignerWithGame, newItem: DesignerWithGame) =
            oldItem === newItem
        /**
         * @return true if both item have the same content
         */
        override fun areContentsTheSame(oldItem: DesignerWithGame, newItem: DesignerWithGame) =
            oldItem.name == newItem.name
    }

    /**
     * call at view holder creation
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
            = ViewHolder(GameListBinding.inflate(LayoutInflater.from(parent.context)))

    /**
     * use to fill the viewHolder
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val datum = getItem(position)
        holder.bind.tvName.text = datum.name
        holder.bind.tvDesigner.text = datum.designer
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
        holder.bind.rootGames.setOnClickListener { client.onGenericClick(datum) }
    }
}



/**
 * A [RecyclerView] adapter for [APISearchActivity]
 * handle [BgaGameBean] use picasso to get Picture
 * show name, designer(if any) and picture(if any)
 * used with [APISearchActivity]
 */
open class BgaListAdapter (private val client: APISearchActivity)
    : ListAdapter<BgaGameBean, BgaListAdapter.ViewHolder>(GameComparator()) {

    /**
     * define the bind variable for accessing binding element
     */
    class ViewHolder(val bind: GameListBinding) : RecyclerView.ViewHolder(bind.root)

    /**
     * comparative rules for detecting change
     */
    class GameComparator : DiffUtil.ItemCallback<BgaGameBean>() {
        /**
         * @return true if both item are the same
         */
        override fun areItemsTheSame(oldItem: BgaGameBean, newItem: BgaGameBean) =
            oldItem === newItem
        /**
         * @return true if both item have the same content
         */
        override fun areContentsTheSame(oldItem: BgaGameBean, newItem: BgaGameBean) =
            oldItem.name == newItem.name
    }

    /**
     * call at view holder creation
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(GameListBinding.inflate(LayoutInflater.from(parent.context)))

    /**
     * use to fill the viewHolder
     */
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
