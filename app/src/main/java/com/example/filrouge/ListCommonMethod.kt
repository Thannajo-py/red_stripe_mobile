package com.example.filrouge

import android.widget.CheckBox
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.GridLayoutManager

/**
 * A class with tools for handling CheckBox in list
 */
class ListCommonMethod {

    /**
     * A method for handling checkBox isChecked properties in adapter list
     */
    fun <T>listContentManager(list:ArrayList<T>, content:T, cb: CheckBox){
        if (list.contains(content)) {
            list.remove(content)
            cb.isChecked = false
        }
        else {
            list.add(content)
            cb.isChecked = true
        }
    }
}

