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
        startActivity(Intent(this, GenericTypeDetails::class.java)
            .putExtra(SerialKey.Name.name, "Résultat de la recherche")
            .putExtra(SerialKey.Type.name, Type.Search.name)
            .putExtra(SerialKey.GenericId.name,0L)
            .putExtra(SerialKey.QueryContent.name, SearchQuery(
                checkEmpty(binding.etNom.text.toString()),
                checkEmpty(binding.etDesigner.text.toString()),
                checkEmpty(binding.etArtist.text.toString()),
                checkEmpty(binding.etPublisher.text.toString()),
                checkEmptyInt(binding.etNbPlayerMin.text.toString()),
                checkEmptyInt(binding.etNbPlayerMax.text.toString()),
                checkEmptyInt(binding.etMaxTime.text.toString()),
                checkEmpty(binding.etDifficulty.text.toString()),
                checkEmptyInt(binding.etAge.text.toString()),
                checkEmpty(binding.etPlayingMode.text.toString()),
                checkEmpty(binding.etLanguage.text.toString()),
                checkEmpty(binding.etTag.text.toString()),
                checkEmpty(binding.etTopic.text.toString()),
                checkEmpty(binding.etMechanism.text.toString()),
            )))
        finish()


    }

    private fun checkEmpty(arg:String) = if (arg.isNullOrBlank()) null else arg
    private fun checkEmptyInt (arg:String) =  if (arg.isNullOrBlank() || !Regex("^[0-9]+$").matches(arg)) null else arg.toInt()

}