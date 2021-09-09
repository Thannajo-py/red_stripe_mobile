package com.example.filrouge

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import com.example.filrouge.databinding.ActivityAddElementBinding

class AddElement : AppCompatActivity(), View.OnClickListener {

    private val binding by lazy{ActivityAddElementBinding.inflate(layoutInflater)}
    private val sharedPreference by lazy{ SharedPreference(this)}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.btnAdd.setOnClickListener(this)
        binding.rbGame.setOnClickListener{
            if (binding.rbGame.isChecked){
                setView(binding.llGame)
            }
        }
        binding.rbAddOn.setOnClickListener{
            if (binding.rbAddOn.isChecked){
                setView(binding.llAddOn)
            }
        }
        binding.rbMultiAddOn.setOnClickListener{
            if (binding.rbMultiAddOn.isChecked){
                setView(binding.llMultiAddOn)
            }
        }

    }

    override fun onClick(v: View?) {
        if(binding.etNom.text.toString().isBlank()){
            Toast.makeText(this, "le nom ne peut pas être vide", Toast.LENGTH_SHORT).show()
        }
        else {
            val name = binding.etNom.text.toString()
            val player_min = testNull(binding.etNbPlayerMin.text.toString())?.toInt()
            val player_max = testNull(binding.etNbPlayerMax.text.toString())?.toInt()
            val playing_time = testNull(binding.etMaxTime.text.toString())
            val difficulty = testNull(binding.etDifficulty.text.toString())
            val designers = returnList(binding.etDesigner.text.toString())
            val artists = returnList(binding.etArtist.text.toString())
            val publishers = returnList(binding.etPublisher.text.toString())
            val bgg_link = testNull(binding.etBggLink.text.toString())
            val playing_mode = returnList(binding.etPlayingMode.text.toString())
            val language = returnList(binding.etLanguage.text.toString())
            val age = testNull(binding.etAge.text.toString())?.toInt()
            val buying_price = testNull(binding.etBuyingPrice.text.toString())?.toInt()
            val stock = testNull(binding.etStock.text.toString())?.toInt()
            val max_time = testNull(binding.etStock.text.toString())?.toInt()
            val by_player = binding.rbByPlayerTrue.isChecked
            val tags = returnList(binding.etPlayingMode.text.toString())
            val topics = returnList(binding.etTopic.text.toString())
            val mechanism = returnList(binding.etMechanism.text.toString())
            val game = testNull(binding.etGameAddOn.text.toString())
            val games = returnList(binding.etGameAddOn.text.toString())
            val add_on: ArrayList<AddOnBean>
            val multi_add_on: ArrayList<MultiAddOnBean>
            when (true) {
                binding.rbGame.isChecked -> registerGame()
                binding.rbAddOn.isChecked -> registerAddOn(name, player_min, player_max,
                    playing_time, difficulty, designers, artists, publishers, bgg_link, playing_mode,
                    language, age, buying_price, stock, max_time, game)
                binding.rbMultiAddOn.isChecked -> registerMultiAddOn(name, player_min, player_max,
                    playing_time, difficulty, designers, artists, publishers, bgg_link, playing_mode,
                    language, age, buying_price, stock, max_time, games)

            }
            sharedPreference.save(gson.toJson(ApiResponse(allGames, allAddOns, allMultiAddOns)),SerialKey.APIStorage.name)
            sharedPreference.save(gson.toJson(ApiResponse(addedGames, addedAddOns, addedMultiAddOns)),SerialKey.AddedContent.name)
        }
    }

    fun registerGame(){
        TODO()
    }

    fun registerAddOn(name: String,
                      player_min: Int?,
                      player_max: Int?,
                      playing_time: String?,
                      difficulty: String?,
                      designers: ArrayList<String>,
                      artists: ArrayList<String>,
                      publishers: ArrayList<String>,
                      bgg_link: String?,
                      playing_mode: ArrayList<String>,
                      language: ArrayList<String>,
                      age: Int?,
                      buying_price:Int?,
                      stock: Int?,
                      max_time: Int?,
                      game: String?){
        val addOn = AddOnBean(
            name,
            player_min,
            player_max,
            playing_time,
            difficulty,
            designers,
            artists,
            publishers,
            bgg_link,
            playing_mode,
            language,
            age,
            buying_price,
            stock,
            max_time,
            game
        )
        allAddOns.add(addOn)
        addedAddOns.add(addOn)

    }
    fun registerMultiAddOn(name: String,
                      player_min: Int?,
                      player_max: Int?,
                      playing_time: String?,
                      difficulty: String?,
                      designers: ArrayList<String>,
                      artists: ArrayList<String>,
                      publishers: ArrayList<String>,
                      bgg_link: String?,
                      playing_mode: ArrayList<String>,
                      language: ArrayList<String>,
                      age: Int?,
                      buying_price:Int?,
                      stock: Int?,
                      max_time: Int?,
                      games: ArrayList<String>){
        val multiAddOn = MultiAddOnBean(
            name,
            player_min,
            player_max,
            playing_time,
            difficulty,
            designers,
            artists,
            publishers,
            bgg_link,
            playing_mode,
            language,
            age,
            buying_price,
            stock,
            max_time,
            games
        )
        allMultiAddOns.add(multiAddOn)
        addedMultiAddOns.add(multiAddOn)
    }

    fun setView(ll: LinearLayout){
        binding.llGame.visibility = View.GONE
        binding.llAddOn.visibility = View.GONE
        binding.llMultiAddOn.visibility = View.GONE
        ll.visibility = View.VISIBLE
    }

    private fun testNull(etContent:String)= if(etContent.isBlank()) null else etContent
    private fun returnList(etContent:String) = testNull(etContent)?.split(",")?.map{it.trim()}?.toCollection(ArrayList<String>())?:ArrayList()
}