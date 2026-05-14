package com.gymmanager.android.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gymmanager.android.R
import com.gymmanager.android.model.Ejercicio

class ExercisesAdapter(
    private val exercises: List<Ejercicio>,
    private val onClick: (Ejercicio) -> Unit
) : RecyclerView.Adapter<ExercisesAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvExName)
        val tvTarget: TextView = view.findViewById(R.id.tvExTarget)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_exercise, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ex = exercises[position]
        holder.tvName.text = ex.nombre
        holder.tvTarget.text = "${ex.series} series x ${ex.repeticiones} reps | ${ex.peso}kg"
        holder.itemView.setOnClickListener { onClick(ex) }
    }

    override fun getItemCount() = exercises.size
}
