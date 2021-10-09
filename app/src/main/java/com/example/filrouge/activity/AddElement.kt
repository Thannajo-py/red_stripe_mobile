package com.example.filrouge.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.filrouge.*
import com.example.filrouge.bean.*
import com.example.filrouge.dao.CommonDao
import com.example.filrouge.dao.GameDao
import com.example.filrouge.databinding.ActivityAddElementBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class AddElement : CommonType(), View.OnClickListener,
    GenericIDCbListener, GenericCommonGameCbListener, GenericOneToOneListener {

    private val binding by lazy{ActivityAddElementBinding.inflate(layoutInflater)}

    private val addedStringContent: ArrayList<ArrayList<String>> = arrayListOf(ArrayList(), ArrayList(), ArrayList()
        , ArrayList(), ArrayList(), ArrayList(), ArrayList(), ArrayList())

    private val addedEditText: ArrayList<ArrayList<EditText>> = arrayListOf(ArrayList(), ArrayList(), ArrayList()
        , ArrayList(), ArrayList(), ArrayList(), ArrayList(), ArrayList())

    private val designerAdapter = GenericIDListCbAdapter<DesignerTableBean>(this, Type.Designer.name, addedStringContent[AddedContent.Designer.ordinal])
    private val artistAdapter = GenericIDListCbAdapter<ArtistTableBean>(this, Type.Artist.name, addedStringContent[AddedContent.Artist.ordinal])
    private val publisherAdapter = GenericIDListCbAdapter<PublisherTableBean>(this, Type.Publisher.name, addedStringContent[AddedContent.Publisher.ordinal])
    private val languageAdapter = GenericIDListCbAdapter<LanguageTableBean>(this, Type.Language.name, addedStringContent[AddedContent.Language.ordinal])
    private val playingModeAdapter = GenericIDListCbAdapter<PlayingModTableBean>(this, Type.PlayingMode.name, addedStringContent[AddedContent.PlayingMod.ordinal])

    private val difficulty:DifficultyTableBean? = null
    private val difficultyAdapter = OneToOneListCbAdapter(this, difficulty)

    private val tagAdapter = GenericIDListCbAdapter<TagTableBean>(this, Type.Tag.name, addedStringContent[AddedContent.Tag.ordinal])

    private val topicAdapter = GenericIDListCbAdapter<TopicTableBean>(this, Type.Topic.name, addedStringContent[AddedContent.Topic.ordinal])

    private val mechanismAdapter = GenericIDListCbAdapter<MechanismTableBean>(this, Type.Mechanism.name, addedStringContent[AddedContent.Mechanism.ordinal])



    private val addedToListAddons = ArrayList<DesignerWithAddOn>()
    private val addOnAdapter = GenericCommonGameListCbAdapter<DesignerWithAddOn>(this, addedToListAddons)

    private val addedToListMultiAddOns = ArrayList<DesignerWithMultiAddOn>()
    private val multiAddOnAdapter = GenericCommonGameListCbAdapter<DesignerWithMultiAddOn>(this, addedToListMultiAddOns)

    private val addedToListGames = ArrayList<DesignerWithGame>()
    private val gamesAdapter = GenericCommonGameListCbAdapter<DesignerWithGame>(this, addedToListGames)

    private var game:DesignerWithGame? = null
    private val gameAdapter = OneToOneListCbAdapter( this, game)

    private val changedObjectId: Long by lazy{intent.getLongExtra(SerialKey.ToModifyDataId.name, 0L)}
    private val changedObjectType: String? by lazy{intent.getStringExtra(SerialKey.ToModifyDataType.name)}
    private val changedObjectName: String? by lazy{intent.getStringExtra(SerialKey.ToModifyDataName.name)}






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

        if(changedObjectId != 0L){
            CoroutineScope(SupervisorJob()).launch{
                changedObjectName?.run{
                    when(changedObjectType){
                        Type.Game.name -> if (appInstance.database.gameDao().getImage(this).isNotEmpty())  binding.cbDelImg.visibility = View.VISIBLE
                        Type.AddOn.name -> if (appInstance.database.addOnDao().getImage(this).isNotEmpty())  binding.cbDelImg.visibility = View.VISIBLE
                        Type.MultiAddOn.name -> if (appInstance.database.multiAddOnDao().getImage(this).isNotEmpty())  binding.cbDelImg.visibility = View.VISIBLE
                    }
                }


            }
            fillView(changedObjectType, changedObjectId)
        }
        val commonRvAdapterList: ArrayList<Pair<RecyclerView, GenericIDListCbAdapter<out ID>>> =
            arrayListOf(Pair(binding.rvDesigner,designerAdapter), Pair(binding.rvArtist, artistAdapter),
                Pair(binding.rvPublisher,publisherAdapter),Pair(binding.rvPlayingMode, playingModeAdapter),
                Pair(binding.rvLanguage, languageAdapter), Pair(binding.rvMechanism, mechanismAdapter),
                Pair(binding.rvTag, tagAdapter), Pair(binding.rvTopic, topicAdapter))
        fillCommonRV(commonRvAdapterList)
        fillPageRv()


        btnAddDeleteModule(binding.btnDesignerAdd, binding.btnDesignerDelete, binding.llDesigner, AddedContent.Designer.ordinal)
        btnAddDeleteModule(binding.btnArtistAdd, binding.btnArtistDelete, binding.llArtist, AddedContent.Artist.ordinal)
        btnAddDeleteModule(binding.btnPublisherAdd, binding.btnPublisherDelete, binding.llPublisher, AddedContent.Publisher.ordinal)
        btnAddDeleteModule(binding.btnLanguageAdd, binding.btnLanguageDelete, binding.llLanguage, AddedContent.Language.ordinal)
        btnAddDeleteModule(binding.btnTagAdd, binding.btnTagDelete, binding.llTag, AddedContent.Tag.ordinal)
        btnAddDeleteModule(binding.btnTopicAdd, binding.btnTopicDelete, binding.llTopic, AddedContent.Topic.ordinal)
        btnAddDeleteModule(binding.btnMechanismAdd, binding.btnMechanismDelete, binding.llMechanism, AddedContent.Mechanism.ordinal)
        btnAddDeleteModule(binding.btnPlayingModeAdd, binding.btnPlayingModeDelete, binding.llPlayingMode, AddedContent.PlayingMod.ordinal)



    }


    private fun btnAddDeleteModule(btnAdd:Button, btnDel:Button, ll:LinearLayout, position: Int){
        btnAdd.setOnClickListener {
            setOnClickAddButton(ll, position)
            btnDel.visibility = View.VISIBLE
        }

        btnDel.setOnClickListener {
            setOnClickDeleteButton(ll, position)
            if (addedEditText[position].isEmpty()) it.visibility = View.GONE
        }
    }

    private fun setOnClickAddButton(ll:LinearLayout, position:Int){
        val et = EditText(this)
        ll.addView(et)
        addedEditText[position].add(et)
    }



    private fun setOnClickDeleteButton(ll:LinearLayout, position:Int){
        if (addedEditText[position].isNotEmpty()){
            val et = addedEditText[position].last()
            ll.removeView(et)
            addedEditText[position].removeLast()
        }

    }

    override fun onClick(v: View?) {
        CoroutineScope(SupervisorJob()).launch {
            appInstance.database.runInTransaction {


                val name = binding.etNom.text.toString()
                if (binding.etNom.text.toString().isBlank()) {
                    Toast.makeText(
                        this@AddElement,
                        "le nom ne peut pas être vide",
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (
                    (changedObjectId == 0L ||
                            changedObjectName != name) &&
                    (binding.rbGame.isChecked && appInstance.database.gameDao().getByName(name)
                        .isNotEmpty() ||
                            binding.rbAddOn.isChecked && appInstance.database.addOnDao()
                        .getByName(name)
                        .isNotEmpty() ||
                            binding.rbMultiAddOn.isChecked && appInstance.database.multiAddOnDao()
                        .getByName(name).isNotEmpty())
                ) {

                    Toast.makeText(
                        this@AddElement,
                        "Un élément de la même catégorie portant le même nom existe déjà!",
                        Toast.LENGTH_SHORT
                    ).show()

                } else {

                    val player_min = testNull(binding.etNbPlayerMin.text.toString())?.toInt()
                    val player_max = testNull(binding.etNbPlayerMax.text.toString())?.toInt()
                    val playing_time = testNull(binding.etMaxTime.text.toString())
                    val designers = addAllEditText(
                        addedStringContent[AddedContent.Designer.ordinal],
                        binding.etDesigner,
                        addedEditText[AddedContent.Designer.ordinal]
                    )
                    val artists = addAllEditText(
                        addedStringContent[AddedContent.Artist.ordinal],
                        binding.etArtist,
                        addedEditText[AddedContent.Artist.ordinal]
                    )
                    val publishers = addAllEditText(
                        addedStringContent[AddedContent.Publisher.ordinal],
                        binding.etPublisher,
                        addedEditText[AddedContent.Publisher.ordinal]
                    )
                    val bgg_link = testNull(binding.etBggLink.text.toString())
                    val playing_mode = addAllEditText(
                        addedStringContent[AddedContent.PlayingMod.ordinal],
                        binding.etPlayingMode,
                        addedEditText[AddedContent.PlayingMod.ordinal]
                    )
                    val language = addAllEditText(
                        addedStringContent[AddedContent.Language.ordinal],
                        binding.etLanguage,
                        addedEditText[AddedContent.Language.ordinal]
                    )
                    val age = testNull(binding.etAge.text.toString())?.toInt()
                    val buying_price = testNull(binding.etBuyingPrice.text.toString())?.toInt() ?: 0
                    val stock = testNull(binding.etStock.text.toString())?.toInt()
                    val max_time = testNull(binding.etStock.text.toString())?.toInt()
                    val by_player = binding.rbByPlayerTrue.isChecked
                    val tags = addAllEditText(
                        addedStringContent[AddedContent.Tag.ordinal],
                        binding.etTag,
                        addedEditText[AddedContent.Tag.ordinal]
                    )
                    val topics = addAllEditText(
                        addedStringContent[AddedContent.Topic.ordinal],
                        binding.etTopic,
                        addedEditText[AddedContent.Topic.ordinal]
                    )
                    val mechanism = addAllEditText(
                        addedStringContent[AddedContent.Mechanism.ordinal],
                        binding.etMechanism,
                        addedEditText[AddedContent.Mechanism.ordinal]
                    )
                    val games = addedToListGames
                    val external_image = testNull(binding.etExternalImage.text.toString())

                    when (true) {
                        binding.rbGame.isChecked -> registerGame(
                            name,
                            player_min,
                            player_max,
                            playing_time,
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
                            addedToListAddons.map { it.name }.toCollection(ArrayList()),
                            addedToListMultiAddOns.map { it.name }.toCollection(ArrayList()),
                            external_image
                        )
                        binding.rbAddOn.isChecked -> registerAddOn(
                            name, player_min, player_max,
                            playing_time, designers, artists, publishers, bgg_link, playing_mode,
                            language, age, buying_price, stock, max_time, game, external_image
                        )
                        binding.rbMultiAddOn.isChecked -> registerMultiAddOn(
                            name,
                            player_min,
                            player_max,
                            playing_time,
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
                            games.map { it.name }.toCollection(ArrayList()),
                            external_image
                        )

                    }
                    if (binding.cbDelImg.isChecked) {
                        appInstance.database.ImageDao().deleteByName("$changedObjectName$changedObjectType")
                        startActivity(Intent(this@AddElement, ViewGamesActivity::class.java))
                        finish()
                    }
                }
            }

        }
    }

    private fun getDifficultyId(): Long?{
        if (binding.etDifficulty.text.toString().isNullOrBlank()) return null
        var difficultyId:Long? = null
        val difficultyL = appInstance.database.difficultyDao().getByName(binding.etDifficulty.text.toString())
        if(difficultyL.isNotEmpty()) difficultyId = difficultyL[0].id
        else difficultyId = appInstance.database.difficultyDao().insert(DifficultyTableBean(0,binding.etDifficulty.text.toString() ))

        return difficultyId
    }

    private fun addAllEditText(list:ArrayList<String>, et:EditText, etList: ArrayList<EditText>): ArrayList<String>{
        if (et.text.toString().isNotBlank()) list.add(et.text.toString().trim())
        list.addAll(etList.map{it.text.toString()}.filter{it.isNotBlank()})
        return list.toCollection(mutableSetOf()).toCollection(ArrayList())
    }





    private fun registerGame(name: String,
                     player_min: Int?,
                     player_max: Int?,
                     playing_time: String?,
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
    ) {
        var previous: GameTableBean? = null
        CoroutineScope(SupervisorJob()).launch {
            if (changedObjectType == Type.Game.name) {
                val gameL = appInstance.database.gameDao().getObjectById(changedObjectId)
                if (gameL.isNotEmpty()) {
                    previous = gameL[0]
                }
            }

            val game = GameTableBean(
                previous?.id ?: 0L,
                previous?.serverId,
                name,
                player_min,
                player_max,
                playing_time,
                getDifficultyId(),
                bgg_link,
                age,
                buying_price,
                stock,
                max_time,
                external_image,
                previous?.picture,
                by_player,
                true
            )
            appInstance.database.gameDao().insert(game)
            val dbMethod = DbMethod()
            if (changedObjectId != 0L) {
                when (changedObjectType) {
                    Type.Game.name -> {
                        val gameL = appInstance.database.gameDao().getObjectById(changedObjectId)
                        if (gameL.isNotEmpty()) dbMethod.delete_link(gameL[0])
                    }
                    Type.AddOn.name -> {
                        val gameL = appInstance.database.addOnDao().getObjectById(changedObjectId)
                        if (gameL.isNotEmpty()) dbMethod.delete(gameL[0])
                    }
                    Type.MultiAddOn.name -> {
                        val gameL =
                            appInstance.database.multiAddOnDao().getObjectById(changedObjectId)
                        if (gameL.isNotEmpty()) dbMethod.delete(gameL[0])

                    }
                }
            }
            dbMethod.insert_link(
                game, arrayListOf(
                    designers, artists, publishers, playing_mode, language, tags,
                    topics, mechanism, add_on, multi_add_on
                )
            )
        }
    }





    private fun registerAddOn(name: String,
                      player_min: Int?,
                      player_max: Int?,
                      playing_time: String?,
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
                      game: DesignerWithGame?,
                      external_image: String?
    ){
        var previous: AddOnTableBean? = null
        CoroutineScope(SupervisorJob()).launch {
            if (changedObjectType == Type.AddOn.name) {
                val gameL = appInstance.database.addOnDao().getObjectById(changedObjectId)
                if (gameL.isNotEmpty()) {
                    previous = gameL[0]
                }
            }
        }
        val addOn = AddOnTableBean(
            previous?.id?:0L,
            previous?.serverId,
            name,
            player_min,
            player_max,
            playing_time,
            getDifficultyId(),
            bgg_link,
            age,
            buying_price,
            stock,
            max_time,
            external_image,
            previous?.picture,
            game?.id,
            true
        )
        appInstance.database.addOnDao().insert(addOn)
        val dbMethod = DbMethod()
        if (changedObjectId != 0L) {
            when (changedObjectType) {
                Type.Game.name -> {
                    val gameL = appInstance.database.gameDao().getObjectById(changedObjectId)
                    if (gameL.isNotEmpty()) dbMethod.delete(gameL[0])
                }
                Type.AddOn.name -> {
                    val gameL = appInstance.database.addOnDao().getObjectById(changedObjectId)
                    if (gameL.isNotEmpty()) dbMethod.delete_link(gameL[0])
                }
                Type.MultiAddOn.name -> {
                    val gameL = appInstance.database.multiAddOnDao().getObjectById(changedObjectId)
                    if (gameL.isNotEmpty()) dbMethod.delete(gameL[0])
                }
            }
        }
        dbMethod.insert_link(
            addOn, arrayListOf(
                designers, artists, publishers, playing_mode, language
            )
        )
    }

    private fun registerMultiAddOn(name: String,
                      player_min: Int?,
                      player_max: Int?,
                      playing_time: String?,
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
        var previous: MultiAddOnTableBean? = null
        CoroutineScope(SupervisorJob()).launch {
            if (changedObjectType == Type.MultiAddOn.name) {
                val gameL = appInstance.database.multiAddOnDao().getObjectById(changedObjectId)
                if (gameL.isNotEmpty()) {
                    previous = gameL[0]
                }
            }
        }
        val multiAddOn = MultiAddOnTableBean(
            previous?.id?:0L,
            previous?.serverId,
            name,
            player_min,
            player_max,
            playing_time,
            getDifficultyId(),
            bgg_link,
            age,
            buying_price,
            stock,
            max_time,
            external_image,
            previous?.picture,
            true
        )

        appInstance.database.multiAddOnDao().insert(multiAddOn)
        val dbMethod = DbMethod()
        if (changedObjectId != 0L) {
            when (changedObjectType) {
                Type.Game.name -> {
                    val gameL = appInstance.database.gameDao().getObjectById(changedObjectId)
                    if (gameL.isNotEmpty()) dbMethod.delete(gameL[0])
                }
                Type.AddOn.name -> {
                    val gameL = appInstance.database.addOnDao().getObjectById(changedObjectId)
                    if (gameL.isNotEmpty()) dbMethod.delete(gameL[0])
                }
                Type.MultiAddOn.name -> {
                    val gameL =
                        appInstance.database.multiAddOnDao().getObjectById(changedObjectId)
                    if (gameL.isNotEmpty()) dbMethod.delete_link(gameL[0])
                }
            }
        }
        dbMethod.insert_link(
            multiAddOn, arrayListOf(
                designers, artists, publishers, playing_mode, language
            )
        )
        appInstance.database.runInTransaction {
            dbMethod.gameMultiAddOnLinkListByMultiAddOn(games, appInstance.database.gameDao(), appInstance.database.gameMultiAddOnDao(), multiAddOn.id)
        }
    }

    private fun setView(ll: LinearLayout){
        binding.llGame.visibility = View.GONE
        binding.llAddOn.visibility = View.GONE
        binding.llMultiAddOn.visibility = View.GONE
        ll.visibility = View.VISIBLE
    }

    private fun testNull(etContent:String) = if(etContent.isBlank()) null else etContent


    private fun fillPageRv() {
        binding.rvGameAddOn.adapter = addOnAdapter
        layout(binding.rvGameAddOn)
        appInstance.database.addOnDao().getAllWithDesigner().observe(this, {it?.let{addOnAdapter.submitList(it)}})

        binding.rvGameMultiAddOn.adapter = multiAddOnAdapter
        layout(binding.rvGameMultiAddOn)
        appInstance.database.multiAddOnDao().getAllWithDesigner().observe(this, {it?.let{multiAddOnAdapter.submitList(it)}})

        binding.rvGames.adapter = gamesAdapter
        layout(binding.rvGames)

        binding.rvGame.adapter = gameAdapter
        layout(binding.rvGame)

        appInstance.database.gameDao().getAllWithDesigner().observe(this, {it?.let{
            gameAdapter.submitList(it)
            gamesAdapter.submitList(it)
        }})

        binding.rvDifficulty.adapter = difficultyAdapter
        layout(binding.rvDifficulty)
        appInstance.database.difficultyDao().getAll().asLiveData().observe(this, {it?.let{difficultyAdapter.submitList(it)}})



    }




    override fun onGenericClick(name: String, type: String, cb:CheckBox) {
        when(type){
            Type.Designer.name -> listContentManager(addedStringContent[AddedContent.Designer.ordinal], name, cb)
            Type.Artist.name -> listContentManager(addedStringContent[AddedContent.Artist.ordinal], name, cb)
            Type.Publisher.name -> listContentManager(addedStringContent[AddedContent.Publisher.ordinal], name, cb)
            Type.Language.name -> listContentManager(addedStringContent[AddedContent.Language.ordinal], name, cb)
            Type.PlayingMode.name -> listContentManager(addedStringContent[AddedContent.PlayingMod.ordinal], name, cb)
            Type.Tag.name -> listContentManager(addedStringContent[AddedContent.Tag.ordinal], name, cb)
            Type.Topic.name -> listContentManager(addedStringContent[AddedContent.Topic.ordinal], name, cb)
            Type.Mechanism.name -> listContentManager(addedStringContent[AddedContent.Mechanism.ordinal], name, cb)
        }

    }

    fun <T>listContentManager(list:ArrayList<T>, content:T, cb:CheckBox){
        if (list.contains(content)) {
            list.remove(content)
            cb.isChecked = false
        }
        else {
            list.add(content)
            cb.isChecked = true
        }
    }



    override fun onGenericClick(datum: CommonGame, view:CheckBox) {
        when (datum){
            is DesignerWithGame -> listContentManager(addedToListGames, datum, view)
            is DesignerWithMultiAddOn -> listContentManager(addedToListMultiAddOns, datum, view)
            is DesignerWithAddOn -> listContentManager(addedToListAddons, datum, view)
        }
    }



    override fun onGenericClick(datum: OneToOne) {
        when(datum){
            is DesignerWithGame -> if (game == datum) game = null else game = datum
            is DifficultyTableBean -> binding.etDifficulty.setText(datum.name)
        }
    }

    fun fillView(type: String?, id:Long){
        when(type){
            Type.Game.name-> {
                fillCommonView(appInstance.database.gameDao(),id)
                CoroutineScope(SupervisorJob()).launch{
                    gameFill(appInstance.database.gameDao(), addedStringContent, id)
                }
            }
            Type.AddOn.name -> {
                fillCommonView(appInstance.database.addOnDao(),id)
                CoroutineScope(SupervisorJob()).launch{
                    genericFill(appInstance.database.addOnDao(), addedStringContent, id)
                    val gameL = appInstance.database.addOnDao().getGameFromAddOns(id)
                    if (gameL.isNotEmpty())game = gameL[0]
                }
            }
            Type.MultiAddOn.name -> {
                fillCommonView(appInstance.database.multiAddOnDao(),id)
                CoroutineScope(SupervisorJob()).launch{
                    genericFill(appInstance.database.multiAddOnDao(), addedStringContent, id)
                    addedToListGames.addAll(appInstance.database.multiAddOnDao().getGameObjectFromMultiAddOn(id))
                }
            }

        }

    }

    fun <T:ID>genericFill(dao: CommonDao<T>, list: ArrayList<ArrayList<String>>, id:Long){
        list[AddedContent.Designer.ordinal].addAll(dao.getDesignerObject(id).map{it.name})
        list[AddedContent.Artist.ordinal].addAll(dao.getArtistObject(id).map{it.name})
        list[AddedContent.Publisher.ordinal].addAll(dao.getPublisherObject(id).map{it.name}?:ArrayList())
        list[AddedContent.PlayingMod.ordinal].addAll(dao.getPlayingModObject(id).map{it.name}?:ArrayList())
        list[AddedContent.Language.ordinal].addAll(dao.getLanguageObject(id).map{it.name}?:ArrayList())
    }

    fun gameFill(dao: GameDao, list: ArrayList<ArrayList<String>>, id:Long){
        genericFill(dao, list, id)
        list[AddedContent.Tag.ordinal].addAll(dao.getTagObject(id).map{it.name}?:ArrayList())
        list[AddedContent.Topic.ordinal].addAll(dao.getTopicObject(id).map{it.name}?:ArrayList())
        list[AddedContent.Mechanism.ordinal].addAll(dao.getMechanismObject(id).map{it.name}?:ArrayList())
        addedToListAddons.addAll(appInstance.database.addOnDao().getDesignerWithAddOnOfGames(id))
        addedToListMultiAddOns.addAll(appInstance.database.multiAddOnDao().getDesignerWithMultiAddOnObjectOfGame(id))
    }


    fun <T : CommonComponent> fillCommonView(dao: CommonDao<T>, id: Long) {
        dao.getById(id).asLiveData().observe(this, {

            if (it.size > 0) it?.let {
                if(it[0] is GameTableBean) {
                    val x = it[0] as GameTableBean
                    binding.rbByPlayerTrue.isChecked = x.by_player?:false
                }

                binding.etNom.setText(it[0].name)
                binding.etAge.setText(it[0].age.toString())
                binding.etMaxTime.setText(it[0].max_time.toString())
                binding.etNbPlayerMin.setText(it[0].player_min.toString())
                binding.etNbPlayerMax.setText(it[0].player_max.toString())
                binding.etStock.setText(it[0].stock.toString())
                binding.etBuyingPrice.setText(it[0].buying_price.toString())
                binding.etBggLink.setText(it[0].bgg_link)
                binding.etExternalImage.setText(it[0].external_img)
                dao.getDifficulty(id).observe(this, {
                    if (it.size > 0) it?.let {
                        binding.etDifficulty.setText(it[0].name)
                    }
                })
            }
        })
    }

    fun fillCommonRV(listPairRecyclerViewAdapter: ArrayList<Pair<RecyclerView, GenericIDListCbAdapter<out ID>>>) {
        listPairRecyclerViewAdapter.forEach {
            it.first.adapter = it.second
            layout(it.first)
        }

        appInstance.database.designerDao().getAll().asLiveData()
            .observe(this, { it?.let { designerAdapter.submitList(it) } })
        appInstance.database.artistDao().getAll().asLiveData()
            .observe(this, { it?.let { artistAdapter.submitList(it) } })
        appInstance.database.publisherDao().getAll().asLiveData()
            .observe(this, { it?.let { publisherAdapter.submitList(it) } })
        appInstance.database.playingModDao().getAll().asLiveData()
            .observe(this, { it?.let { playingModeAdapter.submitList(it) } })
        appInstance.database.languageDao().getAll().asLiveData()
            .observe(this, { it?.let { languageAdapter.submitList(it) } })
        appInstance.database.tagDao().getAll().asLiveData()
            .observe(this, { it?.let { tagAdapter.submitList(it) } })
        appInstance.database.topicDao().getAll().asLiveData()
            .observe(this, { it?.let { topicAdapter.submitList(it) } })
        appInstance.database.mechanismDao().getAll().asLiveData()
            .observe(this, { it?.let { mechanismAdapter.submitList(it) } })


    }




}