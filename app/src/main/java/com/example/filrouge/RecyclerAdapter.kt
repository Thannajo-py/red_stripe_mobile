package com.example.filrouge

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.filrouge.databinding.GameListBinding


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




open class GenericAdapter<T:CommonBase> (val data: ArrayList<T>, val client: OnGenericListListener) : RecyclerView.Adapter<GenericAdapter.ViewHolder>() {


    class ViewHolder(val bind:GameListBinding) : RecyclerView.ViewHolder(bind.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(GameListBinding.inflate(
        LayoutInflater.from(parent.context)))


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val datum = data[position]
        holder.bind.tvName.text = datum.name
        holder.bind.tvDesigner.text = if (datum.designers.size > 0) datum.designers[0] else ""
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
        if (gameName != null){
            holder.bind.tvDesigner.text = if(gameName.designers.size > 0) gameName.designers[0] else "unknown"
        }
        holder.bind.cvGameList.setOnClickListener { client.onElementClick(gameName) }
    }


    override fun getItemCount() = data.size


}


class GenericTypeAdapter (val data: ArrayList<String>, val client: CommonType, val type:String) : RecyclerView.Adapter<GenericTypeAdapter.ViewHolder>(){
    class ViewHolder(val bind:GameListBinding) : RecyclerView.ViewHolder(bind.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(GameListBinding.inflate(
        LayoutInflater.from(parent.context)))


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val datum = data[position]
        holder.bind.tvName.text = datum
        holder.bind.cvGameList.setOnClickListener { client.onGenericClick(datum, type) }
    }


    override fun getItemCount() = data.size

    interface GenericListener{
        fun onGenericClick(datum:String, type: String)

    }
}
