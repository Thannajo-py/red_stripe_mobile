package com.example.filrouge

import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.filrouge.databinding.ActivityGameDetailsBinding

class GameDetails : CommonType(), OnGenericListListener{

    private val binding: ActivityGameDetailsBinding by lazy{ ActivityGameDetailsBinding.inflate(layoutInflater) }
    private val sharedPreference by lazy {SharedPreference(this)}

    private val addOns = ArrayList<AddOnBean>()
    private val multiAddOns = ArrayList<MultiAddOnBean>()
    private val addOnAdapter = GenericAdapter(addOns ,this)
    private val multiAddOnAdapter = GenericAdapter(multiAddOns, this)


    private val tags = ArrayList<String>()
    private val tagAdapter = GenericTypeAdapter(tags, this, Type.Tag.name)
    private val mechanism = ArrayList<String>()
    private val mechanismAdapter = GenericTypeAdapter(mechanism, this, Type.Mechanism.name)
    private val topics = ArrayList<String>()
    private val topicAdapter = GenericTypeAdapter(topics, this, Type.Topic.name)

    private val game:GameBean by lazy{intent.extras!!.getSerializable(SerialKey.Game.name) as GameBean}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        fillCommonRv(binding.rvDesigner, binding.rvArtist, binding.rvPublisher, binding.rvLanguage,
                binding.rvPlayingMode, game)
        fillCommonTextView(binding.tvDifficulty, binding.tvGameDetailName, binding.tvGameDetailPlayer, binding.tvGameDetailAge, game)

        binding.tvGameDetailPlayingTime.text = "jusqu'à ${game.max_time} minutes" + if(game.by_player == true) "/ Joueur" else ""

        loadRv(binding.rvTopic, topics, topicAdapter, game.topics)
        loadRv(binding.rvMechanism, mechanism, mechanismAdapter, game.mechanism)
        loadRv(binding.rvTag, tags, tagAdapter, game.tags)
        loadRv(binding.rvGameDetailAddOn, addOns, addOnAdapter, game.add_on)
        loadRv(binding.rvGameDetailMultiAddOn, multiAddOns, multiAddOnAdapter, game.multi_add_on)


    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            MenuId.DeleteThis.ordinal -> AlertDialog.Builder(this).setMessage("Voulez vous vraiment supprimer ce jeu?").setTitle("Attention")
                .setPositiveButton("ok"){
                        dialog, which -> run{deleteFromList(game, allGames, addedGames, deletedGames, modifiedGames)
                                            refreshedSavedData(sharedPreference)
                        }
                }.setNegativeButton("cancel"){
                        dialog, which -> Toast.makeText(this, "Annulé", Toast.LENGTH_SHORT).show()
                }
                .show()
        }
        return super.onOptionsItemSelected(item)
    }





}


