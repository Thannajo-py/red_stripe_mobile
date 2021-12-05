package com.example.filrouge.activity


import android.app.AlertDialog
import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.text.method.PasswordTransformationMethod
import android.view.*
import android.widget.*
import androidx.recyclerview.widget.GridLayoutManager
import com.example.filrouge.*
import com.example.filrouge.activity.*
import com.example.filrouge.bean.*
import com.example.filrouge.dao.CommonCustomInsert
import com.example.filrouge.dao.CommonDao
import com.example.filrouge.dao.CommonJunctionDAo
import com.example.filrouge.databinding.ActivityViewGamesBinding
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.io.File
import java.lang.Exception


class ViewGamesActivity : AppCompatActivity(), OnGenericListAdapterListener {


    private val binding: ActivityViewGamesBinding by lazy{
        ActivityViewGamesBinding.inflate(layoutInflater)
    }
    private val gson = Gson()
    private val newAdapter = GenericListAdapter(this)
    private val db = appInstance.database
    private val dbMethod = DbMethod()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.rvGames.adapter = newAdapter
        db.gameDao().getAllWithDesigner().observe(this, {it?.let{newAdapter.submitList(it)}})
        binding.rvGames.layoutManager = GridLayoutManager(this, 1)
        binding.rvGames.addItemDecoration(MarginItemDecoration(5))
        getSave()
    }

    override fun onResume() {
        getSave()
        super.onResume()
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (currentUser?.synchronize == true) {
            if (!isLocal){

                menu?.add(0,
                    MenuId.Synchronize.ordinal,
                    0,
                    getString(R.string.save_and_synchronize)
                )
                menu?.add(0, MenuId.ResetDB.ordinal, 0, getString(R.string.reset_content))

            }else{
                menu?.add(0, MenuId.SaveLocalDatabase.ordinal, 0, getString(R.string.save_local))
                menu?.add(0, MenuId.LoadLocalDatabase.ordinal, 0, getString(R.string.reset_local))

            }
            menu?.add(
                0,
                MenuId.SynchronizeParameter.ordinal,
                0,
                getString(R.string.synchronize_parameters)
            )
        }
        menu?.add(0, MenuId.Search.ordinal, 0, getString(R.string.search))
        menu?.add(0, MenuId.LoadImages.ordinal,0,getString(R.string.download_images))
        if (currentUser?.addAccount == true){
            menu?.add(0, MenuId.CreateAccount.ordinal, 0, getString(R.string.add_account))
        }
        if (currentUser?.deleteAccount == true){
            menu?.add(0, MenuId.DeleteAccount.ordinal, 0, getString(R.string.delete_account))
        }
        if(currentUser?.add == true){
            menu?.add(0, MenuId.AddContent.ordinal, 0, getString(R.string.add_element))
        }
        menu?.add(0, MenuId.ApiSearch.ordinal, 0, getString(R.string.api_add_element))
        menu?.add(0, MenuId.ChangePassword.ordinal, 0, getString(R.string.change_password))
        menu?.add(0, MenuId.Disconnect.ordinal, 0, getString(R.string.disconnect))
        menu?.add(0, MenuId.DeleteObject.ordinal, 0, getString(R.string.delete_object))
        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            MenuId.Search.ordinal -> startActivity(Intent(this, Search::class.java))
            MenuId.DeleteAccount.ordinal -> startActivity(
                Intent(this, DeleteAccount::class.java)
            )
            MenuId.AddContent.ordinal -> startActivity(
                Intent(this, AddElement::class.java)
            )
            MenuId.CreateAccount.ordinal -> startActivity(
                Intent(this, CreateNewAccount::class.java)
            )
            MenuId.Synchronize.ordinal -> synchronizeBox(
                getString(R.string.save_synchronize_message),
                false
            )
            MenuId.LoadImages.ordinal -> {
                refreshAll()
            }
            MenuId.SynchronizeParameter.ordinal -> urlParameterBox()
            MenuId.ChangePassword.ordinal -> changePasswordBox()
            MenuId.SaveLocalDatabase.ordinal -> saveLocalDatabase()
            MenuId.LoadLocalDatabase.ordinal -> loadLocalDatabase()
            MenuId.ResetDB.ordinal -> {
                appInstance.sharedPreference.saveFloat(SerialKey.Timestamp.name, 0.0F)
                synchronizeBox(getString(R.string.reset_content_message), true)

            }
            MenuId.Disconnect.ordinal -> {
                appInstance.sharedPreference.removeValue(SerialKey.SavedUser.name)
                currentUser = null
                startActivity(Intent(this, MainActivity::class.java))
                finishAffinity()
            }
            MenuId.ApiSearch.ordinal -> startActivity(
                Intent(this, APISearchActivity::class.java)
            )
            MenuId.DeleteObject.ordinal -> startActivity(
                Intent(this, DbObjectDelete::class.java)
            )
        }
        return super.onOptionsItemSelected(item)
    }

    private fun saveLocalDatabase(){
        val db = appInstance.database
        CoroutineScope(SupervisorJob()).launch {
            db.runInTransaction {
                appInstance.sharedPreference.save(gson.toJson(SavedDatabase(
                    db.gameDao().getList(),
                    db.addOnDao().getList(),
                    db.multiAddOnDao().getList(),
                    db.tagDao().getList(),
                    db.topicDao().getList(),
                    db.mechanismDao().getList(),
                    db.userDao().getList(),
                    db.difficultyDao().getList(),
                    db.designerDao().getList(),
                    db.artistDao().getList(),
                    db.publisherDao().getList(),
                    db.playingModDao().getList(),
                    db.languageDao().getList(),
                    db.gameMultiAddOnDao().getList(),
                    db.gameTagDao().getList(),
                    db.gameTopicDao().getList(),
                    db.gameMechanismDao().getList(),
                    db.gameDesignerDao().getList(),
                    db.addOnDesignerDao().getList(),
                    db.multiAddOnDesignerDao().getList(),
                    db.gameArtistDao().getList(),
                    db.addOnArtistDao().getList(),
                    db.multiAddOnArtistDao().getList(),
                    db.gamePublisherDao().getList(),
                    db.addOnPublisherDao().getList(),
                    db.multiAddOnPublisherDao().getList(),
                    db.gamePlayingModDao().getList(),
                    db.addOnPlayingModDao().getList(),
                    db.multiAddOnPlayingModDao().getList(),
                    db.gameLanguageDao().getList(),
                    db.addOnLanguageDao().getList(),
                    db.multiAddOnLanguageDao().getList(),
                    db.deletedItemDao().getAll(),
                    db.imageDao().getAll()
                )), SerialKey.SaveDatabase.name)
            }
        }

    }

    private fun loadLocalDatabase(){
        val db = appInstance.database

        CoroutineScope(SupervisorJob()).launch {
            db.clearAllTables()
            db.runInTransaction {
                val dbSave = gson.fromJson(
                    appInstance.sharedPreference.getValueString(SerialKey.SaveDatabase.name),
                    SavedDatabase::class.java
                )
                dbSave.difficulty.forEach { db.difficultyDao().insert(it) }
                dbSave.game.forEach { db.gameDao().insert(it) }
                dbSave.addOn.forEach { db.addOnDao().insert(it) }
                dbSave.multiAddOn.forEach { db.multiAddOnDao().insert(it) }
                dbSave.tag.forEach { db.tagDao().insert(it) }
                dbSave.topic.forEach { db.topicDao().insert(it) }
                dbSave.mechanism.forEach { db.mechanismDao().insert(it) }
                dbSave.user.forEach { db.userDao().insert(it) }
                dbSave.designer.forEach { db.designerDao().insert(it) }
                dbSave.artist.forEach { db.artistDao().insert(it) }
                dbSave.publisher.forEach { db.publisherDao().insert(it) }
                dbSave.playingMod.forEach { db.playingModDao().insert(it) }
                dbSave.language.forEach { db.languageDao().insert(it) }
                dbSave.gameMultiAddOn.forEach { db.gameMultiAddOnDao().insert(it) }
                dbSave.gameTag.forEach { db.gameTagDao().insert(it) }
                dbSave.gameTopic.forEach { db.gameTopicDao().insert(it) }
                dbSave.gameMechanism.forEach { db.gameMechanismDao().insert(it) }
                dbSave.gameDesigner.forEach { db.gameDesignerDao().insert(it) }
                dbSave.addOnDesigner.forEach { db.addOnDesignerDao().insert(it) }
                dbSave.multiAddOnDesigner.forEach { db.multiAddOnDesignerDao().insert(it) }
                dbSave.gameArtist.forEach { db.gameArtistDao().insert(it) }
                dbSave.addOnArtist.forEach { db.addOnArtistDao().insert(it) }
                dbSave.multiAddOnArtist.forEach { db.multiAddOnArtistDao().insert(it) }
                dbSave.gamePublisher.forEach { db.gamePublisherDao().insert(it) }
                dbSave.addOnPublisher.forEach { db.addOnPublisherDao().insert(it) }
                dbSave.multiAddOnPublisher.forEach { db.multiAddOnPublisherDao().insert(it) }
                dbSave.gamePlayingMod.forEach { db.gamePlayingModDao().insert(it) }
                dbSave.addOnPlayingMod.forEach { db.addOnPlayingModDao().insert(it) }
                dbSave.multiAddOnPlayingMod.forEach { db.multiAddOnPlayingModDao().insert(it) }
                dbSave.gameLanguage.forEach { db.gameLanguageDao().insert(it) }
                dbSave.addOnLanguage.forEach { db.addOnLanguageDao().insert(it) }
                dbSave.multiAddOnLanguage.forEach { db.multiAddOnLanguageDao().insert(it) }
                dbSave.deletedContent.forEach { db.deletedItemDao().insert(it) }
                dbSave.image.forEach { db.imageDao().insert(it) }

            }
        }
    }

    private fun synchronize(login:String, password:String, cancel:Boolean){
        binding.progressBar.visibility = View.VISIBLE
        binding.tvGameError.visibility = View.GONE
        CoroutineScope(SupervisorJob()).launch {
        val timestamp = appInstance.sharedPreference.getFloat(SerialKey.Timestamp.name)

        var modification = SendApiChange(login,password, ApiResponse(
            ArrayList(), ArrayList(), ArrayList())
            , ApiResponse(ArrayList(), ArrayList(), ArrayList()),
            ApiDelete(ArrayList(), ArrayList(), ArrayList()), timestamp
        )


        if (!cancel){
            modification = SendApiChange(login,password, ApiResponse(
                appInstance.database.gameDao().getWithoutServerId().map{
                    dbMethod.convertToBean(it)
                }.toCollection(ArrayList()),
                appInstance.database.addOnDao().getWithoutServerId().map{
                    dbMethod.convertToBean(it)
                }.toCollection(ArrayList()),
                appInstance.database.multiAddOnDao().getWithoutServerId().map{
                    dbMethod.convertToBean(it)
                }.toCollection(ArrayList())
            )
                , ApiResponse(
                    appInstance.database.gameDao().getChanged().map{
                        dbMethod.convertToBean(it)
                    }.toCollection(ArrayList()),
                    appInstance.database.addOnDao().getChanged().map{
                        dbMethod.convertToBean(it)
                    }.toCollection(ArrayList()),
                    appInstance.database.multiAddOnDao().getChanged().map{
                        dbMethod.convertToBean(it)
                    }.toCollection(ArrayList()),
                ),
                ApiDelete(
                    appInstance.database.deletedItemDao().getByType(Type.Game.name).map{
                        DeletedObject(it.idContent.toInt())
                    }.toCollection(ArrayList()),
                    appInstance.database.deletedItemDao().getByType(Type.AddOn.name).map{
                        DeletedObject(it.idContent.toInt())
                    }.toCollection(ArrayList()),
                    appInstance.database.deletedItemDao().getByType(Type.MultiAddOn.name).map{
                        DeletedObject(it.idContent.toInt())
                    }.toCollection(ArrayList()),
                    ), timestamp)
        }
        val content = gson.toJson(ApiBody(modification))




            try {
                API_URL?.run{
                    val body = sendPostOkHttpRequest(this, content)
                    apiReception(body)
                }

            } catch (e: Exception) {
                apiErrorHandling(e)
            }
        }
    }



    private fun apiReception(body:String){

        if (body.isNotBlank()){
            val result = gson.fromJson(body, ApiReceive::class.java)
            if (result.timestamp > 0.0){
                val timestamp = appInstance.sharedPreference.getFloat(SerialKey.Timestamp.name)
                if (timestamp < 1.0){
                    val list = appInstance.database.userDao().checkEmpty()
                    appInstance.database.clearAllTables()
                    list.forEach { appInstance.database.userDao().insert(it) }
                }
                appInstance.sharedPreference.saveFloat(SerialKey.Timestamp.name, result.timestamp)
            }
            if(
                !result.games.isNullOrEmpty()
                || !result.add_ons.isNullOrEmpty()
                || !result.add_ons.isNullOrEmpty()
            ) {
                CoroutineScope(SupervisorJob()).launch {
                    db.runInTransaction {
                        db.deletedItemDao().deleteAll()
                        db.gameDao().getWithoutServerId().forEach {
                            dbMethod.delete_link(it)
                            db.gameDao().deleteOne(it.id)
                        }

                        db.addOnDao().getWithoutServerId().forEach {
                            dbMethod.delete_link(it)
                            db.addOnDao().deleteOne(it.id)
                        }

                        db.multiAddOnDao().getWithoutServerId().forEach {
                            dbMethod.delete_link(it)
                            db.multiAddOnDao().deleteOne(it.id)
                        }

                    }
                    result.games?.run {
                            db.runInTransaction {
                                val dbGame = db.gameDao()
                                result.games.forEach {
                                    dbGame.insert(dbMethod.convertToTableBean(it))
                                }
                                result?.deleted_games?.forEach {
                                    val gameInDb = dbGame.getByServerId(it.toLong())
                                    if (gameInDb.isNotEmpty()) dbMethod.delete_link(gameInDb[0])
                                    db.gameDao().deleteOne(it.toLong())

                                }

                            }

                    }

                    result.add_ons?.run {

                            db.runInTransaction {
                                result.add_ons.forEach {
                                    db.addOnDao().insert(dbMethod.convertToTableBean(it))
                                }
                                result?.deleted_add_ons?.forEach {
                                    val gameInDb = db.addOnDao().getByServerId(it.toLong())
                                    if (gameInDb.isNotEmpty()) dbMethod.delete_link(gameInDb[0])
                                    db.addOnDao().deleteOne(it.toLong())

                                }

                            }
                    }

                    result.multi_add_ons?.run {
                            db.runInTransaction {
                                val dbGame = db.multiAddOnDao()
                                result.multi_add_ons.forEach {
                                    db.multiAddOnDao().insert(dbMethod.convertToTableBean(it))

                                }
                                result?.deleted_multi_add_ons?.forEach {
                                    val gameInDb = dbGame.getByServerId(it.toLong())
                                    if (gameInDb.isNotEmpty()) dbMethod.delete_link(gameInDb[0])
                                    db.multiAddOnDao().deleteOne(it.toLong())

                                }
                            }
                    }

                    result.games?.run {
                        this.forEach {
                            val listOfTripleFill = dbMethod.getGameTripleListField(it)
                            commonAssociativeFill(it, db.gameDao(), listOfTripleFill)
                            gameMultiAddonAssociativeListFill(it)
                        }
                    }

                    result.add_ons?.run {
                        this.forEach {
                            val listOfTripleFill = dbMethod.getAddOnTripleListField(it)
                            commonAssociativeFill(it, db.addOnDao(), listOfTripleFill)
                        }
                    }

                    result.multi_add_ons?.run {
                        this.forEach {
                            val listOfTripleFill = dbMethod.getMultiAddOnTripleListField(it)
                            commonAssociativeFill(it, db.multiAddOnDao(), listOfTripleFill)
                        }
                    }

                    refreshAll()
                }
            }

        }

        runOnUiThread {

            binding.progressBar.visibility = View.GONE
        }

    }



    private fun <T:CommonBase, U:ID, V, W:ID>associativeTableFill(game: T,
                                                                  searched_array_list:ArrayList<String>,
                                                                  gameDao:CommonDao<U>,
                                                                  otherDao: CommonCustomInsert<W>,
                                                                  junctionDao:CommonJunctionDAo<V> ){
    val gameL = gameDao.getByServerId(game.id?.toLong() ?: 0)
        if (gameL.isNotEmpty()) {
            val gameChild = gameL[0]
            junctionDao.deleteWithMember1Id(gameChild.id)
            searched_array_list.forEach {
                CoroutineScope(SupervisorJob()).launch{
                    db.runInTransaction {
                        val multiAddOnL = otherDao.getByName(it)
                        if (multiAddOnL.isNotEmpty()){
                            val multiAddOn = multiAddOnL[0]
                            junctionDao.insert(gameChild.id, multiAddOn.id)

                        }else{
                            otherDao.insert(it)
                            val multiAddOnNew = otherDao.getByName(it)
                            junctionDao.insert(gameChild.id, multiAddOnNew[0].id)

                        }

                    }
                }
            }
        }
    }

    private fun <T:CommonBase,U:ID, V, W:ID> commonAssociativeFill(
        game:T,gameDao:CommonDao<U>,
        otherDao: ArrayList<Triple<
                ArrayList<String>,
                CommonCustomInsert<out W>,
                CommonJunctionDAo<out V>
                >>
    )
    {
        otherDao.forEach {
            associativeTableFill(
                game,
                it.first,
                gameDao,
                it.second,
                it.third
            )

        }

    }

    private fun gameMultiAddonAssociativeListFill(it:GameBean){
        val gameL = db.gameDao().getByServerId(it.id?.toLong() ?: 0)
        if (gameL.isNotEmpty()) {
            val gameChild = gameL[0]
            db.gameMultiAddOnDao().deleteWithMember1Id(gameChild.id)
            it.multi_add_on.forEach {
                CoroutineScope(SupervisorJob()).launch{
                    db.runInTransaction {
                        val multiAddOnL = db.multiAddOnDao().getByName(it)
                        if (multiAddOnL.isNotEmpty()){
                            val multiAddOn = multiAddOnL[0]
                            db.gameMultiAddOnDao().insert(gameChild.id, multiAddOn.id)

                        }

                    }
                }
            }
        }
    }




    private fun apiErrorHandling(e:Exception){

        e.printStackTrace()
        runOnUiThread {
            binding.tvGameError.text = getString(R.string.error_content, e.message)
            binding.tvGameError.visibility = View.VISIBLE
            binding.progressBar.visibility = View.GONE

        }
    }


    override fun onElementClick(datum: CommonGame) {

        intent = Intent(this, GameDetails::class.java)
        intent.putExtra(SerialKey.GameId.name, datum.id)
        startActivity(intent)
    }





    private fun getSave(){
        isLocal = appInstance.sharedPreference.getBoolean(SerialKey.IsLocal.name)
    }



    private fun refreshAll(){
        loadImages(appInstance.database.gameDao(), Type.Game.name)
        loadImages(appInstance.database.addOnDao(), Type.AddOn.name)
        loadImages(appInstance.database.multiAddOnDao(), Type.MultiAddOn.name)
        cleanImageList()

    }

    private fun <T:CommonComponent> loadImages(dao:CommonDao<T>, type:String){
        CoroutineScope(SupervisorJob()).launch{
            for (game in dao.getList()){
                if (dao.getImage(game.name).isEmpty()){

                    if (!game.external_img.isNullOrBlank()){
                        launch{
                            getImage(game.external_img!!, game.name, type)
                        }

                    }
                    else if (!game.picture.isNullOrBlank() && API_STATIC != null){
                        launch{
                            getImage("$API_STATIC${game.picture}", game.name, type)
                        }
                    }
                }

            }


        }
    }

    private fun getImage(url:String, gameName:String, type:String){
        try {
            val img = sendGetOkHttpRequestImage(url)
            img?.run {
                appInstance.database.imageDao().insert(
                    ImageTableBean(
                        0,
                        "$gameName$type",
                        gameName,
                        type
                    )
                )
                val file = File(
                    this@ViewGamesActivity.filesDir,
                    "$gameName$type"
                )
                file.writeBytes(this)

            }

        }catch(e:Exception){
            e.printStackTrace()
        }
    }


    private fun addTextView(text:String):TextView{
        val textView = TextView(this)
        textView.text = text
        textView.typeface = Typeface.DEFAULT_BOLD
        textView.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        return textView
    }

    fun addLinearLayout(elements:ArrayList<View>):LinearLayout{
        val ll = LinearLayout(this)
        ll.orientation = LinearLayout.VERTICAL
        ll.gravity = Gravity.CENTER
        ll.setPadding(20,0,20,0)
        elements.forEach { ll.addView(it) }
        return ll
    }

    private fun addEditTextPassword():EditText{
        val pwd = EditText(this)
        pwd.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
        pwd.transformationMethod = PasswordTransformationMethod.getInstance()
        return pwd

    }

    private fun cleanImageList(){
        CoroutineScope(SupervisorJob()).launch{
            appInstance.database.runInTransaction {
                appInstance.database.imageDao().getAll().forEach {
                    when(it.gameType){
                        Type.Game.name -> if(appInstance.database.gameDao()
                                .getByName(it.gameName).isEmpty())deleteImage(it.name)
                        Type.AddOn.name -> if(appInstance.database.addOnDao()
                                .getByName(it.gameName).isEmpty()) deleteImage(it.name)
                        Type.MultiAddOn.name -> if(appInstance.database.multiAddOnDao()
                                .getByName(it.gameName).isEmpty()) deleteImage(it.name)
                    }
                }
            }
        }

    }

    private fun deleteImage(fileName:String){
        appInstance.database.imageDao().deleteByName(fileName)
        File(this@ViewGamesActivity.filesDir, fileName).delete()

    }

    private fun synchronizeBox(message:String, cancel:Boolean){
        val login = EditText(this)
        val pwd = addEditTextPassword()
        val view = arrayListOf<View>(
            addTextView(getString(R.string.login)),
            login,
            addTextView(getString(R.string.password)),
            pwd
        )
        val ll = addLinearLayout(view)
        AlertDialog.Builder(this)
            .setMessage(message)
            .setTitle(getString(R.string.warning))
            .setPositiveButton(getString(R.string.ok)) { dialog, which ->
                run {
                    synchronize(login.text.toString(),pwd.text.toString(), cancel)
                }
            }.setNegativeButton(getString(R.string.cancel)) { dialog, which ->
                Toast.makeText(this, getString(R.string.canceled), Toast.LENGTH_SHORT).show()
            }.setView(ll)
            .show()
    }

    private fun urlParameterBox(){
        val url = EditText(this)
        val staticUrl = EditText(this)
        val cbIsLocal = CheckBox(this)
        cbIsLocal.setText(getString(R.string.server_less))
        cbIsLocal.isChecked = isLocal
        val view = arrayListOf<View>(
            addTextView(getString(R.string.url_api)),
            url,
            addTextView(getString(R.string.url_static_file)),
            staticUrl,
            cbIsLocal
        )
        val ll = addLinearLayout(view)
        url.setText(API_URL)
        staticUrl.setText(API_STATIC)
        AlertDialog.Builder(this)
            .setMessage(getString(R.string.url_web_message))
            .setTitle(getString(R.string.parameters))
            .setPositiveButton(getString(R.string.ok)) { dialog, which ->
                API_URL = url.text.toString()
                API_STATIC = staticUrl.text.toString()
                isLocal = cbIsLocal.isChecked
                appInstance.sharedPreference.saveBoolean(SerialKey.IsLocal.name, isLocal)
                appInstance.sharedPreference.save(url.text.toString(), SerialKey.APIUrl.name)
                appInstance.sharedPreference.save(
                    staticUrl.text.toString(),
                    SerialKey.APIStaticUrl.name
                )
                startActivity(Intent(this, ViewGamesActivity::class.java))
                finish()

            }.setNegativeButton(getString(R.string.cancel)) { dialog, which ->
                Toast.makeText(this, getString(R.string.canceled), Toast.LENGTH_SHORT).show()
            }.setView(ll)
            .show()
    }

    private fun changePasswordBox(){
        val exPwd = addEditTextPassword()
        val pwd = addEditTextPassword()
        val pwdConfirm = addEditTextPassword()
        val view = arrayListOf<View>(
            addTextView(getString(R.string.old_password)),
            exPwd,
            addTextView(getString(R.string.new_password)),
            pwd,
            addTextView(getString(R.string.confirm_password)),
            pwdConfirm
        )
        val ll = addLinearLayout(view)
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.change_password))
            .setPositiveButton(getString(R.string.ok)) { dialog, which ->
                run {
                    passwordChange(exPwd, pwd, pwdConfirm)

                }
            }.setNegativeButton(getString(R.string.cancel)) { dialog, which ->
                Toast.makeText(this, getString(R.string.canceled), Toast.LENGTH_SHORT).show()
            }.setView(ll)
            .show()
    }

    private fun passwordChange(exPwd:EditText, pwd:EditText, pwdConfirm:EditText){
        if(SHA256.encryptThisString(exPwd.text.toString()) == currentUser?.password
            && currentUser != null){
            val cUser = currentUser
            if(pwd.text.toString() == pwdConfirm.text.toString() && cUser != null){
                if(Regex(RegexPattern.PassWord.pattern).matches(pwd.text.toString())){
                    CoroutineScope(SupervisorJob()).launch{
                        val newPasswordUser = UserTableBean(
                            cUser.id,
                            cUser.login,
                            SHA256.encryptThisString(pwd.text.toString()),
                            cUser.add,
                            cUser.change,
                            cUser.delete,
                            cUser.synchronize,
                            cUser.addAccount,
                        cUser.deleteAccount)
                        appInstance.database.userDao().insert(newPasswordUser)
                        currentUser = newPasswordUser
                        Toast.makeText(
                            this@ViewGamesActivity,
                            getString(R.string.password_changed),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                else{
                    Toast.makeText(
                        this,
                        getString(R.string.password_rules),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            else{
                Toast.makeText(
                    this,
                    getString(R.string.password_do_not_match),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        else{
            Toast.makeText(
                this,
                getString(R.string.wrong_password),
                Toast.LENGTH_SHORT
            ).show()
        }

    }
}