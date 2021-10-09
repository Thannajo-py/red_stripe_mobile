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
import com.example.filrouge.databinding.ActivityAddOnDetailsBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class AddOnDetails : GameAddOnMultiAddOnCommonMenu() {

    private val binding: ActivityAddOnDetailsBinding by lazy{ ActivityAddOnDetailsBinding.inflate(layoutInflater) }
    private val gameId by lazy{ intent.extras!!.getSerializable(SerialKey.AddOnId.name) as Long }

    private val designerListAdapter = GenericStringListAdapter<DesignerTableBean>(this, Type.Designer.name)
    private val artistListAdapter = GenericStringListAdapter<ArtistTableBean>(this, Type.Artist.name)
    private val publisherListAdapter = GenericStringListAdapter<PublisherTableBean>(this, Type.Publisher.name)
    private val languageListAdapter = GenericStringListAdapter<LanguageTableBean>(this, Type.Language.name)
    private val playingModListAdapter = GenericStringListAdapter<PlayingModTableBean>(this, Type.PlayingMode.name)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        fillCommonTextView()
        val commonRvAdapterList: ArrayList<Pair<RecyclerView, GenericStringListAdapter<out ID>>> =
            arrayListOf(Pair(binding.rvDesigner,designerListAdapter), Pair(binding.rvArtist, artistListAdapter),
                Pair(binding.rvPublisher,publisherListAdapter),Pair(binding.rvPlayingMode, playingModListAdapter),
                Pair(binding.rvLanguage, languageListAdapter))
        fillCommonRV(commonRvAdapterList)

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
            MenuId.DeleteThis.ordinal -> AlertDialog.Builder(this).setMessage("Voulez vous vraiment supprimer cette extension?").setTitle("Attention")
                .setPositiveButton("ok"){
                        dialog, which -> CoroutineScope(SupervisorJob()).launch{
                        val list = appInstance.database.addOnDao().getObjectById(gameId)
                        if(list.isNotEmpty()){
                            DbMethod().delete(list[0])
                        }
                        startActivity(Intent(this@AddOnDetails, ViewGamesActivity::class.java))
                        finish()

                }
                }.setNegativeButton("cancel"){
                        dialog, which -> Toast.makeText(this, "Annulé", Toast.LENGTH_SHORT).show()
                }
                .show()
            MenuId.ModifyThis.ordinal -> startActivity(Intent(this, AddElement::class.java)
                .putExtra(SerialKey.ToModifyDataId.name, gameId)
                .putExtra(SerialKey.ToModifyDataName.name, binding.tvAddOnDetailName.text)
                .putExtra(SerialKey.ToModifyDataType.name, Type.AddOn.name))
        }
        return super.onOptionsItemSelected(item)
    }


    fun fillCommonTextView(){
        appInstance.database.addOnDao().getById(gameId).asLiveData().observe(this, {if(it.size > 0) it?.let{
            loadImage(it[0].name, binding.ivDetails, Type.AddOn.name)
            binding.tvAddOnDetailName.text = it[0].name
            it[0].age?.run{
                binding.tvAddOnDetailAge.text = "${it[0].age} et +"
            }?:run{

            }

            binding.tvAddOnDetailPlayingTime.text = "jusqu'à ${it[0].max_time} minutes"
            binding.tvAddOnDetailPlayer.text = "de ${it[0].player_min} à ${it[0].player_max} joueurs"} })
        appInstance.database.addOnDao().getDifficulty(gameId).observe(this, {
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

        appInstance.database.addOnDao().getDesigners(gameId).observe(this, {it?.let{designerListAdapter.submitList(it)}})
        appInstance.database.addOnDao().getArtists(gameId).observe(this, {it?.let{artistListAdapter.submitList(it)}})
        appInstance.database.addOnDao().getPublishers(gameId).observe(this, {it?.let{publisherListAdapter.submitList(it)}})
        appInstance.database.addOnDao().getPlayingMods(gameId).observe(this, {it?.let{playingModListAdapter.submitList(it)}})
        appInstance.database.addOnDao().getLanguages(gameId).observe(this, {it?.let{languageListAdapter.submitList(it)}})


    }



}