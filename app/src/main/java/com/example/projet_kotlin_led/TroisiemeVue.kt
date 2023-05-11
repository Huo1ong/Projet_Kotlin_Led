/**
@file TroisiemeVue.kt
@author Guillou Quentin
@version du 30/04/23
 */

package com.example.projet_kotlin_led

import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.widget.Button
import android.widget.TextView
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttMessage
import java.util.*

class TroisiemeVue : AppCompatActivity() {

    private val TAG = "SecondeVue"
    private val serverUri = "tcp://172.16.5.202:1883"
    private val clientId = "pi"
    private val topic = "LaPorte/LEDs"
    private val qos = 2 // Quality of Service

    lateinit var mqttClient: MqttAndroidClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_troisieme_vue)

        val btnAnnuler = findViewById<Button>(R.id.btn_Annuler)
        btnAnnuler.setOnClickListener {
            val Intent = Intent(this, SecondeVue::class.java)
            startActivity(Intent)
        }

        // Create an instance of MqttAndroidClient
        mqttClient = MqttAndroidClient(this, serverUri, clientId)

        // Set the callback for receiving messages
        mqttClient.setCallback(object : MqttCallbackExtended {
            override fun connectComplete(reconnect: Boolean, serverURI: String?) {
                // Subscribe to the topic when the connection is established
                mqttClient.subscribe(topic, 0)
            }

            override fun messageArrived(topic: String?, message: MqttMessage?) {
                // Handle the received message
                if (topic == topic && message != null) {
                    val payload = String(message.payload)
                    Log.d("MQTT", "Received message: $payload")
                    runOnUiThread {
                        val textView = findViewById<TextView>(R.id.textView_message)
                        textView.text = payload
                    }
                }
            }

            override fun connectionLost(cause: Throwable?) {
                // Handle the connection loss
            }

            override fun deliveryComplete(token: IMqttDeliveryToken?) {
                // Handle the message delivery completion
            }
        })

        val btnNomTopic = findViewById<Button>(R.id.btn_Nom_Topic)
        btnNomTopic.setOnClickListener {
            val message = "Nom du Topic : " + topic
            mqttClient.publish(topic, message.toByteArray(), qos, false)
        }

        // Connect to the broker
        mqttClient.connect()
    }
}