package com.example.filrouge

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.filrouge.databinding.ActivityAddElementBinding

class AddElement : CommonType(), View.OnClickListener, OnGenericCbListListener, GenericCbListener {

    private val binding by lazy{ActivityAddElementBinding.inflate(layoutInflater)}
    private val sharedPreference by lazy{ SharedPreference(this)}

    private val addedStringContent: ArrayList<ArrayList<String>> = arrayListOf(ArrayList(), ArrayList(), ArrayList()
        , ArrayList(), ArrayList(), ArrayList(), ArrayList(), ArrayList())

    private val addedEditText: ArrayList<ArrayList<EditText>> = arrayListOf(ArrayList(), ArrayList(), ArrayList()
        , ArrayList(), ArrayList(), ArrayList(), ArrayList(), ArrayList())

    private val designerAdapter = GenericTypeCbAdapter(designers,this, Type.Designer.name, addedStringContent[AddedContent.Designer.ordinal])
    private val artistAdapter = GenericTypeCbAdapter(artists,this, Type.Artist.name, addedStringContent[AddedContent.Artist.ordinal])
    private val publisherAdapter = GenericTypeCbAdapter(publishers,this, Type.Publisher.name, addedStringContent[AddedContent.Publisher.ordinal])
    private val languageAdapter = GenericTypeCbAdapter(languages,this, Type.Language.name, addedStringContent[AddedContent.Language.ordinal])
    private val playingModeAdapter = GenericTypeCbAdapter(playing_mode,this, Type.PlayingMode.name, addedStringContent[AddedContent.PlayingMod.ordinal])

    private val difficulties = ArrayList<String>()
    private val difficultyAdapter = GenericTypeAdapter(difficulties, this, Type.Difficulty.name)

    private val tags = ArrayList<String>()
    private val tagAdapter = GenericTypeCbAdapter(tags, this, Type.Tag.name, addedStringContent[AddedContent.Tag.ordinal])

    private val topics = ArrayList<String>()
    private val topicAdapter = GenericTypeCbAdapter(topics, this, Type.Topic.name, addedStringContent[AddedContent.Topic.ordinal])

    private val mechanism = ArrayList<String>()
    private val mechanismAdapter = GenericTypeCbAdapter(mechanism, this, Type.Mechanism.name, addedStringContent[AddedContent.Mechanism.ordinal])



    private val addedToListAddons = ArrayList<AddOnBean>()
    private val freeAddOn = allAddOns.filter{it.game == null}.toCollection(ArrayList())
    private val addOns = ArrayList<AddOnBean>()
    private val addOnAdapter = GenericAdapterWithCheckBox(addOns ,this, addedToListAddons)

    private val addedToListMultiAddOns = ArrayList<MultiAddOnBean>()
    private val multiAddOns = ArrayList<MultiAddOnBean>()
    private val multiAddOnAdapter = GenericAdapterWithCheckBox(multiAddOns, this,
        addedToListMultiAddOns
    )

    private val addedToListGames = ArrayList<GameBean>()
    private val games = ArrayList<GameBean>()
    private val gamesAdapter = GenericAdapterWithCheckBox(games, this, addedToListGames)

    private val game = ArrayList<GameBean>()
    private val gameAdapter = GameAddOnAdapterWithCheckBox(game, this)

