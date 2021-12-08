package com.example.filrouge.activity

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.filrouge.*
import com.example.filrouge.bean.*
import com.example.filrouge.databinding.ActivityMultiAddOnDetailBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class MultiAddOnDetails : GameAddOnMultiAddOnCommonMenu() {

    private val binding: ActivityMultiAddOnDetailBinding by lazy{ ActivityMultiAddOnDetailBinding.inflate(layoutInflater) }
    private val gameId by lazy{intent.extras!!.getSerializable(SerialKey.MultiAddOnId.name) as Long}

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
            MenuId.ModifyThis.ordinal -> startActivity(
                Intent(this, AddElement::class.java)
                .putExtra(SerialKey.ToModifyDataId.name, gameId)
                .putExtra(SerialKey.ToModifyDataName.name, binding.tvMultiAddOnDetailName.text)
                .putExtra(SerialKey.ToModifyDataType.name, Type.MultiAddOn.name))
        }
        return super.onOptionsItemSelected(item)
    }

    fun fillCommonTextView(){
        appInstance.database.multiAddOnDao().getById(gameId).asLiveData().observe(this, {if(it.size > 0) it?.let{
            loadImage(it[0].name, binding.ivDetails, Type.MultiAddOn.name)
            binding.tvMultiAddOnDetailName.text = it[0].name
            binding.tvMultiAddOnDetailAge.text = "${it[0].age} et +"
            binding.tvMultiAddOnDetailPlayingTime.text = "jusqu'à ${it[0].max_time} minutes"
            binding.tvMultiAddOnDetailPlayer.text = "de ${it[0].player_min} à ${it[0].player_max} joueurs"} })
        fillDifficultyField(gameId, this, binding.tvDifficulty, appInstance.database.multiAddOnDao())
    }

    fun fillGameRV(){
        val adapter = GenericListAdapter( this)
        binding.rvMultiAddOn.adapter = adapter
        layout(binding.rvMultiAddOn)
        appInstance.database.multiAddOnDao().getGameFromMultiAddOn(gameId).observe(this, {it?.let{
            adapter.submitList(it)
        }})
    }

}