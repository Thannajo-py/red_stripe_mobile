package com.example.filrouge

import android.view.Menu

open class GameAddOnMultiAddOnCommonMenu :CommonType(){
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.add(0,MenuId.DeleteThis.ordinal,0,"Supprimer")
        menu?.add(0,MenuId.ModifyThis.ordinal,0,"Modifier")

        return super.onCreateOptionsMenu(menu)
    }
}