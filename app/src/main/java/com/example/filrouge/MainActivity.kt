package com.example.filrouge

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.widget.Toast
import com.example.filrouge.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private val binding: ActivityMainBinding by lazy{ ActivityMainBinding.inflate(layoutInflater) }
    private val sharedPreference by lazy{SharedPreference(this)}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        API_URL = sharedPreference.getValueString(SerialKey.APIUrl.name)
        API_STATIC = sharedPreference.getValueString(SerialKey.APIStaticUrl.name)
        binding.btnLogin.setOnClickListener(this)
        binding.etLogin.setText(sharedPreference.getValueString(SerialKey.RememberNameStorage.name)?:"")
        binding.etPassword.setText(sharedPreference.getValueString(SerialKey.RememberPasswordStorage.name)?:"")
        refreshUsers()
        if (allUsers.listOfUsers.isEmpty()){
            startActivity(Intent(this, CreateNewAccount::class.java))
        }


    }

    override fun onResume() {
        refreshUsers()
        super.onResume()
    }
    override fun onClick(v: View?) {

        binding.tvLoginError.visibility = View.GONE
        binding.progressBarLogin.visibility = View.VISIBLE
        val password = binding.etPassword.text.toString()
        val login = binding.etLogin.text.toString()
        val userLogin = allUsers.listOfUsers.filter{it.login == login}

        if (userLogin.isNotEmpty() &&
            SHA256.encryptThisString(password) == userLogin[0].password){
                if(binding.checkBox.isChecked){
                    sharedPreference.save(login, SerialKey.RememberNameStorage.name)
                    sharedPreference.save(password, SerialKey.RememberPasswordStorage.name)
                }
                else{
                    sharedPreference.removeValue(SerialKey.RememberNameStorage.name)
                    sharedPreference.removeValue(SerialKey.RememberPasswordStorage.name)
                }
            currentUser = userLogin[0]
            startActivity(Intent(this, ViewGamesActivity::class.java))
        }
        else{
            binding.tvLoginError.text = "Wrong login or password"
            binding.tvLoginError.visibility = View.VISIBLE
        }
        binding.progressBarLogin.visibility = View.GONE
    }

    private fun refreshUsers(){
        allUsers.listOfUsers.clear()
        val savedInstance = sharedPreference.getValueString(SerialKey.AllUsersStorage.name)
        if (!savedInstance.isNullOrBlank()){
            allUsers.listOfUsers.addAll(
                gson.fromJson(savedInstance,
                    AllUsers::class.java).listOfUsers)
        }

    }
}