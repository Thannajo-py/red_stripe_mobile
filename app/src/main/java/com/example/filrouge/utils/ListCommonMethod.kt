package com.example.filrouge.utils

import android.widget.CheckBox

/**
 * A class with tools for handling CheckBox in list
 */
class ListCommonMethod {

    /**
     * A method for handling checkBox isChecked properties in adapter list
     * @param list an ArrayList containing the checked element
     * @param cb checkbox link to content
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

