package com.example.filrouge

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView



val allGames = ArrayList<GameBean>()
val allAddOns = ArrayList<AddOnBean>()
val allMultiAddOns = ArrayList<MultiAddOnBean>()


class GameAdapter (val data: ArrayList<GameBean>, val client: onGameListListener) : RecyclerView.Adapter<GameAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tv1 : TextView = itemView.findViewById (R.id.tvName)
        val tv2 : TextView = itemView.findViewById (R.id.tvDesigner)
        val root = itemView.findViewById<View>(R.id.cvGameList)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.game_list, null)
        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val datum = data[position]
        holder.tv1.text = datum.name
        holder.tv2.text = if (datum.designers.size > 0) datum.designers[0] else ""
        holder.root.setOnClickListener { client.onGameClick(datum) }
    }


    override fun getItemCount() = data.size

    interface onGameListListener{
        fun onGameClick(datum:GameBean?)
    }
}



class AddonAdapter  (val data: ArrayList<AddOnBean>, val client: onAddOnListListener) : RecyclerView.Adapter<AddonAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tv1 : TextView = itemView.findViewById (R.id.tvName)
        val tv2 : TextView = itemView.findViewById (R.id.tvDesigner)
        val root = itemView.findViewById<View>(R.id.cvGameList)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.game_list, null)
        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val datum = data[position]
        holder.tv1.text = datum.name
        holder.tv2.text = if (datum.designers.size > 0) datum.designers[0] else ""
        holder.root.setOnClickListener { client.onAddOnClick(datum) }
    }


    override fun getItemCount() = data.size

    interface onAddOnListListener {
        fun onAddOnClick(datum:AddOnBean)
    }
}

class MultiAddonAdapter (val data: ArrayList<MultiAddOnBean>, val client: multiAddOnListListener) : RecyclerView.Adapter<MultiAddonAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tv1 : TextView = itemView.findViewById (R.id.tvName)
        val tv2 : TextView = itemView.findViewById (R.id.tvDesigner)
        val root = itemView.findViewById<View>(R.id.cvGameList)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.game_list, null)
        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val datum = data[position]
        holder.tv1.text = datum.name
        holder.tv2.text = if (datum.designers.size > 0) datum.designers[0] else ""
        holder.root.setOnClickListener { client.onMultiAddOnClick(datum) }
    }


    override fun getItemCount() = data.size

    interface multiAddOnListListener{
        fun onMultiAddOnClick(datum:MultiAddOnBean)
    }
}

class MultiAddOnGameAdapater (val data: ArrayList<String>, val client: GameListListener) : RecyclerView.Adapter<MultiAddOnGameAdapater.ViewHolder>(){
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tv1 : TextView = itemView.findViewById (R.id.tvName)
        val tv2 : TextView = itemView.findViewById (R.id.tvDesigner)
        val root = itemView.findViewById<View>(R.id.cvGameList)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.game_list, null)
        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val datum = data[position]
        holder.tv1.text = datum
        val gameNameTry = allGames.filter{it.name == datum}
        val gameName = if (gameNameTry.size == 1) gameNameTry[0] else null
        if (gameName != null){
            holder.tv2.text = if(gameName.designers.size > 0) gameName.designers[0] else "unknown"
        }
        holder.root.setOnClickListener { client.onGameClick(gameName) }
    }


    override fun getItemCount() = data.size

    interface GameListListener{
        fun onGameClick(datum:GameBean?)
    }
}


class GenericTypeAdapater (val data: ArrayList<String>, val client: CommonType, val type:String) : RecyclerView.Adapter<GenericTypeAdapater.ViewHolder>(){
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tv1 : TextView = itemView.findViewById (R.id.tvName)
        val root = itemView.findViewById<View>(R.id.cvNameList)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.name_list, null)
        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val datum = data[position]
        holder.tv1.text = datum
        holder.root.setOnClickListener { client.onGenericClick(datum, type) }
    }


    override fun getItemCount() = data.size

    interface GenericListener{
        fun onGenericClick(datum:String, type: String)

    }
}
