package com.example.filrouge

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.example.filrouge.databinding.ActivityMultiAddOnDetailBinding

class MultiAddOnDetails : CommonType(), MultiAddOnGameAdapater.GameListListener {

    val binding: ActivityMultiAddOnDetailBinding by lazy{ ActivityMultiAddOnDetailBinding.inflate(layoutInflater) }
    val parent:GameBean? by lazy{intent.extras!!.getSerializable(SerialKey.ParentGame.name) as GameBean?}
    val multiAddOn:MultiAddOnBean by lazy{intent.extras!!.getSerializable(SerialKey.MultiAddOn.name) as MultiAddOnBean}
    val gamesList = ArrayList<String>()
    val adapter = MultiAddOnGameAdapater(gamesList, this)



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        fillCommonRv(binding.rvDesigner, binding.rvArtist, binding.rvPublisher, binding.rvLanguage,
                binding.rvPlayingMode, multiAddOn)

        fillCommonTextView(binding.tvDifficulty, binding.tvMultiAddOnDetailName, binding.tvMultiAddOnDetailPlayer, binding.tvMultiAddOnDetailAge, multiAddOn)
        binding.tvMultiAddOnDetailPlayingTime.text = "jusqu'à ${multiAddOn.max_time} minutes" + if(parent != null && parent!!.by_player == true) "/ Joueur" else ""

        loadRv(binding.rvMultiAddOn, gamesList, adapter, multiAddOn.games)

    }


    override fun onGameClick(datum: GameBean?) {
        if (datum == null){
            Toast.makeText(this, "Link Error!",Toast.LENGTH_SHORT).show()
        }
        else{
            intent = Intent(this,GameDetails::class.java)
            intent.putExtra(SerialKey.Game.name, datum)
            startActivity(intent)
            finish()
        }
    }


}