package thannajo.appli.filrouge.view

import thannajo.appli.filrouge.R
import thannajo.appli.filrouge.appInstance
import thannajo.appli.filrouge.databinding.ActivityMultiAddOnDetailBinding
import thannajo.appli.filrouge.utils.GenericListAdapter
import thannajo.appli.filrouge.utils.MenuId
import thannajo.appli.filrouge.utils.SerialKey
import thannajo.appli.filrouge.utils.Type
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.lifecycle.asLiveData

class MultiAddOnDetailsActivity : DetailsCommonMethodAbstractActivity() {

    /**
     * access to xml element by id
     */
    private val binding: ActivityMultiAddOnDetailBinding by lazy{
        ActivityMultiAddOnDetailBinding.inflate(layoutInflater)
    }

    /**
     * Id of the MultiAddOn passed with the intent that start the activity
     */
    private val gameId by lazy{
        intent.extras!!.getSerializable(SerialKey.MultiAddOnId.name) as Long
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        fillCommonRV(binding, gameId, this, appInstance.database.multiAddOnDao())
        fillCommonTextView()
        fillGameRV()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            MenuId.DeleteThis.ordinal -> showAlertBox(
                this,
                getString(R.string.delete_multi_add_on),
                appInstance.database.multiAddOnDao(),
                Type.MultiAddOn.name,
                gameId
            )
            MenuId.ModifyThis.ordinal -> {
                startActivity(
                Intent(this, AddElementActivity::class.java)
                .putExtra(SerialKey.ToModifyDataId.name, gameId)
                .putExtra(SerialKey.ToModifyDataName.name, binding.tvMultiAddOnDetailName.text)
                .putExtra(SerialKey.ToModifyDataType.name, Type.MultiAddOn.name))
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * Fill all Textview from the page with the sent multi-add-on
     */
    private fun fillCommonTextView(){
        appInstance.database.multiAddOnDao().getById(gameId).asLiveData().observe(
            this,
            {
                it?.let{
                    if(it.isNotEmpty()) {
                        val game = it.first()
                        loadImage(
                            game.name,
                            binding.ivDetails,
                            Type.MultiAddOn.name,
                            binding.pbIvDetails
                        )
                        binding.tvMultiAddOnDetailName.text = game.name
                        binding.tvMultiAddOnDetailAge.text = getDataStringOrUnknown(
                            game.age,
                            R.string.age
                        )
                        binding.tvMultiAddOnDetailPlayingTime.text = getDataStringOrUnknown(
                            game.playing_time,
                            R.string.playing_time
                        )
                        binding.tvMultiAddOnDetailPlayer.text = getPlayerNumberOrUnknown(
                                game.player_min,
                                game.player_max
                            )
                    }
                }
            }
        )
        fillDifficultyField(
            gameId,
            this,
            binding.tvDifficulty, appInstance.database.multiAddOnDao()
        )
    }

    /**
     * Fill the RecyclerView with all multi-add-on linked games
     */
    private fun fillGameRV(){
        val adapter = GenericListAdapter( this)
        binding.rvMultiAddOn.adapter = adapter
        layout(binding.rvMultiAddOn)
        appInstance.database.multiAddOnDao().getGameFromMultiAddOn(gameId).observe(
            this,
            {it?.let{ adapter.submitList(it) }}
        )
    }
}