package com.example.projet_kotlin_led

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class TroisiemeVue : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_troisieme_vue)

        val btnAnnuler = findViewById<Button>(R.id.btn_Annuler)
        btnAnnuler.setOnClickListener {
            val Intent = Intent(this, SecondeVue::class.java)
            startActivity(Intent)
        }
    }
}