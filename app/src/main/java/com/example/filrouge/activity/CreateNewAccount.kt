package com.example.filrouge.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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

    private val binding: ActivityCreateNewAccountBinding by lazy{ ActivityCreateNewAccountBinding.inflate(layoutInflater)}
    private val db = appInstance.database.userDao()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.btnRegister.setOnClickListener(this)
        CoroutineScope(SupervisorJob()).launch{
            if(db.checkEmpty().isEmpty()){
                runOnUiThread {
                    binding.llCbox.visibility = View.GONE
                }
            }else{
                runOnUiThread {
                    binding.llCbox.visibility = View.VISIBLE
                }
            }
        }

        CoroutineScope(SupervisorJob()).launch{
        }

    }

    override fun onClick(v: View?) {
        binding.tvError.visibility = View.GONE
        if (binding.editTextTextPassword.text.toString() == binding.editTextTextPassword2.text.toString()){
            val regex = Regex(RegexPattern.PassWord.pattern)
            val password = binding.editTextTextPassword.text.toString()
            val login = binding.editTextTextPersonName.text.toString()
            if (regex.matches(password) && login.isNotBlank()){
                CoroutineScope(SupervisorJob()).launch {
                    val isUserDbEmpty = db.checkEmpty().isEmpty()
                    if (!db.getUser(login).isEmpty()) {
                        runOnUiThread {
                            Toast.makeText(this@CreateNewAccount, "Login déjà pris", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        val user = UserTableBean(
                            0,
                            login,
                            SHA256.encryptThisString(password),
                            isSuperAccount(binding.cbAdd, isUserDbEmpty),
                            isSuperAccount(binding.cbChange, isUserDbEmpty),
                            isSuperAccount(binding.cbDelete, isUserDbEmpty),
                            isSuperAccount(binding.cbSynchronize, isUserDbEmpty),
                            isSuperAccount(binding.cbCreateAccount, isUserDbEmpty),
                            isSuperAccount(binding.cbDeleteAccount, isUserDbEmpty)
                        )
                        if (isUserDbEmpty) {
                            currentUser = user
                            Log.w("AZERT", "this")
                        }
                        db.insert(user)
                        runOnUiThread {
                            Toast.makeText(this@CreateNewAccount, "Succès!", Toast.LENGTH_LONG).show()
                            startActivity(Intent(this@CreateNewAccount, ViewGamesActivity::class.java))
                            finish()
                        }

                    }
                }

            }
            else{
                binding.tvError.text = CommonString.PassWordRequirement.string
                binding.tvError.visibility = View.VISIBLE
            }
        }else{
            binding.tvError.text =  "les mots de passe ne sont pas identiques"
            binding.tvError.visibility = View.VISIBLE
        }
    }

    private fun isSuperAccount(view:CheckBox, isUserDbEmpty:Boolean) = if (isUserDbEmpty) true else view.isChecked
}