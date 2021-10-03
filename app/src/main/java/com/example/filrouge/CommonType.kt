package com.example.filrouge

import android.content.Intent
import android.graphics.BitmapFactory
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.filrouge.activity.*
import com.example.filrouge.bean.*
import java.io.File


abstract class CommonType : AppCompatActivity(),  OnGenericStringListAdapterListener, OnGenericListAdapterListener {




    fun layout(list: RecyclerView){
        list.layoutManager = GridLayoutManager(this,1)
        list.addItemDecoration(MarginItemDecoration(5))
    }

    fun <T, U:RecyclerView.ViewHolder>loadRv(rv:RecyclerView, list:ArrayList<T>, adapter:RecyclerView.Adapter<U>, content:Collection<T>){
        rv.adapter = adapter
        layout(rv)
        list.clear()
        list.addAll(content)
        adapter.notifyDataSetChanged()
    }

    fun fillCommonTextView(tvDifficulty:TextView, tvName:TextView, tvPlayer:TextView,
                           tvAge:TextView, element:CommonBase){
        tvDifficulty.text = "${element.difficulty?:"unknown"}"
        tvDifficulty.setOnClickListener {
            intent = Intent(this, GenericTypeDetails::class.java)
            intent.putExtra(SerialKey.Type.name, Type.Difficulty.name)
            intent.putExtra(SerialKey.Name.name, element.difficulty)
            startActivity(intent)
            finish()
        }
        tvName.text = "${element.name}"
        tvPlayer.text = "de ${element.player_min} à ${element.player_max} joueurs"
        tvAge.text = "${element.age} et +"

    }




    fun loadImage(element:String, image: ImageView){
        if (allImages.list_of_images.contains(element)){
            val file = File(image.context.filesDir, element)
            val compressedBitMap = BitmapFactory.decodeByteArray(file.readBytes(),0,file.readBytes().size)
            image.setImageBitmap(compressedBitMap)
        }
        else{
            image.setImageBitmap(null)
        }
    }

    override fun onElementClick(datum: ID, type:String) {
        startActivity(Intent(this, GenericTypeDetails::class.java)
            .putExtra(SerialKey.Type.name, type)
            .putExtra(SerialKey.GenericId.name, datum.id)
            .putExtra(SerialKey.Name.name, datum.name))
    }

    fun onDifficultyClick(name: String, id:Long) {
        startActivity(Intent(this, GenericTypeDetails::class.java)
            .putExtra(SerialKey.Type.name, Type.Difficulty.name)
            .putExtra(SerialKey.GenericId.name, id)
            .putExtra(SerialKey.Name.name, name))
    }

    override fun onElementClick(datum: CommonGame) {
        when (datum){
            is DesignerWithAddOn -> startActivity(Intent(this, AddOnDetails::class.java).
            putExtra(SerialKey.AddOnId.name, datum.id))
            is DesignerWithMultiAddOn -> startActivity(Intent(this, MultiAddOnDetails::class.java).
            putExtra(SerialKey.MultiAddOnId.name, datum.id))
            is DesignerWithGame -> startActivity(Intent(this, GameDetails::class.java).
            putExtra(SerialKey.GameId.name, datum.id))
        }
    }

}