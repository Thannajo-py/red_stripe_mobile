package thannajo.appli.filrouge.view


import thannajo.appli.filrouge.R
import thannajo.appli.filrouge.appInstance
import thannajo.appli.filrouge.databinding.ActivityViewGamesBinding
import thannajo.appli.filrouge.model.*
import thannajo.appli.filrouge.utils.*
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.text.InputType
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.method.PasswordTransformationMethod
import android.text.style.ForegroundColorSpan
import android.view.*
import android.widget.*
import androidx.recyclerview.widget.GridLayoutManager
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.io.File
import java.lang.Exception

/**
 * A central activity contains all general menu items and a list of all games in a RecyclerView
 */
class ViewGamesActivity : AppCompatActivity(), OnGenericListAdapterListener {

    /**
     * access to xml element by id
     */
    private val binding: ActivityViewGamesBinding by lazy {
        ActivityViewGamesBinding.inflate(layoutInflater)
    }

    /**
     * tools to convert from/into JSON
     */
    private val gson = Gson()

    /**
     * database shortcut name
     */
    private val db = appInstance.database

    /**
     * a class of common method used with database
     */
    private val dbMethod = DbMethod()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        CoroutineScope(SupervisorJob()).launch{
            fillRvGame()
            getSave()
        }
    }

    /**
     * fill the RecyclerView of all games
     */
    private fun fillRvGame() {
            val newAdapter = GenericListAdapter(this@ViewGamesActivity)
            runOnUiThread{
                binding.rvGame.adapter = newAdapter
                db.gameDao().getAllWithDesigner().observe(
                    this@ViewGamesActivity,
                    { it?.let { newAdapter.submitList(it) } }
                )
                binding.rvGame.layoutManager = GridLayoutManager(
                    this@ViewGamesActivity,
                    1
                )
                binding.rvGame.addItemDecoration(MarginItemDecoration(5))
            }
            displayRV()
    }

    override fun onResume() {
        getSave()
        super.onResume()
    }

    /**
     * method to show RecyclerView and hide [ProgressBar]
     */
    private fun displayRV() {
        runOnUiThread {
            binding.progressBar.visibility = View.GONE
            binding.rvGame.visibility = View.VISIBLE
        }
    }

    /**
     * method to show [ProgressBar] and hide RecyclerView
     */
    private fun hideRV() {
        binding.rvGame.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (currentUser?.add == true) {
            menu?.add(0, MenuId.AddContent.ordinal, 0, getString(R.string.add_element))
            menu?.add(0, MenuId.ApiSearch.ordinal, 0, getString(R.string.api_add_element))
        }
        menu?.add(0, MenuId.Search.ordinal, 0, getString(R.string.search))
        if(currentUser?.synchronize == true || currentUser?.delete == true) {
            val dbOptions = menu?.addSubMenu(getString(R.string.database))
            if (currentUser?.synchronize == true) {
                if (!isLocal) {
                    dbOptions?.add(
                        0,
                        MenuId.Synchronize.ordinal,
                        0,
                        getString(R.string.save_and_synchronize)
                    )
                    dbOptions?.add(
                        0,
                        MenuId.ResetDB.ordinal,
                        0,
                        getString(R.string.reset_content)
                    )

                } else {
                    dbOptions?.add(
                        0,
                        MenuId.SaveLocalDatabase.ordinal,
                        0,
                        getString(R.string.save_local)
                    )
                    dbOptions?.add(
                        0,
                        MenuId.LoadLocalDatabase.ordinal,
                        0,
                        getString(R.string.reset_local)
                    )
                }
                dbOptions?.add(
                    0,
                    MenuId.SynchronizeParameter.ordinal,
                    0,
                    getString(R.string.synchronize_parameters)
                )
            }
            if (currentUser?.delete == true) {
                dbOptions?.add(0, MenuId.DeleteObject.ordinal, 0, getString(R.string.delete_object))
            }
        }
        currentUser?.run{
            val accountManagement = menu?.addSubMenu(getString(R.string.account_management))
            if (currentUser?.addAccount == true) {
                accountManagement?.add(0, MenuId.CreateAccount.ordinal, 0, getString(R.string.add_account))
            }
            if (currentUser?.deleteAccount == true) {
                accountManagement?.add(0, MenuId.DeleteAccount.ordinal, 0, getString(R.string.delete_account))
            }
            accountManagement?.add(0, MenuId.ChangePassword.ordinal, 0, getString(R.string.change_password))
        }
        menu?.add(0, MenuId.LoadImages.ordinal, 0, getString(R.string.download_images))
        menu?.add(0, MenuId.Disconnect.ordinal, 0, getString(R.string.disconnect))

        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            MenuId.Search.ordinal -> startActivity(Intent(this, SearchActivity::class.java))
            MenuId.DeleteAccount.ordinal -> startActivity(
                Intent(this, DeleteAccountActivity::class.java)
            )
            MenuId.AddContent.ordinal -> startActivity(
                Intent(this, AddElementActivity::class.java)
            )
            MenuId.CreateAccount.ordinal -> startActivity(
                Intent(this, CreateNewAccountActivity::class.java)
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
                Intent(this, DbObjectDeleteActivity::class.java)
            )
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * A method to generate a local xml JSON save of the database using [SharedPreference]
     * use kotlin reflection
     * available only in a serverless mode
     */
    private fun saveLocalDatabase() {
        CoroutineScope(SupervisorJob()).launch {
            val save = SavedDatabase()
            db.runInTransaction {
                SaveDbField.values().forEach { f ->
                    val name = f.name.highToLowCamelCase()
                    val dao = db.getMember("${name}Dao")
                    val list = dao!!.getMember("getList")
                    save.setMember(name, list)
                }
                appInstance.sharedPreference.saveString(
                    gson.toJson(save),
                    SerialKey.SaveDatabase.name
                )
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

    /**
     * A method lo load the database from a local xml JSON save using [SharedPreference]
     * use kotlin reflection
     * available only in a serverless mode
     */
    private fun loadLocalDatabase() {
        hideRV()
        CoroutineScope(SupervisorJob()).launch {
            db.runInTransaction {
                db.clearAllTables()
                val dbSave = gson.fromJson(
                    appInstance.sharedPreference.getValueString(SerialKey.SaveDatabase.name),
                    SavedDatabase::class.java
                )
                SaveDbField.values().forEach { m ->
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

    /**
     * A method to synchronize loacal database with server
     * available only in server mode
     */
    private fun synchronize(login: String, password: String, cancel: Boolean) {
        hideRV()
        binding.tvGameError.visibility = View.GONE
        CoroutineScope(SupervisorJob()).launch {
            val timestamp = appInstance.sharedPreference.getFloat(SerialKey.Timestamp.name)
            var modification = SendApiChange(
                login, password, ApiResponse(
                    ArrayList(), ArrayList(), ArrayList()
                ), ApiResponse(ArrayList(), ArrayList(), ArrayList()),
                ApiDelete(ArrayList(), ArrayList(), ArrayList()), timestamp
            )
            if (!cancel) {
                modification = getDbModification(login, password, timestamp)
            }
            sendApiChangeToServer(modification)
        }
    }

    /**
     * A method to handle JSON post request to server
     */
    private fun sendApiChangeToServer(modification: SendApiChange) {
        val content = gson.toJson(ApiBody(modification))
        try {
            API_URL?.run {
                val body = sendPostOkHttpRequest(this, content)
                apiReception(body)
            }
        } catch (e: Exception) {
            apiErrorHandling(e)
        }
    }

    /**
     * A method to handle JSON answer from server and transform into a Kotlin Object
     */
    private fun getDbModification(login: String, password: String, timestamp: Float) =
        SendApiChange(
            login,
            password,
            ApiResponse(
                appInstance.database.gameDao().getWithoutServerId().map {
                    dbMethod.convertToBean(it)
                }.toCollection(ArrayList()),
                appInstance.database.addOnDao().getWithoutServerId().map {
                    dbMethod.convertToBean(it)
                }.toCollection(ArrayList()),
                appInstance.database.multiAddOnDao().getWithoutServerId().map {
                    dbMethod.convertToBean(it)
                }.toCollection(ArrayList())
            ),
            ApiResponse(
                appInstance.database.gameDao().getChanged().map {
                    dbMethod.convertToBean(it)
                }.toCollection(ArrayList()),
                appInstance.database.addOnDao().getChanged().map {
                    dbMethod.convertToBean(it)
                }.toCollection(ArrayList()),
                appInstance.database.multiAddOnDao().getChanged().map {
                    dbMethod.convertToBean(it)
                }.toCollection(ArrayList()),
            ),
            ApiDelete(
                appInstance.database.deletedContentDao().getByType(Type.Game.name).map {
                    DeletedObject(it.idContent.toInt())
                }.toCollection(ArrayList()),
                appInstance.database.deletedContentDao().getByType(Type.AddOn.name).map {
                    DeletedObject(it.idContent.toInt())
                }.toCollection(ArrayList()),
                appInstance.database.deletedContentDao().getByType(Type.MultiAddOn.name).map {
                    DeletedObject(it.idContent.toInt())
                }.toCollection(ArrayList()),
            ),
            timestamp
        )


    /**
     * handle api json answer and check content
     */
    private fun apiReception(body: String) {
        if (body.isNotBlank()) {
            val result = gson.fromJson(body, ApiReceive::class.java)
            if (result.timestamp > 0.0) {
                actualizeTimeStamp(result.timestamp)
            }
            if (!result.isNullOrEmpty()) {
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

    /**
     * update save time stamp if local timestamp is zero reset the database
     */
    private fun actualizeTimeStamp(timeStamp: Float) {
        val timestamp = appInstance.sharedPreference.getFloat(SerialKey.Timestamp.name)
        if (timestamp < 1.0) {
            //a 0.0 timestamp means database reset
            dbReset()
        }
        appInstance.sharedPreference.saveFloat(SerialKey.Timestamp.name, timeStamp)
    }

    /**
     * Clear all tables except the user one
     */
    private fun dbReset() {
        val list = appInstance.database.userDao().getList()
        appInstance.database.clearAllTables()
        list.forEach { appInstance.database.userDao().insert(it) }
    }

    /**
     * Delete serverless Id items(temporary item) after server reception
     */
    private fun eraseDeletedItemAndWithoutServerIdItem() {
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

    /**
     * global dispatch function to handle server api result
     */
    private fun handleResult(result: ApiReceive) {
        createDataOfApiResult(result)
        deleteDataOfApiResult(result)
        linkDataOfApiResult(result)
    }

    /**
     * Handle junction table fill
     */
    private fun linkDataOfApiResult(result: ApiReceive) {
        linkGame(result)
        linkAddOn(result)
        linkMultiAddOn(result)
    }

    /**
     * handle all junction table for [GameTableBean] from JSON [GameBean]
     */
    private fun linkGame(result: ApiReceive) {
        result.game?.run {
            this.forEach {
                val gameFieldHashMap = getGameSpecificFieldHashMap(it)
                try {
                    val dbGame = db.gameDao().getByName(it.name).first()
                    dbMethod.insertLink(
                        dbGame,
                        Type.Game.name,
                        it,
                        gameFieldHashMap
                    )
                    dbMethod.setAddOnGameLink(
                        dbMethod.convertStringListToAddOnTableList(it.addOn), dbGame.id
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                    println(it.name)
                }
            }
        }
    }

    /**
     * handle all junction table for [AddOnTableBean] from JSON [AddOnBean]
     */
    private fun linkAddOn(result: ApiReceive) {
        result.addOn?.run {
            this.forEach {
                try {
                    val dbGame = db.addOnDao().getByName(it.name).first()
                    dbMethod.insertLink(
                        dbGame,
                        Type.AddOn.name,
                        it
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                    println(it.name)
                }
            }
        }
    }

    /**
     * handle all junction table for [MultiAddOnTableBean] from JSON [MultiAddOnBean]
     */
    private fun linkMultiAddOn(result: ApiReceive) {
        result.multiAddOn?.run {
            this.forEach {
                try {
                    val dbGame = db.multiAddOnDao().getByName(it.name).first()
                    dbMethod.insertLink(
                        dbGame,
                        Type.MultiAddOn.name,
                        it
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                    println(it.name)
                }
            }
        }
    }

    /**
     * @return a hashmap containing a relation between [Type] [String] and [GameBean] attribute
     * to be used with [DbMethod.insertLink]
     */
    private fun getGameSpecificFieldHashMap(game: GameBean): HashMap<String, ArrayList<String>> {
        val hashMap = HashMap<String, ArrayList<String>>()
        dbMethod.getGameSpecificField().forEach {
            hashMap[it] = game.getMember(it.highToLowCamelCase()) as ArrayList<String>
        }
        return hashMap
    }

    /**
     * convert JSON Api Result into database insert
     */
    private fun createDataOfApiResult(result: ApiReceive) {
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
                            is AddOnBean -> db.addOnDao().insert(dbMethod.convertToTableBean(g))
                            is MultiAddOnBean -> db.multiAddOnDao()
                                .insert(dbMethod.convertToTableBean(g))
                        }
                        insertNewCommonData(g)
                    }
                }
            }
        }
    }

    /**
     * specific insert method for new game, create game pecific element if not present
     */
    private fun insertNewGameData(g: GameBean) {
        db.gameDao().insert(dbMethod.convertToTableBean(g))
        dbMethod.getGameCommonSpecificField().forEach {
            val lowCamelCase = it.highToLowCamelCase()
            val list = g.getMember(lowCamelCase) as ArrayList<String>
            val dao = db.getMember("${lowCamelCase}Dao") as CommonCustomInsert<*>
            list.forEach { m ->
                val isAny = dao.getByName(m)
                if (isAny.isEmpty()) dao.insert(m)
            }
        }
    }

    /**
     * insert element if not already present
     */
    private fun insertNewCommonData(g: Any) {
        dbMethod.getCommonField().forEach {
            val loweCamelCase = it.highToLowCamelCase()
            val list = g.getMember(loweCamelCase) as ArrayList<String>
            val dao = db.getMember("${loweCamelCase}Dao") as CommonCustomInsert<*>
            list.forEach { m ->
                val isAny = dao.getByName(m)
                if (isAny.isEmpty()) dao.insert(m)
            }
        }
    }

    /**
     * Handle deletion of server answer deleted element
     */
    private fun deleteDataOfApiResult(result: ApiReceive) {
        val db = appInstance.database
        val gameTypeList = dbMethod.getGameType()
        gameTypeList.forEach {
            val lowCamelCase = it.highToLowCamelCase()
            val data = result.getMember("deleted$it") as ArrayList<Int>?
            val dataDb = db.getMember("${lowCamelCase}Dao") as CommonDao<ID, *>
            data?.forEach { id ->
                db.runInTransaction {
                    val gameInDb = dataDb.getByServerId(id.toLong())
                    if (gameInDb.isNotEmpty()) dbMethod.deleteLink(gameInDb.first(), it)
                    dataDb.deleteOne(id.toLong())
                }
            }
        }
    }

    /**
     * Handle API server error response
     */
    private fun apiErrorHandling(e: Exception) {
        e.printStackTrace()
        runOnUiThread {
            binding.tvGameError.text = getString(R.string.error_content, e.message)
            binding.tvGameError.visibility = View.VISIBLE
            binding.progressBar.visibility = View.GONE
        }
    }


    override fun onElementClick(datum: CommonGame) {
        intent = Intent(this, GameDetailsActivity::class.java)
        intent.putExtra(SerialKey.GameId.name, datum.id)
        startActivity(intent)
    }

    /**
     * refresh isLocal common variable indication derverless mode
     */
    private fun getSave() {
            isLocal = appInstance.sharedPreference.getBoolean(SerialKey.IsLocal.name)
    }

    /**
     * Handle image downloading from server dispatch and application unused picture
     */
    private fun refreshAll() {
        loadImages(appInstance.database.gameDao(), Type.Game.name)
        loadImages(appInstance.database.addOnDao(), Type.AddOn.name)
        loadImages(appInstance.database.multiAddOnDao(), Type.MultiAddOn.name)
        cleanImageList()
    }

    /**
     * Check source for image download
     */
    private fun <T : CommonComponent, U> loadImages(dao: CommonDao<T, U>, type: String) {
        CoroutineScope(SupervisorJob()).launch {
            for (game in dao.getList()) {
                if (dao.getImage(game.name).isEmpty()) {

                    if (!game.external_img.isNullOrBlank()) {
                        launch {
                            getImage(game.external_img!!, game.name, type)
                        }

                    } else if (!game.picture.isNullOrBlank() && API_STATIC != null) {
                        launch {
                            getImage("$API_STATIC${game.picture}", game.name, type)
                        }
                    }
                }

            }


        }
    }

    /**
     * Download image from an url and save it as a [ByteArray] file
     */
    private fun getImage(url: String, gameName: String, type: String) {
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

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * A method to create and format programmatically [TextView]
     */
    private fun addTextView(text: String): TextView {
        val textView = TextView(this)
        textView.text = text
        textView.typeface = Typeface.DEFAULT_BOLD
        textView.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        return textView
    }

    /**
     * A method to create and format programmatically [LinearLayout]
     */
    private fun addLinearLayout(elements: ArrayList<View>): LinearLayout {
        val ll = LinearLayout(this)
        ll.orientation = LinearLayout.VERTICAL
        ll.gravity = Gravity.CENTER
        ll.setPadding(20, 0, 20, 0)
        elements.forEach { ll.addView(it) }
        return ll
    }

    /**
     * A method to create and format programmatically [EditText] of password type
     */
    private fun addEditTextPassword(): EditText {
        val pwd = EditText(this)
        pwd.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
        pwd.transformationMethod = PasswordTransformationMethod.getInstance()
        return pwd

    }

    /**
     * Check if image is used and call [deleteImage] if not
     */
    private fun cleanImageList() {
        CoroutineScope(SupervisorJob()).launch {
            appInstance.database.runInTransaction {
                appInstance.database.imageDao().getList().forEach {
                    when (it.gameType) {
                        Type.Game.name -> if (appInstance.database.gameDao()
                                .getByName(it.gameName).isEmpty()
                        ) deleteImage(it.name)
                        Type.AddOn.name -> if (appInstance.database.addOnDao()
                                .getByName(it.gameName).isEmpty()
                        ) deleteImage(it.name)
                        Type.MultiAddOn.name -> if (appInstance.database.multiAddOnDao()
                                .getByName(it.gameName).isEmpty()
                        ) deleteImage(it.name)
                    }
                }
            }
        }
    }

    /**
     * Delete Image URI from database and Image file from application
     */
    private fun deleteImage(fileName: String) {
        appInstance.database.imageDao().deleteByName(fileName)
        File(this@ViewGamesActivity.filesDir, fileName).delete()
    }

    /**
     * Create A synchronize AlertDialog box
     */
    private fun synchronizeBox(message: String, cancel: Boolean) {
        val login = EditText(this)
        val pwd = addEditTextPassword()
        val view = arrayListOf<View>(
            addTextView(getString(R.string.login)),
            login,
            addTextView(getString(R.string.password)),
            pwd
        )
        val ll = addLinearLayout(view)
        val title = getString(R.string.warning).colored(getColor(R.color.list_background))
        AlertDialog.Builder(this, R.style.alert_dialog)
            .setMessage(message)
            .setTitle(title)
            .setPositiveButton(getString(R.string.ok)) { _, _ ->
                run {
                    synchronize(login.text.toString(), pwd.text.toString(), cancel)
                }
            }.setNegativeButton(getString(R.string.cancel)) { _, _ ->
                Toast.makeText(this, getString(R.string.canceled), Toast.LENGTH_SHORT).show()
            }.setView(ll)
            .show()
    }

    /**
     * Create A urlParameter AlertDialog box
     */
    private fun urlParameterBox() {
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
            cbIsLocal,
            addTextView(getString(R.string.server_less_note))
        )
        val ll = addLinearLayout(view)
        url.setText(API_URL)
        staticUrl.setText(API_STATIC)
        val title = getString(R.string.parameters).colored(getColor(R.color.list_background))
        AlertDialog.Builder(this, R.style.alert_dialog)
            .setMessage(getString(R.string.url_web_message))
            .setTitle(title)
            .setPositiveButton(getString(R.string.ok)) { _, _ ->
                API_URL = url.text.toString()
                API_STATIC = staticUrl.text.toString()
                isLocal = cbIsLocal.isChecked
                appInstance.sharedPreference.saveBoolean(SerialKey.IsLocal.name, isLocal)
                appInstance.sharedPreference.saveString(url.text.toString(), SerialKey.APIUrl.name)
                appInstance.sharedPreference.saveString(
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

    /**
     * Create A changePassword AlertDialog box
     */
    private fun changePasswordBox() {
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
        val title = getString(R.string.change_password).colored(getColor(R.color.list_background))
        AlertDialog.Builder(this, R.style.alert_dialog)
            .setTitle(title)
            .setPositiveButton(getString(R.string.ok)) { _, _ ->
                run {
                    passwordChangeCheck(exPwd, pwd, pwdConfirm)

                }
            }.setNegativeButton(getString(R.string.cancel)) { _, _ ->
                Toast.makeText(this, getString(R.string.canceled), Toast.LENGTH_SHORT).show()
            }.setView(ll)
            .show()
    }

    /**
     * check old password, new passwords match and enforce password rules
     */
    private fun passwordChangeCheck(exPwd: EditText, pwd: EditText, pwdConfirm: EditText) {
        currentUser?.run {
            when (true) {
                !isValid(this.password, exPwd.text.toString()) ->
                    popup(getString(R.string.wrong_password))
                !checkMatchPwd(pwd, pwdConfirm) -> popup(getString(R.string.password_do_not_match))
                !regexPwd(pwd) -> popup(getString(R.string.password_rules))
                else -> passwordChange(pwd, this)

            }
        } ?: run { popup(getString(R.string.user_unidentified)) }
    }

    /**
     * @return password and password confirm identical
     */
    private fun checkMatchPwd(pwd: EditText, pwd2: EditText) =
        pwd.text.toString() == pwd2.text.toString()

    /**
    *@return password comply to password rules
     */
    private fun regexPwd(pwd: EditText) =
        Regex(RegexPattern.PassWord.pattern).matches(pwd.text.toString())

    /**
     * Make and show a Toast
     */
    private fun popup(message: String) {
        runOnUiThread {
            Toast.makeText(
                this,
                message,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    /**
     * Handle user password change
     */
    private fun passwordChange(pwd: EditText, cUser: UserTableBean) {
        generateHashedPass(pwd.text.toString())?.run {
            val password = this
            CoroutineScope(SupervisorJob()).launch {
                val newPasswordUser = UserTableBean(
                    cUser.id,
                    cUser.login,
                    password,
                    cUser.add,
                    cUser.change,
                    cUser.delete,
                    cUser.synchronize,
                    cUser.addAccount,
                    cUser.deleteAccount
                )
                appInstance.database.userDao().insert(newPasswordUser)
                currentUser = newPasswordUser
                popup(getString(R.string.password_changed))
            }
        } ?: run {
            popup(getString(R.string.password_register_error))
        }
    }
}
