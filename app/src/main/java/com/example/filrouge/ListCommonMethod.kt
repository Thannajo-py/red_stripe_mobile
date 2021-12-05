package com.example.filrouge

import android.widget.CheckBox
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.GridLayoutManager

class ListCommonMethod {

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

