package com.example.filrouge.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.filrouge.*
import com.example.filrouge.bean.UserTableBean
import com.example.filrouge.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private val binding: ActivityMainBinding by lazy{ ActivityMainBinding.inflate(layoutInflater) }
    val dbUser = appInstance.database.userDao()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        API_URL = appInstance.sharedPreference.getValueString(SerialKey.APIUrl.name)
        API_STATIC = appInstance.sharedPreference.getValueString(SerialKey.APIStaticUrl.name)
        binding.btnLogin.setOnClickListener(this)
        appInstance.sharedPreference.getValueString(SerialKey.SavedUser.name)?.run{
            val userRemembered = gson.fromJson(this,UserTableBean::class.java)
            CoroutineScope(SupervisorJob()).launch{
                val savedUserList = dbUser.getUser(userRemembered.login)
                if(savedUserList.isNotEmpty()){
                    val savedUser = savedUserList[0]
                    if (savedUser == userRemembered){
                        currentUser = userRemembered
                        startActivity(Intent(this@MainActivity, ViewGamesActivity::class.java))
                    }
                }
            }
        }
        CoroutineScope(SupervisorJob()).launch {
            if (dbUser.checkEmpty().isEmpty()) {
                startActivity(Intent(this@MainActivity, CreateNewAccount::class.java))
            }
        }

    }


    override fun onClick(v: View?) {

        binding.tvLoginError.visibility = View.GONE
        binding.progressBarLogin.visibility = View.VISIBLE
        val password = binding.etPassword.text.toString()
        val login = binding.etLogin.text.toString()

        CoroutineScope(SupervisorJob()).launch{
            val userLogin = dbUser.getUser(login)
            if (userLogin.isNotEmpty() &&
                SHA256.encryptThisString(password) == userLogin[0].password){
                if(binding.checkBox.isChecked){
                    appInstance.sharedPreference.save(gson.toJson(userLogin[0]), SerialKey.SavedUser.name)
                }
                else{
                    appInstance.sharedPreference.removeValue(SerialKey.SavedUser.name)
                }
                currentUser = userLogin[0].copy()
                startActivity(Intent(this@MainActivity, ViewGamesActivity::class.java))
            }
            else{
                runOnUiThread {
                    binding.tvLoginError.text = "Wrong login or password"
                    binding.tvLoginError.visibility = View.VISIBLE
                }

            }
            runOnUiThread {
                binding.progressBarLogin.visibility = View.GONE
            }

        }

    }

}