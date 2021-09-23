package com.example.filrouge

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.filrouge.databinding.ActivityAddElementBinding
import javax.crypto.ExemptionMechanism

class AddElement : CommonType(), View.OnClickListener {

    private val binding by lazy{ActivityAddElementBinding.inflate(layoutInflater)}
    private val sharedPreference by lazy{ SharedPreference(this)}

    private val addedStringContent = arrayListOf(ArrayList<String>(), ArrayList<String>(), ArrayList<String>()
        , ArrayList<String>(), ArrayList<String>(), ArrayList<String>(), ArrayList<String>(), ArrayList<String>())

    private val designerAdapter = GenericTypeCbAdapter(designers,this, Type.Designer.name, addedStringContent[AddedStringContent.Designer.ordinal])
    private val artistAdapter = GenericTypeCbAdapter(artists,this, Type.Artist.name, addedStringContent[AddedStringContent.Artist.ordinal])
    private val publisherAdapter = GenericTypeCbAdapter(publishers,this, Type.Publisher.name, addedStringContent[AddedStringContent.Publisher.ordinal])
    private val languageAdapter = GenericTypeCbAdapter(languages,this, Type.Language.name, addedStringContent[AddedStringContent.Language.ordinal])
    private val playingModeAdapter = GenericTypeCbAdapter(playing_mode,this, Type.PlayingMode.name, addedStringContent[AddedStringContent.PlayingMod.ordinal])

    private val difficulties = ArrayList<String>()
    private val difficultyAdapter = GenericTypeAdapter(difficulties, this, Type.Difficulty.name)

    private val tags = ArrayList<String>()
    private val tagAdapter = GenericTypeCbAdapter(tags, this, Type.Tag.name, addedStringContent[AddedStringContent.Tag.ordinal])

    private val topics = ArrayList<String>()
    private val topicAdapter = GenericTypeCbAdapter(topics, this, Type.Topic.name, addedStringContent[AddedStringContent.Topic.ordinal])

    private val mechanism = ArrayList<String>()
    private val mechanismAdapter = GenericTypeCbAdapter(mechanism, this, Type.Mechanism.name, addedStringContent[AddedStringContent.Mechanism.ordinal])



    private val addedAddons = ArrayList<AddOnBean>()
    private val addOns = ArrayList<AddOnBean>()
    private val addOnAdapter = GenericAdapterWithCheckBox(addOns ,this, addedAddons)

    private val addedMultiAddons = ArrayList<MultiAddOnBean>()
    private val multiAddOns = ArrayList<MultiAddOnBean>()
    private val multiAddOnAdapter = GenericAdapterWithCheckBox(multiAddOns, this, addedMultiAddons)

    private val addedGames = ArrayList<GameBean>()
    private val games = ArrayList<GameBean>()
    private val gamesAdapter = GenericAdapterWithCheckBox(games, this, addedGames)

    private val game = ArrayList<GameBean>()
    private val gameAdapter = GenericAdapter(game, this)

