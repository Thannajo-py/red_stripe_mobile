package com.example.filrouge.activity

import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.forEach
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.filrouge.*
import com.example.filrouge.bean.*
import com.example.filrouge.dao.CommonCustomInsert
import com.example.filrouge.dao.CommonDao
import com.example.filrouge.dao.GameDao
import com.example.filrouge.databinding.ActivityAddElementBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File

class AddElement : CommonType(), View.OnClickListener,
    GenericIDCbListener, GenericCommonGameCbListener, GenericOneToOneListener {

    private val binding by lazy{ActivityAddElementBinding.inflate(layoutInflater)}

    private val addedStringContent: ArrayList<ArrayList<String>> = arrayListOf(arrayListOf(), arrayListOf(), arrayListOf()
        , arrayListOf(), arrayListOf(), arrayListOf(), arrayListOf(), arrayListOf())

    private val listMethod = ListCommonMethod()

    private val designerAdapter = GenericIDListCbAdapter(this, Type.Designer.name, addedStringContent[AddedContent.Designer.ordinal])
    private val artistAdapter = GenericIDListCbAdapter(this, Type.Artist.name, addedStringContent[AddedContent.Artist.ordinal])
    private val publisherAdapter = GenericIDListCbAdapter(this, Type.Publisher.name, addedStringContent[AddedContent.Publisher.ordinal])
    private val languageAdapter = GenericIDListCbAdapter(this, Type.Language.name, addedStringContent[AddedContent.Language.ordinal])
    private val playingModeAdapter = GenericIDListCbAdapter(this, Type.PlayingMode.name, addedStringContent[AddedContent.PlayingMod.ordinal])

    private val difficultyAdapter = OneToOneListCbAdapter<DifficultyTableBean>(this)

    private val tagAdapter = GenericIDListCbAdapter(this, Type.Tag.name, addedStringContent[AddedContent.Tag.ordinal])

    private val topicAdapter = GenericIDListCbAdapter(this, Type.Topic.name, addedStringContent[AddedContent.Topic.ordinal])

    private val mechanismAdapter = GenericIDListCbAdapter(this, Type.Mechanism.name, addedStringContent[AddedContent.Mechanism.ordinal])



    private val addedToListAddons = ArrayList<DesignerWithAddOn>()
    private val addOnAdapter = GenericCommonGameListCbAdapter<DesignerWithAddOn>(this, addedToListAddons)

    private val addedToListMultiAddOns = ArrayList<DesignerWithMultiAddOn>()
    private val multiAddOnAdapter = GenericCommonGameListCbAdapter<DesignerWithMultiAddOn>(this, addedToListMultiAddOns)

    private val addedToListGames = ArrayList<DesignerWithGame>()
    private val gamesAdapter = GenericCommonGameListCbAdapter<DesignerWithGame>(this, addedToListGames)

    private var game:DesignerWithGame? = null
    private val gameAdapter = OneToOneListCbAdapter<DesignerWithGame>(this)

    private val changedObjectId: Long by lazy{intent.getLongExtra(SerialKey.ToModifyDataId.name, 0L)}
    private val changedObjectType: String? by lazy{intent.getStringExtra(SerialKey.ToModifyDataType.name)}
    private val changedObjectName: String? by lazy{intent.getStringExtra(SerialKey.ToModifyDataName.name)}

    private val bgaApiGame by lazy{intent.getSerializableExtra(SerialKey.ApiBgaGame.name) as BgaGameBean?}

    private var externalImageUrl:String? = null

    private var loadImage = false
    private var deleteImage = false


    val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        binding.ivPictureChoice.setImageURI(uri)
        loadImage = true
    }

    val getPicture = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bmp: Bitmap? ->
        binding.ivPictureChoice.setImageBitmap(bmp)
        loadImage = true
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addOnGame = null
        setContentView(binding.root)
        registerForContextMenu(binding.ivPictureChoice)
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

        if(changedObjectId != 0L) loadChangedObjectData()

        val commonRvAdapterList: ArrayList<Pair<RecyclerView, GenericIDListCbAdapter>> =
            arrayListOf(
                Pair(binding.rvDesigner,designerAdapter),
                Pair(binding.rvArtist, artistAdapter),
                Pair(binding.rvPublisher,publisherAdapter),
                Pair(binding.rvPlayingMode, playingModeAdapter),
                Pair(binding.rvLanguage, languageAdapter),
                Pair(binding.rvMechanism, mechanismAdapter),
                Pair(binding.rvTag, tagAdapter),
                Pair(binding.rvTopic, topicAdapter),
            )
        fillCommonRV(commonRvAdapterList)
        fillPageRv()
        setAddButtonEvent()

        bgaApiGame?.run{
            loadBgaGame(this)
        }



    }


    private fun loadChangedObjectData(){
        CoroutineScope(SupervisorJob()).launch{
            changedObjectName?.run{
                when(changedObjectType){
                    Type.Game.name -> {
                        loadChangedObjectImage(this, appInstance.database.gameDao())
                    }
                    Type.AddOn.name ->  loadChangedObjectImage(this, appInstance.database.addOnDao())
                    Type.MultiAddOn.name ->  loadChangedObjectImage(this, appInstance.database.multiAddOnDao())
                }
            }


        }

        fillView(changedObjectType, changedObjectId)
    }

    private fun<T> loadChangedObjectImage(name:String, dao:CommonDao<T>){
        val imgList = dao.getImage(name)
        if(imgList.isNotEmpty()){
            setImage(imgList.first().name)
        }
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View,
                                     menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)){
            menu.add(0, MenuId.TakePhoto.ordinal, 0, "Prendre une photo")
        }
        menu.add(0, MenuId.AddExternalLink.ordinal, 0, "Ajouter un lien image externe")
        menu.add(0, MenuId.GetInternalFile.ordinal, 0, "Choisir une image")
        if(loadImage){
            menu.add(0, MenuId.ResetImage.ordinal, 0, "Réinitialiser l'image")
        }
        menu.add(0, MenuId.DeleteImage.ordinal, 0, "Supprimer l'image")


    }




    override fun onContextItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            MenuId.AddExternalLink.ordinal -> externalImageMenu()
            MenuId.TakePhoto.ordinal -> {
                requestPermission(
                    PermissionRequest.Camera.perm,
                    PermissionRequest.Camera.ordinal,
                    getPicture,
                    null
                )
                true
            }
            MenuId.GetInternalFile.ordinal -> {
                requestPermission(
                    PermissionRequest.ExternalStorage.perm,
                    PermissionRequest.ExternalStorage.ordinal,
                    getContent,
                    "image/*"
                )
                true
            }
            MenuId.ResetImage.ordinal -> {
                loadImage = false
                binding.ivPictureChoice.setImageBitmap(null)
                changedObjectName?.run{
                    changedObjectType?.run{
                        setImage(changedObjectName!!, this)
                    }
                }
                bgaApiGame?.image_url?.run{setImage()}
                true
            }
            MenuId.DeleteImage.ordinal -> {
                binding.ivPictureChoice.setImageBitmap(null)
                loadImage = false
                externalImageUrl = null
                deleteImage = true
                true
            }
            else -> super.onContextItemSelected(item)
        }
    }

    private fun removeImage(){
        binding.ivPictureChoice.setImageBitmap(null)
        externalImageUrl = null
        if (changedObjectName != null && changedObjectType != null){
            CoroutineScope(SupervisorJob()).launch{
                val img = appInstance.database.imageDao().getByName("$changedObjectName$changedObjectType")
                if (img.isNotEmpty()){
                    appInstance.database.imageDao().deleteByName("$changedObjectName$changedObjectType")
                }

            }

        }
    }

    private fun<T> requestPermission(permission:String, rc:Int, activity: ActivityResultLauncher<T>, arg:T){
            if (
                ContextCompat.checkSelfPermission(this, permission)
                == PackageManager.PERMISSION_GRANTED
            ) {
                activity.launch(arg)
            } else {

                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(permission), rc
                )
            }
    }

    override fun onRequestPermissionsResult(rc: Int, perm: Array<out String>, gr: IntArray) {
        super.onRequestPermissionsResult(rc, perm, gr)
        when(rc){
            PermissionRequest.Camera.ordinal -> if (ContextCompat.checkSelfPermission(
                    this,
                    PermissionRequest.Camera.perm
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                getPicture.launch(null)
                return
            }
            PermissionRequest.ExternalStorage.ordinal -> if (ContextCompat.checkSelfPermission(
                this,
                PermissionRequest.Camera.perm
            ) == PackageManager.PERMISSION_GRANTED
                    ) {
                getContent.launch("image/*")
                return
            }
        }
        Toast.makeText(this, "permission nécessaire", Toast.LENGTH_SHORT).show()
    }


    private fun setImage(name: String, type: String){
        when(type){
            Type.Game.name -> setImage(name, appInstance.database.gameDao())
            Type.AddOn.name -> setImage(name, appInstance.database.addOnDao())
            Type.MultiAddOn.name -> setImage(name, appInstance.database.multiAddOnDao())
        }

    }

    private fun<T> setImage(name: String, dao: CommonDao<T>){
        CoroutineScope(SupervisorJob()).launch{
            val imageList = dao.getImage(name)
            if(imageList.isNotEmpty()){
                val file = File(this@AddElement.filesDir, imageList.first().name)
                val compressedBitMap = BitmapFactory.decodeByteArray(file.readBytes(),0,file.readBytes().size)
                runOnUiThread {
                    binding.ivPictureChoice.setImageBitmap(compressedBitMap)
                }
            }
        }
    }

    private fun setImage(imageName:String){
        val file = File(this@AddElement.filesDir, imageName)
        val compressedBitMap = BitmapFactory.decodeByteArray(file.readBytes(),0,file.readBytes().size)
        runOnUiThread {
            binding.ivPictureChoice.setImageBitmap(compressedBitMap)
        }
    }

    private fun setImage() {
        bgaApiGame?.image_url?.run{
            Picasso.get().load(this).into(binding.ivPictureChoice)
            loadImage = true
        }


    }

    private fun savePicture(gameName:String, gameType:String){
        val name = "$gameName$gameType"
        val imageBitmap = binding.ivPictureChoice.drawable.toBitmap()
        val stream = ByteArrayOutputStream()
        imageBitmap.compress(Bitmap.CompressFormat.JPEG,80,stream)
        val byteArray = stream.toByteArray()
        val file = File(this.filesDir,name)
        file.writeBytes(byteArray)
        val dbImgList = appInstance.database.imageDao().getByName(name)
        if (dbImgList.isEmpty()){
            appInstance.database.imageDao().insert(ImageTableBean(0L, name, gameName, gameType))
        }
    }

    private fun addLinearLayout(elements:ArrayList<View>):LinearLayout{
        val ll = LinearLayout(this)
        ll.orientation = LinearLayout.VERTICAL
        ll.gravity = Gravity.CENTER
        ll.setPadding(20,0,20,0)
        elements.forEach { ll.addView(it) }
        return ll
    }


    private fun externalImageMenu(): Boolean{
        val urlLink = EditText(this)
        val ll = addLinearLayout(arrayListOf(urlLink))
        AlertDialog.Builder(this)
            .setMessage("copier le lien vers l'image")
            .setTitle("Image externe")
            .setPositiveButton("ok") { text,which ->
                externalImageUrl = urlLink.text.toString()
            }
            .setNegativeButton("cancel") { dialog, which ->
                Toast.makeText(this, "Annulé", Toast.LENGTH_SHORT).show()
            }
            .setView(ll)
            .show()
        return true
    }


    private fun setAddButtonEvent(){
        val btn_list = arrayListOf(
            Pair(binding.btnDesignerAdd, binding.llAddDesigner),
            Pair(binding.btnArtistAdd, binding.llAddArtist),
            Pair(binding.btnPublisherAdd, binding.llAddPublisher),
            Pair(binding.btnTagAdd, binding.llAddTag),
            Pair(binding.btnTopicModeAdd, binding.llAddTopic),
            Pair(binding.btnLanguageAdd, binding.llAddLanguage),
            Pair(binding.btnMechanismAdd, binding.llAddMechanism),
            Pair(binding.btnPlayingModeAdd, binding.llAddPlayingMod)
        )
        btn_list.forEach {pair ->
            pair.first.setOnClickListener { setOnClickAddButton(pair.second) }
        }
    }

    private fun setOnClickAddButton( linearLayout:LinearLayout, text: String=""){
        val ll = LinearLayout(this)
        ll.orientation = LinearLayout.HORIZONTAL
        val newEditText = EditText(this)
        newEditText.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            1.0F)
        newEditText.setText(text)
        val floatButton = FloatingActionButton(this)
        floatButton.setImageDrawable(AppCompatResources.getDrawable(this,R.drawable.ic_del))
        ll.addView(newEditText)
        ll.addView(floatButton)
        floatButton.setOnClickListener { linearLayout.removeView(ll) }
        linearLayout.addView(ll)
    }


    override fun onClick(v: View?) {
        binding.etError.visibility = View.GONE
        CoroutineScope(SupervisorJob()).launch {
            appInstance.database.runInTransaction {


                val name = binding.etNom.text.toString()
                if (binding.etNom.text.toString().isBlank()) {
                    runOnUiThread {
                        binding.etError.text = "le nom ne peut pas être vide"
                        binding.etError.visibility = View.VISIBLE

                    }
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
                    runOnUiThread {
                        binding.etError.text = "Un élément de la même catégorie portant le même nom existe déjà!"
                        binding.etError.visibility = View.VISIBLE
                    }


                } else {

                    val player_min = testNull(binding.etNbPlayerMin.text.toString())?.toInt()
                    val player_max = testNull(binding.etNbPlayerMax.text.toString())?.toInt()
                    val playing_time = testNull(binding.etMaxTime.text.toString())
                    val designers = addList(AddedContent.Designer.ordinal, appInstance.database.designerDao(), binding.llAddDesigner)
                    val artists = addList(AddedContent.Artist.ordinal, appInstance.database.artistDao(), binding.llAddArtist)
                    val publishers = addList(AddedContent.Publisher.ordinal, appInstance.database.publisherDao(), binding.llAddPublisher)
                    val bgg_link = testNull(binding.etBggLink.text.toString())
                    val playing_mode = addList(AddedContent.PlayingMod.ordinal, appInstance.database.playingModDao(), binding.llAddPlayingMod)
                    val language = addList(AddedContent.Language.ordinal, appInstance.database.languageDao(), binding.llAddLanguage)
                    val age = testNull(binding.etAge.text.toString())?.toInt()
                    val buying_price = testNull(binding.etBuyingPrice.text.toString())?.toInt() ?: 0
                    val stock = testNull(binding.etStock.text.toString())?.toInt()
                    val max_time = testNull(binding.etStock.text.toString())?.toInt()
                    val by_player = binding.rbByPlayerTrue.isChecked
                    val tags = addList(AddedContent.Tag.ordinal, appInstance.database.tagDao(), binding.llAddTag)
                    val topics = addList(AddedContent.Topic.ordinal, appInstance.database.topicDao(), binding.llAddTopic)
                    val mechanism = addList(AddedContent.Mechanism.ordinal, appInstance.database.mechanismDao(), binding.llAddMechanism)
                    val games = addedToListGames
                    val external_image = externalImageUrl


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

                    startActivity(Intent(this@AddElement, ViewGamesActivity::class.java))
                    finish()
                }
            }

        }
    }

    private fun <T>addList(position: Int, dao:CommonCustomInsert<T>, ll: LinearLayout):ArrayList<String>{
        val finalList = mutableSetOf<String>()
        finalList.addAll(addedStringContent[position].filter { it.isNotBlank() })
        val etList = ArrayList<String>()
        ll.forEach {
            if (it is LinearLayout){
                it.forEach {
                    if(it is EditText && it.text.toString().isNotEmpty()){
                        etList.add(it.text.toString())
                    }
                }
            }
        }
        finalList.addAll(etList)
        etList.forEach {
            if (it.isNotBlank() && dao.getByName(it).isEmpty()) dao.insert(it)
        }


        return finalList.toCollection(ArrayList())
    }

    private fun getDifficultyId(): Long?{
        if (binding.etDifficulty.text.toString().isNullOrBlank()) return null
        var difficultyId:Long? = null
        val difficultyL = appInstance.database.difficultyDao().getByName(binding.etDifficulty.text.toString())
        if(difficultyL.isNotEmpty()) difficultyId = difficultyL[0].id
        else difficultyId = appInstance.database.difficultyDao().insert(DifficultyTableBean(0,binding.etDifficulty.text.toString() ))

        return difficultyId
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
            if (deleteImage) removeImage()

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
                if(deleteImage) null else previous?.picture,
                by_player,
                true
            )
            val id = appInstance.database.gameDao().insert(game)
            val newGame = appInstance.database.gameDao().getObjectById(id).first()
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
                newGame, arrayListOf(
                    designers, artists, publishers, playing_mode, language, tags,
                    topics, mechanism, add_on, multi_add_on
                )
            )
            if (loadImage){
                savePicture(name, Type.Game.name)
            }
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
        val id = appInstance.database.addOnDao().insert(addOn)
        val newGame = appInstance.database.addOnDao().getObjectById(id).first()
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
            newGame, arrayListOf(
                designers, artists, publishers, playing_mode, language
            )
        )
        if (loadImage){
            savePicture(name, Type.AddOn.name)
        }
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

        val id = appInstance.database.multiAddOnDao().insert(multiAddOn)
        val newGame = appInstance.database.multiAddOnDao().getObjectById(id).first()
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
            newGame, arrayListOf(
                designers, artists, publishers, playing_mode, language
            )
        )
        appInstance.database.runInTransaction {
            dbMethod.gameMultiAddOnLinkListByMultiAddOn(games, appInstance.database.gameDao(), appInstance.database.gameMultiAddOnDao(), id)
        }
        if (loadImage){
            savePicture(name, Type.MultiAddOn.name)
        }
    }

    private fun setView(ll: LinearLayout){
        binding.llGame.visibility = View.GONE
        binding.llAddOn.visibility = View.GONE
        binding.llMultiAddOn.visibility = View.GONE
        ll.visibility = View.VISIBLE
    }

    private fun testNull(etContent:String) = if(etContent.isBlank() || etContent == "null") null else etContent


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
            Type.Designer.name -> listMethod.listContentManager(addedStringContent[AddedContent.Designer.ordinal], name, cb)
            Type.Artist.name -> listMethod.listContentManager(addedStringContent[AddedContent.Artist.ordinal], name, cb)
            Type.Publisher.name -> listMethod.listContentManager(addedStringContent[AddedContent.Publisher.ordinal], name, cb)
            Type.Language.name -> listMethod.listContentManager(addedStringContent[AddedContent.Language.ordinal], name, cb)
            Type.PlayingMode.name -> listMethod.listContentManager(addedStringContent[AddedContent.PlayingMod.ordinal], name, cb)
            Type.Tag.name -> listMethod.listContentManager(addedStringContent[AddedContent.Tag.ordinal], name, cb)
            Type.Topic.name -> listMethod.listContentManager(addedStringContent[AddedContent.Topic.ordinal], name, cb)
            Type.Mechanism.name -> listMethod.listContentManager(addedStringContent[AddedContent.Mechanism.ordinal], name, cb)
        }

    }


    override fun onGenericClick(datum: CommonGame, view:CheckBox) {
        when (datum){
            is DesignerWithGame -> listMethod.listContentManager(addedToListGames, datum, view)
            is DesignerWithMultiAddOn -> listMethod.listContentManager(addedToListMultiAddOns, datum, view)
            is DesignerWithAddOn -> listMethod.listContentManager(addedToListAddons, datum, view)
        }
    }



    override fun onGenericClick(datum: OneToOne) {
        when(datum){
            is DesignerWithGame -> {
                if (game == datum) {
                    game = null
                    binding.tvGame.text = "aucun"
                }

                else {
                    game = datum
                    binding.tvGame.text = datum.name
                }
            }
            is DifficultyTableBean -> binding.etDifficulty.setText(datum.name)
        }
    }

    fun fillView(type: String?, id:Long){
        when(type){
            Type.Game.name-> {
                check(binding.rbGame)
                fillCommonView(appInstance.database.gameDao(),id)
                CoroutineScope(SupervisorJob()).launch{
                    gameFill(appInstance.database.gameDao(), addedStringContent, id)
                }
            }
            Type.AddOn.name -> {
                check(binding.rbAddOn)
                fillCommonView(appInstance.database.addOnDao(),id)
                CoroutineScope(SupervisorJob()).launch{
                    genericFill(appInstance.database.addOnDao(), addedStringContent, id)
                    val gameL = appInstance.database.addOnDao().getGameFromAddOns(id)
                    if (gameL.isNotEmpty()) {
                        game = gameL[0]
                        binding.tvGame.text = gameL[0].name
                    }
                }
            }
            Type.MultiAddOn.name -> {
                check(binding.rbMultiAddOn)
                fillCommonView(appInstance.database.multiAddOnDao(),id)
                CoroutineScope(SupervisorJob()).launch{
                    genericFill(appInstance.database.multiAddOnDao(), addedStringContent, id)
                    addedToListGames.addAll(appInstance.database.multiAddOnDao().getGameObjectFromMultiAddOn(id))
                }
            }

        }

    }

    fun check(rb:RadioButton){
        rb.isChecked = true
        when(rb){
            binding.rbGame -> setView(binding.llGame)
            binding.rbAddOn -> setView(binding.llAddOn)
            binding.rbMultiAddOn -> setView(binding.llMultiAddOn)
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
                externalImageUrl = it[0].external_img
                dao.getDifficulty(id).observe(this, {
                    if (it.size > 0) it?.let {
                        binding.etDifficulty.setText(it[0].name)
                    }
                })
            }
        })
    }

    fun fillCommonRV(listPairRecyclerViewAdapter: ArrayList<Pair<RecyclerView, GenericIDListCbAdapter>>) {
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

    fun loadBgaGame(datum:BgaGameBean){
        externalImageUrl = datum.image_url
        binding.etBggLink.setText(datum.url)
        binding.etNom.setText(datum.name)
        binding.etNbPlayerMin.setText(datum.min_players?.toString()?:"")
        binding.etNbPlayerMax.setText(datum.max_players?.toString()?:"")
        binding.etMaxTime.setText(datum.max_playtime?.toString()?:"")
        binding.etAge.setText(datum.min_age?.toString()?:"")
        if (datum.type == Constant.Extension.value){
            check(binding.rbAddOn)
        }
        addElementName(datum.primary_designer?.name, appInstance.database.designerDao(), AddedContent.Designer.ordinal, binding.llAddDesigner)
        addElementName(datum.primary_publisher?.name, appInstance.database.publisherDao(), AddedContent.Publisher.ordinal, binding.llAddPublisher)

        addToAddedList(datum.artists, AddedContent.Artist.ordinal, appInstance.database.artistDao(), binding.llAddArtist)
        addToAddedList(convertIdListToNameList(datum.mechanics, ALL_MECHANICS), AddedContent.Mechanism.ordinal, appInstance.database.mechanismDao(), binding.llAddMechanism)
        addToAddedList(convertIdListToNameList(datum.categories, ALL_CATEGORIES), AddedContent.Topic.ordinal, appInstance.database.topicDao(), binding.llAddTopic)
        bgaApiGame?.image_url?.run{
            setImage()
        }

    }

    fun <T>addElementName(name:String?, dao:CommonCustomInsert<T>, position:Int, ll: LinearLayout){
        CoroutineScope(SupervisorJob()).launch{
            name?.run{
                val answer = dao.getByName(this)
                if (answer.isEmpty()) {
                    setOnClickAddButton(ll, this)
                }
                else addedStringContent[position].add(this)
            }
        }
    }

    fun <T>addToAddedList(list:ArrayList<String>, position:Int, dao:CommonCustomInsert<T>, ll:LinearLayout){
        if (list.isNotEmpty()){
            list.forEach {
                CoroutineScope(SupervisorJob()).launch{
                    val answer = dao.getByName(it)
                    if (answer.isEmpty())setOnClickAddButton(ll, it)
                    else addedStringContent[position].add(it)
                }
            }
        }
    }

    private fun convertIdListToNameList(idList:ArrayList<IdObjectBean>, namedList:ArrayList<NamedResultBean>): ArrayList<String>{
        val resultList = ArrayList<String>()
        idList.forEach { for (m in namedList) {if (m.id==it.id){resultList.add(m.name)}} }
        return resultList

    }
}
