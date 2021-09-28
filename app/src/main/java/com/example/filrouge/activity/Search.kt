package com.example.filrouge.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.filrouge.*
import com.example.filrouge.databinding.ActivitySearchBinding

class Search : AppCompatActivity(), View.OnClickListener {


    private val binding:ActivitySearchBinding by lazy{ActivitySearchBinding.inflate(layoutInflater)}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.btnSearch.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        val searchedGames = allGames.filter{ checkGame(it) }.toCollection(ArrayList())
        val searchedAddOns = ArrayList<AddOnBean>()
        val searchedMultiAddOns = ArrayList<MultiAddOnBean>()
        if (binding.etTopic.text.toString().isBlank() && binding.etTopic.text.toString().isBlank()
            && binding.etTag.text.toString().isBlank()){
            searchedAddOns.addAll(allAddOns.filter{checkCommon(it)})
            searchedMultiAddOns.addAll(allMultiAddOns.filter{checkCommon(it)})
        }
        intent = Intent(this, GenericTypeDetails::class.java)
        intent.putExtra(SerialKey.Name.name, "Résultat de la recherche")
        intent.putExtra(SerialKey.Type.name, Type.Search.name)
        intent.putExtra(SerialKey.SearchResult.name, ApiResponse(searchedGames, searchedAddOns, searchedMultiAddOns))
        startActivity(intent)
        finish()


    }

    private fun checkAge(game: CommonBase, input:String) =  game.age == null || !Regex("^[0-9]+$").matches(input) || game.age > input.toInt()

    private fun checkList(list:ArrayList<String>, input:String) = input.isBlank() || list.any{ it.lowercase().contains(Regex(".*${input.lowercase()}.*")) }

    private fun checkMinNbPlayer(base: CommonBase, playerMin:String) = base.player_min == null || !Regex("^[0-9]+$").matches(playerMin) || base.player_min < playerMin.toInt()

    private fun checkMaxNbPlayer(base: CommonBase, playerMax:String) = base.player_max == null || !Regex("^[0-9]+$").matches(playerMax) || base.player_max > playerMax.toInt()

    private fun checkPlayingTime(base: CommonBase, playingTime:String) = base.max_time == null || !Regex("^[0-9]+$").matches(playingTime) || base.max_time < playingTime.toInt()

    private fun checkDifficulty(base: CommonBase, difficulty:String) = base.difficulty == null || difficulty.isBlank() || base.difficulty.lowercase().contains(difficulty.lowercase())

    private fun checkCommon(games: CommonBase):Boolean{
        return games.name.lowercase().contains(binding.etNom.text.toString().lowercase())&&
                checkList(games.designers,binding.etDesigner.text.toString()) &&
                checkList(games.artists,binding.etArtist.text.toString()) &&
                checkList(games.publishers,binding.etPublisher.text.toString()) &&
                checkList(games.language,binding.etLanguage.text.toString()) &&
                checkList(games.playing_mode,binding.etPlayingMode.text.toString()) &&
                checkAge(games,binding.etAge.text.toString())&&
                checkMinNbPlayer(games,binding.etNbPlayerMin.text.toString())&&
                checkMaxNbPlayer(games,binding.etNbPlayerMax.text.toString())&&
                checkPlayingTime(games, binding.etMaxTime.text.toString())&&
                checkDifficulty(games, binding.etDifficulty.text.toString())

    }
    private fun checkGame(game: GameBean):Boolean{
        return checkCommon(game) && checkList(game.mechanism, binding.etMechanism.text.toString())
                && checkList(game.tags, binding.etTag.text.toString())
                && checkList(game.topics, binding.etTopic.text.toString())
    }

}