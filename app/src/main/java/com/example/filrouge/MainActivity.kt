package com.example.filrouge

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.widget.Toast
import com.example.filrouge.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), View.OnClickListener {

    val binding: ActivityMainBinding by lazy{ ActivityMainBinding.inflate(layoutInflater) }
    val sharedPreference by lazy{SharedPreference(this)}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.btnLogin.setOnClickListener(this)
        binding.etLogin.setText(sharedPreference.getValueString(SerialKey.RememberNameStorage.name)?:"")
        binding.etPassword.setText(sharedPreference.getValueString(SerialKey.RememberPasswordStorage.name)?:"")
        Toast.makeText(this, sharedPreference.getValueString("test"), Toast.LENGTH_LONG).show()

    }

    override fun onClick(v: View?) {

        binding.tvLoginError.visibility = View.GONE
        binding.progressBarLogin.visibility = View.VISIBLE
        val password = binding.etPassword.text.toString()
        val login = binding.etLogin.text.toString()

        if (sharedPreference.getValueString(login) != null &&
            SHA256.encryptThisString(password) == sharedPreference.getValueString(login)?:""){
                if(binding.checkBox.isChecked){
                    sharedPreference.save(login, SerialKey.RememberNameStorage.name)
                    sharedPreference.save(password, SerialKey.RememberPasswordStorage.name)
                }
                else{
                    sharedPreference.removeValue(SerialKey.RememberNameStorage.name)
                    sharedPreference.removeValue(SerialKey.RememberPasswordStorage.name)
                }
            startActivity(Intent(this, ViewGamesActivity::class.java))
        }
        else{
            binding.tvLoginError.text = "Wrong login or password"
            binding.tvLoginError.visibility = View.VISIBLE
        }
        binding.progressBarLogin.visibility = View.GONE
    }
}