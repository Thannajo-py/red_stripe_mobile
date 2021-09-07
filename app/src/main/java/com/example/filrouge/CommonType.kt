package com.example.filrouge

import android.content.Intent
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding


abstract class CommonType : AppCompatActivity(), GenericTypeAdapater.GenericListener{


    val designers = ArrayList<String>()
    val designerAdapter = GenericTypeAdapater(designers, this, Type.Designer.name)

    val artists = ArrayList<String>()
    val artistAdapter = GenericTypeAdapater(artists, this, Type.Artist.name)

    val publishers = ArrayList<String>()
    val publisherAdapter = GenericTypeAdapater(publishers, this, Type.Publisher.name)

    val languages = ArrayList<String>()
    val languageAdapter = GenericTypeAdapater(languages, this, Type.Language.name)

    val playing_mode = ArrayList<String>()
    val playingModeAdapter = GenericTypeAdapater(playing_mode, this, Type.PlayingMode.name)

    override fun onGenericClick(datum: String, type:String) {
        intent = Intent(this, GenericTypeDetails::class.java)
        intent.putExtra(SerialKey.Type.name, type)
        intent.putExtra(SerialKey.Name.name, datum)
        startActivity(intent)
        finish()
    }

    fun layout(list: RecyclerView){
        list.layoutManager = GridLayoutManager(this,1)
        list.addItemDecoration(MarginItemDecoration(5))
    }

    fun <T, U:RecyclerView.ViewHolder>loadRv(rv:RecyclerView, list:ArrayList<T>, adapter:RecyclerView.Adapter<U>, content:ArrayList<T>){
        rv.adapter = adapter
        layout(rv)
        list.clear()
        list.addAll(content)
        adapter.notifyDataSetChanged()
    }

    fun fillCommonTextView(tvDifficulty:TextView, tvName:TextView, tvPlayer:TextView,
                           tvAge:TextView, element:CommonBase){
        tvDifficulty.text = "${element.difficulty?:"unknown"}"
        tvDifficulty.setOnClickListener {
            intent = Intent(this, GenericTypeDetails::class.java)
            intent.putExtra(SerialKey.Type.name, Type.Difficulty.name)
            intent.putExtra(SerialKey.Name.name, element.difficulty)
            startActivity(intent)
            finish()
        }
        tvName.text = "${element.name}"
        tvPlayer.text = "de ${element.player_min} à ${element.player_max} joueurs"
        tvAge.text = "${element.age} et +"

    }

    fun fillCommonRv(rvDesigner:RecyclerView, rvArtist:RecyclerView, rvPublisher:RecyclerView,
    rvLanguage:RecyclerView, rvPlayingMod:RecyclerView, element:CommonBase){
        loadRv(rvDesigner, designers, designerAdapter, element.designers)
        loadRv(rvArtist, artists, artistAdapter, element.artists)
        loadRv(rvPublisher, publishers, publisherAdapter, element.publishers)
        loadRv(rvLanguage, languages, languageAdapter, element.language)
        loadRv(rvPlayingMod, playing_mode, playingModeAdapter, element.playing_mode)
    }


}