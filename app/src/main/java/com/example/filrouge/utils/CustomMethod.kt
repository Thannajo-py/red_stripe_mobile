package com.example.filrouge.utils

import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.memberProperties


/**
 * transform High camel case [String] to Low camel case [String]
 */
fun String.highToLowCamelCase() = this.replaceFirstChar { it.lowercase() }


/**
 * @return member of any class get by [String] name as [Any]
 * @throws NullPointerException if member does not exist
 */
fun Any.getMember(name:String) = this::class.members.find{it.name == name}!!.call(this)


/**
 * @return result of member called with passed arguments as [Any]
 * @throws NullPointerException if member does not exist or if arguments are of wrong types
 */
fun<T> Any.getMember(name:String, vararg arg:T) =
    this::class.members.find{it.name == name}!!.call(this, *arg)

/**
 * call the setter of a mutable properties with the passed arguments
 * @throws NullPointerException  if member does not exist, if arguments are of wrong types
 * or if member is not mutable
 */
fun<T> Any.setMember(name:String, vararg arg:T) =
    this::class.memberProperties.filterIsInstance<KMutableProperty<*>>().find{
        it.name == name
    }!!.setter.call(this,*arg)
