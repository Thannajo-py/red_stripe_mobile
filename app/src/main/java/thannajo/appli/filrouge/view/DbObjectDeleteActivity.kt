package thannajo.appli.filrouge.view

import thannajo.appli.filrouge.appInstance
import thannajo.appli.filrouge.databinding.ActivityDbObjectDeleteBinding
import thannajo.appli.filrouge.model.CommonCustomInsert
import thannajo.appli.filrouge.model.ID
import thannajo.appli.filrouge.utils.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class DbObjectDeleteActivity : AppCompatActivity(), GenericIDCbListenerId, View.OnClickListener {

    private val binding by lazy{
        ActivityDbObjectDeleteBinding.inflate(layoutInflater)
    }
    private val addedContentHashMap = HashMap<String, ArrayList<Long>>()
    private val adapterHashMap = HashMap<String, GenericIDListCbAdapterId>()
    private val listMethod = ListCommonMethod()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val typeList = getTypeList()
        fillHashMap(typeList)
        bindRvToAdapter(typeList)
        binding.btnDel.setOnClickListener(this)
    }

    private fun getTypeList():ArrayList<String>{
        val dbMethod = DbMethod()
        return dbMethod.getDeletableList()
    }

    private fun fillHashMap(typeList:ArrayList<String>){
        typeList.forEach {
            addedContentHashMap[it] = ArrayList()
            adapterHashMap[it] = GenericIDListCbAdapterId(
                this,
                it,
                addedContentHashMap[it]!!
            )
        }
    }

    private fun bindRvToAdapter(typeList:ArrayList<String>){
        typeList.forEach { type ->
            CoroutineScope(SupervisorJob()).launch {
                val lowercase = type.highToLowCamelCase()
                val rv = binding.getMember("rv$type") as RecyclerView
                val dao = appInstance.database.getMember("${lowercase}Dao")
                        as CommonCustomInsert<ID>
                runOnUiThread {
                    bindRvToAdapter(rv, type, dao)
                }
            }
        }
    }

    private fun<T:ID> bindRvToAdapter(rv:RecyclerView, key:String, dao:CommonCustomInsert<T>){
        rv.adapter = adapterHashMap[key]
        rv.layoutManager = GridLayoutManager(this, 1)
        rv.addItemDecoration(MarginItemDecoration(5))
        dao.getDeletableNameList().asLiveData().observe(
            this,
            {it?.let{adapterHashMap[key]!!.submitList(it)}}
        )
    }

    override fun onGenericClick(id: Long, type: String, cb:CheckBox) {
        listMethod.listContentManager(addedContentHashMap[type]!!, id, cb)
    }

    override fun onClick(p0: View?) {
        CoroutineScope(SupervisorJob()).launch{
            val db = appInstance.database
            db.runInTransaction {
                deleteRelatedContent(getTypeList())
            }
        }
    }

    private fun deleteRelatedContent(typeList:ArrayList<String>){
        typeList.forEach { type ->
                val lowercase = type.replaceFirstChar { it.lowercase() }
                val dao = appInstance.database::class.members.find {
                    it.name == "${lowercase}Dao"
                }!!.call(appInstance.database) as CommonCustomInsert<ID>
                addedContentHashMap[type]!!.forEach { dao.deleteOne(it) }
        }
    }
}