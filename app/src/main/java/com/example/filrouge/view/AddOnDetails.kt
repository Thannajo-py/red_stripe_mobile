package com.example.filrouge.view

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.lifecycle.asLiveData
import com.example.filrouge.*
import com.example.filrouge.utils.*
import com.example.filrouge.databinding.ActivityAddOnDetailsBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class AddOnDetails : GameAddOnMultiAddOnCommonMenu() {

    private val binding: ActivityAddOnDetailsBinding by lazy{
        ActivityAddOnDetailsBinding.inflate(layoutInflater)
    }
    private val gameId by lazy{ intent.extras!!.getSerializable(SerialKey.AddOnId.name) as Long }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        fillCommonTextView()
        fillCommonRV(binding, gameId, this, appInstance.database.addOnDao())
        fillGameField()
    }

    private fun fillGameField(){
        CoroutineScope(SupervisorJob()).launch{
            val addOn = appInstance.database.addOnDao().getObjectById(gameId)
            if (addOn.isNotEmpty() && addOn[0].gameId != null) {
                runOnUiThread {
                    appInstance.database.addOnDao().getGameFromAddOn(
                        addOn[0].gameId?:0L
                    ).observe(
                        this@AddOnDetails,
                        { it?.let {
                                if (it.isNotEmpty()) {
                                    val game = it[0]
                                    binding.tvNomJeuAddon.text = game.name
                                    binding.tvNomAuteurAddOn.text = game.designer
                                    loadImage(game.name,
                                        binding.ivPicture,
                                        Type.Game.name,
                                        binding.pbIvPicture
                                    )
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
                        }
                    )
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
            MenuId.ModifyThis.ordinal ->    {
                startActivity(
                Intent(this, AddElement::class.java)
                .putExtra(SerialKey.ToModifyDataId.name, gameId)
                .putExtra(SerialKey.ToModifyDataName.name, binding.tvAddOnDetailName.text)
                .putExtra(SerialKey.ToModifyDataType.name, Type.AddOn.name)
                )
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun fillCommonTextView(){
        appInstance.database.addOnDao().getById(gameId).asLiveData().observe(this, {
            it?.let {
                if (it.isNotEmpty()) {
                    val addOn = it.first()
                    loadImage(addOn.name, binding.ivDetails, Type.AddOn.name, binding.pbIvDetails)
                    binding.tvAddOnDetailName.text = addOn.name
                    binding.tvAddOnDetailAge.text = getDataStringOrUnknown(
                        addOn.age,
                        R.string.age
                    )
                    binding.tvAddOnDetailPlayingTime.text = getDataStringOrUnknown(
                        addOn.playing_time,
                        R.string.age
                    )
                    binding.tvAddOnDetailPlayer.text = getPlayerNumberOrUnknown(
                        addOn.player_min,
                        addOn.player_max,
                    )
                    fillDifficultyField(
                        gameId,
                        this,
                        binding.tvDifficulty,
                        appInstance.database.addOnDao()
                    )
                }
            }
        })
    }
}
