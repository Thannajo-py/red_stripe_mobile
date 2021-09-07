package com.example.filrouge

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.filrouge.databinding.ActivityMainBinding
import com.example.filrouge.databinding.ActivityMainMenuBinding

class MainMenuActivity : AppCompatActivity(), View.OnClickListener {

    val binding: ActivityMainMenuBinding by lazy{ ActivityMainMenuBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.btnViewGames.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        startActivity(Intent(this, ViewGamesActivity::class.java))
    }
}