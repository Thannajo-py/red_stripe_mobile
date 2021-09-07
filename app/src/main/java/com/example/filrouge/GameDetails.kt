package com.example.filrouge

import android.content.Intent
import android.os.Bundle
import com.example.filrouge.databinding.ActivityGameDetailsBinding

class GameDetails : CommonType(), AddonAdapter.onAddOnListListener, MultiAddonAdapter.multiAddOnListListener {

    val binding: ActivityGameDetailsBinding by lazy{ ActivityGameDetailsBinding.inflate(layoutInflater) }

    val addOns = ArrayList<AddOnBean>()
    val multiAddOns = ArrayList<MultiAddOnBean>()
    val addOnAdapter = AddonAdapter(addOns ,this)
    val multiAddOnAdapter = MultiAddonAdapter(multiAddOns, this)


    val tags = ArrayList<String>()
    val tagAdapter = GenericTypeAdapater(tags, this, Type.Tag.name)
    val mechanism = ArrayList<String>()
    val mechanismAdapter = GenericTypeAdapater(mechanism, this, Type.Mechanism.name)
    val topics = ArrayList<String>()
    val topicAdapter = GenericTypeAdapater(topics, this, Type.Topic.name)

    val game:GameBean by lazy{intent.extras!!.getSerializable(SerialKey.Game.name) as GameBean}

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

    override fun onAddOnClick(datum: AddOnBean) {
        intent = Intent(this, AddOnDetails::class.java)
        intent.putExtra(SerialKey.AddOn.name, datum)
        intent.putExtra(SerialKey.ParentGame.name, game)
        startActivity(intent)
        finish()
    }

    override fun onMultiAddOnClick(datum: MultiAddOnBean) {
        intent = Intent(this, MultiAddOnDetails::class.java)
        intent.putExtra(SerialKey.MultiAddOn.name, datum)
        intent.putExtra(SerialKey.ParentGame.name, game)
        startActivity(intent)
        finish()
    }




}


