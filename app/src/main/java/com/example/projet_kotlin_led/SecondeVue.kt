package com.example.projet_kotlin_led
/**
@file SecondeVue.kt
@author Guillou Quentin
@version du 30/04/23
 */


import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*
import java.nio.charset.Charset

class SecondeVue : AppCompatActivity() {

    private val TAG = "SecondeVue"
    private val serverUri = "tcp://172.16.5.202:1883"
    private val clientId = "pi"
    private val topic = "LaPorte/LEDs"
    private val qos = 2 // Quality of Service

    private var selectedLanguage: String = "fr" // Par défaut, le français est sélectionné

    private fun saveSelectedLanguage() {
        val sharedPref = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putString("selectedLanguage", selectedLanguage)
        editor.commit() // Utilisez commit() au lieu de apply()
    }

    lateinit var client: MqttAndroidClient

    private fun updateButtonHints() {
        //Textes
        val txtViewTitre1 = findViewById<TextView>(R.id.textView_Titre1)
        val txtViewTitre2 = findViewById<TextView>(R.id.textView_Titre2)
        val txtViewCouleur = findViewById<TextView>(R.id.textView_Couleur)

        //Boutons
        val btnCycleCouleur1 = findViewById<Button>(R.id.btn_Cycle_Couleur1)
        val btnCycleCouleur2 = findViewById<Button>(R.id.btn_Cycle_Couleur2)
        val btnCycleCouleur3 = findViewById<Button>(R.id.btn_Cycle_Couleur3)
        val btnCycleCouleur4 = findViewById<Button>(R.id.btn_Cycle_Couleur4)
        val btnDeconnexion = findViewById<Button>(R.id.btn_Deconnexion)
        val btnParametres = findViewById<Button>(R.id.btn_Parametres)

        if (selectedLanguage == "en")
        {
            //Textes
            txtViewTitre1.hint = resources.getString(R.string.titre_en)
            txtViewTitre2.hint = resources.getString(R.string.sous_titre_en)
            txtViewCouleur.hint = resources.getString(R.string.couleur_en)

            //Boutons
            btnCycleCouleur1.hint = resources.getString(R.string.btn_Cycle_Couleur1_en)
            btnCycleCouleur2.hint = resources.getString(R.string.btn_Cycle_Couleur2_en)
            btnCycleCouleur3.hint = resources.getString(R.string.btn_Cycle_Couleur3_en)
            btnCycleCouleur4.hint = resources.getString(R.string.btn_Cycle_Couleur4_en)
            btnDeconnexion.hint = resources.getString(R.string.btn_Logout_en)
            btnParametres.hint = resources.getString(R.string.btn_Paramètres_en)
        }
        else
        {
            //Textes
            txtViewTitre1.hint = resources.getString(R.string.titre)
            txtViewTitre2.hint = resources.getString(R.string.sous_titre)
            txtViewCouleur.hint = resources.getString(R.string.couleur)

            //Boutons
            btnCycleCouleur1.hint = resources.getString(R.string.btn_Cycle_Couleur1)
            btnCycleCouleur2.hint = resources.getString(R.string.btn_Cycle_Couleur2)
            btnCycleCouleur3.hint = resources.getString(R.string.btn_Cycle_Couleur3)
            btnCycleCouleur4.hint = resources.getString(R.string.btn_Cycle_Couleur4)
            btnDeconnexion.hint = resources.getString(R.string.btn_Logout)
            btnParametres.hint = resources.getString(R.string.btn_Paramètres)
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
        setContentView(R.layout.activity_seconde_vue)

        val btnDeconnexion = findViewById<Button>(R.id.btn_Deconnexion)
        btnDeconnexion.setOnClickListener {
            val intent = Intent(this@SecondeVue, PremiereVue::class.java)
            intent.putExtra("selectedLanguage", selectedLanguage)
            startActivityForResult(intent, 1)
        }

        val btnParametres = findViewById<Button>(R.id.btn_Parametres)
        btnParametres.setOnClickListener {
            val intent = Intent(this@SecondeVue, TroisiemeVue::class.java)
            intent.putExtra("selectedLanguage", selectedLanguage)
            startActivityForResult(intent, 1)
        }

        // Création du client MQTT
        client = MqttAndroidClient(this, serverUri, clientId)

        // Configuration des options de connexion
        val options = MqttConnectOptions()
        options.isCleanSession = true
        options.keepAliveInterval = 60
        options.connectionTimeout = 30

        // Configuration du callback pour la réception des messages
        client.setCallback(object : MqttCallbackExtended {
            override fun connectComplete(reconnect: Boolean, serverURI: String?) {
                // Connecté au broker
                Log.d(TAG, "MQTT Connection : Complete")
            }

            override fun connectionLost(cause: Throwable?) {
                // Connexion perdue
                Log.d(TAG, "MQTT Connection : Lost")
            }

            override fun messageArrived(topic: String?, message: MqttMessage?) {
                // Message reçu
                val payload = message?.payload
                // Faire quelque chose avec le message reçu
                Log.d(TAG, "MQTT Message: " + message?.toString())
            }

            override fun deliveryComplete(token: IMqttDeliveryToken?) {
                // Message délivré
                Log.d(TAG, "MQTT Delivery")
            }
        })

        // Connexion au broker
        client.connect(options, null, object : IMqttActionListener {
            override fun onSuccess(asyncActionToken: IMqttToken?) {
                // Connexion réussie
                Log.d(TAG, "MQTT Connection : Reussie")

                // Abonnement au topic spécifié
                client.subscribe(topic, qos)
            }

            override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                // Connexion échouée
                Log.d(TAG, "MQTT Connection : Failed")
                exception?.printStackTrace()
            }
        })

        val btnCycleCouleur1 = findViewById<Button>(R.id.btn_Cycle_Couleur1)
        btnCycleCouleur1.setOnClickListener {
            val message = "Bouton Cycle Couleur 1 - Bleu_Blanc_Rouge"
            client.publish(topic, message.toByteArray(), qos, false)
        }

        val btnCycleCouleur2 = findViewById<Button>(R.id.btn_Cycle_Couleur2)
        btnCycleCouleur2.setOnClickListener {
            val message = "Bouton Cycle Couleur 2 - Vert_Blanc_Rouge"
            client.publish(topic, message.toByteArray(), qos, false)
        }

        val btnCycleCouleur3 = findViewById<Button>(R.id.btn_Cycle_Couleur3)
        btnCycleCouleur3.setOnClickListener {
            val message = "Bouton Cycle Couleur 3 - Rouge_Bleu_Violet"
            client.publish(topic, message.toByteArray(), qos, false)
        }

        val btnCycleCouleur4 = findViewById<Button>(R.id.btn_Cycle_Couleur4)
        btnCycleCouleur4.setOnClickListener {
            val message = "Bouton Cycle Couleur 4 - Violet_Bleu_Vert_Jaune"
            client.publish(topic, message.toByteArray(), qos, false)
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
}