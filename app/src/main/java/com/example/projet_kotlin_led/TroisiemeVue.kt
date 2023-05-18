/**
@file TroisiemeVue.kt
@author Guillou Quentin
@version du 30/04/23
 */

package com.example.projet_kotlin_led

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.widget.*
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttMessage
import java.util.*

class TroisiemeVue : AppCompatActivity() {

    private val serverUri = "tcp://172.16.5.202:1883"
    private val clientId = "pi"
    private val topic = "LaPorte/LEDs"
    private val qos = 2 // Quality of Service

    private var selectedLanguage: String = "fr" // Par défaut, le français est sélectionné

    private fun redrawViews() {
        val rootView: View = findViewById(android.R.id.content)
        rootView.invalidate()
        rootView.requestLayout()
    }

    lateinit var mqttClient: MqttAndroidClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_troisieme_vue)

        selectedLanguage = intent.getStringExtra("selectedLanguage") ?: selectedLanguage

        val btnAnnuler = findViewById<Button>(R.id.btn_Annuler)
        btnAnnuler.setOnClickListener {
            val intent = Intent(this@TroisiemeVue, SecondeVue::class.java)
            intent.putExtra("selectedLanguage", selectedLanguage)
            startActivityForResult(intent, 1)
        }

        val spinner = findViewById<Spinner>(R.id.spinner)
        val options = resources.getStringArray(R.array.spinner_options)

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, options)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        val languageIndex = when (selectedLanguage) {
            "fr" -> 0
            "en" -> 1
            "es" -> 2
            "ja" -> 3
            else -> 0
        }
        spinner.setSelection(languageIndex)

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val newLanguage = when (position) {
                    0 -> "fr" // Français
                    1 -> "en" // Anglais
                    2 -> "es" // Espagnol
                    3 -> "ja" // Japonais
                    else -> "fr" // Par défaut, sélectionnez le français
                }

                if (newLanguage != selectedLanguage) {
                    selectedLanguage = newLanguage
                    updateTextsLanguage(selectedLanguage)

                    val intent = Intent()
                    intent.putExtra("selectedLanguage", selectedLanguage) // Ajoutez la langue sélectionnée à l'intent
                    setResult(RESULT_OK, intent)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Gérer l'absence de sélection
            }
        }

        // Restaurer la langue sélectionnée lors du retour à cette vue
        updateTextsLanguage(selectedLanguage)

        // Create an instance of MqttAndroidClient
        mqttClient = MqttAndroidClient(this, serverUri, clientId)

        if (!mqttClient.isConnected) {
            mqttClient.connect()
        }

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

    private fun updateTextsLanguage(language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)

        val resources: Resources = applicationContext.resources
        val configuration: Configuration = resources.configuration
        configuration.locale = locale
        resources.updateConfiguration(configuration, resources.displayMetrics)

        //Textes
        val txtViewTitre4 = findViewById<TextView>(R.id.textView_Titre4)
        val txtViewTitre5 = findViewById<TextView>(R.id.textView_Titre5)

        //Boutons
        val btnAnnuler = findViewById<Button>(R.id.btn_Annuler)
        val btnNomTopic = findViewById<Button>(R.id.btn_Nom_Topic)

        if (selectedLanguage == "en") {
            //Textes
            txtViewTitre4.hint = resources.getString(R.string.titre_en)
            txtViewTitre5.hint = resources.getString(R.string.sous_titre_en)

            //Boutons
            btnAnnuler.hint = resources.getString(R.string.btn_Annuler_en)
            btnNomTopic.hint = resources.getString(R.string.btn_Nom_Topic_en)
        }
        else if(selectedLanguage == "es") {
            //Textes
            txtViewTitre4.hint = resources.getString(R.string.titre_es)
            txtViewTitre5.hint = resources.getString(R.string.sous_titre_es)

            //Boutons
            btnAnnuler.hint = resources.getString(R.string.btn_Annuler_es)
            btnNomTopic.hint = resources.getString(R.string.btn_Nom_Topic_es)
        }
        else if(selectedLanguage == "ja") {
            //Textes
            txtViewTitre4.hint = resources.getString(R.string.titre_ja)
            txtViewTitre5.hint = resources.getString(R.string.sous_titre_ja)

            //Boutons
            btnAnnuler.hint = resources.getString(R.string.btn_Annuler_ja)
            btnNomTopic.hint = resources.getString(R.string.btn_Nom_Topic_ja)
        }
        else {
            //Textes
            txtViewTitre4.hint = resources.getString(R.string.titre)
            txtViewTitre5.hint = resources.getString(R.string.sous_titre)

            //Boutons
            btnAnnuler.hint = resources.getString(R.string.btn_Annuler)
            btnNomTopic.hint = resources.getString(R.string.btn_Nom_Topic)
        }

        // Redessinez manuellement les vues pour refléter les changements de langue
        redrawViews()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1 && resultCode == RESULT_OK) {
            val selectedLanguage = data?.getStringExtra("selectedLanguage")
            if (selectedLanguage != null && selectedLanguage != this.selectedLanguage) {
                this.selectedLanguage = selectedLanguage
                updateTextsLanguage(selectedLanguage)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mqttClient.isConnected) {
            mqttClient.disconnect()
        }
        mqttClient.close()
    }
}