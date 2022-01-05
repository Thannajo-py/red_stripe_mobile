package thannajo.appli.filrouge.view

import thannajo.appli.filrouge.R
import thannajo.appli.filrouge.appInstance
import thannajo.appli.filrouge.databinding.ActivityDeleteAccountBinding
import thannajo.appli.filrouge.model.UserTableBean
import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import thannajo.appli.filrouge.utils.*

class DeleteAccountActivity : AppCompatActivity(), View.OnClickListener, UserListener {

    private val binding by lazy{ ActivityDeleteAccountBinding.inflate(layoutInflater)}
    private val accountSelected = ArrayList<UserTableBean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.btnDel.setOnClickListener(this)
        fillRvAccount()
    }

    private fun fillRvAccount(){
        val adapter = UserBeanAdapter(this, accountSelected)
        binding.rvAccount.adapter = adapter
        binding.rvAccount.layoutManager = GridLayoutManager(this, 1)
        binding.rvAccount.addItemDecoration(MarginItemDecoration(5))
        appInstance.database.userDao().getAll().asLiveData().observe(
            this,
            {it?.let{adapter.submitList(it)}}
        )
    }

    override fun onClick(v: View?) {
        if (accountSelected.isNotEmpty()){
            val title = getString(R.string.warning).colored(getColor(R.color.list_background))
            AlertDialog.Builder(this, R.style.alert_dialog)
                .setMessage(
                    resources.getQuantityString(
                    R.plurals.delete_accounts,
                    accountSelected.size
                    )
                )
                .setTitle(title)
                .setPositiveButton(getString(R.string.ok)){
                        _, _ -> run{
                    deleteSelectedAccount()
                }
                }
                .setNegativeButton(getString(R.string.cancel)){
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
        else{
            Toast.makeText(
                this,
                getString(R.string.delete_account_none_selected),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun handleNullOrDeletedUser(){
        currentUser?.run{
            if(appInstance.database.userDao().getUser(this.login).isEmpty()){
                finishAffinity()
            }
        }?:run{
            finishAffinity()
        }
    }

    override fun onUserClick(datum: UserTableBean, position:Int) {
        if (accountSelected.contains(datum)) {
            accountSelected.remove(datum)
        }else{
            accountSelected.add(datum)
        }
    }

    private fun deleteSelectedAccount(){
        CoroutineScope(SupervisorJob()).launch{
            accountSelected.forEach {
                appInstance.database.userDao().deleteUser(it.id)
            }
            accountSelected.clear()
            handleNullOrDeletedUser()
        }
    }
}
