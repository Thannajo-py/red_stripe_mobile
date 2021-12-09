package com.example.filrouge.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import com.example.filrouge.*
import com.example.filrouge.dao.CommonComponentDao
import com.example.filrouge.dao.CommonDao
import com.example.filrouge.databinding.ActivitySearchBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class Search : AppCompatActivity(), View.OnClickListener {

    private val binding:ActivitySearchBinding by lazy{
        ActivitySearchBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.btnSearch.setOnClickListener(this)
        autoCompleteViewBinding()
    }


    private fun autoCompleteViewBinding(){
        val db = appInstance.database
        arrayOf(
            Pair(db.designerDao(), binding.actvDesigner),
            Pair(db.artistDao(), binding.actvArtist),
            Pair(db.publisherDao(), binding.actvPublisher),
            Pair(db.difficultyDao(), binding.actvDifficulty),
            Pair(db.playingModDao(), binding.actvPlayingMode),
            Pair(db.languageDao(), binding.actvLanguage),
            Pair(db.tagDao(), binding.actvTag),
            Pair(db.topicDao(), binding.actvTopic),
            Pair(db.mechanismDao(), binding.actvMechanic),
        ).forEach { autoCompleteViewBinding(it.first, it.second) }
        nameAutoCompleteBinding()
    }

    private fun<T> autoCompleteViewBinding
                (dao:CommonComponentDao<T>,
                 autocompleteTextView:
                 AutoCompleteTextView){
        CoroutineScope(SupervisorJob()).launch{
            val allName = dao.getNameList()
            val acvNameAdapter = ArrayAdapter(
                this@Search,
                android.R.layout.simple_dropdown_item_1line,
                allName
            )
            runOnUiThread {
                autocompleteTextView.setAdapter(acvNameAdapter)
            }
        }
    }

    private fun nameAutoCompleteBinding(){
        CoroutineScope(SupervisorJob()).launch {
            val allNameList = ArrayList<String>()
             allNameList.addAll(appInstance.database.gameDao().getNameList())
             allNameList.addAll(appInstance.database.addOnDao().getNameList())
             allNameList.addAll(appInstance.database.multiAddOnDao().getNameList())
            val acvNameAdapter = ArrayAdapter(
                this@Search,
                android.R.layout.simple_dropdown_item_1line,
                allNameList )
            runOnUiThread {
                binding.actvName.setAdapter(acvNameAdapter)
            }
        }
    }

    override fun onClick(v: View?) {
        startActivity(Intent(this, GenericTypeDetails::class.java)
            .putExtra(SerialKey.Name.name, getString(R.string.search_result))
            .putExtra(SerialKey.Type.name, Type.Search.name)
            .putExtra(SerialKey.GenericId.name,0L)
            .putExtra(SerialKey.QueryContent.name, SearchQuery(
                checkEmpty(binding.actvName.text.toString()),
                checkEmpty(binding.actvDesigner.text.toString()),
                checkEmpty(binding.actvArtist.text.toString()),
                checkEmpty(binding.actvPublisher.text.toString()),
                checkEmptyInt(binding.etNbPlayerMin.text.toString()),
                checkEmptyInt(binding.etNbPlayerMax.text.toString()),
                checkEmptyInt(binding.etMaxTime.text.toString()),
                checkEmpty(binding.actvDifficulty.text.toString()),
                checkEmptyInt(binding.etAge.text.toString()),
                checkEmpty(binding.actvPlayingMode.text.toString()),
                checkEmpty(binding.actvLanguage.text.toString()),
                checkEmpty(binding.actvTag.text.toString()),
                checkEmpty(binding.actvTopic.text.toString()),
                checkEmpty(binding.actvMechanic.text.toString()),
            )))
        finish()
    }

    private fun checkEmpty(arg:String) = if (arg.isNullOrBlank()) null else arg

    private fun checkEmptyInt (arg:String) =  if (
        arg.isNullOrBlank() || !Regex("^[0-9]+$"
        ).matches(arg)) null else arg.toInt()
}