    private val changedObject: CommonBase? by lazy{intent.getSerializableExtra(SerialKey.ToModifyData.name) as CommonBase?}




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addOnGame = null
        setContentView(binding.root)
        binding.btnAdd.setOnClickListener(this)
        binding.rbGame.setOnClickListener{
            if (binding.rbGame.isChecked){
                setView(binding.llGame)
            }
        }
        binding.rbAddOn.setOnClickListener{
            if (binding.rbAddOn.isChecked){
                setView(binding.llAddOn)
            }
        }
        binding.rbMultiAddOn.setOnClickListener{
            if (binding.rbMultiAddOn.isChecked){
                setView(binding.llMultiAddOn)
            }
        }
        fillCommonRv(binding.rvDesigner, binding.rvArtist, binding.rvPublisher, binding.rvLanguage, binding.rvPlayingMode)
        fillPageRv(binding.rvDifficulty, binding.rvTag, binding.rvMechanism, binding.rvTopic)
        loadRv(binding.rvGameAddOn, addOns, addOnAdapter, freeAddOn)
        loadRv(binding.rvGameMultiAddOn, multiAddOns, multiAddOnAdapter, allMultiAddOns)
        loadRv(binding.rvGame, game, gameAdapter, allGames)
        loadRv(binding.rvGames, games, gamesAdapter, allGames)
        changedObject?.run{
            if (allImages.list_of_images.contains(this.name)){
                binding.cbDelImg.visibility = View.VISIBLE
            }
            setCommonElement(this)
            when(this){
                is GameBean -> {setGameBeanElement(this)}
                is AddOnBean -> {

                    addOnGame = loadAddOnGame(this)
                    binding.rbAddOn.isChecked = true
                    setView(binding.llAddOn)
                    allGames.filter{it.add_on.remove(this.name)}
                    gameAdapter.notifyDataSetChanged()
                }
                is MultiAddOnBean -> {
                    addedToListGames.addAll(allGames.filter{this.games.contains(it.name)})
                    binding.rbMultiAddOn.isChecked = true
                    setView(binding.llMultiAddOn)
                    gamesAdapter.notifyDataSetChanged()
                    allGames.forEach { it.multi_add_on.remove(this.name) }
                }
            }
        }
        btnAddDeleteModule(binding.btnDesignerAdd, binding.btnDesignerDelete, binding.llDesigner, AddedContent.Designer.ordinal)
        btnAddDeleteModule(binding.btnArtistAdd, binding.btnArtistDelete, binding.llArtist, AddedContent.Artist.ordinal)
        btnAddDeleteModule(binding.btnPublisherAdd, binding.btnPublisherDelete, binding.llPublisher, AddedContent.Publisher.ordinal)
        btnAddDeleteModule(binding.btnLanguageAdd, binding.btnLanguageDelete, binding.llLanguage, AddedContent.Language.ordinal)
        btnAddDeleteModule(binding.btnTagAdd, binding.btnTagDelete, binding.llTag, AddedContent.Tag.ordinal)
        btnAddDeleteModule(binding.btnTopicAdd, binding.btnTopicDelete, binding.llTopic, AddedContent.Topic.ordinal)
        btnAddDeleteModule(binding.btnMechanismAdd, binding.btnMechanismDelete, binding.llMechanism, AddedContent.Mechanism.ordinal)
        btnAddDeleteModule(binding.btnPlayingModeAdd, binding.btnPlayingModeDelete, binding.llPlayingMode, AddedContent.PlayingMod.ordinal)



    }

    fun loadAddOnGame(element:AddOnBean):GameBean?{
        val possibleGame = allGames.filter{it.name == element.game}
        return if (possibleGame.size == 1) possibleGame[0] else null
    }

    fun btnAddDeleteModule(btnAdd:Button, btnDel:Button, ll:LinearLayout, position: Int){
        btnAdd.setOnClickListener {
            setOnClickAddButton(ll, position)
            btnDel.visibility = View.VISIBLE
        }

        btnDel.setOnClickListener {
            setOnClickDeleteButton(ll, position)
            if (addedEditText[position].isEmpty()) it.visibility = View.GONE
        }
    }

    fun setOnClickAddButton(ll:LinearLayout, position:Int){
        val et = EditText(this)
        ll.addView(et)
        addedEditText[position].add(et)
    }



    fun setOnClickDeleteButton(ll:LinearLayout, position:Int){
        if (addedEditText[position].isNotEmpty()){
            val et = addedEditText[position].last()
            ll.removeView(et)
            addedEditText[position].removeLast()
        }

    }

    override fun onClick(v: View?) {
        val name = binding.etNom.text.toString()
        if(binding.etNom.text.toString().isBlank()){
            Toast.makeText(this, "le nom ne peut pas être vide", Toast.LENGTH_SHORT).show()
        }
        else if (
            (changedObject == null ||
            changedObject?.name != name) &&
            (binding.rbGame.isChecked && allGames.any{it.name == name} ||
            binding.rbAddOn.isChecked && allAddOns.any{it.name == name} ||
            binding.rbMultiAddOn.isChecked && allMultiAddOns.any{it.name == name})){

            Toast.makeText(this, "Un élément de la même catégorie portant le même nom existe déjà!", Toast.LENGTH_SHORT).show()

        }
        else {

            val player_min = testNull(binding.etNbPlayerMin.text.toString())?.toInt()
            val player_max = testNull(binding.etNbPlayerMax.text.toString())?.toInt()
            val playing_time = testNull(binding.etMaxTime.text.toString())
            val difficulty = testNull(binding.etDifficulty.text.toString())
            val designers = addAllEditText(addedStringContent[AddedContent.Designer.ordinal], binding.etDesigner, addedEditText[AddedContent.Designer.ordinal])
            val artists = addAllEditText(addedStringContent[AddedContent.Artist.ordinal], binding.etArtist, addedEditText[AddedContent.Artist.ordinal])
            val publishers = addAllEditText(addedStringContent[AddedContent.Publisher.ordinal], binding.etPublisher, addedEditText[AddedContent.Publisher.ordinal])
            val bgg_link = testNull(binding.etBggLink.text.toString())
            val playing_mode = addAllEditText(addedStringContent[AddedContent.PlayingMod.ordinal], binding.etPlayingMode, addedEditText[AddedContent.PlayingMod.ordinal])
            val language = addAllEditText(addedStringContent[AddedContent.Language.ordinal], binding.etLanguage, addedEditText[AddedContent.Language.ordinal])
            val age = testNull(binding.etAge.text.toString())?.toInt()
            val buying_price = testNull(binding.etBuyingPrice.text.toString())?.toInt()?:0
            val stock = testNull(binding.etStock.text.toString())?.toInt()
            val max_time = testNull(binding.etStock.text.toString())?.toInt()
            val by_player = binding.rbByPlayerTrue.isChecked
            val tags = addAllEditText(addedStringContent[AddedContent.Tag.ordinal], binding.etTag, addedEditText[AddedContent.Tag.ordinal])
            val topics = addAllEditText(addedStringContent[AddedContent.Topic.ordinal], binding.etTopic, addedEditText[AddedContent.Topic.ordinal])
            val mechanism = addAllEditText(addedStringContent[AddedContent.Mechanism.ordinal], binding.etMechanism, addedEditText[AddedContent.Mechanism.ordinal])
            val game = addOnGame
            val games = addedToListGames
            val external_image = testNull(binding.etExternalImage.text.toString())

            when (true) {
                binding.rbGame.isChecked -> registerGame(name, player_min, player_max,
                    playing_time, difficulty, designers, artists, publishers, bgg_link, playing_mode,
                    language, age, buying_price, stock, max_time, by_player, tags, topics, mechanism,
                    addedToListAddons.map{it.name}.toCollection(ArrayList()), addedToListMultiAddOns.map{it.name}.toCollection(ArrayList()), external_image)
                binding.rbAddOn.isChecked -> registerAddOn(name, player_min, player_max,
                    playing_time, difficulty, designers, artists, publishers, bgg_link, playing_mode,
                    language, age, buying_price, stock, max_time, game?.name, external_image)
                binding.rbMultiAddOn.isChecked -> registerMultiAddOn(name, player_min, player_max,
                    playing_time, difficulty, designers, artists, publishers, bgg_link, playing_mode,
                    language, age, buying_price, stock, max_time, games.map{it.name}.toCollection(ArrayList()), external_image)

            }
            if(binding.cbDelImg.isChecked){
                allImages.list_of_images.remove(changedObject?.name)
                sharedPreference.save(gson.toJson(allImages), SerialKey.AllImagesStorage.name)
            }
            refreshedSavedData(sharedPreference)
            startActivity(Intent(this,ViewGamesActivity::class.java))
            finish()
        }

    }


    private fun addAllEditText(list:ArrayList<String>, et:EditText, etList: ArrayList<EditText>): ArrayList<String>{
        if (et.text.toString().isNotBlank()) list.add(et.text.toString().trim())
        list.addAll(etList.map{it.text.toString()}.filter{it.isNotBlank()})
        return list.toCollection(mutableSetOf()).toCollection(ArrayList())
    }
    private fun <T:CommonBase, U:CommonBase>modifyElement(originalChangedList:ArrayList<U>, changedList:ArrayList<T>,
                                                  originalAllList:ArrayList<U>, allList:ArrayList<T>,
                                                  originalData: U, modifiedData:T){

        originalChangedList.removeIf{it == originalData}
        originalAllList.removeIf{it == originalData }
        changedList.add(modifiedData)
        allList.add(modifiedData)
    }




    private fun registerGame(name: String,
                     player_min: Int?,
                     player_max: Int?,
                     playing_time: String?,
                     difficulty: String?,
                     designers: ArrayList<String>,
                     artists: ArrayList<String>,
                     publishers: ArrayList<String>,
                     bgg_link: String?,
                     playing_mode: ArrayList<String>,
                     language: ArrayList<String>,
                     age: Int?,
                     buying_price:Int?,
                     stock: Int?,
                     max_time: Int?,
                     by_player: Boolean?,
                     tags: ArrayList<String>,
                     topics: ArrayList<String>,
                     mechanism: ArrayList<String>,
                     add_on: ArrayList<String>,
                     multi_add_on: ArrayList<String>,
                     external_image:String?
    ){
        var id:Int? = null
        if (changedObject is GameBean){
            id = (changedObject as GameBean).id
        }

        val game = GameBean( id,
            name,
            player_min,
            player_max,
            playing_time,
            difficulty,
            designers,
            artists,
            publishers,
            bgg_link,
            playing_mode,
            language,
            age,
            buying_price,
            stock,
            max_time,
            by_player,
            tags,
            topics,
            mechanism,
            add_on,
            multi_add_on,
            external_image,
            null
        )
        changedObject?.run{
            allGames.removeIf { it == changedObject }
            allAddOns.removeIf { it == changedObject }
            allMultiAddOns.removeIf { it == changedObject }
        }
        allGames.add(game)
        allAddOns.forEach { if (game.add_on.contains(it.name)) it.game = game.name }
        allMultiAddOns.forEach { if (game.multi_add_on.contains(it.name)) it.games.add(game.name)}

    }



    private fun registerAddOn(name: String,
                      player_min: Int?,
                      player_max: Int?,
                      playing_time: String?,
                      difficulty: String?,
                      designers: ArrayList<String>,
                      artists: ArrayList<String>,
                      publishers: ArrayList<String>,
                      bgg_link: String?,
                      playing_mode: ArrayList<String>,
                      language: ArrayList<String>,
                      age: Int?,
                      buying_price:Int?,
                      stock: Int?,
                      max_time: Int?,
                      game: String?,
                      external_image: String?
    ){
        var id:Int? = null
        if (changedObject is AddOnBean){
            id = (changedObject as AddOnBean).id
        }
        val addOn = AddOnBean(id,
            name,
            player_min,
            player_max,
            playing_time,
            difficulty,
            designers,
            artists,
            publishers,
            bgg_link,
            playing_mode,
            language,
            age,
            buying_price,
            stock,
            max_time,
            game,
            external_image,
            null
        )

        changedObject?.run{
            allGames.removeIf { it == changedObject }
            allAddOns.removeIf { it == changedObject }
            allMultiAddOns.removeIf { it == changedObject }
        }
        allAddOns.add(addOn)


    }
    private fun registerMultiAddOn(name: String,
                      player_min: Int?,
                      player_max: Int?,
                      playing_time: String?,
                      difficulty: String?,
                      designers: ArrayList<String>,
                      artists: ArrayList<String>,
                      publishers: ArrayList<String>,
                      bgg_link: String?,
                      playing_mode: ArrayList<String>,
                      language: ArrayList<String>,
                      age: Int?,
                      buying_price:Int?,
                      stock: Int?,
                      max_time: Int?,
                      games: ArrayList<String>,
                           external_image: String?){
        var id:Int? = null
        if (changedObject is MultiAddOnBean){
            id = (changedObject as MultiAddOnBean).id
        }
        val multiAddOn = MultiAddOnBean(
            id,
            name,
            player_min,
            player_max,
            playing_time,
            difficulty,
            designers,
            artists,
            publishers,
            bgg_link,
            playing_mode,
            language,
            age,
            buying_price,
            stock,
            max_time,
            games,
            external_image,
            null
        )

        changedObject?.run{
            allGames.removeIf { it == changedObject }
            allAddOns.removeIf { it == changedObject }
            allMultiAddOns.removeIf { it == changedObject }
        }
        allMultiAddOns.add(multiAddOn)
        allGames.filter{multiAddOn.games.contains(it.name)}.forEach{it.multi_add_on.add(multiAddOn.name)}
    }

    private fun setView(ll: LinearLayout){
        binding.llGame.visibility = View.GONE
        binding.llAddOn.visibility = View.GONE
        binding.llMultiAddOn.visibility = View.GONE
        ll.visibility = View.VISIBLE
    }

    private fun testNull(etContent:String) = if(etContent.isBlank()) null else etContent


    private fun fillPageRv(rvDifficulties: RecyclerView, rvTag: RecyclerView, rvMechanism: RecyclerView,
                     rvTopic: RecyclerView
    ){
        loadRv(rvDifficulties, difficulties, difficultyAdapter, allDifficulties())
        loadRv(rvTag, tags, tagAdapter, allTags())
        loadRv(rvMechanism, mechanism, mechanismAdapter, allMechanism())
        loadRv(rvTopic, topics, topicAdapter, allTopics())

    }




    private fun setCommonElement(it:CommonBase){
        binding.etNom.setText(it.name)
        addedStringContent[AddedContent.Designer.ordinal].addAll(it.designers)
        addedStringContent[AddedContent.Artist.ordinal].addAll(it.artists)
        addedStringContent[AddedContent.Publisher.ordinal].addAll(it.publishers)
        addedStringContent[AddedContent.Language.ordinal].addAll(it.language)
        addedStringContent[AddedContent.PlayingMod.ordinal].addAll(it.playing_mode)
        binding.etNbPlayerMin.setText(it.player_min?.toString()?:"")
        binding.etNbPlayerMax.setText(it.player_max?.toString()?:"")
        binding.etMaxTime.setText(it.max_time?.toString()?:"")
        binding.etDifficulty.setText(it.difficulty?:"")
        binding.etAge.setText(it.age?.toString()?:"")
        binding.etBggLink.setText(it.bgg_link?:"")
        binding.etStock.setText(it.stock?.toString()?:"")
        binding.etBuyingPrice.setText(it.buying_price?.toString()?:"")
        binding.etExternalImage.setText(it.external_img?:"")

    }

    private fun setGameBeanElement(game:GameBean){

        binding.rbGame.isChecked = true
        setView(binding.llGame)
        addedStringContent[AddedContent.Tag.ordinal].addAll(game.tags)
        addedStringContent[AddedContent.Topic.ordinal].addAll(game.topics)
        addedStringContent[AddedContent.Mechanism.ordinal].addAll(game.mechanism)

        tagAdapter.notifyDataSetChanged()
        topicAdapter.notifyDataSetChanged()
        mechanismAdapter.notifyDataSetChanged()

        addOns.addAll(allAddOns.filter{game.name == it.game})
        addedToListAddons.addAll(allAddOns.filter{game.name == it.game})
        addedToListMultiAddOns.addAll(allMultiAddOns.filter{it.games.contains(game.name)})
        multiAddOnAdapter.notifyDataSetChanged()
        addOnAdapter.notifyDataSetChanged()
        if (game.by_player == true) binding.rbByPlayerTrue.isChecked = true

        allMultiAddOns.filter{ game.multi_add_on.contains(it.name)}.forEach { it.games.remove(game.name) }
        allAddOns.filter{ it.game == game.name }.forEach { it.game = null }

    }

    override fun fillCommonRv(
        rvDesigner: RecyclerView,
        rvArtist: RecyclerView,
        rvPublisher: RecyclerView,
        rvLanguage: RecyclerView,
        rvPlayingMod: RecyclerView
    ) {
        loadRv(rvDesigner, designers, designerAdapter, allDesigners())
        loadRv(rvArtist, artists, artistAdapter, allArtists())
        loadRv(rvPublisher, publishers, publisherAdapter, allPublishers())
        loadRv(rvLanguage, languages, languageAdapter, allLanguages())
        loadRv(rvPlayingMod, playing_mode, playingModeAdapter, allPlayingModes())
    }

    override fun onElementClick(datum: CommonBase?, position: Int) {
        when(datum){
            is GameBean -> {
                addOnGame = if(addOnGame == datum) null else datum
                gameAdapter.notifyDataSetChanged()
                addedListManager(addedToListGames, datum, position, gamesAdapter)
            }

            is MultiAddOnBean -> addedListManager(addedToListMultiAddOns, datum, position, multiAddOnAdapter)

            is AddOnBean -> addedListManager(addedToListAddons, datum, position, addOnAdapter)

        }
    }

    override fun onGenericClick(datum: String, type: String, position: Int) {
        when(type){
            Type.Difficulty.name -> binding.etDifficulty.setText("${datum}")
            Type.Designer.name -> addedListManager(addedStringContent[AddedContent.Designer.ordinal], datum, position, designerAdapter)
            Type.Artist.name ->  addedListManager(addedStringContent[AddedContent.Artist.ordinal], datum, position, artistAdapter)
            Type.Publisher.name ->  addedListManager(addedStringContent[AddedContent.Publisher.ordinal], datum, position, publisherAdapter)
            Type.Language.name ->  addedListManager(addedStringContent[AddedContent.Language.ordinal], datum, position, languageAdapter)
            Type.PlayingMode.name -> addedListManager(addedStringContent[AddedContent.PlayingMod.ordinal], datum, position, playingModeAdapter)
            Type.Tag.name ->  addedListManager(addedStringContent[AddedContent.Tag.ordinal], datum, position, tagAdapter)
            Type.Mechanism.name ->  addedListManager(addedStringContent[AddedContent.Mechanism.ordinal], datum, position, mechanismAdapter)
            Type.Topic.name ->  addedListManager(addedStringContent[AddedContent.Topic.ordinal], datum, position, topicAdapter)
        }
    }

    fun <T, U:RecyclerView.ViewHolder>addedListManager(list:ArrayList<T>,
                                                       element:T, position: Int,
                                                       adapter:RecyclerView.Adapter<U>){
        if (list.contains(element)){
            list.remove(element)
        }
        else{
            list.add(element)
        }
        adapter.notifyItemChanged(position)
    }


}