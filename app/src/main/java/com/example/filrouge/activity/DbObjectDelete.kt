package com.example.filrouge.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.filrouge.*
import com.example.filrouge.bean.ArtistTableBean
import com.example.filrouge.bean.CommonGame
import com.example.filrouge.bean.ID
import com.example.filrouge.dao.CommonCustomInsert
import com.example.filrouge.databinding.ActivityDbObjectDeleteBinding
import com.example.filrouge.databinding.ActivitySearchBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class DbObjectDelete : AppCompatActivity(), GenericIDCbListenerId, View.OnClickListener {

    private val binding: ActivityDbObjectDeleteBinding by lazy{
        ActivityDbObjectDeleteBinding.inflate(layoutInflater)
    }
    private val addedIdContent: ArrayList<ArrayList<Long>> = arrayListOf(
        arrayListOf(),
        arrayListOf(),
        arrayListOf(),
        arrayListOf(),
        arrayListOf(),
        arrayListOf(),
        arrayListOf(),
        arrayListOf(),
        arrayListOf()
    )
    private val adapter = arrayListOf(
        GenericIDListCbAdapterId(this, Type.Designer.name, addedIdContent[AddedContent.Designer.ordinal]),
        GenericIDListCbAdapterId(this, Type.Artist.name, addedIdContent[AddedContent.Artist.ordinal]),
        GenericIDListCbAdapterId(this, Type.Publisher.name, addedIdContent[AddedContent.Publisher.ordinal]),
        GenericIDListCbAdapterId(this, Type.Tag.name, addedIdContent[AddedContent.Tag.ordinal]),
        GenericIDListCbAdapterId(this, Type.Topic.name, addedIdContent[AddedContent.Topic.ordinal]),
        GenericIDListCbAdapterId(this, Type.Language.name, addedIdContent[AddedContent.Language.ordinal]),
        GenericIDListCbAdapterId(this, Type.Mechanism.name, addedIdContent[AddedContent.Mechanism.ordinal]),
        GenericIDListCbAdapterId(this, Type.PlayingMode.name, addedIdContent[AddedContent.PlayingMod.ordinal]),
        GenericIDListCbAdapterId(this, Type.Difficulty.name, addedIdContent[AddedContent.Difficulty.ordinal]),
    )
    private val listMethod = ListCommonMethod()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        bindRvToAdapter()
        binding.btnDel.setOnClickListener(this)
    }


    private fun bindRvToAdapter(){
        val db = appInstance.database
        arrayListOf(
            Triple(binding.rvDesigner, AddedContent.Designer.ordinal, db.designerDao()),
            Triple(binding.rvArtist, AddedContent.Artist.ordinal, db.artistDao()),
            Triple(binding.rvPublisher, AddedContent.Publisher.ordinal, db.publisherDao()),
            Triple(binding.rvTag, AddedContent.Tag.ordinal, db.tagDao()),
            Triple(binding.rvTopic, AddedContent.Topic.ordinal, db.topicDao()),
            Triple(binding.rvLanguage, AddedContent.Language.ordinal, db.languageDao()),
            Triple(binding.rvMechanism, AddedContent.Mechanism.ordinal, db.mechanismDao()),
            Triple(binding.rvPlayingMod, AddedContent.PlayingMod.ordinal, db.playingModDao()),
            Triple(binding.rvDifficulty, AddedContent.Difficulty.ordinal, db.difficultyDao()),
        ).forEach { bindRvToAdapter(it.first, it.second, it.third) }
    }


    private fun<T:ID> bindRvToAdapter(rv:RecyclerView, position:Int, dao:CommonCustomInsert<T>){
        rv.adapter = adapter[position]
        rv.layoutManager = GridLayoutManager(this, 1)
        rv.addItemDecoration(MarginItemDecoration(5))
        dao.getDeletableNameList().asLiveData().observe(this, {it?.let{adapter[position].submitList(it)}})
    }

    override fun onGenericClick(id: Long, type: String, cb:CheckBox) {
        when(type){
            Type.Designer.name -> listMethod.listContentManager(addedIdContent[AddedContent.Designer.ordinal], id, cb)
            Type.Artist.name -> listMethod.listContentManager(addedIdContent[AddedContent.Artist.ordinal], id, cb)
            Type.Publisher.name -> listMethod.listContentManager(addedIdContent[AddedContent.Publisher.ordinal], id, cb)
            Type.Language.name -> listMethod.listContentManager(addedIdContent[AddedContent.Language.ordinal], id, cb)
            Type.PlayingMode.name -> listMethod.listContentManager(addedIdContent[AddedContent.PlayingMod.ordinal], id, cb)
            Type.Tag.name -> listMethod.listContentManager(addedIdContent[AddedContent.Tag.ordinal], id, cb)
            Type.Topic.name -> listMethod.listContentManager(addedIdContent[AddedContent.Topic.ordinal], id, cb)
            Type.Mechanism.name -> listMethod.listContentManager(addedIdContent[AddedContent.Mechanism.ordinal], id, cb)
            Type.Difficulty.name -> listMethod.listContentManager(addedIdContent[AddedContent.Difficulty.ordinal], id, cb)
        }

    }

    override fun onClick(p0: View?) {
        CoroutineScope(SupervisorJob()).launch{
            val db = appInstance.database
            db.runInTransaction {
                arrayListOf(
                    Pair(AddedContent.Designer.ordinal, db.designerDao()),
                    Pair(AddedContent.Artist.ordinal, db.artistDao()),
                    Pair(AddedContent.Publisher.ordinal, db.publisherDao()),
                    Pair(AddedContent.Tag.ordinal, db.tagDao()),
                    Pair(AddedContent.Topic.ordinal, db.topicDao()),
                    Pair(AddedContent.Language.ordinal, db.languageDao()),
                    Pair(AddedContent.Mechanism.ordinal, db.mechanismDao()),
                    Pair(AddedContent.PlayingMod.ordinal, db.playingModDao()),
                    Pair(AddedContent.Difficulty.ordinal, db.difficultyDao()),
                ).forEach { pair ->
                    addedIdContent[pair.first].forEach {
                        pair.second.deleteOne(it)
                    }
                }
            }
        }
    }
}