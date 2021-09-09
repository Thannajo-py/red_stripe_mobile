package com.example.filrouge


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
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
        menu?.add(0,MenuId.Synchronize.ordinal,0,"Synchroniser")
        menu?.add(0,MenuId.Search.ordinal,0,"Rechercher")
        menu?.add(0,MenuId.DeleteAccount.ordinal,0,"Supprimer compte")
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            MenuId.Synchronize.ordinal -> synchronize()
            MenuId.Search.ordinal -> startActivity(Intent(this, Search::class.java))
            MenuId.DeleteAccount.ordinal -> startActivity(Intent(this, DeleteAccount::class.java))

        }
        return super.onOptionsItemSelected(item)
    }


    private fun synchronize() {
        binding.progressBar.visibility = View.VISIBLE
        binding.tvGameError.visibility = View.GONE


        CoroutineScope(SupervisorJob()).launch {
            try {
                val body = sendGetOkHttpRequest(APIUrl.ALL_GAMES.url)
                sharedPreference.save(body, SerialKey.APIStorage.name)
                val result = gson.fromJson(body, ApiResponse::class.java)

                refreshAll(result)

                runOnUiThread {
                    adapter.notifyDataSetChanged()
                    binding.progressBar.visibility = View.GONE
                }

            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    binding.tvGameError.text = "error: ${e.message}"
                    binding.tvGameError.visibility = View.VISIBLE
                    binding.progressBar.visibility = View.GONE

                }
            }
        }
    }



    override fun onElementClick(datum: CommonBase?) {
        intent = Intent(this, GameDetails::class.java)
        intent.putExtra(SerialKey.Game.name, datum)
        startActivity(intent)
    }


    private fun <T>fillList(list:ArrayList<T>, fill:ArrayList<T>){
        list.clear()
        list.addAll(fill)

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