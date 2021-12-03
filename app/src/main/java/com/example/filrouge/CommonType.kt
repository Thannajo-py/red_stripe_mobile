package com.example.filrouge

import android.content.Intent
import android.graphics.BitmapFactory
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.filrouge.activity.*
import com.example.filrouge.bean.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.io.File


abstract class CommonType : AppCompatActivity(),  OnGenericStringListAdapterListener, OnGenericListAdapterListener {




    fun layout(list: RecyclerView){
        list.layoutManager = GridLayoutManager(this,1)
        list.addItemDecoration(MarginItemDecoration(5))
    }


    fun loadImage(element:String, image: ImageView, type:String){
        CoroutineScope(SupervisorJob()).launch {
            when (type) {
                Type.Game.name -> if (appInstance.database.gameDao().getImage(element)
                        .isNotEmpty()
                ) getFile("$element$type", image) else image.setImageBitmap(null)
                Type.AddOn.name -> if (appInstance.database.addOnDao().getImage(element)
                        .isNotEmpty()
                ) getFile("$element$type", image) else image.setImageBitmap(null)
                Type.MultiAddOn.name -> if (appInstance.database.multiAddOnDao().getImage(element)
                        .isNotEmpty()
                ) getFile("$element$type", image) else image.setImageBitmap(null)
            }
        }

    }

    private fun getFile(name:String, image:ImageView){
        runOnUiThread {
            val file = File(image.context.filesDir, name)
            val compressedBitMap = BitmapFactory.decodeByteArray(file.readBytes(),0,file.readBytes().size)
            image.setImageBitmap(compressedBitMap)
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