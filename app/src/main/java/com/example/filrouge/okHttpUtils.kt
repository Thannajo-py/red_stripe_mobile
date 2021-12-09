package com.example.filrouge

import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody


val client = OkHttpClient()
val gson = Gson()
val MEDIA_TYPE_JSON = "application/json; charset=utf-8".toMediaType()


fun sendPostOkHttpRequest(url: String, paramJson:String): String {
//Création de la requete
    val body = paramJson.toRequestBody(MEDIA_TYPE_JSON)
    val request = Request.Builder().post(body).url(url).build()

    val response = client.newCall(request).execute() //Analyse du code retour
    return if (response.code !in 200..299) {
        throw Exception("Réponse du serveur incorrect : ${response.code}")
    } else {
        //Résultat de la requete.
        response.body?.string() ?: ""
    }
}


fun sendGetOkHttpRequestImage(url: String): ByteArray? {
//Création de la requete
    val request = Request.Builder().url(url).build() //Execution de la requête
    val response = client.newCall(request).execute() //Analyse du code retour
    return if (response.code !in 200..299) {
        throw Exception("Réponse du serveur incorrect : ${response.code}")
    } else {
        //Résultat de la requete.
        response.body?.bytes()
    }
}


fun sendGetOkHttpRequest(url: String): String? {
//Création de la requete
    val request = Request.Builder().url(url).build() //Execution de la requête
    val response = client.newCall(request).execute() //Analyse du code retour
    return if (response.code !in 200..299) {
        throw Exception("Réponse du serveur incorrect : ${response.code}")
    } else {
        //Résultat de la requete.
        response.body?.string()
    }
}
