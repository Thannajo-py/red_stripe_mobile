package com.example.filrouge

import android.view.Menu

open class GameAddOnMultiAddOnCommonMenu :CommonType(){

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if(currentUser?.delete == true){
            menu?.add(0,MenuId.DeleteThis.ordinal,0,getString(R.string.delete))
        }
        if (currentUser?.change == true){
            menu?.add(0,MenuId.ModifyThis.ordinal,0,getString(R.string.modify))
        }
        return super.onCreateOptionsMenu(menu)
    }
}
