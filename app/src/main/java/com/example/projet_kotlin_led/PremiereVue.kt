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

        val buttonConnexion = findViewById<Button>(R.id.btn_Valider_Connexion)
        buttonConnexion.setOnClickListener {
            val identifiant = findViewById<EditText>(R.id.editText_Login).text.toString()
            val mdp = findViewById<EditText>(R.id.editText_Mdp).text.toString()
            sendLoginRequest(identifiant, mdp)
        }
    }

    fun showErrorDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Erreur")
        builder.setMessage("Identifiant ou mot de passe incorrect")
        builder.setPositiveButton("OK") { dialog, which -> }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()
    }

    fun sendLoginRequest(identifiant: String, mdp: String) {
        val client = OkHttpClient()
        val formBody = FormBody.Builder()
            .add("identifiant", identifiant)
            .add("mdp", mdp)
            .build()
        val request = Request.Builder()
            .url("http://192.168.107.172:8080/projet_leds/connexion.php")
            .post(formBody)
            .build()

        Log.d("DEBUG1", "Sending login request with identifiant=$identifiant, mdp=$mdp")
        client.newCall(request).enqueue(object : Callback {
            // Code pour traiter la réponse de l'API
            override fun onResponse(call: Call, response: Response) {
                Log.d("DEBUG2", "Received response: ${response.code}")
                val responseBody = response.body?.string()
                Log.d("DEBUG3", "Response body: $responseBody")
                handleLoginResponse(responseBody)
            }

            override fun onFailure(call: Call, e: IOException) {
                // Gestion des erreurs
                Log.e("ERROR1", "Login request failed", e)
                runOnUiThread {
                    val builder = AlertDialog.Builder(this@PremiereVue)
                    builder.setMessage("Problème de connexion")
                    builder.setPositiveButton("OK", null)
                    builder.show()
                }
            }
        })
    }

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