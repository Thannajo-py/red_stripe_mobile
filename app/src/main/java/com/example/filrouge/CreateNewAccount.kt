package com.example.filrouge

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.filrouge.databinding.ActivityCreateNewAccountBinding

class CreateNewAccount : AppCompatActivity(), View.OnClickListener {

    private val binding: ActivityCreateNewAccountBinding by lazy{ ActivityCreateNewAccountBinding.inflate(layoutInflater)}
    private val sharedPreference by lazy{SharedPreference(this)}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.button.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        binding.tvError.visibility = View.GONE
        if (binding.editTextTextPassword.text.toString() == binding.editTextTextPassword2.text.toString()){
            val regex = Regex("^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=.*[^A-Za-z0-9]).{6,}$")
            val password = binding.editTextTextPassword.text.toString()
            val login = binding.editTextTextPersonName.text.toString()
            if (regex.matches(password) && login.isNotBlank()){
                sharedPreference.save(SHA256.encryptThisString(password), login)
                Toast.makeText(this, "Success!", Toast.LENGTH_LONG).show()
                sharedPreference.save(login, SerialKey.AccountName.name)
                startActivity(Intent(this, ViewGamesActivity::class.java))
                finish()

            }
            else{
                binding.tvError.text = "login must not be empty\npassword must contain at least:1 minuscule letter, 1 majuscule letter, 1 number and 1 special character and 6 characters"
                binding.tvError.visibility = View.VISIBLE
            }
        }else{
            binding.tvError.text =  "password didn't match"
            binding.tvError.visibility = View.VISIBLE
        }
    }
}