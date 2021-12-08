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
import com.example.filrouge.databinding.ActivityGameDetailsBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class GameDetails : GameAddOnMultiAddOnCommonMenu(){

    private val binding: ActivityGameDetailsBinding by lazy{ ActivityGameDetailsBinding.inflate(layoutInflater) }
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

    fun fillCommonTextView(){
        appInstance.database.gameDao().getById(gameId).asLiveData().observe(this, {if(it.size > 0) it?.let{
            loadImage(it[0].name, binding.ivDetails, Type.Game.name)
            binding.tvGameDetailName.text = it[0].name
            binding.tvGameDetailAge.text = "${it[0].age} et +"
            binding.tvGameDetailPlayingTime.text = "jusqu'à ${it[0].max_time} minutes"
            binding.tvGameDetailPlayer.text = "de ${it[0].player_min} à ${it[0].player_max} joueurs"} })
        fillDifficultyField(gameId,this, binding.tvDifficulty, appInstance.database.gameDao())
    }

    fun fillGameSpecificRv(){
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

    fun fillRvAddOn(){
        val adapter = GenericListAdapter( this)
        binding.rvAddOn.adapter = adapter
        layout(binding.rvAddOn)
        appInstance.database.addOnDao().getDesignerWithAddOnOfGame(gameId).observe(this, {it?.let{adapter.submitList(it)}})
    }

    fun fillRvMultiAddOn(){
        val adapter = GenericListAdapter( this)
        binding.rvMultiAddOn.adapter = adapter
        layout(binding.rvMultiAddOn)
        appInstance.database.multiAddOnDao().getDesignerWithMultiAddOnOfGame(gameId).observe(this, {it?.let{adapter.submitList(it)}})
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
            MenuId.ModifyThis.ordinal -> startActivity(Intent(this, AddElement::class.java)
                .putExtra(SerialKey.ToModifyDataId.name, gameId)
                .putExtra(SerialKey.ToModifyDataName.name, binding.tvGameDetailName.text)
                .putExtra(SerialKey.ToModifyDataType.name, Type.Game.name))
        }
        return super.onOptionsItemSelected(item)
    }
}