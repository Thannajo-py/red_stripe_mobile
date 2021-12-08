package com.example.filrouge


fun String.highToLowCamelCase() = this.replaceFirstChar { it.lowercase() }


fun Any.getMember(name:String) = this::class.members.find{it.name == name}!!.call(this)

fun<T> Any.getMember(name:String, vararg arg:T) = this::class.members.find{it.name == name}!!.call(this, *arg)
