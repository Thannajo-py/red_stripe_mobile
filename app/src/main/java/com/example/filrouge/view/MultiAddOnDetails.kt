package com.example.filrouge.view

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.lifecycle.asLiveData
import com.example.filrouge.*
import com.example.filrouge.databinding.ActivityMultiAddOnDetailBinding

class MultiAddOnDetails : GameAddOnMultiAddOnCommonMenu() {

    private val binding: ActivityMultiAddOnDetailBinding by lazy{
        ActivityMultiAddOnDetailBinding.inflate(layoutInflater)
    }
    private val gameId by lazy{
        intent.extras!!.getSerializable(SerialKey.MultiAddOnId.name) as Long
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        fillCommonRV(binding, gameId, this, appInstance.database.multiAddOnDao())
        fillCommonTextView()
        fillGameRV()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            MenuId.DeleteThis.ordinal -> showAlertBox(
                this,
                getString(R.string.delete_multi_add_on),
                appInstance.database.multiAddOnDao(),
                Type.MultiAddOn.name,
                gameId
            )
            MenuId.ModifyThis.ordinal -> {
                startActivity(
                Intent(this, AddElement::class.java)
                .putExtra(SerialKey.ToModifyDataId.name, gameId)
                .putExtra(SerialKey.ToModifyDataName.name, binding.tvMultiAddOnDetailName.text)
                .putExtra(SerialKey.ToModifyDataType.name, Type.MultiAddOn.name))
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun fillCommonTextView(){
        appInstance.database.multiAddOnDao().getById(gameId).asLiveData().observe(
            this,
            {
                it?.let{
                    if(it.isNotEmpty()) {
                        val game = it.first()
                        loadImage(
                            game.name,
                            binding.ivDetails,
                            Type.MultiAddOn.name,
                            binding.pbIvDetails
                        )
                        binding.tvMultiAddOnDetailName.text = game.name
                        binding.tvMultiAddOnDetailAge.text = getDataStringOrUnknown(
                            game.age,
                            R.string.age
                        )
                        binding.tvMultiAddOnDetailPlayingTime.text = getDataStringOrUnknown(
                            game.playing_time,
                            R.string.playing_time
                        )
                        binding.tvMultiAddOnDetailPlayer.text = getPlayerNumberOrUnknown(
                                game.player_min,
                                game.player_max
                            )
                    }
                }
            }
        )
        fillDifficultyField(
            gameId,
            this,
            binding.tvDifficulty, appInstance.database.multiAddOnDao()
        )
    }

    private fun fillGameRV(){
        val adapter = GenericListAdapter( this)
        binding.rvMultiAddOn.adapter = adapter
        layout(binding.rvMultiAddOn)
        appInstance.database.multiAddOnDao().getGameFromMultiAddOn(gameId).observe(
            this,
            {it?.let{ adapter.submitList(it) }}
        )
    }
}
