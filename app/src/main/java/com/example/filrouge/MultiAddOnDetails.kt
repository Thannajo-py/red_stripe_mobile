package com.example.filrouge

import android.os.Bundle
import com.example.filrouge.databinding.ActivityMultiAddOnDetailBinding

class MultiAddOnDetails : CommonType() {

    private val binding: ActivityMultiAddOnDetailBinding by lazy{ ActivityMultiAddOnDetailBinding.inflate(layoutInflater) }
    private val parent:GameBean? by lazy{intent.extras!!.getSerializable(SerialKey.ParentGame.name) as GameBean?}
    private val multiAddOn:MultiAddOnBean by lazy{intent.extras!!.getSerializable(SerialKey.MultiAddOn.name) as MultiAddOnBean}
    private val gamesList = ArrayList<String>()
    private val adapter = MultiAddOnGameAdapter(gamesList, this)



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        fillCommonRv(binding.rvDesigner, binding.rvArtist, binding.rvPublisher, binding.rvLanguage,
                binding.rvPlayingMode, multiAddOn)

        fillCommonTextView(binding.tvDifficulty, binding.tvMultiAddOnDetailName, binding.tvMultiAddOnDetailPlayer, binding.tvMultiAddOnDetailAge, multiAddOn)
        binding.tvMultiAddOnDetailPlayingTime.text = "jusqu'à ${multiAddOn.max_time} minutes" + if(parent != null && parent!!.by_player == true) "/ Joueur" else ""

        loadRv(binding.rvMultiAddOn, gamesList, adapter, multiAddOn.games)

    }





}