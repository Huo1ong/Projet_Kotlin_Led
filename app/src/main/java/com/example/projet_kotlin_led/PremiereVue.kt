/**
    @file PremiereVue.kt
    @author Guillou Quentin
    @version du 30/04/23
 */

package com.example.projet_kotlin_led

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import androidx.appcompat.app.AlertDialog

class PremiereVue : AppCompatActivity() {

    private var selectedLanguage: String = "fr" // Par défaut, le français est sélectionné

    private fun saveSelectedLanguage() {
        val sharedPref = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putString("selectedLanguage", selectedLanguage)
        editor.commit() // Utilisez commit() au lieu de apply()
    }

    private fun updateButtonHints() {
        //Textes
        val txtViewTitre1 = findViewById<TextView>(R.id.textView_Titre1)
        val txtViewTitre2 = findViewById<TextView>(R.id.textView_Titre2)
        val txtViewLogin = findViewById<TextView>(R.id.textView_Login)
        val textViewMdp = findViewById<TextView>(R.id.textView_Mdp)

        //EditText
        val editTextLogin = findViewById<EditText>(R.id.editText_Login)
        val editTextMdp = findViewById<EditText>(R.id.editText_Mdp)

        //Boutons
        val btnValiderConnexion = findViewById<Button>(R.id.btn_Valider_Connexion)
        val btnPageSuivante = findViewById<Button>(R.id.btn_PageSuivante)


        if (selectedLanguage == "en")
        {
            //Textes
            txtViewTitre1.hint = resources.getString(R.string.titre_en)
            txtViewTitre2.hint = resources.getString(R.string.sous_titre_en)
            txtViewLogin.hint = resources.getString(R.string.login_en)
            textViewMdp.hint = resources.getString(R.string.mdp_en)

            //EditText
            editTextLogin.hint = resources.getString(R.string.login_en)
            editTextMdp.hint = resources.getString(R.string.mdp_en)

            //Boutons
            btnValiderConnexion.hint = resources.getString(R.string.btn_Valider_en)
            btnPageSuivante.hint = resources.getString(R.string.btn_PageSuivante_en)
        }
        else if (selectedLanguage == "es")
        {
            //Textes
            txtViewTitre1.hint = resources.getString(R.string.titre_es)
            txtViewTitre2.hint = resources.getString(R.string.sous_titre_es)
            txtViewLogin.hint = resources.getString(R.string.login_es)
            textViewMdp.hint = resources.getString(R.string.mdp_es)

            //EditText
            editTextLogin.hint = resources.getString(R.string.login_es)
            editTextMdp.hint = resources.getString(R.string.mdp_es)

            //Boutons
            btnValiderConnexion.hint = resources.getString(R.string.btn_Valider_es)
            btnPageSuivante.hint = resources.getString(R.string.btn_PageSuivante_es)
        }
        else
        {
            //Textes
            txtViewTitre1.hint = resources.getString(R.string.titre)
            txtViewTitre2.hint = resources.getString(R.string.sous_titre)
            txtViewLogin.hint = resources.getString(R.string.login)
            textViewMdp.hint = resources.getString(R.string.mdp)

            //EditText
            editTextLogin.hint = resources.getString(R.string.login)
            editTextMdp.hint = resources.getString(R.string.mdp)

            //Boutons
            btnValiderConnexion.hint = resources.getString(R.string.btn_Valider)
            btnPageSuivante.hint = resources.getString(R.string.btn_PageSuivante)
        }

        // Redessinez manuellement les vues pour refléter les changements de langue
        redrawViews()
        // Mettez à jour les "hints" des autres boutons de la même manière
    }

    private fun redrawViews() {
        val rootView: View = findViewById(android.R.id.content)
        rootView.invalidate()
        rootView.requestLayout()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_premiere_vue)

        val btnPageSuivante = findViewById<Button>(R.id.btn_PageSuivante)
        btnPageSuivante.setOnClickListener {
            val intent = Intent(this@PremiereVue, SecondeVue::class.java)
            intent.putExtra("selectedLanguage", selectedLanguage)
            startActivityForResult(intent, 1)
        }

        // Initialisation du bouton de connexion et ajout d'un listener sur le clic du bouton
        val buttonConnexion = findViewById<Button>(R.id.btn_Valider_Connexion)
        buttonConnexion.setOnClickListener {
            // Récupération de l'identifiant et du mot de passe saisis par l'utilisateur
            val identifiant = findViewById<EditText>(R.id.editText_Login).text.toString()
            val mdp = findViewById<EditText>(R.id.editText_Mdp).text.toString()
            // Envoi de la demande de connexion au serveur via une fonction dédiée
            sendLoginRequest(identifiant, mdp)
        }

        updateButtonHints()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1 && resultCode == RESULT_OK) {
            val selectedLanguage = data?.getStringExtra("selectedLanguage")
            if (selectedLanguage != null && selectedLanguage != this.selectedLanguage) {
                this.selectedLanguage = selectedLanguage
                updateButtonHints()
            }
        }
        saveSelectedLanguage() // Enregistrez la langue sélectionnée avant de quitter l'activité
    }

    override fun onResume() {
        super.onResume()
        val selectedLanguage = intent.getStringExtra("selectedLanguage")
        if (selectedLanguage != null && selectedLanguage != this.selectedLanguage) {
            this.selectedLanguage = selectedLanguage
            updateButtonHints()
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