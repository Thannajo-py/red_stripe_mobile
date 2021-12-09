package com.example.filrouge.activity

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.GridLayoutManager
import com.example.filrouge.*
import com.example.filrouge.bean.UserTableBean
import com.example.filrouge.databinding.ActivityDeleteAccountBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class DeleteAccount : AppCompatActivity(), View.OnClickListener, UserListener {

    private val binding by lazy{ActivityDeleteAccountBinding.inflate(layoutInflater)}
    private val accountSelected = ArrayList<UserTableBean>()
    private val accountAdapter = UserBeanAdapter(this, accountSelected)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.btnDel.setOnClickListener(this)
        fillRvAccount()
    }

    private fun fillRvAccount(){
        binding.rvAccount.adapter = accountAdapter
        binding.rvAccount.layoutManager = GridLayoutManager(this, 1)
        binding.rvAccount.addItemDecoration(MarginItemDecoration(5))
        appInstance.database.userDao().getAll().asLiveData().observe(
            this,
            {it?.let{accountAdapter.submitList(it)}}
        )
    }

    override fun onClick(v: View?) {
        AlertDialog.Builder(this)
            .setMessage(getString(R.string.delete_accounts))
            .setTitle(getString(R.string.warning))
            .setPositiveButton(getString(R.string.ok)){
                    _, _ -> run{
                CoroutineScope(SupervisorJob()).launch{
                    accountSelected.forEach {
                        appInstance.database.userDao().deleteUser(it.id)
                    }
                    accountSelected.clear()
                }
            }
            }.setNegativeButton(getString(R.string.cancel)){
                    _, _ -> kotlin.run {

                Toast.makeText(
                    this,
                    getString(R.string.canceled),
                    Toast.LENGTH_SHORT
                ).show()
            }
            }
            .show()
    }

    override fun onUserClick(datum: UserTableBean, position:Int) {
        if (accountSelected.contains(datum)) {
            accountSelected.remove(datum)
        }else{
            accountSelected.add(datum)
        }
    }
}
