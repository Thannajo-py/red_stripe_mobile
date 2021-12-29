package thannajo.appli.filrouge.view

import thannajo.appli.filrouge.R
import thannajo.appli.filrouge.appInstance
import thannajo.appli.filrouge.databinding.ActivitySearchBinding
import thannajo.appli.filrouge.model.CommonComponentDao
import thannajo.appli.filrouge.utils.SearchQuery
import thannajo.appli.filrouge.utils.SerialKey
import thannajo.appli.filrouge.utils.Type
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class SearchActivity : AppCompatActivity(), View.OnClickListener {

    /**
     * access to xml element by id
     */
    private val binding: ActivitySearchBinding by lazy{
        ActivitySearchBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.btnSearch.setOnClickListener(this)
        autoCompleteViewBinding()
    }

    /**
     * Generate list of link between Dao and [AutoCompleteTextView]
     */
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

    /**
     * Handle general [AutoCompleteTextView] linking with dao
     */
    private fun<T> autoCompleteViewBinding
                (dao: CommonComponentDao<T>,
                 autocompleteTextView:
                 AutoCompleteTextView){
        CoroutineScope(SupervisorJob()).launch{
            val allName = dao.getNameList()
            val acvNameAdapter = ArrayAdapter(
                this@SearchActivity,
                android.R.layout.simple_dropdown_item_1line,
                allName
            )
            runOnUiThread {
                autocompleteTextView.setAdapter(acvNameAdapter)
            }
        }
    }

    /**
     * Handle name [AutoCompleteTextView] with Game name AddOn name MultiAddOn name
     */
    private fun nameAutoCompleteBinding(){
        CoroutineScope(SupervisorJob()).launch {
            val allNameList = ArrayList<String>()
             allNameList.addAll(appInstance.database.gameDao().getNameList())
             allNameList.addAll(appInstance.database.addOnDao().getNameList())
             allNameList.addAll(appInstance.database.multiAddOnDao().getNameList())
            val acvNameAdapter = ArrayAdapter(
                this@SearchActivity,
                android.R.layout.simple_dropdown_item_1line,
                allNameList )
            runOnUiThread {
                binding.actvName.setAdapter(acvNameAdapter)
            }
        }
    }

    override fun onClick(v: View?) {
        startActivity(Intent(this, GenericTypeDetailsActivity::class.java)
            .putExtra(SerialKey.Name.name, getString(R.string.search_result))
            .putExtra(SerialKey.Type.name, Type.Search.name)
            .putExtra(SerialKey.GenericId.name,0L)
            .putExtra(
                SerialKey.QueryContent.name, SearchQuery(
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
            )
            ))
        finish()
    }

    /**
     * return null if field is empty
     */
    private fun checkEmpty(arg:String) = if (arg.isNullOrBlank()) null else arg

    /**
     * return null if field is empty or filled with non-number element
     */
    private fun checkEmptyInt (arg:String) =  if (
        arg.isNullOrBlank() || !Regex("^[0-9]+$"
        ).matches(arg)) null else arg.toInt()
}
