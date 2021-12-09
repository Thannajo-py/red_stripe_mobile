package com.example.filrouge.activity

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.filrouge.*
import com.example.filrouge.bean.CommonGame
import com.example.filrouge.bean.DesignerWithGame
import com.example.filrouge.databinding.ActivityGenericTypeDetailsBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class GenericTypeDetails : CommonType() {

    private val binding: ActivityGenericTypeDetailsBinding by lazy{
        ActivityGenericTypeDetailsBinding.inflate(layoutInflater)
    }
    private val type:String by lazy{intent.extras!!.getString(SerialKey.Type.name, "")}
    private val id:Long by lazy{intent.extras!!.getLong(SerialKey.GenericId.name, 0L)}
    private val name:String by lazy{intent.extras!!.getString(SerialKey.Name.name, "")}
    private val db = appInstance.database
    private val dbMethod = DbMethod()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        handleIntent()
        binding.tvGenericDetailName.text = name
    }

    private fun displayRv(pb:ProgressBar, rv:RecyclerView){
        runOnUiThread {
            rv.visibility = View.VISIBLE
            pb.visibility = View.GONE
        }
    }

    private fun handleIntent(){
        val gameSpecificList = dbMethod.getGameCommonSpecificField()
        when(true){
            type == Type.Search.name -> handleSearchQuery()
            gameSpecificList.contains(type) -> handleGameTypeIntent()
            else -> handleIntent(type)
        }
    }

    private fun handleGameTypeIntent(){
        handleTypeSpecificIntent(type, Type.Game.name)
        displayRv(binding.pbRvGenericDetailAddOn, binding.rvGenericDetailAddOn)
        displayRv(binding.pbRvGenericDetailMultiAddOn, binding.rvGenericDetailMultiAddOn)
    }

    private fun handleSearchQuery(){
        val search = intent.extras!!.getSerializable(SerialKey.QueryContent.name) as SearchQuery
        CoroutineScope(SupervisorJob()).launch{
            dbMethod.getGameType().forEach {
                val adapter = GenericListAdapter(this@GenericTypeDetails)
                if(it == Type.Game.name){
                    handleGameSearchQuery(search, adapter)
                }
                else{
                    handleOtherSearchQuery(it, search, adapter)
                }
            }
        }
    }

    private fun handleOtherSearchQuery(it: String, search:SearchQuery, adapter: GenericListAdapter){
        val rv = binding.getMember("rvGenericDetail$it") as RecyclerView
        val pb = binding.getMember("pbRvGenericDetail$it") as ProgressBar
        val dao = db.getMember("${it.highToLowCamelCase()}Dao")
        val data = dao!!.getMember(
            "getWithDesignerFromSearchQuery",
            search.name,
            search.designer,
            search.artist,
            search.publisher,
            search.playerMin,
            search.playerMax,
            search.maxTime,
            search.difficulty,
            search.age,
            search.playingMod,
            search.language,
        ) as LiveData<List<CommonGame>>
        runOnUiThread {
            rv.adapter = adapter
            layout(rv)
            data.observe(
                this@GenericTypeDetails,
                {data -> data?.let{adapter.submitList(data)}}
            )
            displayRv(pb, rv)
        }
    }

    private fun handleGameSearchQuery(search: SearchQuery, adapter:GenericListAdapter){
        runOnUiThread {
            binding.rvGenericDetailGame.adapter = adapter
            layout(binding.rvGenericDetailGame)
            db.gameDao().getWithDesignerFromSearchQuery(
                search.name,
                search.designer,
                search.artist,
                search.publisher,
                search.playerMin,
                search.playerMax,
                search.maxTime,
                search.difficulty,
                search.age,
                search.playingMod,
                search.language,
                search.tag,
                search.topic,
                search.mechanism)
                .observe(
                    this@GenericTypeDetails,
                    {data -> data?.let{adapter.submitList(data)}}
                )
            displayRv(
                binding.pbRvGenericDetailGame,
                binding.rvGenericDetailGame
            )
        }
    }

    private fun handleTypeSpecificIntent(type:String, gameType:String){
        CoroutineScope(SupervisorJob()).launch{
            val data = db.getMember("${gameType.highToLowCamelCase()}Dao")!!
                .getMember("getWithDesignerFrom${type}Id", id)
                    as LiveData<List<DesignerWithGame>>
            val adapter = GenericListAdapter(this@GenericTypeDetails)
            val rv = binding.getMember("rvGenericDetail$gameType") as RecyclerView
            val pb = binding.getMember("pbRvGenericDetail$gameType") as ProgressBar
            runOnUiThread {
                rv.adapter = adapter
                layout(rv)
                data.observe(
                    this@GenericTypeDetails,
                    {it?.let{adapter.submitList(it)}}
                )
                displayRv(pb,rv)
            }
        }
    }

    private fun handleIntent(type:String){
        dbMethod.getGameType().forEach {
         handleTypeSpecificIntent(type, it)
        }
    }
}
