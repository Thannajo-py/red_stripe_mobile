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


    private val binding: ActivityViewGamesBinding by lazy{ ActivityViewGamesBinding.inflate(layoutInflater) }
    private val gson = Gson()
    private val newAdapter = GenericListAdapter<DesignerWithGame>(this)
    private val db = appInstance.database
    private val dbMethod = DbMethod()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.rvGames.adapter = newAdapter
        db.gameDao().getDesignerWithGame().observe(this, {it?.let{newAdapter.submitList(it)}})
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
                menu?.add(
                    0,
                    MenuId.CancelAndSynchronize.ordinal,
                    0,
                    "Annuler modification et synchroniser"
                )
                menu?.add(0, MenuId.Synchronize.ordinal, 0, "Sauvegarder modification et synchroniser")
                menu?.add(0, MenuId.ResetDB.ordinal, 0, "Réinitialiser le contenu")

            }
            menu?.add(0, MenuId.SynchronizeParameter.ordinal, 0, "Paramètres de synchronisation")
        }
        menu?.add(0, MenuId.Search.ordinal,0,"Rechercher")
        menu?.add(0, MenuId.LoadImages.ordinal,0,"Recharger les images")
        if (currentUser?.addAccount == true){
            menu?.add(0, MenuId.CreateAccount.ordinal,0,"Ajouter un Compte")
        }
        if (currentUser?.deleteAccount == true){
            menu?.add(0, MenuId.DeleteAccount.ordinal,0,"Supprimer compte")
        }
        if(currentUser?.add == true){
            menu?.add(0, MenuId.AddContent.ordinal,0,"Ajouter un élément")
        }
        menu?.add(0, MenuId.ChangePassword.ordinal,0,"Changer de Mot de passe")
        menu?.add(0, MenuId.Disconnect.ordinal,0,"Se Déconnecter")
        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            MenuId.CancelAndSynchronize.ordinal -> synchronizeBox("Voulez vous annuler toutes vos modifications et re-synchroniser?", true)
            MenuId.Search.ordinal -> startActivity(Intent(this, Search::class.java))
            MenuId.DeleteAccount.ordinal -> startActivity(Intent(this, DeleteAccount::class.java))
            MenuId.AddContent.ordinal -> startActivity(Intent(this, AddElement::class.java))
            MenuId.CreateAccount.ordinal -> startActivity(Intent(this, CreateNewAccount::class.java))
            MenuId.Synchronize.ordinal -> synchronizeBox("Voulez vous sauvegarder toutes vos modifications et re-synchroniser?", false)
            MenuId.LoadImages.ordinal -> {
                loadImages(appInstance.database.gameDao())
                loadImages(appInstance.database.addOnDao())
                loadImages(appInstance.database.multiAddOnDao())
                cleanImageList()
            }
            MenuId.SynchronizeParameter.ordinal -> urlParameterBox()
            MenuId.ChangePassword.ordinal -> changePasswordBox()
            MenuId.ResetDB.ordinal -> {
                appInstance.sharedPreference.saveFloat(SerialKey.Timestamp.name, 0.0F)
                synchronizeBox("Voulez vous supprimer votre contenu et recharger la base de données?", true)

            }
            MenuId.Disconnect.ordinal -> {
                appInstance.sharedPreference.removeValue(SerialKey.SavedUser.name)
                currentUser = null
                startActivity(Intent(this, MainActivity::class.java))
                finishAffinity()
            }
        }
        return super.onOptionsItemSelected(item)
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
                appInstance.database.gameDao().getWithoutServerId().map{dbMethod.convertToGameBean(it)}.toCollection(ArrayList()),
                appInstance.database.addOnDao().getWithoutServerId().map{dbMethod.convertToAddOnBean(it)}.toCollection(ArrayList()),
                appInstance.database.multiAddOnDao().getWithoutServerId().map{dbMethod.convertToMultiAddOnBean(it)}.toCollection(ArrayList())
            )
                , ApiResponse(
                    appInstance.database.gameDao().getChanged().map{dbMethod.convertToGameBean(it)}.toCollection(ArrayList()),
                    appInstance.database.addOnDao().getChanged().map{dbMethod.convertToAddOnBean(it)}.toCollection(ArrayList()),
                    appInstance.database.multiAddOnDao().getChanged().map{dbMethod.convertToMultiAddOnBean(it)}.toCollection(ArrayList()),
                ),
                ApiDelete(
                    appInstance.database.deletedItemDao().getByType(Type.Game.name).map{DeletedObject(it.idContent.toInt())}.toCollection(ArrayList()),
                    appInstance.database.deletedItemDao().getByType(Type.AddOn.name).map{DeletedObject(it.idContent.toInt())}.toCollection(ArrayList()),
                    appInstance.database.deletedItemDao().getByType(Type.MultiAddOn.name).map{DeletedObject(it.idContent.toInt())}.toCollection(ArrayList()),
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
            if(!result.games.isNullOrEmpty() || !result.add_ons.isNullOrEmpty() || !result.add_ons.isNullOrEmpty()) {
                CoroutineScope(SupervisorJob()).launch {
                    db.runInTransaction {
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
                                    var gameId: Long = 0L
                                    val gameInDb = dbGame.getByServerId(it.id?.toLong() ?: 0L)
                                    var gameDifficulty: Long? = null
                                    it.difficulty?.run {
                                        val listGameDifficulty = db.difficultyDao().getByName(this)
                                        if (listGameDifficulty.isNotEmpty()) {
                                            gameDifficulty = listGameDifficulty[0].id
                                        } else {
                                            val id = db.difficultyDao()
                                                .insert(DifficultyTableBean(0, this))
                                            gameDifficulty = id
                                        }
                                    }
                                    if (gameInDb.isNotEmpty()) {
                                        gameId = gameInDb[0].id
                                    }
                                    dbGame.insert(
                                        GameTableBean(
                                            gameId,
                                            it.id,
                                            it.name,
                                            it.player_min,
                                            it.player_max,
                                            it.playing_time,
                                            gameDifficulty,
                                            it.bgg_link,
                                            it.age,
                                            it.buying_price,
                                            it.stock,
                                            it.max_time,
                                            it.external_img,
                                            it.picture,
                                            it.by_player,
                                            false
                                        )
                                    )
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
                                val dbGame = db.addOnDao()
                                result.add_ons.forEach {
                                    val gameInDb = dbGame.getByServerId(it.id?.toLong() ?: 0L)
                                    var addOnId: Long = 0L
                                    var gameDifficulty: Long? = null
                                    it.difficulty?.run {
                                        val listGameDifficulty = db.difficultyDao().getByName(this)
                                        if (listGameDifficulty.isNotEmpty()) gameDifficulty =
                                            listGameDifficulty[0].id

                                    }
                                    var gameId: Long? = null
                                    it.game?.run {
                                        val listGame = appInstance.database.gameDao().getByName(this)
                                        if (listGame.isNotEmpty()) gameId = listGame[0].id

                                    }

                                    if (gameInDb.isNotEmpty()) addOnId = gameInDb[0].id

                                    dbGame.insert(
                                        AddOnTableBean(
                                            addOnId,
                                            it.id,
                                            it.name,
                                            it.player_min,
                                            it.player_max,
                                            it.playing_time,
                                            gameDifficulty,
                                            it.bgg_link,
                                            it.age,
                                            it.buying_price,
                                            it.stock,
                                            it.max_time,
                                            it.external_img,
                                            it.picture,
                                            gameId,
                                            false
                                        )
                                    )

                                }
                                result?.deleted_add_ons?.forEach {
                                    val gameInDb = dbGame.getByServerId(it.toLong())
                                    if (gameInDb.isNotEmpty()) dbMethod.delete_link(gameInDb[0])
                                    db.addOnDao().deleteOne(it.toLong())

                                }

                            }
                    }

                    result.multi_add_ons?.run {
                            db.runInTransaction {
                                val dbGame = db.multiAddOnDao()
                                result.multi_add_ons.forEach {
                                    var gameId: Long = 0L
                                    val gameInDb = dbGame.getByServerId(it.id?.toLong() ?: 0L)
                                    var gameDifficulty: Long? = null
                                    it.difficulty?.run {
                                        val listGameDifficulty = db.difficultyDao().getByName(this)

                                        if (listGameDifficulty.isNotEmpty()) gameDifficulty =
                                            listGameDifficulty[0].id

                                    }
                                    if (gameInDb.isNotEmpty()) gameId = gameInDb[0].id

                                    dbGame.insert(
                                        MultiAddOnTableBean(
                                            gameId,
                                            it.id,
                                            it.name,
                                            it.player_min,
                                            it.player_max,
                                            it.playing_time,
                                            gameDifficulty,
                                            it.bgg_link,
                                            it.age,
                                            it.buying_price,
                                            it.stock,
                                            it.max_time,
                                            it.external_img,
                                            it.picture,
                                            false
                                        )
                                    )

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
                                                                  gameDao:CommonDao<U>, otherDao: CommonCustomInsert<W>,
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

    private fun <T:CommonBase,U:ID, V, W:ID> commonAssociativeFill(game:T,gameDao:CommonDao<U>,  otherDao: ArrayList<Triple<ArrayList<String>, CommonCustomInsert<out W>,CommonJunctionDAo<out V>>>)
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
            binding.tvGameError.text = "error: ${e.message}"
            binding.tvGameError.visibility = View.VISIBLE
            binding.progressBar.visibility = View.GONE

        }
    }


    override fun onElementClick(datum: CommonGame) {

        intent = Intent(this, GameDetails::class.java)
        intent.putExtra(SerialKey.GameId.name, datum.id)
        startActivity(intent)
    }



    private fun <T: CommonBase>fillList(list:ArrayList<T>, fill:ArrayList<T>){
        list.clear()
        list.addAll(fill)
        list.sortBy{it.name}

    }

    private fun getSave(){
        isLocal = appInstance.sharedPreference.getBoolean(SerialKey.IsLocal.name)
        allImages.list_of_images.addAll(gson.fromJson(appInstance.sharedPreference.getValueString(SerialKey.AllImagesStorage.name), allImages::class.java).list_of_images)


    }



    private fun refreshAll(){
        loadImages(appInstance.database.gameDao())
        loadImages(appInstance.database.addOnDao())
        loadImages(appInstance.database.multiAddOnDao())
        cleanImageList()

    }

    private fun <T:CommonComponent> loadImages(dao:CommonDao<T>){
        CoroutineScope(SupervisorJob()).launch{
            for (game in dao.getList()){
                if (!allImages.list_of_images.contains(game.name)){

                    if (!game.external_img.isNullOrBlank()){
                        launch{
                            getImage(game.external_img!!, game.name)

                        }

                    }
                    else if (!game.picture.isNullOrBlank() && API_STATIC != null){
                        launch{
                            getImage("$API_STATIC${game.picture}", game.name)
                        }
                    }
                }

            }


        }
    }

    private fun getImage(url:String, name:String){
        try {
            val img = sendGetOkHttpRequestImage(url)
            img?.run {
                val file = File(this@ViewGamesActivity.filesDir, name)
                file.writeBytes(this)
                allImages.list_of_images.add(name)
                appInstance.sharedPreference.save(gson.toJson(allImages), SerialKey.AllImagesStorage.name)
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
            allImages.list_of_images.removeIf {
                appInstance.database.gameDao().getByName(it).isEmpty() &&
                        appInstance.database.addOnDao().getByName(it).isEmpty() &&
                        appInstance.database.multiAddOnDao().getByName(it).isEmpty()

            }
        }

    }

    private fun synchronizeBox(message:String, cancel:Boolean){
        val login = EditText(this)
        val pwd = addEditTextPassword()
        val view = arrayListOf<View>(addTextView("Login"),login,addTextView("Mot de passe"), pwd)
        val ll = addLinearLayout(view)
        AlertDialog.Builder(this)
            .setMessage(message)
            .setTitle("Attention")
            .setPositiveButton("ok") { dialog, which ->
                run {
                    synchronize(login.text.toString(),pwd.text.toString(), cancel)
                }
            }.setNegativeButton("cancel") { dialog, which ->
                Toast.makeText(this, "Annulé", Toast.LENGTH_SHORT).show()
            }.setView(ll)
            .show()
    }

    private fun urlParameterBox(){
        val url = EditText(this)
        val staticUrl = EditText(this)
        val cbIsLocal = CheckBox(this)
        cbIsLocal.setText("pas de serveur")
        cbIsLocal.isChecked = isLocal
        val view = arrayListOf<View>(addTextView("API url"),url,addTextView("API static url"), staticUrl, cbIsLocal)
        val ll = addLinearLayout(view)
        url.setText(API_URL)
        staticUrl.setText(API_STATIC)
        AlertDialog.Builder(this)
            .setMessage("Entrez votre adresse d'api et de fichiers statiques")
            .setTitle("Paramètres")
            .setPositiveButton("ok") { dialog, which ->
                API_URL = url.text.toString()
                API_STATIC = staticUrl.text.toString()
                isLocal = cbIsLocal.isChecked
                appInstance.sharedPreference.saveBoolean(SerialKey.IsLocal.name, isLocal)
                appInstance.sharedPreference.save(url.text.toString(), SerialKey.APIUrl.name)
                appInstance.sharedPreference.save(staticUrl.text.toString(), SerialKey.APIStaticUrl.name)
                startActivity(Intent(this, ViewGamesActivity::class.java))
                finish()

            }.setNegativeButton("cancel") { dialog, which ->
                Toast.makeText(this, "Annulé", Toast.LENGTH_SHORT).show()
            }.setView(ll)
            .show()
    }

    private fun changePasswordBox(){
        val exPwd = addEditTextPassword()
        val pwd = addEditTextPassword()
        val pwdConfirm = addEditTextPassword()
        val view = arrayListOf<View>(addTextView("Ancien mot de passe"),exPwd,addTextView("nouveau mot de passe"), pwd, addTextView("Confirmer nouveau mot de passe"), pwdConfirm)
        val ll = addLinearLayout(view)
        AlertDialog.Builder(this)
            .setTitle("Changer mot de passe")
            .setPositiveButton("ok") { dialog, which ->
                run {
                    passwordChange(exPwd, pwd, pwdConfirm)

                }
            }.setNegativeButton("cancel") { dialog, which ->
                Toast.makeText(this, "Annulé", Toast.LENGTH_SHORT).show()
            }.setView(ll)
            .show()
    }

    private fun passwordChange(exPwd:EditText, pwd:EditText, pwdConfirm:EditText){
        if(SHA256.encryptThisString(exPwd.text.toString()) == currentUser?.password && currentUser != null){
            val cUser = currentUser
            if(pwd.text.toString() == pwdConfirm.text.toString() && cUser != null){
                if(Regex(RegexPattern.PassWord.pattern).matches(pwd.text.toString())){
                    CoroutineScope(SupervisorJob()).launch{
                        val newPasswordUser = UserTableBean(cUser.id, cUser.login,
                            SHA256.encryptThisString(pwd.text.toString()),
                            cUser.add, cUser.change, cUser.delete, cUser.synchronize, cUser.addAccount,
                        cUser.deleteAccount)
                        appInstance.database.userDao().insert(newPasswordUser)
                        currentUser = newPasswordUser
                        Toast.makeText(this@ViewGamesActivity, "changement de mot de passe effectué", Toast.LENGTH_SHORT).show()
                    }
                }
                else{
                    Toast.makeText(this, CommonString.PassWordRequirement.string, Toast.LENGTH_LONG).show()
                }
            }
            else{
                Toast.makeText(this, "les nouveaux mots de passe ne sont pas identiques", Toast.LENGTH_SHORT).show()
            }
        }
        else{
            Toast.makeText(this, "mauvais mot de passe", Toast.LENGTH_SHORT).show()
        }

    }




}