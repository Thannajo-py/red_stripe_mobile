package com.example.filrouge.activity

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.filrouge.*
import com.example.filrouge.bean.*
import com.example.filrouge.databinding.ActivityAddOnDetailsBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class AddOnDetails : GameAddOnMultiAddOnCommonMenu() {

    private val binding: ActivityAddOnDetailsBinding by lazy{ ActivityAddOnDetailsBinding.inflate(layoutInflater) }
    private val gameId by lazy{ intent.extras!!.getSerializable(SerialKey.AddOnId.name) as Long }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        fillCommonTextView()
        fillCommonRV(binding, gameId, this, appInstance.database.addOnDao())
        fillGameField()
    }

    fun fillGameField(){
        CoroutineScope(SupervisorJob()).launch{
            val addOn = appInstance.database.addOnDao().getObjectById(gameId)
            if (addOn.isNotEmpty() && addOn[0].gameId != null) {
                runOnUiThread {
                    appInstance.database.addOnDao().getGameFromAddOn(addOn[0].gameId?:0L)
                        .observe(this@AddOnDetails, {
                            it?.let {
                                if (it.isNotEmpty()) {
                                    val game = it[0]
                                    binding.tvNomJeuAddon.text = game.name
                                    binding.tvNomAuteurAddOn.text = game.designer
                                    loadImage(game.name, binding.ivPicture, Type.Game.name)
                                    binding.cvGame.setOnClickListener {
                                        startActivity(
                                            Intent(
                                                this@AddOnDetails,
                                                GameDetails::class.java
                                            ).putExtra(SerialKey.GameId.name, game.id)
                                        )
                                        finish()
                                    }
                                }
                            }
                        })
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            MenuId.DeleteThis.ordinal -> showAlertBox(
                this,
                getString(R.string.delete_add_on),
                appInstance.database.addOnDao(),
                Type.AddOn.name,
                gameId
            )
            MenuId.ModifyThis.ordinal -> startActivity(Intent(this, AddElement::class.java)
                .putExtra(SerialKey.ToModifyDataId.name, gameId)
                .putExtra(SerialKey.ToModifyDataName.name, binding.tvAddOnDetailName.text)
                .putExtra(SerialKey.ToModifyDataType.name, Type.AddOn.name))
        }
        return super.onOptionsItemSelected(item)
    }



    private fun fillCommonTextView(){
        appInstance.database.addOnDao().getById(gameId).asLiveData().observe(this, {
            if (it.size > 0) it?.let {
                val addOn = it.first()
                loadImage(addOn.name, binding.ivDetails, Type.AddOn.name)
                binding.tvAddOnDetailName.text = addOn.name
                addOn.age?.run {
                    binding.tvAddOnDetailAge.text = getString(R.string.age, addOn.age.toString())
                } ?: run {
                    binding.tvAddOnDetailAge.text = getString(R.string.unknown)
                }

                binding.tvAddOnDetailPlayingTime.text =
                    getString(R.string.playing_time, addOn.playing_time)
                binding.tvAddOnDetailPlayer.text = getString(
                    R.string.player_number,
                    addOn.player_min?.toString() ?: getString(R.string.unknown),
                    addOn.player_max?.toString() ?: getString(R.string.unknown),

                    )
                fillDifficultyField(gameId,this,binding.tvDifficulty, appInstance.database.addOnDao())
            }
        })
    }

}