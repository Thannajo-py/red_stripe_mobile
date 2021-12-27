package com.example.filrouge.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.filrouge.*
import com.example.filrouge.databinding.ActivityMainBinding
import com.example.filrouge.utils.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), View.OnClickListener {

    /**
     * access to xml element by id
     */
    private val binding: ActivityMainBinding by lazy{ ActivityMainBinding.inflate(layoutInflater) }

    /**
     * user Dao shortcut
     */
    private val dbUser = appInstance.database.userDao()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.btnLogin.setOnClickListener(this@MainActivity)
        CoroutineScope(SupervisorJob()).launch{
            recoverApiStatic()
            handleNoUser()
            handleRememberedUser()
        }
    }

    override fun onRestart() {
        super.onRestart()
        displayLoginField()
    }

    /**
     * Check if there is a remembered user and if present in the database
     */
    private fun handleRememberedUser(){
        appInstance.sharedPreference.getValueString(SerialKey.SavedUser.name)?.run{
            val userRemembered = gson.fromJson(this,UserTableBean::class.java)
            CoroutineScope(SupervisorJob()).launch{
                val savedUserList = dbUser.getUser(userRemembered.login)
                when(true){
                    savedUserList.isEmpty() -> displayLoginField()
                    rememberedUserError(savedUserList, userRemembered) -> displayLoginField()
                    else -> autoLogin(userRemembered)
                }
            }
        }?:run{
            displayLoginField()
        }
    }

    /**
     * check if user inside the database is the same as the remembered user
     */
    private fun rememberedUserError(userList:List<UserTableBean>, userRemembered: UserTableBean) =
        userList.first() != userRemembered

    /**
     *
     */
    private fun autoLogin(userRemembered:UserTableBean){
        currentUser = userRemembered
        startActivity(Intent(this@MainActivity, ViewGamesActivity::class.java))
        finish()
    }

    private fun displayLoginField(){
        runOnUiThread {
            binding.progressBarRegisterUser.visibility = View.GONE
            binding.llLoginField.visibility = View.VISIBLE
        }
    }

   private fun handleNoUser(){
        CoroutineScope(SupervisorJob()).launch {
            if (dbUser.getList().isEmpty()) {
                startActivity(Intent(this@MainActivity, CreateNewAccountActivity::class.java))
                finish()
            }
        }
    }

    private fun recoverApiStatic(){
        API_URL = appInstance.sharedPreference.getValueString(SerialKey.APIUrl.name)
        API_STATIC = appInstance.sharedPreference.getValueString(SerialKey.APIStaticUrl.name)
    }

    override fun onClick(v: View?) {
        startLoading()
        val password = binding.etPassword.text.toString()
        val login = binding.etLogin.text.toString()
        CoroutineScope(SupervisorJob()).launch{
            val userLogin = dbUser.getUser(login)
            when(true){
                userLogin.isEmpty() -> handleLoginPasswordError()
                !isValid(password, userLogin.first().password) -> handleLoginPasswordError()
                else -> manualLogin(userLogin.first())
            }
            endLoading()
        }
    }

    private fun handleLoginPasswordError(){
        runOnUiThread {
            binding.tvLoginError.text = getString(R.string.wrong_login_or_password)
            binding.tvLoginError.visibility = View.VISIBLE
        }
    }

    private fun manualLogin(user:UserTableBean){
        if(binding.checkBox.isChecked){
            appInstance.sharedPreference.saveString(gson.toJson(user), SerialKey.SavedUser.name)
        }
        else{
            appInstance.sharedPreference.removeValue(SerialKey.SavedUser.name)
        }
        currentUser = user.copy()
        startActivity(Intent(this@MainActivity, ViewGamesActivity::class.java))
        finish()
    }

    private fun startLoading(){
        binding.tvLoginError.visibility = View.GONE
        binding.progressBarLogin.visibility = View.VISIBLE
    }

    private fun endLoading() = runOnUiThread {
        binding.progressBarLogin.visibility = View.GONE
    }

}
