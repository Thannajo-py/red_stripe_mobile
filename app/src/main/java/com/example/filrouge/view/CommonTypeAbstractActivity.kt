package com.example.filrouge.view

import android.content.Intent
import android.graphics.BitmapFactory
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.filrouge.appInstance
import com.example.filrouge.model.CommonDao
import com.example.filrouge.utils.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.io.File


/**
 * Common method for [AddElementActivity], [GenericTypeDetailsActivity], [GameDetailsActivity], [AddOnDetailsActivity],
 * [MultiAddOnDetailsActivity]
 */
abstract class CommonTypeAbstractActivity
    : AppCompatActivity(), OnGenericStringListAdapterListener, OnGenericListAdapterListener {

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
     * handle click on non-game and non-difficulty element and launch the associated [GenericTypeDetailsActivity]
     */
    override fun onElementClick(datum: ID, type:String) {
        startActivity(Intent(this, GenericTypeDetailsActivity::class.java)
            .putExtra(SerialKey.Type.name, type)
            .putExtra(SerialKey.GenericId.name, datum.id)
            .putExtra(SerialKey.Name.name, datum.name))
        finish()
    }

    /**
     * handle click on specific difficulty element and launch the associated [GenericTypeDetailsActivity]
     */
    protected fun onDifficultyClick(name: String, id:Long) {
        startActivity(Intent(this, GenericTypeDetailsActivity::class.java)
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
                        AddOnDetailsActivity::class.java
                    ).putExtra(SerialKey.AddOnId.name, datum.id)
                )
                    finish()
            }
            is DesignerWithMultiAddOn -> {
                startActivity(
                    Intent(
                        this,
                        MultiAddOnDetailsActivity::class.java
                    ).putExtra(SerialKey.MultiAddOnId.name, datum.id)
                )
                finish()
            }
            is DesignerWithGame -> {
                startActivity(
                    Intent(
                        this,
                        GameDetailsActivity::class.java
                    ).putExtra(SerialKey.GameId.name, datum.id)
                )
                finish()
            }
        }
    }
}
