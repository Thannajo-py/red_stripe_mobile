package com.example.filrouge.view

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.filrouge.*
import com.example.filrouge.bean.*
import com.example.filrouge.databinding.ActivityGameDetailsBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class GameDetails : GameAddOnMultiAddOnCommonMenu(){

    private val binding: ActivityGameDetailsBinding by lazy{
        ActivityGameDetailsBinding.inflate(layoutInflater)
    }
    private val gameId by lazy{intent.extras!!.getSerializable(SerialKey.GameId.name) as Long}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        fillCommonRV(binding, gameId, this, appInstance.database.gameDao())
        fillGameSpecificRv()
        fillRvAddOn()
        fillRvMultiAddOn()
        fillCommonTextView()
    }

    private fun fillCommonTextView(){
        appInstance.database.gameDao().getById(gameId).asLiveData().observe(
        this,
            {
                it?.let{
                    if(it.isNotEmpty()) {
                        val element = it.first()
                        loadImage(
                            element.name,
                            binding.ivDetails,
                            Type.Game.name,
                            binding.pbIvDetails
                        )
                        binding.tvGameDetailName.text = element.name
                        binding.tvGameDetailAge.text = getDataStringOrUnknown(
                            element.age,
                            R.string.age
                        )
                        binding.tvGameDetailPlayingTime.text =getDataStringOrUnknown(
                            element.playing_time,
                            R.string.playing_time,
                        )
                        binding.tvGameDetailPlayer.text = getPlayerNumberOrUnknown(
                            element.player_min,
                            element.player_max,
                        )
                    }
                }
            }
        )
        fillDifficultyField(gameId,this, binding.tvDifficulty, appInstance.database.gameDao())
    }

    private fun fillGameSpecificRv(){
        val dbMethod = DbMethod()
        dbMethod.getGameCommonSpecificField().forEach{
            CoroutineScope(SupervisorJob()).launch{
                val rv = binding.getMember("rv$it") as RecyclerView
                val adapter = GenericStringListAdapter(this@GameDetails, it)
                val field = appInstance.database.gameDao().getMember("get${it}s", gameId)
                        as LiveData<List<ID>>
                runOnUiThread {
                    rv.adapter = adapter
                    layout(rv)
                    field.observe(this@GameDetails, {it?.let{adapter.submitList(it)}})
                }
            }
        }
    }

    private fun fillRvAddOn(){
        val adapter = GenericListAdapter( this)
        binding.rvAddOn.adapter = adapter
        layout(binding.rvAddOn)
        appInstance.database.addOnDao().getDesignerWithAddOnOfGame(gameId).observe(
            this,
            {it?.let{adapter.submitList(it)}}
        )
    }

    private fun fillRvMultiAddOn(){
        val adapter = GenericListAdapter( this)
        binding.rvMultiAddOn.adapter = adapter
        layout(binding.rvMultiAddOn)
        appInstance.database.multiAddOnDao().getDesignerWithMultiAddOnOfGame(gameId).observe(
            this,
            {it?.let{adapter.submitList(it)}}
        )
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            MenuId.DeleteThis.ordinal -> showAlertBox(
                this,
                getString(R.string.delete_game),
                appInstance.database.gameDao(),
                Type.Game.name,
                gameId
            )
            MenuId.ModifyThis.ordinal -> {
                startActivity(
                Intent(this, AddElement::class.java)
                .putExtra(SerialKey.ToModifyDataId.name, gameId)
                .putExtra(SerialKey.ToModifyDataName.name, binding.tvGameDetailName.text)
                .putExtra(SerialKey.ToModifyDataType.name, Type.Game.name))
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
