package com.example.filrouge

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import android.widget.Toast
import com.example.filrouge.databinding.ActivityCreateNewAccountBinding

class CreateNewAccount : AppCompatActivity(), View.OnClickListener {

    private val binding: ActivityCreateNewAccountBinding by lazy{ ActivityCreateNewAccountBinding.inflate(layoutInflater)}
    private val sharedPreference by lazy{SharedPreference(this)}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.btnRegister.setOnClickListener(this)
        binding.llCbox.visibility = if (allUsers.listOfUsers.isEmpty()) View.GONE else View.VISIBLE
    }

    override fun onClick(v: View?) {
        binding.tvError.visibility = View.GONE
        if (binding.editTextTextPassword.text.toString() == binding.editTextTextPassword2.text.toString()){
            val regex = Regex("^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=.*[^A-Za-z0-9]).{6,}$")
            val password = binding.editTextTextPassword.text.toString()
            val login = binding.editTextTextPersonName.text.toString()
            if (allUsers.listOfUsers.any { it.login == login }){
                Toast.makeText(this, "Login déjà pris", Toast.LENGTH_LONG).show()
                return
            }
            if (regex.matches(password) && login.isNotBlank()){

                val user =  UserBean(login, SHA256.encryptThisString(password), PermissionBean(
                    isSuperAccount(binding.cbAdd),
                    isSuperAccount(binding.cbChange),
                    isSuperAccount(binding.cbDelete),
                    isSuperAccount(binding.cbSynchronize),
                    isSuperAccount(binding.cbCreateAccount),
                    isSuperAccount(binding.cbDeleteAccount)
                )
                )
                allUsers.listOfUsers.add(user)
                sharedPreference.save(gson.toJson(allUsers), SerialKey.AllUsersStorage.name)
                Toast.makeText(this, "Succés!", Toast.LENGTH_LONG).show()

                startActivity(Intent(this, ViewGamesActivity::class.java))
                finish()

            }
            else{
                binding.tvError.text = "le login ne doit pas être vide\n" +
                        "le mot de passe doit contenir au moins:\n" +
                        "- 1 lettre minuscule\n" +
                        "- 1 lettre majuscule\n" +
                        "- 1 nombre\n" +
                        "- 1 caractère spécial\n" +
                        "- 6 caractères"
                binding.tvError.visibility = View.VISIBLE
            }
        }else{
            binding.tvError.text =  "les mots de passe ne sont pas identiques"
            binding.tvError.visibility = View.VISIBLE
        }
    }

    private fun isSuperAccount(view:CheckBox) = if (allUsers.listOfUsers.isEmpty()) true else view.isChecked
}