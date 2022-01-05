package thannajo.appli.filrouge.view

import thannajo.appli.filrouge.R
import thannajo.appli.filrouge.model.CommonDao
import thannajo.appli.filrouge.model.ID
import thannajo.appli.filrouge.model.Previous
import thannajo.appli.filrouge.utils.*
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * parent class GameDetails, AddOnDetails, MultiAddOnDetails
 * give common method
 */
abstract class DetailsCommonMethodAbstractActivity : CommonTypeAbstractActivity(){

    /**
     * Common menu for class children
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if(currentUser?.delete == true){
            menu?.add(0, MenuId.DeleteThis.ordinal,0,getString(R.string.delete))
        }
        if (currentUser?.change == true){
            menu?.add(0, MenuId.ModifyThis.ordinal,0,getString(R.string.modify))
        }
        return super.onCreateOptionsMenu(menu)
    }

    /**
     * give adapter to RecyclerView change layout and
     * use kotlin reflection to semantically linked RecyclerView to Progress bar
     * for CommonField see: [DbMethod.getCommonField]
     */
    protected fun fillCommonRV(binding:ViewBinding, gameId:Long, app:LifecycleOwner, dao: CommonDao<*, *>){
        val dbMethod = DbMethod()
        dbMethod.getCommonField().forEach{
            CoroutineScope(SupervisorJob()).launch{
                val rv = binding.getMember("rv$it") as RecyclerView
                val adapter = GenericStringListAdapter(this@DetailsCommonMethodAbstractActivity, it)
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

    /**
     * Handle difficulty for class children
     */
    protected fun fillDifficultyField(gameId:Long, app:LifecycleOwner, tvDifficulty:TextView, dao:CommonDao<*,*>){
        dao.getDifficulty(gameId).observe(app, {
             it?.let {
                 if (it.isNotEmpty()) {
                     val difficulty = it.first()
                     val name = difficulty.name
                     val id = difficulty.id
                     tvDifficulty.text = name
                     tvDifficulty.setOnClickListener { onDifficultyClick(name, id) }
                 }
                 else tvDifficulty.text = getString(R.string.unknown)
            }?:let{
                 tvDifficulty.text = getString(R.string.unknown)
             }
        })
    }

    /**
     * handle deletion alert box for game, add-on, multi-add-on
     */
    protected fun<T: Previous> showAlertBox(context: Context, message:String, dao:CommonDao<T,*>, type:String, gameId:Long){
        val title = getString(R.string.warning).colored(getColor(R.color.list_background))
        AlertDialog.Builder(context,R.style.alert_dialog)
            .setMessage(message)
            .setTitle(title)
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

    /**
     * @return filled string if not null [data] else "unknown"
     */
    protected fun getDataStringOrUnknown(data:Any?, id:Int):String{
        var string = ""
        data?.run{
            string = getString(id, data.toString())
        }?:run{
            string = getString(R.string.unknown)
        }
        return string
    }

    /**
     * @return String answered based on non-null field
     */
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
