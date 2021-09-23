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
            val regex = Regex(RegexPattern.PassWord.pattern)
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
                if (allUsers.listOfUsers.isEmpty()){
                    currentUser = user}
                allUsers.listOfUsers.add(user)
                sharedPreference.save(gson.toJson(allUsers), SerialKey.AllUsersStorage.name)
                Toast.makeText(this, "Succès!", Toast.LENGTH_LONG).show()

                startActivity(Intent(this, ViewGamesActivity::class.java))
                finish()

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

    private fun isSuperAccount(view:CheckBox) = if (allUsers.listOfUsers.isEmpty()) true else view.isChecked
}