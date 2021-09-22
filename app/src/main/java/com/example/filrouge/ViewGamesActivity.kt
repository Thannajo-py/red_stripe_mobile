package com.example.filrouge


import android.app.AlertDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.text.method.PasswordTransformationMethod
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.example.filrouge.databinding.ActivityViewGamesBinding
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import java.io.File
import java.lang.Exception
class ViewGamesActivity : AppCompatActivity(),  OnGenericListListener {


    private val binding: ActivityViewGamesBinding by lazy{ ActivityViewGamesBinding.inflate(layoutInflater) }
    private val gson = Gson()
    private val adapter = GenericAdapter(allGames, this)
    private val sharedPreference by lazy{SharedPreference(this)}


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.rvGames.adapter = adapter
        binding.rvGames.layoutManager = GridLayoutManager(this, 1)
        binding.rvGames.addItemDecoration(MarginItemDecoration(5))
        getSave()




    }

    override fun onResume() {
        super.onResume()
        getSave()
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (currentUser?.permission?.synchronize == true) {
            menu?.add(
                0,
                MenuId.CancelAndSynchronize.ordinal,
                0,
                "Annuler modification et synchroniser"
            )
            menu?.add(0, MenuId.Synchronize.ordinal, 0, "Sauvegarder modification et synchroniser")
            menu?.add(0, MenuId.SynchronizeParameter.ordinal, 0, "Paramètres de synchronisation")
        }
        menu?.add(0,MenuId.Search.ordinal,0,"Rechercher")
        if (currentUser?.permission?.addAccount == true){
            menu?.add(0,MenuId.CreateAccount.ordinal,0,"Ajouter un Compte")
        }
        if (currentUser?.permission?.deleteAccount == true){
            menu?.add(0,MenuId.DeleteAccount.ordinal,0,"Supprimer compte")
        }
        if(currentUser?.permission?.add == true){
            menu?.add(0,MenuId.AddContent.ordinal,0,"Ajouter un élément")
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            MenuId.CancelAndSynchronize.ordinal -> {
                val (input, pwd, ll) = alertDialogLoginBox("Login", "Mot de passe", true)
                AlertDialog.Builder(this)
                    .setMessage("Voulez vous annuler toutes vos modifications et re-synchroniser?")
                    .setTitle("Attention")
                    .setPositiveButton("ok") { dialog, which ->
                        run {
                            synchronize(input.text.toString(),pwd.text.toString(), true)
                        }
                    }.setNegativeButton("cancel") { dialog, which ->
                        Toast.makeText(this, "Annulé", Toast.LENGTH_SHORT).show()
                    }.setView(ll)
                    .show()
            }
            MenuId.Search.ordinal -> startActivity(Intent(this, Search::class.java))
            MenuId.DeleteAccount.ordinal -> startActivity(Intent(this, DeleteAccount::class.java))
            MenuId.AddContent.ordinal -> startActivity(Intent(this, AddElement::class.java))
            MenuId.CreateAccount.ordinal -> startActivity(Intent(this, CreateNewAccount::class.java))
            MenuId.Synchronize.ordinal -> {
                val (input, pwd, ll) = alertDialogLoginBox("Login", "Mot de passe", true)
                AlertDialog.Builder(this)
                    .setMessage("Voulez vous sauvegarder toutes vos modifications et re-synchroniser?")
                    .setTitle("Attention")
                    .setPositiveButton("ok") { dialog, which ->
                        run {
                            synchronize(input.text.toString(),pwd.text.toString(), false)
                        }
                    }.setNegativeButton("cancel") { dialog, which ->
                        Toast.makeText(this, "Annulé", Toast.LENGTH_SHORT).show()
                    }.setView(ll)
                    .show()
            }
            MenuId.SynchronizeParameter.ordinal -> {

                val (url, staticUrl, ll) = alertDialogLoginBox("API url", "API Static url", false)
                url.setText(API_URL)
                staticUrl.setText(API_STATIC)
                AlertDialog.Builder(this)
                    .setMessage("Entrez votre adresse d'api et de fichiers statiques")
                    .setTitle("Paramètres")
                    .setPositiveButton("ok") { dialog, which ->
                        API_URL = url.text.toString()
                        API_STATIC = staticUrl.text.toString()
                        sharedPreference.save(url.text.toString(),SerialKey.APIUrl.name)
                        sharedPreference.save(staticUrl.text.toString(), SerialKey.APIStaticUrl.name)

                    }.setNegativeButton("cancel") { dialog, which ->
                        Toast.makeText(this, "Annulé", Toast.LENGTH_SHORT).show()
                    }.setView(ll)
                    .show()
            }
        }
        return super.onOptionsItemSelected(item)
    }


    private fun synchronize(login:String, password:String, cancel:Boolean){
        binding.progressBar.visibility = View.VISIBLE
        binding.tvGameError.visibility = View.GONE

        var modification =SendApiChange(login,password,ApiResponse(
            addedGames, addedAddOns, addedMultiAddOns)
            , ApiResponse(modifiedGames, modifiedAddOns, modifiedMultiAddOns),
            ApiResponse(deletedGames, deletedAddOns, deletedMultiAddOns)
        )
        if (cancel){
            modification = SendApiChange(login,password,ApiResponse(
                ArrayList(), ArrayList(), ArrayList())
                , ApiResponse(ArrayList(), ArrayList(), ArrayList()),
                ApiResponse(ArrayList(), ArrayList(), ArrayList())
            )
        }
        val content = gson.toJson(ApiBody(modification))



        CoroutineScope(SupervisorJob()).launch {
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
            sharedPreference.save(body, SerialKey.APIStorage.name)
            val result = gson.fromJson(body, ApiResponse::class.java)
            refreshAll(result)
        }


        runOnUiThread {
            adapter.notifyDataSetChanged()
            binding.progressBar.visibility = View.GONE
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


    override fun onElementClick(datum: CommonBase?) {
        intent = Intent(this, GameDetails::class.java)
        intent.putExtra(SerialKey.Game.name, datum)
        startActivity(intent)
    }


    private fun <T:CommonBase>fillList(list:ArrayList<T>, fill:ArrayList<T>){
        list.clear()
        list.addAll(fill)
        list.sortBy{it.name}

    }

    private fun getSave(){
        val savedContent = sharedPreference.getValueString(SerialKey.APIStorage.name)
        if (savedContent != null && savedContent.isNotBlank()) {
            println(sharedPreference.getValueString(SerialKey.APIStorage.name))
            val answer = gson.fromJson(
                sharedPreference.getValueString(SerialKey.APIStorage.name),
                ApiResponse::class.java
            )
            fillList(allGames, answer.games)
            adapter.notifyDataSetChanged()
            fillList(allAddOns, answer.add_ons)
            fillList(allMultiAddOns, answer.multi_add_ons)
            val savedList = sharedPreference.getValueString(SerialKey.AllImagesStorage.name)
            allImages.list_of_images.clear()
            allImages.list_of_images.addAll(gson.fromJson(savedList, AllImages::class.java)?.list_of_images?: mutableSetOf())
        }else{
                binding.tvGameError.text = "Liste vide synchonisez là!"
                binding.tvGameError.visibility = View.VISIBLE
        }

    }

    private fun refreshAll(answer:ApiResponse){
        fillList(allGames, answer.games)
        fillList(allAddOns, answer.add_ons)
        fillList(allMultiAddOns, answer.multi_add_ons)
        loadImages(allGames)
        loadImages(allAddOns)
        loadImages(allMultiAddOns)
        clean_image_list()



    }

    private fun <T:CommonBase> loadImages(listOfObject:ArrayList<T>){
        CoroutineScope(SupervisorJob()).run{
            for (game in listOfObject){
                if (!allImages.list_of_images.contains(game.name)){

                    if (!game.external_img.isNullOrBlank()){
                        launch{
                            getImage(game.external_img, game.name)

                        }

                    }
                    else if (!game.picture.isNullOrBlank() && API_STATIC != null){
                        launch{
                            getImage("${API_STATIC}${game.picture}", game.name)
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
                sharedPreference.save(gson.toJson(allImages),SerialKey.AllImagesStorage.name)
            }

        }catch(e:Exception){
            e.printStackTrace()
        }
    }

    fun alertDialogLoginBox(box1Text:String, box2Text:String, isSecondBoxPassword:Boolean):Triple<EditText, EditText, LinearLayout>{
        val loginText = TextView(this)
        loginText.text = box1Text
        loginText.typeface = Typeface.DEFAULT_BOLD
        loginText.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        val login = EditText(this)
        val pwdText = TextView(this)
        pwdText.text = box2Text
        pwdText.typeface = Typeface.DEFAULT_BOLD
        pwdText.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        val pwd = EditText(this)
        if (isSecondBoxPassword){
            pwd.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
            pwd.transformationMethod = PasswordTransformationMethod.getInstance()
        }
        val ll = LinearLayout(this)
        ll.orientation = LinearLayout.VERTICAL
        ll.gravity = Gravity.CENTER
        ll.setPadding(20,0,20,0)
        ll.addView(loginText)
        ll.addView(login)
        ll.addView(pwdText)
        ll.addView(pwd)
        return Triple(login, pwd, ll)
    }

    private fun clean_image_list(){
        allImages.list_of_images.removeIf {
            !allGames.any {game -> game.name == it } && !allAddOns.any {game -> game.name == it }
                && !allMultiAddOns.any {game -> game.name == it }
        }
        sharedPreference.save(gson.toJson(allImages), SerialKey.AllImagesStorage.name)
    }


}