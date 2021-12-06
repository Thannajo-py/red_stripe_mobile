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
    private val listMethod = ListCommonMethod()

    private val addedStringContentTypeList = arrayListOf(
        Type.Designer.name,
        Type.Artist.name,
        Type.Publisher.name,
        Type.PlayingMod.name,
        Type.Language.name,
        Type.Mechanism.name,
        Type.Tag.name,
        Type.Topic.name,
    )
    private val addedStringContentHashMap = HashMap<String,ArrayList<String>>()

    private val difficultyAdapter = OneToOneListCbAdapter<DifficultyTableBean>(this)

    private val gameTypeList = arrayListOf(
        Type.Game.name,
        Type.MultiAddOn.name,
        Type.AddOn.name,
    )
    private val addedGameContentHashMap = HashMap<String,ArrayList<CommonGame>>()

    private var game:DesignerWithGame? = null
    private val addOnGameAdapter = OneToOneListCbAdapter<DesignerWithGame>(this)

    private val changedObjectId: Long by lazy{intent.getLongExtra(SerialKey.ToModifyDataId.name, 0L)}
    private val changedObjectType: String? by lazy{intent.getStringExtra(SerialKey.ToModifyDataType.name)}
    private val changedObjectName: String? by lazy{intent.getStringExtra(SerialKey.ToModifyDataName.name)}

    private val bgaApiGame by lazy{intent.getSerializableExtra(SerialKey.ApiBgaGame.name) as BgaGameBean?}

    private var externalImageUrl:String? = null

    private var loadImage = false
    private var deleteImage = false

    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent())
    { uri: Uri? ->
        binding.ivPictureChoice.setImageURI(uri)
        loadImage = true
    }

    private val getPicture = registerForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bmp: Bitmap? ->
        binding.ivPictureChoice.setImageBitmap(bmp)
        loadImage = true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addOnGame = null
        setContentView(binding.root)
        registerForContextMenu(binding.ivPictureChoice)
        fillCommonRv()
        binding.btnAdd.setOnClickListener(this)
        radioButtonSetOnClickListener()
        if(changedObjectId != 0L) loadChangedObjectData()
        fillPageRv()
        setAddButtonEvent()
        bgaApiGame?.run{
            loadBgaGame(this)
        }
    }

    private fun radioButtonSetOnClickListener(){
        gameTypeList.forEach{type->
            CoroutineScope(SupervisorJob()).launch{
                val rb = binding::class.members.find{it.name=="rb$type"}!!.call(binding) as RadioButton
                val ll = binding::class.members.find{it.name=="ll$type"}!!.call(binding) as LinearLayout
                runOnUiThread {
                    rb.setOnClickListener { radioButtonSelect(rb.isChecked, ll) }
                }
            }
        }
    }

    private fun radioButtonSelect(checked:Boolean, ll:LinearLayout){
        if (checked){
            setView(ll)
        }
    }


    private fun loadChangedObjectData(){
        CoroutineScope(SupervisorJob()).launch{
            changedObjectName?.run{
                val lowercase = changedObjectType?.replaceFirstChar { it.lowercase() }
                val dao = appInstance.database::class.members.find{
                    it.name == "${lowercase}Dao"
                }!!.call(appInstance.database) as CommonDao<CommonComponent, CommonGame>
                loadChangedObjectImage(this, dao)
            }
        }
        fillView(changedObjectType, changedObjectId)
    }

    private fun<T, U> loadChangedObjectImage(name:String, dao:CommonDao<T, U>){
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
        val lowercase = type.replaceFirstChar { it.lowercase() }
        val dao = appInstance.database::class.members.find{
            it.name == "${lowercase}Dao"
        }!!.call(appInstance.database) as CommonDao<CommonComponent, CommonGame>
        setImage(name, dao)
    }

    private fun<T, U> setImage(name: String, dao: CommonDao<T, U>){
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
            runOnUiThread {
                Picasso.get().load(this).into(binding.ivPictureChoice)
            }
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
        CoroutineScope(SupervisorJob()).launch{
            addedStringContentTypeList.forEach { type ->
                val btn = binding::class.members.find{it.name == "btn${type}Add"}!!.call(binding) as FloatingActionButton
                val ll = binding::class.members.find{it.name == "llAdd$type"}!!.call(binding) as LinearLayout
                runOnUiThread {
                    btn.setOnClickListener { setOnClickAddButton(ll) }
                }
            }
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
                val name = binding.etNom.text.toString()
                checkNameAvailability(name)
        }
    }

    private fun checkNameAvailability(name:String){
        if (binding.etNom.text.toString().isBlank()) {
            runOnUiThread {
                binding.etError.text = "le nom ne peut pas être vide"
                binding.etError.visibility = View.VISIBLE
            }
        } else if (
            (changedObjectId == 0L || changedObjectName != name) &&
            (binding.rbGame.isChecked
                    && appInstance.database.gameDao().getByName(name).isNotEmpty()
                    || binding.rbAddOn.isChecked
                    && appInstance.database.addOnDao().getByName(name).isNotEmpty()
                    || binding.rbMultiAddOn.isChecked
                    && appInstance.database.multiAddOnDao().getByName(name).isNotEmpty())
        ) {
            runOnUiThread {
                binding.etError.text = "Un élément de la même catégorie portant le même nom existe déjà!"
                binding.etError.visibility = View.VISIBLE
            }
        } else {
            appInstance.database.runInTransaction {
                registerObject(name)
            }
        }
    }

    private fun registerObject(name:String){
        val commonData = CommonAddObject(
            name = name,
            player_min = testNull(binding.etNbPlayerMin.text.toString())?.toInt(),
            player_max = testNull(binding.etNbPlayerMax.text.toString())?.toInt(),
            playing_time = testNull(binding.etMaxTime.text.toString()),
            designers = addList(Type.Designer.name, appInstance.database.designerDao(), binding.llAddDesigner),
            artists = addList(Type.Artist.name, appInstance.database.artistDao(), binding.llAddArtist),
            publishers = addList(Type.Publisher.name, appInstance.database.publisherDao(), binding.llAddPublisher),
            bgg_link = testNull(binding.etBggLink.text.toString()),
            playing_mode = addList(Type.PlayingMod.name, appInstance.database.playingModDao(), binding.llAddPlayingMod),
            language = addList(Type.Language.name, appInstance.database.languageDao(), binding.llAddLanguage),
            age = testNull(binding.etAge.text.toString())?.toInt(),
            buying_price = testNull(binding.etBuyingPrice.text.toString())?.toInt() ?: 0,
            stock = testNull(binding.etStock.text.toString())?.toInt(),
            max_time = testNull(binding.etStock.text.toString())?.toInt(),
            external_image = externalImageUrl,
        )
        when (true) {
            binding.rbGame.isChecked -> registerGame(commonData)
            binding.rbAddOn.isChecked -> registerAddOn(commonData)
            binding.rbMultiAddOn.isChecked -> registerMultiAddOn(commonData)
            else -> throw Exception("Unknown type")
        }
        startActivity(Intent(this@AddElement, ViewGamesActivity::class.java))
        finish()
    }

    private fun <T>addList(type: String, dao:CommonCustomInsert<T>, ll: LinearLayout):ArrayList<String>{
        val finalList = mutableSetOf<String>()
        finalList.addAll(addedStringContentHashMap[type]!!.filter { it.isNotBlank() })
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
        if (binding.etDifficulty.text.toString().isBlank()) return null
        val difficultyId: Long?
        val difficultyL = appInstance.database.difficultyDao().getByName(binding.etDifficulty.text.toString())
        difficultyId = if(difficultyL.isNotEmpty()) difficultyL[0].id
        else appInstance.database.difficultyDao().insert(DifficultyTableBean(0,binding.etDifficulty.text.toString() ))
        return difficultyId
    }

    private fun registerGame(data: CommonAddObject) {

        val tags = addList(Type.Tag.name, appInstance.database.tagDao(), binding.llAddTag)
        val topics = addList(Type.Topic.name, appInstance.database.topicDao(), binding.llAddTopic)
        val mechanism = addList(Type.Mechanism.name, appInstance.database.mechanismDao(), binding.llAddMechanism)
        val add_on = addedGameContentHashMap[Type.AddOn.name]!!.map {
            it.name
        }.toCollection(ArrayList())
        val multi_add_on = addedGameContentHashMap[Type.MultiAddOn.name]!!.map {
            it.name
        }.toCollection(ArrayList())

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
                data.name,
                data.player_min,
                data.player_max,
                data.playing_time,
                getDifficultyId(),
                data.bgg_link,
                data.age,
                data.buying_price,
                data.stock,
                data.max_time,
                data.external_image,
                if(deleteImage) null else previous?.picture,
                binding.rbByPlayerTrue.isChecked,
                true
            )
            val id = appInstance.database.gameDao().insert(game)
            val newGame = appInstance.database.gameDao().getObjectById(id).first()
            val dbMethod = DbMethod()
            if (changedObjectId != 0L) {
                when (changedObjectType) {
                    Type.Game.name -> {
                        val gameL = appInstance.database.gameDao().getObjectById(changedObjectId)
                        if (gameL.isNotEmpty()) dbMethod.deleteLink(gameL[0])
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
            dbMethod.insertLink(
                newGame, arrayListOf(
                    data.designers, data.artists, data.publishers, data.playing_mode, data.language, tags,
                    topics, mechanism, add_on, multi_add_on
                )
            )
            if (loadImage){
                savePicture(data.name, Type.Game.name)
            }
        }
    }

    private fun registerAddOn(data: CommonAddObject){
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
            data.name,
            data.player_min,
            data.player_max,
            data.playing_time,
            getDifficultyId(),
            data.bgg_link,
            data.age,
            data.buying_price,
            data.stock,
            data.max_time,
            data.external_image,
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
                    if (gameL.isNotEmpty()) dbMethod.deleteLink(gameL[0])
                }
                Type.MultiAddOn.name -> {
                    val gameL = appInstance.database.multiAddOnDao().getObjectById(changedObjectId)
                    if (gameL.isNotEmpty()) dbMethod.delete(gameL[0])
                }
            }
        }
        dbMethod.insertLink(
            newGame, arrayListOf(
                data.designers, data.artists, data.publishers, data.playing_mode, data.language
            )
        )
        if (loadImage){
            savePicture(data.name, Type.AddOn.name)
        }
    }

    private fun registerMultiAddOn(data:CommonAddObject){
        val games = addedGameContentHashMap[Type.Game.name]!!.map{it.name}.toCollection(ArrayList())
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
            data.name,
            data.player_min,
            data.player_max,
            data.playing_time,
            getDifficultyId(),
            data.bgg_link,
            data.age,
            data.buying_price,
            data.stock,
            data.max_time,
            data.external_image,
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
                    if (gameL.isNotEmpty()) dbMethod.deleteLink(gameL[0])
                }
            }
        }
        dbMethod.insertLink(
            newGame, arrayListOf(
                data.designers, data.artists, data.publishers, data.playing_mode, data.language
            )
        )
        appInstance.database.runInTransaction {
            dbMethod.gameMultiAddOnLinkListByMultiAddOn(
                games,
                appInstance.database.gameDao(),
                appInstance.database.gameMultiAddOnDao(),
                id
            )
        }
        if (loadImage){
            savePicture(data.name, Type.MultiAddOn.name)
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
        gameTypeList.forEach {type ->
            CoroutineScope(SupervisorJob()).launch{
                val lowerCase = type.replaceFirstChar { it.lowercase() }
                val rvName = if(type==Type.Game.name) "rv$type" else "rvGame$type"
                val rv = binding::class.members.find { it.name == rvName }!!.call(binding) as RecyclerView
                addedGameContentHashMap[type] = ArrayList()
                val adapter = GenericCommonGameListCbAdapter(this@AddElement, addedGameContentHashMap[type]!!, type)
                val dao = appInstance.database::class.members.find{it.name == "${lowerCase}Dao"}!!.call(
                    appInstance.database) as CommonDao<CommonComponent,CommonGame>
                runOnUiThread {
                    rv.adapter = adapter
                    layout(rv)
                    dao.getAllWithDesigner().observe(this@AddElement, {it?.let{adapter.submitList(it)}})
                }
            }
        }

        binding.rvAddOnGame.adapter = addOnGameAdapter
        layout(binding.rvAddOnGame)
        appInstance.database.gameDao().getAllWithDesigner().observe(
            this,
            {it?.let{ addOnGameAdapter.submitList(it) }}
        )

        binding.rvDifficulty.adapter = difficultyAdapter
        layout(binding.rvDifficulty)
        appInstance.database.difficultyDao().getAll().asLiveData().observe(
            this,
            {it?.let{difficultyAdapter.submitList(it)}}
        )

    }

    override fun onGenericClick(name: String, type: String, cb:CheckBox) {
        listMethod.listContentManager(addedStringContentHashMap[type]!!, name ,cb)
    }

    override fun onGenericClick(datum: CommonGame, view:CheckBox, type:String) {
        listMethod.listContentManager(addedGameContentHashMap[type]!!, datum, view)
    }

    override fun onGenericClick(datum: OneToOne) {
        when(datum){
            is DesignerWithGame -> {
                if (game == datum) {
                    game = null
                    binding.tvGame.text = getString(R.string.none)
                }

                else {
                    game = datum
                    binding.tvGame.text = datum.name
                }
            }
            is DifficultyTableBean -> binding.etDifficulty.setText(datum.name)
        }
    }

    private fun fillView(type: String?, id:Long){
        when(type){
            Type.Game.name-> {
                check(binding.rbGame)
                fillCommonView(appInstance.database.gameDao(),id)
                CoroutineScope(SupervisorJob()).launch{
                    gameFill(appInstance.database.gameDao(), addedStringContentHashMap, id)
                }
            }
            Type.AddOn.name -> {
                check(binding.rbAddOn)
                fillCommonView(appInstance.database.addOnDao(),id)
                CoroutineScope(SupervisorJob()).launch{
                    genericFill(appInstance.database.addOnDao(), addedStringContentHashMap, id)
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
                    genericFill(appInstance.database.multiAddOnDao(), addedStringContentHashMap, id)
                    addedGameContentHashMap[Type.Game.name]!!.addAll(appInstance.database.multiAddOnDao().getGameObjectFromMultiAddOn(id))
                }
            }
        }
    }

    private fun check(rb:RadioButton){
        rb.isChecked = true
        when(rb){
            binding.rbGame -> setView(binding.llGame)
            binding.rbAddOn -> setView(binding.llAddOn)
            binding.rbMultiAddOn -> setView(binding.llMultiAddOn)
        }
    }

    fun <T:ID, U>genericFill(dao: CommonDao<T, U>, map: HashMap<String, ArrayList<String>>, id:Long){
        map[Type.Designer.name]!!.addAll(dao.getDesignerObject(id).map{it.name})
        map[Type.Artist.name]!!.addAll(dao.getArtistObject(id).map{it.name})
        map[Type.Publisher.name]!!.addAll(dao.getPublisherObject(id).map{it.name})
        map[Type.PlayingMod.name]!!.addAll(dao.getPlayingModObject(id).map{it.name})
        map[Type.Language.name]!!.addAll(dao.getLanguageObject(id).map{it.name})
    }

    fun gameFill(dao: GameDao, map: HashMap<String, ArrayList<String>>, id:Long){
        genericFill(dao, map, id)
        map[Type.Tag.name]!!.addAll(dao.getTagObject(id).map{it.name})
        map[Type.Topic.name]!!.addAll(dao.getTopicObject(id).map{it.name})
        map[Type.Mechanism.name]!!.addAll(dao.getMechanismObject(id).map{it.name})
        addedGameContentHashMap[Type.AddOn.name]!!.addAll(appInstance.database.addOnDao().getDesignerWithAddOnOfGames(id))
        addedGameContentHashMap[Type.MultiAddOn.name]!!.addAll(appInstance.database.multiAddOnDao().getDesignerWithMultiAddOnObjectOfGame(id))
    }


    fun <T : CommonComponent, U> fillCommonView(dao: CommonDao<T, U>, id: Long) {
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

    private fun fillCommonRv(){
        addedStringContentTypeList.forEach { type ->
            CoroutineScope(SupervisorJob()).launch{
                addedStringContentHashMap[type] = ArrayList()
                val lowercase = type.replaceFirstChar { it.lowercase() }
                val adapter = GenericIDListCbAdapter(this@AddElement, type, addedStringContentHashMap[type]!!)
                val rv = binding::class.members.find {
                    it.name == "rv$type"
                }!!.call(binding) as RecyclerView
                val dao = appInstance.database::class.members.find{
                    it.name == "${lowercase}Dao"
                }!!.call(appInstance.database) as CommonCustomInsert<ID>
                runOnUiThread {
                    layout(rv)
                    rv.adapter = adapter
                    dao.getAll().asLiveData().observe(this@AddElement, {it?.let{adapter.submitList(it)}})
                }
            }
        }
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
        addElementName(datum.primary_designer?.name, appInstance.database.designerDao(), Type.Designer.name, binding.llAddDesigner)
        addElementName(datum.primary_publisher?.name, appInstance.database.publisherDao(), Type.Publisher.name, binding.llAddPublisher)
        addToAddedList(datum.artists, Type.Artist.name, appInstance.database.artistDao(), binding.llAddArtist)
        addToAddedList(convertIdListToNameList(datum.mechanics, ALL_MECHANICS), Type.Mechanism.name, appInstance.database.mechanismDao(), binding.llAddMechanism)
        addToAddedList(convertIdListToNameList(datum.categories, ALL_CATEGORIES), Type.Topic.name, appInstance.database.topicDao(), binding.llAddTopic)
        bgaApiGame?.image_url?.run{
            setImage()
        }
    }

    fun <T>addElementName(name:String?, dao:CommonCustomInsert<T>, type:String, ll: LinearLayout){
        CoroutineScope(SupervisorJob()).launch{
            name?.run{
                val answer = dao.getByName(this)
                if (answer.isEmpty()) {
                    setOnClickAddButton(ll, this)
                }
                else addedStringContentHashMap[type]!!.add(this)
            }
        }
    }

    fun <T>addToAddedList(list:ArrayList<String>, type:String, dao:CommonCustomInsert<T>, ll:LinearLayout){
        if (list.isNotEmpty()){
            list.forEach {
                CoroutineScope(SupervisorJob()).launch{
                    val answer = dao.getByName(it)
                    if (answer.isEmpty())setOnClickAddButton(ll, it)
                    else addedStringContentHashMap[type]!!.add(it)
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