    private val changedObject: CommonBase? by lazy{intent.getSerializableExtra(SerialKey.ToModifyData.name) as CommonBase?}




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        loadRv(binding.rvGameAddOn, addOns, addOnAdapter, allAddOns)
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
                is AddOnBean -> addedAddons.addAll(allAddOns.filter{it == this})binding.etAddOnGame.setText(this.game)
                is MultiAddOnBean -> binding.etMultiAddOnGames.setText(this.games.joinToString(", "))
            }
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
            val designers = returnList(binding.etDesigner.text.toString())
            val artists = returnList(binding.etArtist.text.toString())
            val publishers = returnList(binding.etPublisher.text.toString())
            val bgg_link = testNull(binding.etBggLink.text.toString())
            val playing_mode = returnList(binding.etPlayingMode.text.toString())
            val language = returnList(binding.etLanguage.text.toString())
            val age = testNull(binding.etAge.text.toString())?.toInt()
            val buying_price = testNull(binding.etBuyingPrice.text.toString())?.toInt()?:0
            val stock = testNull(binding.etStock.text.toString())?.toInt()
            val max_time = testNull(binding.etStock.text.toString())?.toInt()
            val by_player = binding.rbByPlayerTrue.isChecked
            val tags = returnList(binding.etPlayingMode.text.toString())
            val topics = returnList(binding.etTopic.text.toString())
            val mechanism = returnList(binding.etMechanism.text.toString())
            val game = testNull(binding.etGameAddOn.text.toString())
            val games = returnList(binding.etGameAddOn.text.toString())
            val add_on = returnMemberByName(binding.etAddOnGame.text.toString(), allAddOns)
            val multi_add_on = returnMemberByName(binding.etMultiAddOnGames.text.toString(), allMultiAddOns)
            val external_image = testNull(binding.etExternalImage.text.toString())

            when (true) {
                binding.rbGame.isChecked -> registerGame(name, player_min, player_max,
                    playing_time, difficulty, designers, artists, publishers, bgg_link, playing_mode,
                    language, age, buying_price, stock, max_time, by_player, tags, topics, mechanism,
                    add_on, multi_add_on, external_image)
                binding.rbAddOn.isChecked -> registerAddOn(name, player_min, player_max,
                    playing_time, difficulty, designers, artists, publishers, bgg_link, playing_mode,
                    language, age, buying_price, stock, max_time, game, external_image)
                binding.rbMultiAddOn.isChecked -> registerMultiAddOn(name, player_min, player_max,
                    playing_time, difficulty, designers, artists, publishers, bgg_link, playing_mode,
                    language, age, buying_price, stock, max_time, games, external_image)

            }
            if(binding.cbDelImg.isChecked){
                allImages.list_of_images.remove(changedObject?.name)
                sharedPreference.save(gson.toJson(allImages), SerialKey.AllImagesStorage.name)
            }
            sharedPreference.save(gson.toJson(ApiResponse(allGames, allAddOns, allMultiAddOns)),SerialKey.APIStorage.name)
            sharedPreference.save(gson.toJson(ApiResponse(addedGames, addedAddOns, addedMultiAddOns)),SerialKey.AddedContent.name)
            startActivity(Intent(this,ViewGamesActivity::class.java))
            finish()
        }

    }

    private fun <T:CommonBase, U:CommonBase>modifyElement(originalChangedList:ArrayList<U>, changedList:ArrayList<T>,
                                                  originalAllList:ArrayList<U>, allList:ArrayList<T>,
                                                  originalData: U, modifiedData:T){

        originalChangedList.removeIf{it == originalData}
        originalAllList.removeIf{it == originalData }
        changedList.add(modifiedData)
        allList.add(modifiedData)
    }

    private fun <T:CommonBase>handleCategoryChange(changedList:ArrayList<T>, allList:ArrayList<T>,
                                           modifiedData:T){
        changedObject!!.id?.run{
            when(changedObject){
                is GameBean -> modifyElement(modifiedGames, changedList, allGames, allList, changedObject as GameBean, modifiedData)
                is AddOnBean -> modifyElement(modifiedAddOns, changedList, allAddOns, allList, changedObject as AddOnBean, modifiedData)
                is MultiAddOnBean -> modifyElement(modifiedMultiAddOns, changedList, allMultiAddOns, allList, changedObject as MultiAddOnBean, modifiedData)
            }
        }?:run{
            when(changedObject){
                is GameBean -> modifyElement(addedGames, changedList, allGames, allList, changedObject as GameBean, modifiedData)
                is AddOnBean -> modifyElement(addedAddOns, changedList, allAddOns, allList, changedObject as AddOnBean, modifiedData)
                is MultiAddOnBean -> modifyElement(addedMultiAddOns, changedList, allMultiAddOns, allList, changedObject as MultiAddOnBean, modifiedData)
            }
        }

    }

    private fun deleteChangeObject(){
        when(changedObject){
            is GameBean -> deletedGames.add(changedObject as GameBean)
            is AddOnBean -> deletedAddOns.add(changedObject as AddOnBean)
            is MultiAddOnBean -> deletedMultiAddOns.add(changedObject as MultiAddOnBean)
        }
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
                     add_on: ArrayList<AddOnBean>,
                     multi_add_on: ArrayList<MultiAddOnBean>,
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
            game.id?.run{
                handleCategoryChange(modifiedGames, allGames, game)
        }?:run{
                changedObject?.id?.run{deleteChangeObject()}
                handleCategoryChange(addedGames, allGames, game)
            }}?:run{
            allGames.add(game)
            addedGames.add(game)
        }

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
            addOn.id?.run{
                handleCategoryChange(modifiedAddOns, allAddOns, addOn)
            }?:run{
                changedObject?.id?.run{deleteChangeObject()}
                handleCategoryChange(addedAddOns, allAddOns, addOn)
            }}?:run{
            allAddOns.add(addOn)
            addedAddOns.add(addOn)
        }


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
            multiAddOn.id?.run{
                handleCategoryChange(modifiedMultiAddOns, allMultiAddOns, multiAddOn)
            }?:run{
                changedObject?.id?.run{deleteChangeObject()}
                handleCategoryChange(addedMultiAddOns, allMultiAddOns, multiAddOn)
            }}?:run{
            allMultiAddOns.add(multiAddOn)
            addedMultiAddOns.add(multiAddOn)
        }


    }

    private fun setView(ll: LinearLayout){
        binding.llGame.visibility = View.GONE
        binding.llAddOn.visibility = View.GONE
        binding.llMultiAddOn.visibility = View.GONE
        ll.visibility = View.VISIBLE
    }

    private fun testNull(etContent:String) = if(etContent.isBlank()) null else etContent

    private fun returnList(etContent:String) = testNull(etContent)?.
        trim(',')?.
        split(",")?.
        map{it.trim()}?.
        toCollection(ArrayList())?:
        ArrayList()

    private fun <T:CommonBase> returnMemberByName(etContent:String, globalList:ArrayList<T>):ArrayList<T> = globalList.
    filter{
        (testNull(etContent)?.trim(',')?.split(",")?.map{it.trim()}?.toCollection(ArrayList())?:ArrayList()).
    contains(it.name)}.
    toCollection(ArrayList())

    private fun fillPageRv(rvDifficulties: RecyclerView, rvTag: RecyclerView, rvMechanism: RecyclerView,
                     rvTopic: RecyclerView
    ){
        loadRv(rvDifficulties, difficulties, difficultyAdapter, allDifficulties())
        loadRv(rvTag, tags, tagAdapter, allTags())
        loadRv(rvMechanism, mechanism, mechanismAdapter, allMechanism())
        loadRv(rvTopic, topics, topicAdapter, allTopics())

    }



    override fun onGenericClick(datum: String, type: String) {
        when(type){
            Type.Difficulty.name -> binding.etDifficulty.setText("${datum}")
            Type.Designer.name -> binding.etDesigner.setText(autoAddElement(binding.etDesigner.text.toString(),datum))
            Type.Artist.name -> binding.etArtist.setText(autoAddElement(binding.etArtist.text.toString(),datum))
            Type.Publisher.name -> binding.etPublisher.setText(autoAddElement(binding.etPublisher.text.toString(),datum))
            Type.Language.name -> binding.etLanguage.setText(autoAddElement(binding.etLanguage.text.toString(),datum))
            Type.PlayingMode.name -> binding.etPlayingMode.setText(autoAddElement(binding.etPlayingMode.text.toString(),datum))
            Type.Tag.name -> binding.etTag.setText(autoAddElement(binding.etTag.text.toString(),datum))
            Type.Mechanism.name -> binding.etMechanism.setText(autoAddElement(binding.etMechanism.text.toString(),datum))
            Type.Topic.name -> binding.etTopic.setText(autoAddElement(binding.etTopic.text.toString(),datum))
        }
    }

    private fun autoAddElement(originalText:String, adding:String) = if(originalText == "") adding else if (originalText.contains(adding)) "$originalText" else "$originalText, $adding"
    override fun onElementClick(datum: CommonBase?) {
        when(datum){
            is GameBean -> {
                binding.etAddOnGame.setText(datum.name)
                binding.etMultiAddOnGames.setText(autoAddElement(binding.etMultiAddOnGames.text.toString(),datum.name))
            }

            is MultiAddOnBean -> {
                binding.etGameMultiAddOn.setText(autoAddElement(binding.etGameMultiAddOn.text.toString(),datum.name))
            }

            is AddOnBean -> {
                binding.etGameAddOn.setText(autoAddElement(binding.etGameAddOn.text.toString(),datum.name))
            }

        }
    }

    private fun setCommonElement(it:CommonBase){
        binding.etNom.setText(it.name)
        binding.etDesigner.setText(it.designers.joinToString(", "))
        binding.etArtist.setText(it.artists.joinToString(", "))
        binding.etPublisher.setText(it.publishers.joinToString(", "))
        binding.etNbPlayerMin.setText(it.player_min?.toString()?:"")
        binding.etNbPlayerMax.setText(it.player_max?.toString()?:"")
        binding.etMaxTime.setText(it.max_time?.toString()?:"")
        binding.etDifficulty.setText(it.difficulty?:"")
        binding.etAge.setText(it.age?.toString()?:"")
        binding.etPlayingMode.setText(it.playing_mode.joinToString(", "))
        binding.etLanguage.setText(it.language.joinToString(", "))
        binding.etBggLink.setText(it.bgg_link?:"")
        binding.etStock.setText(it.stock?.toString()?:"")
        binding.etBuyingPrice.setText(it.buying_price?.toString()?:"")
        binding.etExternalImage.setText(it.external_img?:"")

    }

    private fun setGameBeanElement(it:GameBean){
        binding.rbGame.isChecked = true
        setView(binding.llGame)
        binding.etTag.setText(it.tags.joinToString(", "))
        binding.etTopic.setText(it.topics.joinToString(", "))
        binding.etMechanism.setText(it.mechanism.joinToString(", "))
        binding.etGameAddOn.setText(it.add_on.joinToString (", "){ add_on -> add_on.name })
        binding.etGameMultiAddOn.setText(it.multi_add_on.joinToString (", "){ multi_add_on -> multi_add_on.name })
        if (it.by_player == true) binding.rbByPlayerTrue.isChecked = true

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


}