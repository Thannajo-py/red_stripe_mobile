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
    private val db = appInstance.database
    private val dbMethod = DbMethod()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        fillRvGame()
        getSave()
    }

    private fun fillRvGame(){
        val newAdapter = GenericListAdapter(this)
        binding.rvGame.adapter = newAdapter
        db.gameDao().getAllWithDesigner().observe(this, {it?.let{newAdapter.submitList(it)}})
        binding.rvGame.layoutManager = GridLayoutManager(this, 1)
        binding.rvGame.addItemDecoration(MarginItemDecoration(5))
        displayRV()
    }

    override fun onResume() {
        getSave()
        super.onResume()
    }

    private fun displayRV(){
        binding.progressBar.visibility = View.GONE
        binding.rvGame.visibility = View.VISIBLE
    }

    private fun hideRV(){
        binding.rvGame.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (currentUser?.synchronize == true) {
            if (!isLocal){
                menu?.add(
                    0,
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
            menu?.add(0, MenuId.ApiSearch.ordinal, 0, getString(R.string.api_add_element))
        }
        menu?.add(0, MenuId.ChangePassword.ordinal, 0, getString(R.string.change_password))
        menu?.add(0, MenuId.Disconnect.ordinal, 0, getString(R.string.disconnect))
        if (currentUser?.delete == true) {
            menu?.add(0, MenuId.DeleteObject.ordinal, 0, getString(R.string.delete_object))
        }
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
                synchronizeBox(
                    getString(R.string.reset_content_message),
                    true
                )
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
        CoroutineScope(SupervisorJob()).launch {
            val save = SavedDatabase()
            db.runInTransaction {
                SaveDbField.values().forEach { f->
                    val name = f.name.highToLowCamelCase()
                    val dao = db.getMember("${name}Dao")
                    val list = dao!!.getMember("getList")
                    save.setMember(name, list)
                }
                appInstance.sharedPreference.save(gson.toJson(save), SerialKey.SaveDatabase.name)
            }
            runOnUiThread {
                Toast.makeText(
                    this@ViewGamesActivity,
                    "db save",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun loadLocalDatabase(){
        hideRV()
        CoroutineScope(SupervisorJob()).launch{
            db.runInTransaction {
                db.clearAllTables()
                val dbSave = gson.fromJson(
                    appInstance.sharedPreference.getValueString(SerialKey.SaveDatabase.name),
                    SavedDatabase::class.java
                )
                SaveDbField.values().forEach{ m->
                    val name = m.name.highToLowCamelCase()
                    val list = dbSave.getMember(name) as ArrayList<*>
                    list.forEach {
                        val dao = db.getMember("${name}Dao")
                        dao!!.getMember("insert", it)
                    }
                }
            }
            runOnUiThread { displayRV() }
        }
    }

    private fun synchronize(login:String, password:String, cancel:Boolean){
        hideRV()
        binding.tvGameError.visibility = View.GONE
        CoroutineScope(SupervisorJob()).launch {
        val timestamp = appInstance.sharedPreference.getFloat(SerialKey.Timestamp.name)
        var modification = SendApiChange(login,password, ApiResponse(
            ArrayList(), ArrayList(), ArrayList())
            , ApiResponse(ArrayList(), ArrayList(), ArrayList()),
            ApiDelete(ArrayList(), ArrayList(), ArrayList()), timestamp
        )
        if (!cancel){
            modification = getDbModification(login, password, timestamp)
        }
            sendApiChangeToServer(modification)
        }
    }

    private fun sendApiChangeToServer(modification:SendApiChange){
        val content = gson.toJson(ApiBody(modification))
        try {
            API_URL?.run{
                val body = sendPostOkHttpRequest(this, content)
                apiReception(body)
            }
        }
        catch (e: Exception) {
            apiErrorHandling(e)
        }
    }

    private fun getDbModification(login:String, password:String, timestamp:Float) =
    SendApiChange(
        login,
        password,
        ApiResponse(
            appInstance.database.gameDao().getWithoutServerId().map{
                dbMethod.convertToBean(it)
            }.toCollection(ArrayList()),
            appInstance.database.addOnDao().getWithoutServerId().map{
                dbMethod.convertToBean(it)
            }.toCollection(ArrayList()),
            appInstance.database.multiAddOnDao().getWithoutServerId().map{
                dbMethod.convertToBean(it)
            }.toCollection(ArrayList())
        ),
        ApiResponse(
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
            appInstance.database.deletedContentDao().getByType(Type.Game.name).map{
                DeletedObject(it.idContent.toInt())
            }.toCollection(ArrayList()),
            appInstance.database.deletedContentDao().getByType(Type.AddOn.name).map{
                DeletedObject(it.idContent.toInt())
            }.toCollection(ArrayList()),
            appInstance.database.deletedContentDao().getByType(Type.MultiAddOn.name).map{
                DeletedObject(it.idContent.toInt())
            }.toCollection(ArrayList()),
        ),
        timestamp
    )


    private fun apiReception(body:String){
        if (body.isNotBlank()){
            val result = gson.fromJson(body, ApiReceive::class.java)
            if (result.timestamp > 0.0){
                actualizeTimeStamp(result.timestamp)
            }
            if(!result.isNullOrEmpty()) {
                CoroutineScope(SupervisorJob()).launch {
                        eraseDeletedItemAndWithoutServerIdItem()
                        handleResult(result)
                        refreshAll()
                }
            }
        }
        runOnUiThread {
            displayRV()
        }
    }

    private fun actualizeTimeStamp(timeStamp: Float){
        val timestamp = appInstance.sharedPreference.getFloat(SerialKey.Timestamp.name)
        if (timestamp < 1.0){
            //a 0.0 timestamp means database reset
            dbReset()
        }
        appInstance.sharedPreference.saveFloat(SerialKey.Timestamp.name, timeStamp)
    }

    private fun dbReset(){
        val list = appInstance.database.userDao().getList()
        appInstance.database.clearAllTables()
        list.forEach { appInstance.database.userDao().insert(it) }
    }

    private fun eraseDeletedItemAndWithoutServerIdItem(){
        db.runInTransaction {
            db.deletedContentDao().deleteAll()
            dbMethod.getGameType().forEach {
                val lowCamelCase = it.highToLowCamelCase()
                val dao = db.getMember("${lowCamelCase}Dao") as CommonDao<ID, *>
                dao.getWithoutServerId().forEach { id ->
                    dbMethod.deleteLink(id, it)
                    dao.deleteOne(id.id)
                }
            }
        }
    }

    private fun handleResult(result:ApiReceive){
        createDataOfApiResult(result)
        deleteDataOfApiResult(result)
        linkDataOfApiResult(result)
    }

    private fun linkDataOfApiResult(result:ApiReceive){
        linkGame(result)
        linkAddOn(result)
        linkMultiAddOn(result)
    }

    private fun linkGame(result:ApiReceive){
        result.game?.run {
            this.forEach {
                val gameFieldHashMap = getGameSpecificFieldHashMap(it)
                try{
                    val dbGame = db.gameDao().getByName(it.name).first()
                    dbMethod.insertLink(
                        dbGame,
                        Type.Game.name,
                        it,
                        gameFieldHashMap
                    )
                    dbMethod.setAddOnGameLink(
                        dbMethod.convertStringListToAddOnTableList(it.addOn), dbGame.id)
                }
                catch(e:Exception){
                    e.printStackTrace()
                    println(it.name)
                }
            }
        }
    }

    private fun linkAddOn(result:ApiReceive){
        result.addOn?.run {
            this.forEach {
                try{
                    val dbGame = db.addOnDao().getByName(it.name).first()
                    dbMethod.insertLink(
                        dbGame,
                        Type.AddOn.name,
                        it
                    )
                }
                catch(e:Exception){
                    e.printStackTrace()
                    println(it.name)
                }
            }
        }
    }

    private fun linkMultiAddOn(result: ApiReceive){
        result.multiAddOn?.run {
            this.forEach {
                try{
                    val dbGame = db.multiAddOnDao().getByName(it.name).first()
                    dbMethod.insertLink(
                        dbGame,
                        Type.MultiAddOn.name,
                        it
                    )
                }
                catch(e:Exception){
                    e.printStackTrace()
                    println(it.name)
                }
            }
        }
    }

    private fun getGameSpecificFieldHashMap(game:GameBean):HashMap<String, ArrayList<String>>{
        val hashMap = HashMap<String, ArrayList<String>>()
        dbMethod.getGameSpecificField().forEach {
            hashMap[it] = game.getMember(it.highToLowCamelCase()) as ArrayList<String>
        }
        return hashMap
    }

    private fun createDataOfApiResult(result:ApiReceive){
        val db = appInstance.database
        val gameTypeList = dbMethod.getGameType()
        gameTypeList.forEach {
            val lowCamelCase = it.highToLowCamelCase()
            val data = result.getMember(lowCamelCase) as ArrayList<*>?
            data?.run {
                db.runInTransaction {
                    this.forEach { g ->
                        when (g) {
                            is GameBean -> insertNewGameData(g)
                            is AddOnBean ->  db.addOnDao().insert(dbMethod.convertToTableBean(g))
                            is MultiAddOnBean -> db.multiAddOnDao().insert(dbMethod.convertToTableBean(g))
                        }
                        insertNewCommonData(g)
                    }
                }
            }
        }
    }

    private fun insertNewGameData(g:GameBean){
        db.gameDao().insert(dbMethod.convertToTableBean(g))
        dbMethod.getGameCommonSpecificField().forEach {
            val loweCamelCase = it.highToLowCamelCase()
            val list = g.getMember(loweCamelCase) as ArrayList<String>
            val dao = db.getMember("${loweCamelCase}Dao") as CommonCustomInsert<*>
            list.forEach { m->
                val isAny = dao.getByName(m)
                if(isAny.isEmpty())dao.insert(m)
            }
        }
    }

    private fun insertNewCommonData(g:Any){
        dbMethod.getCommonField().forEach {
            val loweCamelCase = it.highToLowCamelCase()
            val list = g.getMember(loweCamelCase) as ArrayList<String>
            val dao = db.getMember("${loweCamelCase}Dao") as CommonCustomInsert<*>
            list.forEach { m->
                val isAny = dao.getByName(m)
                if(isAny.isEmpty())dao.insert(m)
            }
        }
    }

    private fun deleteDataOfApiResult(result: ApiReceive) {
        val db = appInstance.database
        val gameTypeList = dbMethod.getGameType()
        gameTypeList.forEach {
            val lowCamelCase = it.highToLowCamelCase()
            val data = result.getMember("deleted$it") as ArrayList<Int>?
            val dataDb = db.getMember("${lowCamelCase}Dao") as CommonDao<ID,*>
            data?.forEach { id->
                db.runInTransaction {
                    val gameInDb = dataDb.getByServerId(id.toLong())
                    if (gameInDb.isNotEmpty()) dbMethod.deleteLink(gameInDb.first(), it)
                    dataDb.deleteOne(id.toLong())
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

    private fun <T:CommonComponent, U> loadImages(dao:CommonDao<T, U>, type:String){
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

    private fun addLinearLayout(elements:ArrayList<View>):LinearLayout{
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
                appInstance.database.imageDao().getList().forEach {
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
            .setPositiveButton(getString(R.string.ok)) { _, _ ->
                run {
                    synchronize(login.text.toString(),pwd.text.toString(), cancel)
                }
            }.setNegativeButton(getString(R.string.cancel)) { _, _ ->
                Toast.makeText(this, getString(R.string.canceled), Toast.LENGTH_SHORT).show()
            }.setView(ll)
            .show()
    }

    private fun urlParameterBox(){
        val url = EditText(this)
        val staticUrl = EditText(this)
        val cbIsLocal = CheckBox(this)
        cbIsLocal.text = getString(R.string.server_less)
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
            .setPositiveButton(getString(R.string.ok)) { _, _ ->
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
            }.setNegativeButton(getString(R.string.cancel)) { _, _ ->
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
            .setPositiveButton(getString(R.string.ok)) { _, _ ->
                run {
                    passwordChangeCheck(exPwd, pwd, pwdConfirm)

                }
            }.setNegativeButton(getString(R.string.cancel)) { _, _ ->
                Toast.makeText(this, getString(R.string.canceled), Toast.LENGTH_SHORT).show()
            }.setView(ll)
            .show()
    }

    private fun passwordChangeCheck(exPwd:EditText, pwd:EditText, pwdConfirm:EditText){
        currentUser?.run{
            when(true){
                !checkPassword(this, exPwd) -> popup(getString(R.string.wrong_password))
                !checkMatchPwd(pwd, pwdConfirm) -> popup(getString(R.string.password_do_not_match))
                !regexPwd(pwd) -> popup(getString(R.string.password_rules))
                else -> passwordChange(pwd, this)

            }
        }?:run{popup(getString(R.string.user_unidentified))}
    }

    private fun checkPassword(user:UserTableBean, etPwd:EditText) =
        SHA256.encryptThisString(etPwd.text.toString()) == user.password

    private fun checkMatchPwd(pwd:EditText, pwd2:EditText) =
        pwd.text.toString() == pwd2.text.toString()

    private fun regexPwd(pwd:EditText) =
        Regex(RegexPattern.PassWord.pattern).matches(pwd.text.toString())

    private fun popup(message:String){
        Toast.makeText(
            this,
            message,
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun passwordChange(pwd:EditText, cUser:UserTableBean){
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
                    cUser.deleteAccount
                )
                appInstance.database.userDao().insert(newPasswordUser)
                currentUser = newPasswordUser
            runOnUiThread {
                popup(getString(R.string.password_changed))
            }
        }
    }
}
