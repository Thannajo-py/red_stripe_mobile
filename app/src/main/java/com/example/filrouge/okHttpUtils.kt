package com.example.filrouge

import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request

val client = OkHttpClient()
val gson = Gson()
fun sendGetOkHttpRequest(url: String): String { println("url : $url")
//Création de la requete
    val request = Request.Builder().url(url).build() //Execution de la requête
    val response = client.newCall(request).execute() //Analyse du code retour
    return if (response.code !in 200..299) {
        throw Exception("Réponse du serveur incorrect : ${response.code}")
    } else {

         response.body?.string() ?: ""
    }

}