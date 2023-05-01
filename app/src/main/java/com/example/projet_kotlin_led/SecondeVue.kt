package com.example.projet_kotlin_led

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*
import java.nio.charset.Charset

class SecondeVue : AppCompatActivity() {

    private val TAG = "SecondeVue"
    private val serverUri = "tcp://172.16.5.202:1883"
    private val clientId = "pi"
    private val topic = "LaPorte/LEDs"
    private val qos = 2 // Quality of Service

    lateinit var client: MqttAndroidClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seconde_vue)

        val btnDeconnexion = findViewById<Button>(R.id.btn_Deconnexion)
        btnDeconnexion.setOnClickListener {
            val Intent = Intent(this, PremiereVue::class.java)
            startActivity(Intent)
        }

        val btnParametre = findViewById<Button>(R.id.btn_Parametres)
        btnParametre.setOnClickListener {
            val Intent = Intent(this, TroisiemeVue::class.java)
            startActivity(Intent)
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
    }
}