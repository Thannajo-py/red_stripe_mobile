package com.example.filrouge

import android.content.Intent
import android.graphics.BitmapFactory
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.filrouge.activity.*
import com.example.filrouge.bean.*
import com.example.filrouge.dao.CommonDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.io.File


/**
 * Common method for [AddElement], [GenericTypeDetails], [GameDetails], [AddOnDetails],
 * [MultiAddOnDetails]
 */
abstract class CommonType
    : AppCompatActivity(),  OnGenericStringListAdapterListener, OnGenericListAdapterListener {

    /**
     * Handle [RecyclerView] layout Manager and space between [RecyclerView] element
     */
    protected fun layout(list: RecyclerView){
        list.layoutManager = GridLayoutManager(this,1)
        list.addItemDecoration(MarginItemDecoration(5))
    }

    /**
     * get the proper image for an [ImageView] if in the database else null
     * handle [ProgressBar] and [ImageView] visibility
     */
    protected fun loadImage(element:String, image: ImageView, type:String, pb:ProgressBar){
        CoroutineScope(SupervisorJob()).launch {
            val lowCamelCase = type.highToLowCamelCase()
            val dao = appInstance.database.getMember("${lowCamelCase}Dao") as CommonDao<*, *>
            val imageList = dao.getImage(element)
            if (imageList.isNotEmpty()) {
                getFile("$element$type", image)
            } else {
                runOnUiThread {
                    image.setImageBitmap(null)
                }
            }
            runOnUiThread {
                pb.visibility = View.GONE
                image.visibility = View.VISIBLE
            }
        }
    }

    /**
     * get a file by name
     * transform the [ByteArray] file into a Bitmap
     * load image inside an [ImageView]
     */
    private fun getFile(name:String, image:ImageView){
        runOnUiThread {
            val file = File(image.context.filesDir, name)
            val compressedBitMap = BitmapFactory.decodeByteArray(
                file.readBytes(),
                0,
                file.readBytes().size
            )
            image.setImageBitmap(compressedBitMap)
        }
    }

    /**
     * handle click on non-game and non-difficulty element and launch the associated [GenericTypeDetails]
     */
    override fun onElementClick(datum: ID, type:String) {
        startActivity(Intent(this, GenericTypeDetails::class.java)
            .putExtra(SerialKey.Type.name, type)
            .putExtra(SerialKey.GenericId.name, datum.id)
            .putExtra(SerialKey.Name.name, datum.name))
        finish()
    }

    /**
     * handle click on specific difficulty element and launch the associated [GenericTypeDetails]
     */
    protected fun onDifficultyClick(name: String, id:Long) {
        startActivity(Intent(this, GenericTypeDetails::class.java)
            .putExtra(SerialKey.Type.name, Type.Difficulty.name)
            .putExtra(SerialKey.GenericId.name, id)
            .putExtra(SerialKey.Name.name, name))
        finish()
    }

    /**
     * handle click on game element and launch the associated activity
     */
    override fun onElementClick(datum: CommonGame) {
        when (datum){
            is DesignerWithAddOn -> {
                startActivity(
                    Intent(
                        this,
                        AddOnDetails::class.java
                    ).putExtra(SerialKey.AddOnId.name, datum.id)
                )
                    finish()
            }
            is DesignerWithMultiAddOn -> {
                startActivity(
                    Intent(
                        this,
                        MultiAddOnDetails::class.java
                    ).putExtra(SerialKey.MultiAddOnId.name, datum.id)
                )
                finish()
            }
            is DesignerWithGame -> {
                startActivity(
                    Intent(
                        this,
                        GameDetails::class.java
                    ).putExtra(SerialKey.GameId.name, datum.id)
                )
                finish()
            }
        }
    }
}
