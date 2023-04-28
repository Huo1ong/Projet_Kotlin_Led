package com.example.projet_kotlin_led

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*


class SecondeVue : AppCompatActivity() {
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

        val serverUri = "tcp://172.16.5.202:1883"
        val clientId = "pi"
        val topic = "LaPorte/LEDs"
        val qos = 2 // Quality of Service
        val textTest = findViewById<TextView>(R.id.textTest)


        // Créer un client MQTT
        try {
            val client = MqttAndroidClient(this, serverUri, clientId)
            // Configurer une callback pour la réception des messages
            val options = MqttConnectOptions()
            options.isCleanSession = true
            options.keepAliveInterval = 60
            options.connectionTimeout = 30
            options.isCleanSession = true
            client.setCallback(object : MqttCallbackExtended {
                override fun connectComplete(reconnect: Boolean, serverURI: String?) {
                    // Connecté au broker

                    client.subscribe(topic, qos)
                    println("MQTT Connection : Complete")
                }

                override fun connectionLost(cause: Throwable?) {
                    // Connexion perdue
                    println("MQTT Connection : Lost")
                }

                override fun messageArrived(topic: String?, message: MqttMessage?) {
                    // Message reçu
                    val payload = message?.payload
                    // Faire quelque chose avec le message reçu
                    textTest.text = message?.toString()
                    println("MQTT Message: " + message?.toString())

                }

                override fun deliveryComplete(token: IMqttDeliveryToken?) {
                    // Message délivré
                    println("MQTT Delivery")
                }
            })
            client.connect(options, this, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    // Connexion réussie
                    println("MQTT Connection : Reussie")
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    // Connexion échouée
                    println("MQTT Connection : Failed")
                    exception?.printStackTrace()
                }
            })

            // Connecter au broker
        } catch (ex: MqttException) {
            // Gérer l'exception et imprimer le code d'erreur
            println("MQTT Connection : Erreur de connexion au broker MQTT: ${ex.reasonCode}")
        }
    }
}