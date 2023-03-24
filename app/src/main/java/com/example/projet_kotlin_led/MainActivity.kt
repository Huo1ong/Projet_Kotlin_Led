package com.example.projet_kotlin_led

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray

class MainActivity : AppCompatActivity() {

    private var adpater:CouleurAdapter ?= null
    private var couleurs:ArrayList<Couleur> ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        couleurs = ArrayList()
        getCouleursFromServer()
        adpater = CouleurAdapter(couleurs!!)

        var recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(this)

        recyclerView.adapter = adpater
    }

    fun getCouleursFromServer()
    {
        var url = "http://localhost:8080/Node_API/read.php"
        var stringRequest = StringRequest(Request.Method.GET, url,
            Response.Listener { response -> },
            Response.ErrorListener { error ->
                Toast.makeText(this, "Erreur de connexion au serveur.", Toast.LENGTH_SHORT).show()
            })
        var req = Volley.newRequestQueue(this)
        req.add(stringRequest)
    }

    fun parseData(response:String)
    {
        var arrayJson = JSONArray(response)

        for(i in 0..arrayJson.length()-1) {
            var currentObject = arrayJson.getJSONObject(i)

            var couleur = Couleur(currentObject.getInt("id"),
            currentObject.getString("couleurs"))

            couleurs?.add(couleur)
        }
    }
}