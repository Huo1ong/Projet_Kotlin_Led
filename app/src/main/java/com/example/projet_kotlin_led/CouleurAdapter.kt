package com.example.projet_kotlin_led

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CouleurAdapter (var couleurs:ArrayList<Couleur>) : RecyclerView.Adapter<CouleurAdapter.MyViewHolder>()
{
    class MyViewHolder (var vue:  View):RecyclerView.ViewHolder(vue)
    {
        var id = vue.findViewById<TextView>(R.id.tvId)
        var couleurs = vue.findViewById<TextView>(R.id.tvCouleurs)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        var vue = LayoutInflater.from(parent.context).inflate(R.layout.item_article, parent, false)
        return MyViewHolder(vue)
    }

    override fun getItemCount(): Int {
        return couleurs.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var couleur = couleurs.get(position)
        holder.id.setText(couleur.id)
        holder.couleurs.setText(couleur.couleurs)
    }
}