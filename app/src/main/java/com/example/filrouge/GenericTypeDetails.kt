package com.example.filrouge

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.example.filrouge.databinding.ActivityGenericTypeDetailsBinding


class GenericTypeDetails : CommonType(), OnGenericListListener {

    private val binding: ActivityGenericTypeDetailsBinding by lazy{ ActivityGenericTypeDetailsBinding.inflate(layoutInflater) }
    private val addOns = ArrayList<AddOnBean>()
    private val multiAddOns = ArrayList<MultiAddOnBean>()
    private val games = ArrayList<GameBean>()
    private val genericAddOnAdapter = GenericAdapter(addOns ,this)
    private val genericMultiAddOnAdapter = GenericAdapter(multiAddOns, this)
    private val genericGameAdapater = GenericAdapter(games, this)
    private val type:String by lazy{intent.extras!!.getString(SerialKey.Type.name, "")}
    private val name:String by lazy{intent.extras!!.getString(SerialKey.Name.name, "")}


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.rvGenericDetailGame.adapter = genericGameAdapater
        binding.rvGenericDetailGame.layoutManager = GridLayoutManager(this, 1)
        binding.rvGenericDetailGame.addItemDecoration(MarginItemDecoration(5))

        binding.rvGenericDetailAddOn.adapter = genericAddOnAdapter
        binding.rvGenericDetailAddOn.layoutManager = GridLayoutManager(this, 1)
        binding.rvGenericDetailAddOn.addItemDecoration(MarginItemDecoration(5))

        binding.rvGenericDetailMultiAddOn.adapter = genericMultiAddOnAdapter
        binding.rvGenericDetailMultiAddOn.layoutManager = GridLayoutManager(this, 1)
        binding.rvGenericDetailMultiAddOn.addItemDecoration(MarginItemDecoration(5))

        when (type){
            Type.Designer.name -> {
                games.addAll(allGames.filter{it.designers.contains(name)})
                addOns.addAll(allAddOns.filter{it.designers.contains(name)})
                multiAddOns.addAll(allMultiAddOns.filter{it.designers.contains(name)})
            }
            Type.Artist.name -> {
                games.addAll(allGames.filter{it.artists.contains(name)})
                addOns.addAll(allAddOns.filter{it.artists.contains(name)})
                multiAddOns.addAll(allMultiAddOns.filter{it.artists.contains(name)})
            }
            Type.Publisher.name -> {
                games.addAll(allGames.filter{it.publishers.contains(name)})
                addOns.addAll(allAddOns.filter{it.publishers.contains(name)})
                multiAddOns.addAll(allMultiAddOns.filter{it.publishers.contains(name)})
            }
            Type.Difficulty.name -> {
                games.addAll(allGames.filter{it.difficulty == name})
                addOns.addAll(allAddOns.filter{it.difficulty == name})
                multiAddOns.addAll(allMultiAddOns.filter{it.difficulty == name})
            }
            Type.PlayingMode.name -> {
                games.addAll(allGames.filter{it.playing_mode.contains(name)})
                addOns.addAll(allAddOns.filter{it.playing_mode.contains(name)})
                multiAddOns.addAll(allMultiAddOns.filter{it.playing_mode.contains(name)})
            }
            Type.Language.name -> {
                games.addAll(allGames.filter{it.language.contains(name)})
                addOns.addAll(allAddOns.filter{it.language.contains(name)})
                multiAddOns.addAll(allMultiAddOns.filter{it.language.contains(name)})
            }
            Type.Tag.name -> games.addAll(allGames.filter{it.tags.contains(name)})
            Type.Mechanism.name -> games.addAll(allGames.filter{it.mechanism.contains(name)})
            Type.Topic.name -> games.addAll(allGames.filter{it.topics.contains(name)})
            Type.Search.name -> {
                val searchResult = intent.extras!!.getSerializable(SerialKey.SearchResult.name) as ApiResponse
                games.addAll(searchResult.games)
                addOns.addAll(searchResult.add_ons)
                multiAddOns.addAll(searchResult.multi_add_ons)
            }
        }

        binding.tvGenericDetailName.text = name
        genericGameAdapater.notifyDataSetChanged()
        genericAddOnAdapter.notifyDataSetChanged()
        genericMultiAddOnAdapter.notifyDataSetChanged()
    }




}