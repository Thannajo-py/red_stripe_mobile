package com.example.filrouge.activity

import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.example.filrouge.*
import com.example.filrouge.bean.AddOnTableBean
import com.example.filrouge.bean.DesignerWithAddOn
import com.example.filrouge.bean.DesignerWithGame
import com.example.filrouge.bean.DesignerWithMultiAddOn
import com.example.filrouge.databinding.ActivityGenericTypeDetailsBinding


class GenericTypeDetails : CommonType() {

    private val binding: ActivityGenericTypeDetailsBinding by lazy{ ActivityGenericTypeDetailsBinding.inflate(layoutInflater) }
    private val genericAddOnAdapter = GenericListAdapter(this)
    private val genericMultiAddOnAdapter = GenericListAdapter(this)
    private val genericGameAdapter = GenericListAdapter(this)
    private val type:String by lazy{intent.extras!!.getString(SerialKey.Type.name, "")}
    private val id:Long by lazy{intent.extras!!.getLong(SerialKey.GenericId.name, 0L)}
    private val name:String by lazy{intent.extras!!.getString(SerialKey.Name.name, "")}
    private val db = appInstance.database


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.rvGenericDetailGame.adapter = genericGameAdapter
        layout(binding.rvGenericDetailGame)

        binding.rvGenericDetailAddOn.adapter = genericAddOnAdapter
        layout(binding.rvGenericDetailAddOn)

        binding.rvGenericDetailMultiAddOn.adapter = genericMultiAddOnAdapter
        layout(binding.rvGenericDetailMultiAddOn)


        when (type){
            Type.Designer.name -> {
                db.gameDao().getWithDesignerFromDesignerId(id).observe(this, {it?.let{genericGameAdapter.submitList(it)}})
                db.addOnDao().getWithDesignerFromDesignerId(id).observe(this, {it?.let{genericAddOnAdapter.submitList(it)}})
                db.multiAddOnDao().getWithDesignerFromDesignerId(id).observe(this, {it?.let{genericMultiAddOnAdapter.submitList(it)}})

            }
            Type.Artist.name -> {
                db.gameDao().getWithDesignerFromArtistId(id).observe(this, {it?.let{genericGameAdapter.submitList(it)}})
                db.addOnDao().getWithDesignerFromArtistId(id).observe(this, {it?.let{genericAddOnAdapter.submitList(it)}})
                db.multiAddOnDao().getWithDesignerFromArtistId(id).observe(this, {it?.let{genericMultiAddOnAdapter.submitList(it)}})
            }
            Type.Publisher.name -> {
                db.gameDao().getWithDesignerFromPublisherId(id).observe(this, {it?.let{genericGameAdapter.submitList(it)}})
                db.addOnDao().getWithDesignerFromPublisherId(id).observe(this, {it?.let{genericAddOnAdapter.submitList(it)}})
                db.multiAddOnDao().getWithDesignerFromPublisherId(id).observe(this, {it?.let{genericMultiAddOnAdapter.submitList(it)}})
            }
            Type.Difficulty.name -> {
                db.gameDao().getWithDesignerFromDifficultyId(id).observe(this, {it?.let{genericGameAdapter.submitList(it)}})
                db.addOnDao().getWithDesignerFromDifficultyId(id).observe(this, {it?.let{genericAddOnAdapter.submitList(it)}})
                db.multiAddOnDao().getWithDesignerFromDifficultyId(id).observe(this, {it?.let{genericMultiAddOnAdapter.submitList(it)}})
            }
            Type.PlayingMode.name -> {
                db.gameDao().getWithDesignerFromPlayingModId(id).observe(this, {it?.let{genericGameAdapter.submitList(it)}})
                db.addOnDao().getWithDesignerFromPlayingModId(id).observe(this, {it?.let{genericAddOnAdapter.submitList(it)}})
                db.multiAddOnDao().getWithDesignerFromPlayingModId(id).observe(this, {it?.let{genericMultiAddOnAdapter.submitList(it)}})
            }
            Type.Language.name -> {
                db.gameDao().getWithDesignerFromLanguageId(id).observe(this, {it?.let{genericGameAdapter.submitList(it)}})
                db.addOnDao().getWithDesignerFromLanguageId(id).observe(this, {it?.let{genericAddOnAdapter.submitList(it)}})
                db.multiAddOnDao().getWithDesignerFromLanguageId(id).observe(this, {it?.let{genericMultiAddOnAdapter.submitList(it)}})

            }
            Type.Tag.name -> db.gameDao().getWithDesignerFromTagId(id).observe(this, {it?.let{genericGameAdapter.submitList(it)}})
            Type.Mechanism.name -> db.gameDao().getWithDesignerFromMechanismId(id).observe(this, {it?.let{genericGameAdapter.submitList(it)}})
            Type.Topic.name -> db.gameDao().getWithDesignerFromTopicId(id).observe(this, {it?.let{genericGameAdapter.submitList(it)}})
            Type.Search.name -> {
                val search = intent.extras!!.getSerializable(SerialKey.QueryContent.name) as SearchQuery
                db.gameDao().getWithDesignerFromSearchQuery(search.name, search.designer, search.artist,
                search.publisher, search.playerMin, search.playerMax, search.maxTime, search.difficulty, search.age,
                search.playingMod, search.language, search.tag, search.topic, search.mechanism)
                    .observe(this, {it?.let{genericGameAdapter.submitList(it)}})
                if(search.topic == null && search.tag == null && search.mechanism == null){
                    db.addOnDao().getWithDesignerFromSearchQuery(search.name, search.designer, search.artist,
                        search.publisher, search.playerMin, search.playerMax, search.maxTime, search.difficulty, search.age,
                        search.playingMod, search.language)
                        .observe(this, {it?.let{genericAddOnAdapter.submitList(it)}})

                    db.multiAddOnDao().getWithDesignerFromSearchQuery(search.name, search.designer, search.artist,
                        search.publisher, search.playerMin, search.playerMax, search.maxTime, search.difficulty, search.age,
                        search.playingMod, search.language)
                        .observe(this, {it?.let{genericMultiAddOnAdapter.submitList(it)}})

                }
            }
        }

        binding.tvGenericDetailName.text = name

    }




}