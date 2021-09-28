package com.example.filrouge

import android.view.Menu

open class GameAddOnMultiAddOnCommonMenu :CommonType(){
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if(currentUser?.delete == true){
            menu?.add(0,MenuId.DeleteThis.ordinal,0,"Supprimer")
        }
        if (currentUser?.change == true){
            menu?.add(0,MenuId.ModifyThis.ordinal,0,"Modifier")
        }
        return super.onCreateOptionsMenu(menu)
    }
}