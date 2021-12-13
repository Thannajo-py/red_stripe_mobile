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
    GenericIDCbListener, GenericCommonGameCbListener, GenericOneToOneListener{

    private val binding by lazy{ActivityAddElementBinding.inflate(layoutInflater)}
    private val listMethod = ListCommonMethod()
    private val dbMethod = DbMethod()

    private val addedStringContentHashMap = HashMap<String,ArrayList<String>>()
    private val addedGameContentHashMap = HashMap<String,ArrayList<CommonGame>>()

    private var game:DesignerWithGame? = null

    private val changedObjectId: Long by lazy{
        intent.getLongExtra(SerialKey.ToModifyDataId.name, 0L)
    }
    private val changedObjectType: String? by lazy{
        intent.getStringExtra(SerialKey.ToModifyDataType.name)
    }
    private val changedObjectName: String? by lazy{
        intent.getStringExtra(SerialKey.ToModifyDataName.name)
    }

    private val bgaApiGame by lazy{
        intent.getSerializableExtra(SerialKey.ApiBgaGame.name) as BgaGameBean?
    }

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
        setContentView(binding.root)
        registerForContextMenu(binding.ivPictureChoice)
        fillAddedStringContentHashMap()
        fillCommonRv()
        binding.btnAdd.setOnClickListener(this)
        radioButtonSetOnClickListener()
        fillPageRv()
        setAddButtonEvent()
        loadObject()
    }

    private fun fillAddedStringContentHashMap(){
        getAddedStringContentType().forEach {
            addedStringContentHashMap[it] = arrayListOf()
        }
    }

    private fun loadObject(){
        when(true){
            changedObjectId != 0L -> loadChangedObjectData()
            bgaApiGame != null -> bgaApiGame?.run{loadBgaGame(this)}
            else -> showImage()
        }
    }

    private fun showImage(){
        runOnUiThread {
            binding.pbIvPictureChoice.visibility = View.GONE
            binding.ivPictureChoice.visibility = View.VISIBLE
        }
    }

    private fun radioButtonSetOnClickListener(){
        dbMethod.getGameType().forEach{type->
            CoroutineScope(SupervisorJob()).launch{
                val rb = binding.getMember("rb$type") as RadioButton
                val ll = binding.getMember("ll$type") as LinearLayout
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

    private fun getAddedStringContentType(): ArrayList<String>{
        val list = dbMethod.getCommonField()
        list.addAll(dbMethod.getGameCommonSpecificField())
        return list
    }

    private fun loadChangedObjectData(){
        CoroutineScope(SupervisorJob()).launch{
            changedObjectName?.run{
                val lowCamelCase = changedObjectType?.highToLowCamelCase()
                val dao = appInstance.database.getMember("${lowCamelCase}Dao")
                        as CommonDao<CommonComponent, CommonGame>
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
        showImage()
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View,
                                     menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)){
            menu.add(0, MenuId.TakePhoto.ordinal, 0, getString(R.string.take_photo))
        }
        menu.add(0, MenuId.AddExternalLink.ordinal, 0, getString(R.string.add_external_url))
        menu.add(0, MenuId.GetInternalFile.ordinal, 0, getString(R.string.get_image_from_storage))
        if(loadImage) {
            menu.add(0, MenuId.ResetImage.ordinal, 0, getString(R.string.reset_image))
            menu.add(0, MenuId.DeleteImage.ordinal, 0, getString(R.string.delete_image))
        }
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
            MenuId.ResetImage.ordinal -> resetImage()
            MenuId.DeleteImage.ordinal -> deleteImage()
            else -> super.onContextItemSelected(item)
        }
    }

    private fun resetImage():Boolean{
        loadImage = false
        binding.ivPictureChoice.setImageBitmap(null)
        changedObjectName?.apply{
            val name = this
            changedObjectType?.run{
                setImage(name, this)
            }
        }
        bgaApiGame?.image_url?.run{setImage()}
        return true
    }
    private fun deleteImage():Boolean{
        binding.ivPictureChoice.setImageBitmap(null)
        loadImage = false
        externalImageUrl = null
        deleteImage = true
        return true
    }

    private fun removeImage(){
        binding.ivPictureChoice.setImageBitmap(null)
        externalImageUrl = null
        if (changedObjectName != null && changedObjectType != null){
            CoroutineScope(SupervisorJob()).launch{
                val img = appInstance.database.imageDao().getByName(
                    "$changedObjectName$changedObjectType"
                )
                if (img.isNotEmpty()){
                    appInstance.database.imageDao().deleteByName(
                        "$changedObjectName$changedObjectType"
                    )
                }

            }
        }
    }

    private fun<T> requestPermission(
        permission:String,
        rc:Int,
        activity: ActivityResultLauncher<T>,
        arg:T
    ){
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
        Toast.makeText(
            this,
            getString(R.string.authorization_required),
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun setImage(name: String, type: String){
        val lowCamelCase = type.highToLowCamelCase()
        val dao = appInstance.database.getMember("${lowCamelCase}Dao")
                as CommonDao<CommonComponent, CommonGame>
        setImage(name, dao)
    }

    private fun<T, U> setImage(name: String, dao: CommonDao<T, U>){
        CoroutineScope(SupervisorJob()).launch{
            val imageList = dao.getImage(name)
            if(imageList.isNotEmpty()){
                val file = File(this@AddElement.filesDir, imageList.first().name)
                val compressedBitMap = BitmapFactory.decodeByteArray(
                    file.readBytes(),
                    0,
                    file.readBytes().size
                )
                runOnUiThread {
                    binding.ivPictureChoice.setImageBitmap(compressedBitMap)
                }
            }
        }
    }

    private fun setImage(imageName:String){
        val file = File(this@AddElement.filesDir, imageName)
        val compressedBitMap = BitmapFactory.decodeByteArray(
            file.readBytes(),
            0,
            file.readBytes().size
        )
        runOnUiThread {
            binding.ivPictureChoice.setImageBitmap(compressedBitMap)
        }
    }

    private fun setImage() {
        bgaApiGame?.image_url?.run{
            runOnUiThread {
                Picasso.get().load(this).into(binding.ivPictureChoice)
                showImage()
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
            .setMessage(getString(R.string.external_image_link_copy))
            .setTitle(getString(R.string.external_image_link))
            .setPositiveButton(getString(R.string.ok)) { _,_ ->
                externalImageUrl = urlLink.text.toString()
            }
            .setNegativeButton(getString(R.string.cancel)) { _, _ ->
                Toast.makeText(this, getString(R.string.canceled), Toast.LENGTH_SHORT).show()
            }
            .setView(ll)
            .show()
        return true
    }

    private fun setAddButtonEvent(){
        CoroutineScope(SupervisorJob()).launch{
            getAddedStringContentType().forEach { type ->
                val btn = binding.getMember("btn${type}Add") as FloatingActionButton
                val ll = binding.getMember("llAdd$type") as LinearLayout
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
                binding.etError.text = getString(R.string.name_cannot_be_empty)
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
                binding.etError.text = getString(R.string.name_already_exist)
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
            designer = addList(
                Type.Designer.name,
                appInstance.database.designerDao(),
                binding.llAddDesigner
            ),
            artist = addList(Type.Artist.name, appInstance.database.artistDao(), binding.llAddArtist),
            publisher = addList(
                Type.Publisher.name,
                appInstance.database.publisherDao(),
                binding.llAddPublisher
            ),
            bgg_link = testNull(binding.etBggLink.text.toString()),
            playingMod = addList(
                Type.PlayingMod.name,
                appInstance.database.playingModDao(),
                binding.llAddPlayingMod
            ),
            language = addList(Type.Language.name,
                appInstance.database.languageDao(),
                binding.llAddLanguage
            ),
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

    private fun <T>addList(
        type: String,
        dao:CommonCustomInsert<T>,
        ll: LinearLayout
    ):ArrayList<String>{
        val finalList = mutableSetOf<String>()
        finalList.addAll(addedStringContentHashMap[type]!!.filter { it.isNotBlank() })
        val etList = getAllEditTextTextFromLinearLayout(ll)
        finalList.addAll(etList)
        etList.forEach {
            if (dao.getByName(it).isEmpty()) dao.insert(it)
        }
        return finalList.toCollection(ArrayList())
    }

    private fun getAllEditTextTextFromLinearLayout(ll: LinearLayout):List<String>{
        val etList = ArrayList<String>()
        ll.forEach {child->
            if (child is LinearLayout){
                child.forEach {
                    if(it is EditText && it.text.toString().isNotBlank()){
                        etList.add(it.text.toString())
                    }
                }
            }
        }
        return etList
    }

    private fun getDifficultyId(): Long?{
        if (binding.etDifficulty.text.toString().isBlank()) return null
        val difficultyId: Long?
        val difficultyL = appInstance.database.difficultyDao().getByName(
            binding.etDifficulty.text.toString()
        )
        difficultyId = if(difficultyL.isNotEmpty()) difficultyL[0].id
        else appInstance.database.difficultyDao().insert(
            DifficultyTableBean(
            0,
            binding.etDifficulty.text.toString()
            )
        )
        return difficultyId
    }

    private fun registerGame(data: CommonAddObject) {
        val dbMethod = DbMethod()
        val gameSpecificField = fillGameSpecificHashMap()
        val addOnList = addedGameContentHashMap[Type.AddOn.name]!!
        CoroutineScope(SupervisorJob()).launch {
            val previous = getPrevious(Type.Game.name, appInstance.database.gameDao())
            if (deleteImage) removeImage()
            val prototypeGame = GameTableBean(
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
            // prototype might have no Id
            val id = appInstance.database.gameDao().insert(prototypeGame)
            val newGame = appInstance.database.gameDao().getObjectById(id).first()
            oldObjectHandling(Type.Game.name)
            dbMethod.insertLink(newGame, Type.Game.name, data, gameSpecificField)
           dbMethod.setAddOnGameLink(addOnList, changedObjectId)
            if (loadImage) savePicture(data.name, Type.Game.name)
        }
    }

    private fun<T:Previous> getPrevious(type:String, dao:CommonDao<T,*>): Previous?{
        if (changedObjectType == type) {
            val gameL = dao.getObjectById(changedObjectId)
            if (gameL.isNotEmpty()) {
                return gameL.first()
            }
        }
        return null
    }

    private fun fillGameSpecificHashMap(): HashMap<String, ArrayList<String>>{
        val gameSpecificField = HashMap<String, ArrayList<String>>()
        val dbMethod = DbMethod()
        dbMethod.getGameSpecificField().forEach {
        val list = ArrayList<String>()
            if(it == Type.MultiAddOn.name){
                list.addAll(addedGameContentHashMap[Type.MultiAddOn.name]!!.map {cg->
                    cg.name
                }.toCollection(ArrayList()))
            }
            else{
                val lowCamelCase = it.highToLowCamelCase()
                val dao = appInstance.database.getMember("${lowCamelCase}Dao")
                        as CommonCustomInsert<*>
                val ll = binding.getMember("llAdd$it") as LinearLayout
                list.addAll(addList(it, dao, ll))
            }
            gameSpecificField[it] = list
        }
        return gameSpecificField
    }

    private fun oldObjectHandling(type: String){
        val dbMethod = DbMethod()
        if (changedObjectId != 0L) {
            changedObjectType?.run{
                val lowCamelCase = this.highToLowCamelCase()
                val db = appInstance.database
                val dao = db.getMember("${lowCamelCase}Dao") as CommonDao<Previous, *>
                val gameList = dao.getObjectById(changedObjectId)
                if (type == this && gameList.isNotEmpty()){
                    dbMethod.deleteLink(gameList.first(), this)
                }
                else if (gameList.isNotEmpty()){
                    dbMethod.delete(gameList.first(), this)
                }
            }

        }
    }

    private fun registerAddOn(data: CommonAddObject){
        CoroutineScope(SupervisorJob()).launch {
            val previous = getPrevious(Type.AddOn.name, appInstance.database.addOnDao())
            val prototypeAddOn = AddOnTableBean(
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
                previous?.picture,
                game?.id,
                true
            )
            // prototype might have no Id
            val id = appInstance.database.addOnDao().insert(prototypeAddOn)
            val newGame = appInstance.database.addOnDao().getObjectById(id).first()
            val dbMethod = DbMethod()
            oldObjectHandling(Type.AddOn.name)
            dbMethod.insertLink(newGame, Type.AddOn.name, data)
            if (loadImage) {
                savePicture(data.name, Type.AddOn.name)
            }
        }
    }

    private fun registerMultiAddOn(data:CommonAddObject) {
        val games =
            addedGameContentHashMap[Type.Game.name]!!.map { it.name }.toCollection(ArrayList())
        CoroutineScope(SupervisorJob()).launch {
            val previous = getPrevious(Type.MultiAddOn.name, appInstance.database.multiAddOnDao())
            val prototypeMultiAddOn = MultiAddOnTableBean(
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
                previous?.picture,
                true
            )
            // prototype might have no Id
            val id = appInstance.database.multiAddOnDao().insert(prototypeMultiAddOn)
            val newGame = appInstance.database.multiAddOnDao().getObjectById(id).first()
            val dbMethod = DbMethod()
            oldObjectHandling(Type.MultiAddOn.name)
            dbMethod.insertLink(newGame, Type.MultiAddOn.name, data)
            appInstance.database.runInTransaction {
                dbMethod.gameMultiAddOnLinkListByMultiAddOn(
                    games,
                    appInstance.database.gameDao(),
                    appInstance.database.gameMultiAddOnDao(),
                    id
                )
            }
            if (loadImage) {
                savePicture(data.name, Type.MultiAddOn.name)
            }
        }
    }

    private fun setView(ll: LinearLayout){
        binding.llGame.visibility = View.GONE
        binding.llAddOn.visibility = View.GONE
        binding.llMultiAddOn.visibility = View.GONE
        ll.visibility = View.VISIBLE
    }

    private fun testNull(etContent:String) =
        if(etContent.isBlank() || etContent == "null") null else etContent

    private fun fillPageRv() {
        setGameTypeListAdapter()
        setAddOnGameAdapter()
        setDifficultyAdapter()
    }

    private fun setGameTypeListAdapter() {
        dbMethod.getGameType().forEach {type ->
            CoroutineScope(SupervisorJob()).launch{
                addedGameContentHashMap[type] = ArrayList()
                val adapter = GenericCommonGameListCbAdapter(
                    this@AddElement,
                    addedGameContentHashMap[type]!!,
                    type
                )
                if(type == Type.AddOn.name){
                    setAddOnRv(adapter)
                }
                else {
                    setOtherGameTypeRv(adapter, type)
                }
            }
        }
    }

    private fun setAddOnRv(adapter: GenericCommonGameListCbAdapter<CommonGame>){
        runOnUiThread {
            val rv = binding.rvGameAddOn
            rv.adapter = adapter
            layout(rv)
            appInstance.database.addOnDao()
                .getWithoutGameAndSelectedGameWithDesigner(changedObjectId)
                .observe(this@AddElement, { it?.let { adapter.submitList(it) } })
        }
    }

    private fun setAddOnGameAdapter(){
        val addOnAdapter = AddOnGameListAdapter(this)
        binding.rvAddOnGame.adapter = addOnAdapter
        layout(binding.rvAddOnGame)
        appInstance.database.gameDao().getAllWithDesigner().observe(
            this,
            {it?.let{ addOnAdapter.submitList(it) }}
        )
    }

    private fun setDifficultyAdapter(){
        val difficultyAdapter = OneToOneListAdapter<DifficultyTableBean>(this)
        binding.rvDifficulty.adapter = difficultyAdapter
        layout(binding.rvDifficulty)
        appInstance.database.difficultyDao().getAll().asLiveData().observe(
            this,
            {it?.let{difficultyAdapter.submitList(it)}}
        )
    }

    private fun setOtherGameTypeRv(
        adapter: GenericCommonGameListCbAdapter<CommonGame>,
        type:String){
        val lowCamelCase = type.highToLowCamelCase()
        val rvName = "rv$type"
        val rv = binding.getMember(rvName) as RecyclerView
        val dao =
            appInstance.database.getMember("${lowCamelCase}Dao")
                    as CommonDao<CommonComponent, CommonGame>
        runOnUiThread {
            rv.adapter = adapter
            layout(rv)
            dao.getAllWithDesigner()
                .observe(this@AddElement, { it?.let { adapter.submitList(it) } })
        }
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
                    addedGameContentHashMap[Type.Game.name]!!.addAll(
                        appInstance.database.multiAddOnDao().getGameObjectFromMultiAddOn(id)
                    )
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

    private fun <T:ID, U>genericFill(
        dao: CommonDao<T, U>,
        map: HashMap<String, ArrayList<String>>,
        id:Long
    ){
        map[Type.Designer.name]!!.addAll(dao.getDesignerObject(id).map{it.name})
        map[Type.Artist.name]!!.addAll(dao.getArtistObject(id).map{it.name})
        map[Type.Publisher.name]!!.addAll(dao.getPublisherObject(id).map{it.name})
        map[Type.PlayingMod.name]!!.addAll(dao.getPlayingModObject(id).map{it.name})
        map[Type.Language.name]!!.addAll(dao.getLanguageObject(id).map{it.name})
    }

    private fun gameFill(dao: GameDao, map: HashMap<String, ArrayList<String>>, id:Long){
        genericFill(dao, map, id)
        map[Type.Tag.name]!!.addAll(dao.getTagObject(id).map{it.name})
        map[Type.Topic.name]!!.addAll(dao.getTopicObject(id).map{it.name})
        map[Type.Mechanism.name]!!.addAll(dao.getMechanismObject(id).map{it.name})
        addedGameContentHashMap[Type.AddOn.name]!!.addAll(
            appInstance.database.addOnDao().getDesignerWithAddOnOfGames(id)
        )
        addedGameContentHashMap[Type.MultiAddOn.name]!!.addAll(
            appInstance.database.multiAddOnDao().getDesignerWithMultiAddOnObjectOfGame(id)
        )
    }

    private fun <T : CommonComponent, U> fillCommonView(dao: CommonDao<T, U>, id: Long) {
        dao.getById(id).asLiveData().observe(this, {
             it?.let { list->
                 if (list.isNotEmpty()) {
                     val element = list.first()
                     if (element is GameTableBean) {
                         binding.rbByPlayerTrue.isChecked = element.by_player ?: false
                     }
                     binding.etNom.setText(element.name)
                     binding.etAge.setText(element.age.toString())
                     binding.etMaxTime.setText(element.max_time.toString())
                     binding.etNbPlayerMin.setText(element.player_min.toString())
                     binding.etNbPlayerMax.setText(element.player_max.toString())
                     binding.etStock.setText(element.stock.toString())
                     binding.etBuyingPrice.setText(element.buying_price.toString())
                     binding.etBggLink.setText(element.bgg_link)
                     externalImageUrl = element.external_img
                     dao.getDifficulty(id).observe(this, { data ->
                         data?.let { list ->
                             if (list.isNotEmpty()) binding.etDifficulty.setText(list.first().name)
                         }
                     })
                 }
            }
        })
    }

    private fun fillCommonRv(){
        getAddedStringContentType().forEach { type ->
            CoroutineScope(SupervisorJob()).launch{
                val lowCamelCase = type.highToLowCamelCase()
                val adapter = GenericIDListCbAdapter(
                    this@AddElement,
                    type,
                    addedStringContentHashMap[type]!!
                )
                val rv = binding.getMember("rv$type") as RecyclerView
                val dao = appInstance.database.getMember("${lowCamelCase}Dao")
                        as CommonCustomInsert<ID>
                val pb = binding.getMember("pbRv$type") as ProgressBar
                runOnUiThread {
                    layout(rv)
                    rv.adapter = adapter
                    dao.getAll().asLiveData().observe(
                        this@AddElement,
                        {it?.let{adapter.submitList(it)}}
                    )
                    pb.visibility = View.GONE
                    rv.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun loadBgaGame(datum:BgaGameBean){
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
        addElementName(
            datum.primary_designer?.name,
            appInstance.database.designerDao(),
            Type.Designer.name,
            binding.llAddDesigner
        )
        addElementName(
            datum.primary_publisher?.name,
            appInstance.database.publisherDao(),
            Type.Publisher.name,
            binding.llAddPublisher
        )
        addToAddedList(
            datum.artists,
            Type.Artist.name,
            appInstance.database.artistDao(),
            binding.llAddArtist
        )
        addToAddedList(
            convertIdListToNameList(datum.mechanics, ALL_MECHANICS),
            Type.Mechanism.name,
            appInstance.database.mechanismDao(),
            binding.llAddMechanism
        )
        addToAddedList(
            convertIdListToNameList(datum.categories, ALL_CATEGORIES),
            Type.Topic.name,
            appInstance.database.topicDao(),
            binding.llAddTopic
        )
        bgaApiGame?.image_url?.run{
            setImage()
        }?:run{showImage()}
    }

    private fun <T>addElementName(
        name:String?,
        dao:CommonCustomInsert<T>,
        type:String,
        ll: LinearLayout
    ){
        CoroutineScope(SupervisorJob()).launch{
            name?.run{
                val answer = dao.getByName(this)
                if (answer.isEmpty()) {
                    runOnUiThread {
                        setOnClickAddButton(ll, this)
                    }
                }
                else addedStringContentHashMap[type]!!.add(this)
            }
        }
    }

    private fun <T>addToAddedList(
        list:ArrayList<String>,
        type:String,
        dao:CommonCustomInsert<T>,
        ll:LinearLayout
    ){
        if (list.isNotEmpty()){
            list.forEach {
                CoroutineScope(SupervisorJob()).launch{
                        val answer = dao.getByName(it)
                        if (answer.isEmpty()) runOnUiThread { setOnClickAddButton(ll, it)}
                        else addedStringContentHashMap[type]!!.add(it)
                }
            }
        }
    }

    private fun convertIdListToNameList(
        idList:ArrayList<IdObjectBean>,
        namedList:ArrayList<NamedResultBean>
    ): ArrayList<String>{
        val resultList = ArrayList<String>()
        idList.forEach { for (m in namedList) {if (m.id==it.id){resultList.add(m.name)}} }
        return resultList
    }
}
