package com.example.filrouge


import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.example.filrouge.databinding.ActivityViewGamesBinding
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
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
        menu?.add(0,MenuId.CancelAndSynchronize.ordinal,0,"Annuler modification et synchroniser")
        menu?.add(0,MenuId.Synchronize.ordinal,0,"Sauvegarder modification et synchroniser")
        menu?.add(0,MenuId.Search.ordinal,0,"Rechercher")
        menu?.add(0,MenuId.DeleteAccount.ordinal,0,"Supprimer compte")
        menu?.add(0,MenuId.AddContent.ordinal,0,"Ajouter un élément")
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            MenuId.CancelAndSynchronize.ordinal -> AlertDialog.Builder(this).setMessage("Voulez vous annuler toutes vos modifications et re-synchroniser?").setTitle("Attention")
                .setPositiveButton("ok"){
                        dialog, which -> run{cancelAndSynchronize()
                }
                }.setNegativeButton("cancel"){
                        dialog, which -> Toast.makeText(this, "Annulé", Toast.LENGTH_SHORT).show()
                }
                .show()
            MenuId.Search.ordinal -> startActivity(Intent(this, Search::class.java))
            MenuId.DeleteAccount.ordinal -> startActivity(Intent(this, DeleteAccount::class.java))
            MenuId.AddContent.ordinal -> startActivity(Intent(this, AddElement::class.java))
            MenuId.Synchronize.ordinal -> AlertDialog.Builder(this).setMessage("Voulez vous sauvegarder toutes vos modifications et re-synchroniser?").setTitle("Attention")
                .setPositiveButton("ok"){
                        dialog, which -> run{synchronize()
                }
                }.setNegativeButton("cancel"){
                        dialog, which -> Toast.makeText(this, "Annulé", Toast.LENGTH_SHORT).show()
                }
                .show()
        }
        return super.onOptionsItemSelected(item)
    }


    private fun synchronize(){
        binding.progressBar.visibility = View.VISIBLE
        binding.tvGameError.visibility = View.GONE


        CoroutineScope(SupervisorJob()).launch {
            try {
                val body = sendPostOkHttpRequest(APIUrl.ALL_GAMES.url, gson.toJson(SendApiChange("","",ApiResponse(
                    addedGames, addedAddOns, addedMultiAddOns)
                , ApiResponse(modifiedGames, modifiedAddOns, modifiedMultiAddOns),
                    ApiResponse(deletedGames, deletedAddOns, deletedMultiAddOns)
                )))
                apiReception(body)


            } catch (e: Exception) {
                apiErrorHandling(e)
            }
        }
    }


    private fun cancelAndSynchronize() {
        binding.progressBar.visibility = View.VISIBLE
        binding.tvGameError.visibility = View.GONE


        CoroutineScope(SupervisorJob()).launch {
            try {
                val body = sendGetOkHttpRequest(APIUrl.ALL_GAMES.url)
                apiReception(body)

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
            val answer = gson.fromJson(
                sharedPreference.getValueString(SerialKey.APIStorage.name),
                ApiResponse::class.java
            )
            fillList(allGames, answer.games)
            adapter.notifyDataSetChanged()
            fillList(allAddOns, answer.add_ons)
            fillList(allMultiAddOns, answer.multi_add_ons)
        }else{
                binding.tvGameError.text = "Liste vide synchonisez là!"
                binding.tvGameError.visibility = View.VISIBLE
        }

    }

    private fun refreshAll(answer:ApiResponse){
        fillList(allGames, answer.games)
        fillList(allAddOns, answer.add_ons)
        fillList(allMultiAddOns, answer.multi_add_ons)
    }


}