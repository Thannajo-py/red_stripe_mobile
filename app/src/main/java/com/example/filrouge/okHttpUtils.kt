package com.example.filrouge

import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody


val client = OkHttpClient()
val gson = Gson()
val MEDIA_TYPE_JSON = "application/json; charset=utf-8".toMediaType()

/**
 * method for POST httpRequest with String Body Return
 * use for communication with API of the server
 * @author Anthony Monteiro
 */
fun sendPostOkHttpRequest(url: String, paramJson:String): String {
//Build request
    val body = paramJson.toRequestBody(MEDIA_TYPE_JSON)
    val request = Request.Builder().post(body).url(url).build() //Execute request
    val response = client.newCall(request).execute() //Analyse response code
    return if (response.code !in 200..299) {
        throw Exception("Incorrect server answer : ${response.code}")
    } else {
        //Request result
        response.body?.string() ?: ""
    }
}


/**
 * method for GET httpRequest with ByteArray Body Return
 * used for downloading picture from external link
 */
fun sendGetOkHttpRequestImage(url: String): ByteArray? {
//Build request
    val request = Request.Builder().url(url).build() //Execute request
    val response = client.newCall(request).execute() //Analyse response code
    return if (response.code !in 200..299) {
        throw Exception("Incorrect server answer : ${response.code}")
    } else {
        //Request result
        response.body?.bytes()
    }
}


/**
 * method for GET httpRequest with String Body Return
 * use for communication with BGA API
 * @author Anthony Monteiro
 */
fun sendGetOkHttpRequest(url: String): String? {
//Build request
    val request = Request.Builder().url(url).build() //Execute request
    val response = client.newCall(request).execute() //Analyse response code
    return if (response.code !in 200..299) {
        throw Exception("Incorrect server answer : ${response.code}")
    } else {
        //Request result
        response.body?.string()
    }
}
