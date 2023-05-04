/**
    @file PremiereVue.kt
    @author Guillou Quentin
    @version du 30/04/23
 */

package com.example.projet_kotlin_led

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import androidx.appcompat.app.AlertDialog

class PremiereVue : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_premiere_vue)

        // Initialisation du bouton de connexion et ajout d'un listener sur le clic du bouton
        val buttonConnexion = findViewById<Button>(R.id.btn_Valider_Connexion)
        buttonConnexion.setOnClickListener {
            // Récupération de l'identifiant et du mot de passe saisis par l'utilisateur
            val identifiant = findViewById<EditText>(R.id.editText_Login).text.toString()
            val mdp = findViewById<EditText>(R.id.editText_Mdp).text.toString()
            // Envoi de la demande de connexion au serveur via une fonction dédiée
            sendLoginRequest(identifiant, mdp)
        }
    }

    // Fonction qui affiche une boîte de dialogue d'erreur en cas d'échec de connexion
    fun showErrorDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Erreur")
        builder.setMessage("Identifiant ou mot de passe incorrect")
        builder.setPositiveButton("OK") { dialog, which -> }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()
    }

    // Fonction qui envoie la demande de connexion au serveur
    fun sendLoginRequest(identifiant: String, mdp: String) {
        val client = OkHttpClient()
        // Construction de la requête POST avec les paramètres d'identifiant et de mot de passe
        val formBody = FormBody.Builder()
            .add("identifiant", identifiant)
            .add("mdp", mdp)
            .build()
        val request = Request.Builder()
            .url("http://172.16.7.236:8080/projet_leds/connexion.php")
            .post(formBody)
            .build()

        // Envoi de la demande de connexion au serveur
        client.newCall(request).enqueue(object : Callback {
            // Code pour traiter la réponse de l'API
            override fun onResponse(call: Call, response: Response) {
                // Récupération de la réponse du serveur
                val responseBody = response.body?.string()
                // Traitement de la réponse du serveur
                handleLoginResponse(responseBody)
            }

            // Gestion des erreurs en cas d'échec de la demande de connexion
            override fun onFailure(call: Call, e: IOException) {
                // Gestion des erreurs
                runOnUiThread {
                    val builder = AlertDialog.Builder(this@PremiereVue)
                    builder.setMessage("Problème de connexion")
                    builder.setPositiveButton("OK", null)
                    builder.show()
                }
            }
        })
    }

    //prend en paramètre une chaîne de caractères responseBody, qui est la réponse renvoyée par une API suite à une requête
    fun handleLoginResponse(responseBody: String?) {
        if (responseBody == null) {
            runOnUiThread {
                Toast.makeText(this, "Réponse invalide de l'API", Toast.LENGTH_SHORT).show()
            }
            return
        }

        val jsonObject = JSONObject(responseBody)
        if (jsonObject.has("success")) {
            val success = jsonObject.getInt("success")
            if (success == 1) {
                val intent = Intent(this@PremiereVue, SecondeVue::class.java)
                startActivity(intent)
            } else {
                runOnUiThread {
                    AlertDialog.Builder(this@PremiereVue)
                        .setTitle("Erreur")
                        .setMessage("Identifiant ou mot de passe incorrect")
                        .setPositiveButton("OK", null)
                        .show()
                }
            }
        } else {
            runOnUiThread {
                Toast.makeText(this, "Réponse invalide de l'API", Toast.LENGTH_SHORT).show()
            }
        }
    }
}