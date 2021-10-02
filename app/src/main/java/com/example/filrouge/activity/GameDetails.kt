package com.example.filrouge.activity

import android.app.AlertDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.filrouge.*
import com.example.filrouge.bean.*
import com.example.filrouge.databinding.ActivityGameDetailsBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.io.File

class GameDetails : GameAddOnMultiAddOnCommonMenu(), OnGenericListListener{

    private val binding: ActivityGameDetailsBinding by lazy{ ActivityGameDetailsBinding.inflate(layoutInflater) }

    private val designerListAdapter = GenericStringListAdapter<DesignerTableBean>(this, Type.Designer.name)
    private val artistListAdapter = GenericStringListAdapter<ArtistTableBean>(this, Type.Artist.name)
    private val publisherListAdapter = GenericStringListAdapter<PublisherTableBean>(this, Type.Publisher.name)
    private val languageListAdapter = GenericStringListAdapter<LanguageTableBean>(this, Type.Language.name)
    private val playingModListAdapter = GenericStringListAdapter<PlayingModTableBean>(this, Type.PlayingMode.name)
    private val mechanismListAdapter = GenericStringListAdapter<MechanismTableBean>(this, Type.Mechanism.name)
    private val tagListAdapter = GenericStringListAdapter<TagTableBean>(this, Type.Tag.name)
    private val topicListAdapter = GenericStringListAdapter<TopicTableBean>(this, Type.Topic.name)

    private val addOnAdapter = GenericListAdapter<DesignerWithAddOn>(this)
    private val multiAddOnAdapter = GenericListAdapter<DesignerWithMultiAddOn>( this)


    private val gameId by lazy{intent.extras!!.getSerializable(SerialKey.GameId.name) as Long}


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
       val commonRvAdapterList: ArrayList<Pair<RecyclerView, GenericStringListAdapter<out ID>>> =
           arrayListOf(Pair(binding.rvDesigner,designerListAdapter), Pair(binding.rvArtist, artistListAdapter),
           Pair(binding.rvPublisher,publisherListAdapter),Pair(binding.rvPlayingMode, playingModListAdapter),
           Pair(binding.rvLanguage, languageListAdapter), Pair(binding.rvMechanism, mechanismListAdapter),
               Pair(binding.rvTag, tagListAdapter), Pair(binding.rvTopic, topicListAdapter))
        fillCommonRV(commonRvAdapterList)
        appInstance.database.gameDao().getTagsOfGame(gameId).observe(this, {it?.let{tagListAdapter.submitList(it)}})
        appInstance.database.gameDao().getTopicsOfGame(gameId).observe(this, {it?.let{topicListAdapter.submitList(it)}})
        appInstance.database.gameDao().getMechanismsOfGame(gameId).observe(this, {it?.let{mechanismListAdapter.submitList(it)}})
        fillCommonTextView()
        binding.rvGameDetailAddOn.adapter = addOnAdapter
        layout(binding.rvGameDetailAddOn)
        appInstance.database.addOnDao().getDesignerWithAddOnOfGame(gameId).observe(this, {it?.let{addOnAdapter.submitList(it)}})
        binding.rvGameDetailMultiAddOn.adapter = multiAddOnAdapter
        layout(binding.rvGameDetailMultiAddOn)
        appInstance.database.multiAddOnDao().getDesignerWithMultiAddOnOfGame(gameId).observe(this, {it?.let{multiAddOnAdapter.submitList(it)}})





    }

    fun fillCommonTextView(){
        appInstance.database.gameDao().getById(gameId).asLiveData().observe(this, {if(it.size > 0) it?.let{
            loadImage(it[0].name, binding.ivDetails)
            binding.tvGameDetailName.text = it[0].name
            binding.tvGameDetailAge.text = "${it[0].age} et +"
            binding.tvGameDetailPlayingTime.text = "jusqu'à ${it[0].max_time} minutes"
            binding.tvGameDetailPlayer.text = "de ${it[0].player_min} à ${it[0].player_max} joueurs"} })
        appInstance.database.gameDao().getDifficultyOfGame(gameId).observe(this, {
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

        appInstance.database.gameDao().getDesignersOfGame(gameId).observe(this, {it?.let{designerListAdapter.submitList(it)}})
        appInstance.database.gameDao().getArtistsOfGame(gameId).observe(this, {it?.let{artistListAdapter.submitList(it)}})
        appInstance.database.gameDao().getPublishersOfGame(gameId).observe(this, {it?.let{publisherListAdapter.submitList(it)}})
        appInstance.database.gameDao().getPlayingModsOfGame(gameId).observe(this, {it?.let{playingModListAdapter.submitList(it)}})
        appInstance.database.gameDao().getLanguagesOfGame(gameId).observe(this, {it?.let{languageListAdapter.submitList(it)}})


    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){

            MenuId.DeleteThis.ordinal -> {

                AlertDialog.Builder(this).setMessage("Voulez vous vraiment supprimer ce jeu?").setTitle("Attention")
                .setPositiveButton("ok"){
                        dialog, which -> run{

                    CoroutineScope(SupervisorJob()).launch{
                        val list = appInstance.database.gameDao().getObjectById(gameId)
                        if(list.isNotEmpty())DbMethod().delete(list[0])
                    }

                        }
                }.setNegativeButton("cancel"){
                        dialog, which -> kotlin.run {


                    Toast.makeText(this, "Annulé", Toast.LENGTH_SHORT).show()
                }
                }
                .show()}
            MenuId.ModifyThis.ordinal -> TODO()
        }
        return super.onOptionsItemSelected(item)
    }








}


