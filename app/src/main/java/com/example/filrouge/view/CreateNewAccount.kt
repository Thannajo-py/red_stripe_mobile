package com.example.filrouge.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import android.widget.Toast
import com.example.filrouge.*
import com.example.filrouge.bean.UserTableBean
import com.example.filrouge.databinding.ActivityCreateNewAccountBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class CreateNewAccount : AppCompatActivity(), View.OnClickListener {

    private val binding: ActivityCreateNewAccountBinding by lazy{
        ActivityCreateNewAccountBinding.inflate(layoutInflater)
    }
    private val db = appInstance.database.userDao()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.btnRegister.setOnClickListener(this)
        handleNoUserCase()
    }

    private fun handleNoUserCase(){
        CoroutineScope(SupervisorJob()).launch{
            if(db.getList().isEmpty()){
                runOnUiThread {
                    binding.llCbox.visibility = View.GONE
                }
            }else{
                runOnUiThread {
                    binding.llCbox.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onClick(v: View?) {
        binding.tvError.visibility = View.GONE
        CoroutineScope(SupervisorJob()).launch{
            val regex = Regex(RegexPattern.PassWord.pattern)
            val password = binding.editTextTextPassword.text.toString()
            val login = binding.editTextTextPersonName.text.toString()
            when(true){
                !checkPasswordMatch() -> handleError(getString(R.string.password_do_not_match))
                !checkPassword(password, login, regex) ->
                    handleError(getString(R.string.password_rules))
                !checkFreeLogin(login) -> handleError(getString(R.string.login_already_taken))
                else -> createUser(login, password)
            }
        }
    }

    private fun createUser(login:String, password:String){
        val isUserDbEmpty = db.getList().isEmpty()
        try{
           val encryptedPassword = generateHashedPass(password)!!
            val user = UserTableBean(
                0,
                login,
                encryptedPassword,
                isSuperAccount(binding.cbAdd, isUserDbEmpty),
                isSuperAccount(binding.cbChange, isUserDbEmpty),
                isSuperAccount(binding.cbDelete, isUserDbEmpty),
                isSuperAccount(binding.cbSynchronize, isUserDbEmpty),
                isSuperAccount(binding.cbCreateAccount, isUserDbEmpty),
                isSuperAccount(binding.cbDeleteAccount, isUserDbEmpty)
            )
            if (isUserDbEmpty) {
                currentUser = user
            }
            db.insert(user)
            runOnUiThread {
                Toast.makeText(
                    this@CreateNewAccount,
                    getString(R.string.success),
                    Toast.LENGTH_LONG
                ).show()
                startActivity(Intent(this@CreateNewAccount, ViewGamesActivity::class.java))
                finish()
            }
        }
        catch(e:Exception){
            e.printStackTrace()
            handleError(getString(R.string.password_register_error))
        }
    }

    private fun checkFreeLogin(login:String) = db.getUser(login).isEmpty()

    private fun handleError(message:String){
        runOnUiThread {
            binding.tvError.text =  message
            binding.tvError.visibility = View.VISIBLE
        }
    }

    private fun checkPassword(password: String, login: String, regex:Regex) =
        regex.matches(password) && login.isNotBlank()

    private fun checkPasswordMatch() =
        binding.editTextTextPassword.text.toString() == binding.editTextTextPassword2.text.toString()

    private fun isSuperAccount(view:CheckBox, isUserDbEmpty:Boolean) =
        if (isUserDbEmpty) true else view.isChecked
}
