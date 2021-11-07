package com.example.filrouge.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.example.filrouge.*
import com.example.filrouge.databinding.ActivityApisearchBinding
import com.example.filrouge.databinding.ActivityCreateNewAccountBinding
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class APISearchActivity : AppCompatActivity() {

    private val binding: ActivityApisearchBinding by lazy{ ActivityApisearchBinding.inflate(layoutInflater) }
    private val adapter = BgaListAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.rvAPI.adapter = adapter
        binding.rvAPI.layoutManager = GridLayoutManager(this,1)
        binding.rvAPI.addItemDecoration(MarginItemDecoration(5))
        binding.btnSearch.setOnClickListener {
            apiSearch()
            binding.btnSearch.visibility = View.GONE
        }
    }

    private fun apiSearch(){
        val name = binding.etGameName.text.toString()
        val url = "https://api.boardgameatlas.com/api/search?name=${name}&fuzzy_match=true&client_id=${Constant.ApiBgaKey.value}"
        val gson = Gson()
        binding.progressBar2.visibility = View.VISIBLE

        CoroutineScope(SupervisorJob()).launch {
            val answer = gson.fromJson(sendGetOkHttpRequest(url), BgaApi::class.java)
            if (answer.games == null || answer.games.isEmpty()){
                runOnUiThread {
                    binding.btnSearch.visibility = View.VISIBLE
                    binding.progressBar2.visibility = View.GONE
                    answer.games?.run{
                        Toast.makeText(this@APISearchActivity, "aucun jeu correspondant trouvé", Toast.LENGTH_SHORT).show()
                    }?:run{
                        Toast.makeText(this@APISearchActivity, "une erreur est survenue, veuillez réessayer", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            else{
                runOnUiThread {
                    binding.rvAPI.visibility = View.VISIBLE
                    adapter.submitList(answer.games)
                    binding.btnSearch.visibility = View.VISIBLE
                    binding.progressBar2.visibility = View.GONE
                }
                if (ALL_MECHANICS.isEmpty()){
                    ALL_MECHANICS.addAll(gson.fromJson(sendGetOkHttpRequest(Constant.UrlMechanics.value), MechanicApiResult::class.java).mechanics)
                }
                if (ALL_CATEGORIES.isEmpty()) {
                    ALL_CATEGORIES.addAll(
                        gson.fromJson(
                            sendGetOkHttpRequest(Constant.UrlCategories.value),
                            CategoriesApiResult::class.java
                        ).categories
                    )
                }


            }
        }
    }

    fun onElementClick(datum:BgaGameBean){
        startActivity(
            Intent(this, AddElement::class.java)
            .putExtra(SerialKey.ApiBgaGame.name, datum)
        )
        finish()
    }
}
