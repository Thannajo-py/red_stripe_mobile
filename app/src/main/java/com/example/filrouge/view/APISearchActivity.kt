package com.example.filrouge.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.example.filrouge.*
import com.example.filrouge.databinding.ActivityApisearchBinding
import com.example.filrouge.utils.*
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class APISearchActivity : AppCompatActivity() {

    private val binding: ActivityApisearchBinding by lazy{
        ActivityApisearchBinding.inflate(layoutInflater)
    }
    private val adapter = BgaListAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setRvApi()
        binding.btnSearch.setOnClickListener {
            apiSearch()
            binding.btnSearch.visibility = View.GONE
        }
    }

    private fun setRvApi(){
        binding.rvAPI.adapter = adapter
        binding.rvAPI.layoutManager = GridLayoutManager(this,1)
        binding.rvAPI.addItemDecoration(MarginItemDecoration(5))
    }

    private fun badAnswer(answer:BgaApi) = answer.games == null || answer.games.isEmpty()

    private fun apiSearch(){
        binding.etError.visibility = View.GONE
        binding.progressBar2.visibility = View.VISIBLE

        val name = binding.etGameName.text.toString()
        val url = "https://api.boardgameatlas.com/api/search?name=${
            name
        }&fuzzy_match=true&client_id=${
            Constant.ApiBgaKey.value
        }"
        val gson = Gson()

        CoroutineScope(SupervisorJob()).launch {
            try {
                val answer = gson.fromJson(sendGetOkHttpRequest(url), BgaApi::class.java)
                if (badAnswer(answer)) handleBadAnswer(answer)
                else handleApiAnswer(answer)
            }
            catch(e:Exception){
                handleConnectionError(e)
            }
        }
    }

    private fun handleApiAnswer(answer:BgaApi){
        runOnUiThread {
            binding.rvAPI.visibility = View.VISIBLE
            adapter.submitList(answer.games)
            finishLoading()
        }
        getApiMechanicListIfNone()
        getApiCategoriesListIfNone()
    }

    private fun getApiMechanicListIfNone(){
        try{
            if (ALL_MECHANICS.isEmpty()) {
                ALL_MECHANICS.addAll(
                    gson.fromJson(
                        sendGetOkHttpRequest(Constant.UrlMechanics.value),
                        MechanicApiResult::class.java
                    ).mechanics
                )
            }
        }
        catch(e:Exception){
            e.printStackTrace()
        }
    }

    private fun getApiCategoriesListIfNone(){
        try{
            if (ALL_CATEGORIES.isEmpty()) {
                ALL_CATEGORIES.addAll(
                    gson.fromJson(
                        sendGetOkHttpRequest(Constant.UrlCategories.value),
                        CategoriesApiResult::class.java
                    ).categories
                )
            }
        }
        catch(e:Exception){
            e.printStackTrace()
        }
    }

    private fun handleConnectionError(e:Exception){
        e.printStackTrace()
        runOnUiThread {
            finishLoading()
            binding.etError.text = getString(R.string.connection_error)
            binding.etError.visibility = View.VISIBLE
        }
    }

    private fun handleBadAnswer(answer:BgaApi){
        runOnUiThread {
            finishLoading()
            answer.games?.run {
                binding.etError.text = getString(R.string.no_matching_games)
                binding.etError.visibility = View.VISIBLE
            } ?: run {
                binding.etError.text = getString(R.string.common_error)
                binding.etError.visibility = View.VISIBLE
            }
        }
    }

    private fun finishLoading(){
        binding.btnSearch.visibility = View.VISIBLE
        binding.progressBar2.visibility = View.GONE
    }

    fun onElementClick(datum:BgaGameBean, img:String?){
        startActivity(
            Intent(this, AddElement::class.java)
            .putExtra(SerialKey.ApiBgaGame.name, datum)
                .putExtra(SerialKey.ApiBgaImage.name, img)
        )
        finish()
    }
}
