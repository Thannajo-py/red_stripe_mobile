package com.example.filrouge

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import com.example.filrouge.databinding.ActivityMultiAddOnDetailBinding

class MultiAddOnDetails : GameAddOnMultiAddOnCommonMenu() {

    private val binding: ActivityMultiAddOnDetailBinding by lazy{ ActivityMultiAddOnDetailBinding.inflate(layoutInflater) }
    private val sharedPreference by lazy {SharedPreference(this)}
    private val parent:GameBean? by lazy{intent.extras!!.getSerializable(SerialKey.ParentGame.name) as GameBean?}
    private val multiAddOn:MultiAddOnBean by lazy{intent.extras!!.getSerializable(SerialKey.MultiAddOn.name) as MultiAddOnBean}
    private val gamesList = ArrayList<String>()
    private val adapter = MultiAddOnGameAdapter(gamesList, this)



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        fillCommonRv(binding.rvDesigner, binding.rvArtist, binding.rvPublisher, binding.rvLanguage,
                binding.rvPlayingMode, multiAddOn)

        fillCommonTextView(binding.tvDifficulty, binding.tvMultiAddOnDetailName, binding.tvMultiAddOnDetailPlayer, binding.tvMultiAddOnDetailAge, multiAddOn)
        binding.tvMultiAddOnDetailPlayingTime.text = "jusqu'à ${multiAddOn.max_time} minutes" + if(parent != null && parent!!.by_player == true) "/ Joueur" else ""

        loadRv(binding.rvMultiAddOn, gamesList, adapter, multiAddOn.games)
        loadImage(multiAddOn, binding.ivDetails)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            MenuId.DeleteThis.ordinal -> AlertDialog.Builder(this).setMessage("Voulez vous vraiment supprimer cette extension partagée?").setTitle("Attention")
                .setPositiveButton("ok"){
                        dialog, which -> run{deleteFromList(multiAddOn, allMultiAddOns)
                    refreshedSavedData(sharedPreference)

                        }
                }.setNegativeButton("cancel"){
                        dialog, which -> Toast.makeText(this, "Annulé", Toast.LENGTH_SHORT).show()
                }
                .show()
            MenuId.ModifyThis.ordinal -> startActivity(Intent(this, AddElement::class.java).putExtra(SerialKey.ToModifyData.name, multiAddOn))
        }
        return super.onOptionsItemSelected(item)
    }





}