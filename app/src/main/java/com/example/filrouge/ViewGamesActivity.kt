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
class ViewGamesActivity : AppCompatActivity(),  GameAdapter.onGameListListener {


    val binding: ActivityViewGamesBinding by lazy{ ActivityViewGamesBinding.inflate(layoutInflater) }
    val gson = Gson()
    val adapter = GameAdapter(allGames, this)
    val sharedPreference by lazy{SharedPreference(this)}


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.rvGames.adapter = adapter
        binding.rvGames.layoutManager = GridLayoutManager(this, 1)
        binding.rvGames.addItemDecoration(MarginItemDecoration(5))
        getSave()



    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.add(0,MenuId.Synchronize.ordinal,0,"Synchronize")
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            MenuId.Synchronize.ordinal -> synchronize()
        }
        return super.onOptionsItemSelected(item)
    }


    fun synchronize() {
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



    override fun onGameClick(datum: GameBean?) {
        intent = Intent(this, GameDetails::class.java)
        intent.putExtra(SerialKey.Game.name, datum)
        startActivity(intent)
    }


    fun <T>fillList(list:ArrayList<T>, fill:ArrayList<T>){
        list.clear()
        list.addAll(fill)

    }

    fun getSave(){
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
        }

    }

    fun refreshAll(answer:ApiResponse){
        fillList(allGames, answer.games)
        fillList(allAddOns, answer.add_ons)
        fillList(allMultiAddOns, answer.multi_add_ons)
    }


}