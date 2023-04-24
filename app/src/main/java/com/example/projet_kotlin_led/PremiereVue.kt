package com.example.projet_kotlin_led

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class PremiereVue : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_premiere_vue)

        val btnValiderConnexion = findViewById<Button>(R.id.btn_Valider_Connexion)
        btnValiderConnexion.setOnClickListener {
            val Intent = Intent(this, SecondeVue::class.java)
            startActivity(Intent)
        }
    }
}