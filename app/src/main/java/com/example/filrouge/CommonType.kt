package com.example.filrouge

import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Typeface
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File


abstract class CommonType : AppCompatActivity(), OnGenericListListener, GenericListener{



    protected val designers = ArrayList<String>()
    private val designerAdapter = GenericTypeAdapter(designers, this, Type.Designer.name)

    protected val artists = ArrayList<String>()
    private val artistAdapter = GenericTypeAdapter(artists, this, Type.Artist.name)

    protected val publishers = ArrayList<String>()
    private val publisherAdapter = GenericTypeAdapter(publishers, this, Type.Publisher.name)

    protected val languages = ArrayList<String>()
    private val languageAdapter = GenericTypeAdapter(languages, this, Type.Language.name)

    protected val playing_mode = ArrayList<String>()
    private val playingModeAdapter = GenericTypeAdapter(playing_mode, this, Type.PlayingMode.name)



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

    fun <T, U:RecyclerView.ViewHolder>loadRv(rv:RecyclerView, list:ArrayList<T>, adapter:RecyclerView.Adapter<U>, content:Collection<T>){
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

    open fun fillCommonRv(rvDesigner:RecyclerView, rvArtist:RecyclerView, rvPublisher:RecyclerView,
                     rvLanguage:RecyclerView, rvPlayingMod:RecyclerView){
        loadRv(rvDesigner, designers, designerAdapter, allDesigners())
        loadRv(rvArtist, artists, artistAdapter, allArtists())
        loadRv(rvPublisher, publishers, publisherAdapter, allPublishers())
        loadRv(rvLanguage, languages, languageAdapter, allLanguages())
        loadRv(rvPlayingMod, playing_mode, playingModeAdapter, allPlayingModes())

    }

    private fun onAddOnClick(datum: AddOnBean) {
        intent = Intent(this, AddOnDetails::class.java)
        intent.putExtra(SerialKey.AddOn.name, datum)
        val parentList = allGames.filter{datum.game == it.name}
        val parent:GameBean? = if(parentList.size == 1)parentList[0] else null
        intent.putExtra(SerialKey.ParentGame.name, parent)
        startActivity(intent)
        finish()
    }


    private fun onMultiAddOnClick(datum: MultiAddOnBean) {
        intent = Intent(this, MultiAddOnDetails::class.java)
        intent.putExtra(SerialKey.MultiAddOn.name, datum)
        val parent:GameBean? = null
        intent.putExtra(SerialKey.ParentGame.name, parent)
        startActivity(intent)
        finish()
    }

    override fun onElementClick(datum:CommonBase?) {
        when(datum){
            is GameBean? -> onGameClick(datum)
            is AddOnBean -> onAddOnClick(datum)
            is MultiAddOnBean -> onMultiAddOnClick(datum)
        }

    }
    open fun onGameClick(datum:GameBean?){
        if (datum == null){
            Toast.makeText(this, "Link Error!", Toast.LENGTH_SHORT).show()
        }
        else{
            intent = Intent(this,GameDetails::class.java)
            intent.putExtra(SerialKey.Game.name, datum)
            startActivity(intent)
            finish()
        }
    }

    fun <T:CommonBase>deleteFromList(game:T, allGames:ArrayList<T>, addedGames:ArrayList<T>, deletedGames:ArrayList<T>, modifiedGames:ArrayList<T>){
        allGames.removeIf{it == game}
        modifiedGames.removeIf{it == game}
        addedGames.removeIf{it == game}
        game.id?.run{deletedGames.add(game)}
    }



    fun refreshedSavedData(sharedPreference:SharedPreference){
        sharedPreference.save(gson.toJson(ApiResponse(allGames, allAddOns, allMultiAddOns)),SerialKey.APIStorage.name)
        sharedPreference.save(gson.toJson(ApiResponse(deletedGames, deletedAddOns, deletedMultiAddOns)),SerialKey.APIDeleteStorage.name)
        sharedPreference.save(gson.toJson(ApiResponse(modifiedGames, modifiedAddOns, modifiedMultiAddOns)),SerialKey.APIModifyStorage.name)
        sharedPreference.save(gson.toJson(ApiResponse(addedGames, addedAddOns, addedMultiAddOns)),SerialKey.APIAddStorage.name)
        startActivity(Intent(this,ViewGamesActivity::class.java))
        finish()
    }

    fun allDesigners():MutableSet<String>{
        val tempSet = mutableSetOf<String>()
        allGames.forEach{tempSet.addAll(it.designers)}
        allAddOns.forEach { tempSet.addAll(it.designers) }
        allMultiAddOns.forEach { tempSet.addAll(it.designers) }
        return tempSet
    }

    fun allArtists():MutableSet<String>{
        val tempSet = mutableSetOf<String>()
        allGames.forEach{tempSet.addAll(it.artists)}
        allAddOns.forEach { tempSet.addAll(it.artists) }
        allMultiAddOns.forEach { tempSet.addAll(it.artists) }
        return tempSet
    }
    fun allPublishers():MutableSet<String>{
        val tempSet = mutableSetOf<String>()
        allGames.forEach{tempSet.addAll(it.publishers)}
        allAddOns.forEach { tempSet.addAll(it.publishers) }
        allMultiAddOns.forEach { tempSet.addAll(it.publishers) }
        return tempSet
    }

    fun allPlayingModes():MutableSet<String>{
        val tempSet = mutableSetOf<String>()
        allGames.forEach{tempSet.addAll(it.playing_mode)}
        allAddOns.forEach { tempSet.addAll(it.playing_mode) }
        allMultiAddOns.forEach { tempSet.addAll(it.playing_mode) }
        return tempSet
    }

    fun allDifficulties():MutableSet<String>{
        val tempSet = mutableSetOf<String>()
        allGames.forEach{tempSet.add(it.difficulty?:"unknown")}
        allAddOns.forEach { tempSet.add(it.difficulty?:"unknown") }
        allMultiAddOns.forEach { tempSet.add(it.difficulty?:"unknown") }
        return tempSet
    }

    fun allLanguages():MutableSet<String>{
        val tempSet = mutableSetOf<String>()
        allGames.forEach{tempSet.addAll(it.language)}
        allAddOns.forEach { tempSet.addAll(it.language) }
        allMultiAddOns.forEach { tempSet.addAll(it.language) }
        return tempSet
    }

    fun allTags():MutableSet<String>{
        val tempSet = mutableSetOf<String>()
        allGames.forEach{tempSet.addAll(it.tags)}
        return tempSet
    }

    fun allMechanism():MutableSet<String>{
        val tempSet = mutableSetOf<String>()
        allGames.forEach{tempSet.addAll(it.mechanism)}
        return tempSet
    }

    fun allTopics():MutableSet<String>{
        val tempSet = mutableSetOf<String>()
        allGames.forEach{tempSet.addAll(it.topics)}
        return tempSet
    }

    fun<T:CommonBase> loadImage(element:T, image:ImageView){
        if (allImages.list_of_images.contains(element.name)){
            val file = File(image.context.filesDir, element.name)
            val compressedBitMap = BitmapFactory.decodeByteArray(file.readBytes(),0,file.readBytes().size)
            image.setImageBitmap(compressedBitMap)
        }
        else{
            image.setImageBitmap(null)
        }
    }















}