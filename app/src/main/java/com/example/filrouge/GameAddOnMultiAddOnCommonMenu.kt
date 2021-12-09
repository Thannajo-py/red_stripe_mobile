package com.example.filrouge

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.Menu
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.filrouge.activity.ViewGamesActivity
import com.example.filrouge.bean.CommonComponent
import com.example.filrouge.bean.DesignerTableBean
import com.example.filrouge.bean.ID
import com.example.filrouge.bean.Previous
import com.example.filrouge.dao.CommonDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

open class GameAddOnMultiAddOnCommonMenu :CommonType(){

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if(currentUser?.delete == true){
            menu?.add(0,MenuId.DeleteThis.ordinal,0,getString(R.string.delete))
        }
        if (currentUser?.change == true){
            menu?.add(0,MenuId.ModifyThis.ordinal,0,getString(R.string.modify))
        }
        return super.onCreateOptionsMenu(menu)
    }
    protected fun fillCommonRV(binding:ViewBinding, gameId:Long, app:LifecycleOwner, dao:CommonDao<*,*>){
        val dbMethod = DbMethod()
        dbMethod.getCommonField().forEach{
            CoroutineScope(SupervisorJob()).launch{
                val rv = binding.getMember("rv$it") as RecyclerView
                val adapter = GenericStringListAdapter(this@GameAddOnMultiAddOnCommonMenu, it)
                val field = dao.getMember("get${it}s", gameId)
                        as LiveData<List<ID>>
                val pb = binding.getMember("pbRv$it") as ProgressBar
                runOnUiThread {
                    rv.adapter = adapter
                    layout(rv)
                    field.observe(app, {it?.let{adapter.submitList(it)}})
                    rv.visibility = View.VISIBLE
                    pb.visibility = View.GONE
                }
            }
        }
    }

    protected fun fillDifficultyField(gameId:Long, app:LifecycleOwner, tvDifficulty:TextView, dao:CommonDao<*,*>){
        dao.getDifficulty(gameId).observe(app, {
            if (it.size > 0) it?.let {
                val difficulty = it.first()
                val name = difficulty.name
                val id = difficulty.id
                tvDifficulty.text = name
                tvDifficulty.setOnClickListener { onDifficultyClick(name, id) }
            }
            else tvDifficulty.text = getString(R.string.unknown)
        })
    }

    protected fun<T:Previous> showAlertBox(context: Context, message:String, dao:CommonDao<T,*>, type:String, gameId:Long){
        AlertDialog.Builder(context).setMessage(message).setTitle(getString(R.string.warning))
            .setPositiveButton(getString(R.string.ok)){
                    _, _ -> CoroutineScope(SupervisorJob()).launch{
                val list = dao.getObjectById(gameId)
                if(list.isNotEmpty()){
                    DbMethod().delete(list.first(), type)
                }
                startActivity(Intent(context, ViewGamesActivity::class.java))
                finish()
            }
            }.setNegativeButton(getString(R.string.cancel)){
                    _, _ -> Toast.makeText(this, getString(R.string.canceled), Toast.LENGTH_SHORT).show()
            }
            .show()
    }

    protected fun getDataStringOrUnknown(data:Any?, id:Int):String{
        var string = ""
        data?.run{
            string = getString(id, data.toString())
        }?:run{
            string = getString(R.string.unknown)
        }
        return string
    }

    protected fun getPlayerNumberOrUnknown(playerMin: Int?, playerMax: Int?) =
        when(true){
            playerMin == null && playerMax == null -> getString(R.string.unknown)
            playerMin != null && playerMax != null -> getString(
                R.string.player_number,
                playerMin.toString(),
                playerMax.toString()
            )
            playerMin != null -> getString(R.string.player_min, playerMin.toString())
            else -> getString(R.string.player_max, playerMax.toString())
        }
}
