package com.example.filrouge

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.filrouge.databinding.ActivityDeleteAccountBinding

class DeleteAccount : AppCompatActivity(), View.OnClickListener {
    private val binding by lazy{ActivityDeleteAccountBinding.inflate(layoutInflater)}
    private val sharedPreference by lazy{SharedPreference(this)}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.button.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        val login = binding.etLogin.text.toString()
        val password = binding.etPassword.text.toString()
        if(login == sharedPreference.getValueString(SerialKey.AccountName.name) && SHA256.encryptThisString(password) == sharedPreference.getValueString(login)){
            Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()
            sharedPreference.clear()
            allGames.clear()
            allAddOns.clear()
            allMultiAddOns.clear()
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }
    }
}