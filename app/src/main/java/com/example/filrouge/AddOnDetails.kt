package com.example.filrouge

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.core.graphics.drawable.toBitmap
import com.example.filrouge.databinding.ActivityAddOnDetailsBinding
import java.io.ByteArrayOutputStream
import java.io.File

class AddOnDetails : GameAddOnMultiAddOnCommonMenu() {

    private val sharedPreference by lazy {SharedPreference(this)}
    private val binding: ActivityAddOnDetailsBinding by lazy{ ActivityAddOnDetailsBinding.inflate(layoutInflater) }
    private val parent:GameBean? by lazy{intent.extras!!.getSerializable(SerialKey.ParentGame.name) as GameBean?}
    private val addOn:AddOnBean by lazy{intent.extras!!.getSerializable(SerialKey.AddOn.name) as AddOnBean}


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        fillCommonRv(binding.rvDesigner, binding.rvArtist, binding.rvPublisher, binding.rvLanguage,
                binding.rvPlayingMode, addOn)

        fillCommonTextView(binding.tvDifficulty, binding.tvAddOnDetailName, binding.tvAddOnDetailPlayer, binding.tvAddOnDetailAge, addOn)
        binding.tvAddOnDetailPlayingTime.text = "jusqu'à ${addOn.max_time} minutes" + if(parent != null && parent!!.by_player == true) "/ Joueur" else ""

        if (parent != null){
            binding.tvNomJeuAddon.text = parent!!.name
            binding.tvNomAuteurAddOn.text = if(parent!!.designers.size >0) parent!!.designers[0] else "unknown"
        }
        binding.tvNomJeuAddon.setOnClickListener {
            intent = Intent(this, GameDetails::class.java)
            intent.putExtra(SerialKey.Game.name, parent)
            startActivity(intent)
            finish()

        }
        loadImage(addOn, binding.ivDetails)
        parent?.run{
            loadImage(this, binding.ivPicture)
        }
        addOn.game?.run{
            val gameBean = allGames.filter{this == it.name}
            if (gameBean.size == 1){
                loadImage(gameBean[0], binding.ivPicture)
            }
        }



    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            MenuId.DeleteThis.ordinal -> AlertDialog.Builder(this).setMessage("Voulez vous vraiment supprimer cette extension?").setTitle("Attention")
                .setPositiveButton("ok"){
                        dialog, which -> run{deleteFromList(addOn, allAddOns)
                    refreshedSavedData(sharedPreference)
                }
                }.setNegativeButton("cancel"){
                        dialog, which -> Toast.makeText(this, "Annulé", Toast.LENGTH_SHORT).show()
                }
                .show()
            MenuId.ModifyThis.ordinal -> startActivity(Intent(this, AddElement::class.java).putExtra(SerialKey.ToModifyData.name, addOn))
        }
        return super.onOptionsItemSelected(item)
    }




}