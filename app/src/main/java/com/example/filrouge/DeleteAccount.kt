package com.example.filrouge

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.example.filrouge.databinding.ActivityDeleteAccountBinding

class DeleteAccount : AppCompatActivity(), View.OnClickListener, UserListener {
    private val binding by lazy{ActivityDeleteAccountBinding.inflate(layoutInflater)}
    private val accountSelected = ArrayList<UserBean>()
    private val accountAdapter = UserBeanAdapter(allUsers.listOfUsers, this, accountSelected)
    private val sharedPreference by lazy{SharedPreference(this)}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.btnDel.setOnClickListener(this)
        binding.rvAccount.adapter = accountAdapter
        binding.rvAccount.layoutManager = GridLayoutManager(this, 1)
        binding.rvAccount.addItemDecoration(MarginItemDecoration(5))
        accountAdapter.notifyDataSetChanged()
    }

    override fun onClick(v: View?) {
        AlertDialog.Builder(this).setMessage("Voulez vous vraiment supprimer ce jeu?").setTitle("Attention")
            .setPositiveButton("ok"){
                    dialog, which -> run{

                allUsers.listOfUsers.removeAll(accountSelected)
                sharedPreference.save(gson.toJson(allUsers), SerialKey.AllUsersStorage.name)
                accountAdapter.notifyDataSetChanged()
            }
            }.setNegativeButton("cancel"){
                    dialog, which -> kotlin.run {

                Toast.makeText(this, "Annulé", Toast.LENGTH_SHORT).show()
            }
            }
            .show()
    }

    override fun onUserClick(datum: UserBean, position:Int) {
        if (accountSelected.contains(datum)) {
            accountSelected.remove(datum)
        }else{
            accountSelected.add(datum)
        }
        accountAdapter.notifyItemChanged(position)
        println(accountSelected)
    }
}