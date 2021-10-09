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

    private val adapter = GenericListAdapter<DesignerWithMultiAddOn>( this)

    private val designerListAdapter = GenericStringListAdapter<DesignerTableBean>(this, Type.Designer.name)
    private val artistListAdapter = GenericStringListAdapter<ArtistTableBean>(this, Type.Artist.name)
    private val publisherListAdapter = GenericStringListAdapter<PublisherTableBean>(this, Type.Publisher.name)
    private val languageListAdapter = GenericStringListAdapter<LanguageTableBean>(this, Type.Language.name)
    private val playingModListAdapter = GenericStringListAdapter<PlayingModTableBean>(this, Type.PlayingMode.name)



    private val gameId by lazy{intent.extras!!.getSerializable(SerialKey.MultiAddOnId.name) as Long}



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val commonRvAdapterList: ArrayList<Pair<RecyclerView, GenericStringListAdapter<out ID>>> =
            arrayListOf(Pair(binding.rvDesigner,designerListAdapter), Pair(binding.rvArtist, artistListAdapter),
                Pair(binding.rvPublisher,publisherListAdapter),Pair(binding.rvPlayingMode, playingModListAdapter),
                Pair(binding.rvLanguage, languageListAdapter))
        fillCommonRV(commonRvAdapterList)

        fillCommonTextView()

        binding.rvMultiAddOn.adapter = adapter
        layout(binding.rvMultiAddOn)
        appInstance.database.multiAddOnDao().getGameFromMultiAddOn(gameId).observe(this, {it?.let{
            adapter.submitList(it)
        }})


    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            MenuId.DeleteThis.ordinal -> AlertDialog.Builder(this).setMessage("Voulez vous vraiment supprimer cette extension partagée?").setTitle("Attention")
                .setPositiveButton("ok"){
                        dialog, which -> CoroutineScope(SupervisorJob()).launch{
                    val list = appInstance.database.multiAddOnDao().getObjectById(gameId)
                    if(list.isNotEmpty()){
                        DbMethod().delete(list[0])
                    }
                    startActivity(Intent(this@MultiAddOnDetails, ViewGamesActivity::class.java))
                    finish()
                }
                }.setNegativeButton("cancel"){
                        dialog, which -> Toast.makeText(this, "Annulé", Toast.LENGTH_SHORT).show()
                }
                .show()
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
        appInstance.database.multiAddOnDao().getDifficulty(gameId).observe(this, {
            if (it.size > 0) it?.let {
                val name = it[0].name
                val id = it[0].id
                binding.tvDifficulty.text = it[0].name
                binding.tvDifficulty.setOnClickListener { onDifficultyClick(name,id) }
            }
            else binding.tvDifficulty.text = "unknown"
        })
    }

    fun fillCommonRV(listPairRecyclerViewAdapter:ArrayList<Pair<RecyclerView, GenericStringListAdapter<out ID>>>){
        listPairRecyclerViewAdapter.forEach {
            it.first.adapter = it.second
            layout(it.first)
        }

        appInstance.database.multiAddOnDao().getDesigners(gameId).observe(this, {it?.let{designerListAdapter.submitList(it)}})
        appInstance.database.multiAddOnDao().getArtists(gameId).observe(this, {it?.let{artistListAdapter.submitList(it)}})
        appInstance.database.multiAddOnDao().getPublishers(gameId).observe(this, {it?.let{publisherListAdapter.submitList(it)}})
        appInstance.database.multiAddOnDao().getPlayingMods(gameId).observe(this, {it?.let{playingModListAdapter.submitList(it)}})
        appInstance.database.multiAddOnDao().getLanguages(gameId).observe(this, {it?.let{languageListAdapter.submitList(it)}})


    }





}