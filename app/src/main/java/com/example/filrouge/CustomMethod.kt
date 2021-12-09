package com.example.filrouge

import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.memberProperties


fun String.highToLowCamelCase() = this.replaceFirstChar { it.lowercase() }


fun Any.getMember(name:String) = this::class.members.find{it.name == name}!!.call(this)

fun<T> Any.getMember(name:String, vararg arg:T) =
    this::class.members.find{it.name == name}!!.call(this, *arg)

fun<T> Any.setMember(name:String, vararg arg:T) =
    this::class.memberProperties.filterIsInstance<KMutableProperty<*>>().find{
        it.name == name
    }!!.setter.call(this,*arg)