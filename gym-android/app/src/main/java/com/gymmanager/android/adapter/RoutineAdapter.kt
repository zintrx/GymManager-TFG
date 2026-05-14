package com.gymmanager.android.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gymmanager.android.R
import com.gymmanager.android.model.Rutina
import java.text.SimpleDateFormat
import java.util.*

class RoutineAdapter(
    private val routines: List<Rutina>,
    private val onDelete: (Rutina) -> Unit,
    private val onClick: (Rutina) -> Unit
) : RecyclerView.Adapter<RoutineAdapter.RoutineViewHolder>() {

    class RoutineViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvRoutineName)
        val tvDesc: TextView = view.findViewById(R.id.tvRoutineDesc)
        val tvDate: TextView = view.findViewById(R.id.tvRoutineDate)
        val btnDelete: ImageButton = view.findViewById(R.id.btnDeleteRoutine)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoutineViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_routine, parent, false)
        return RoutineViewHolder(view)
    }

    override fun onBindViewHolder(holder: RoutineViewHolder, position: Int) {
        val routine = routines[position]
        holder.tvName.text = routine.nombreRutina
        holder.tvDesc.text = routine.descripcion
        
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        holder.tvDate.text = "Asignada: ${if (routine.fechaAsignacion != null) sdf.format(routine.fechaAsignacion) else "N/A"}"
        
        holder.btnDelete.setOnClickListener { onDelete(routine) }
        holder.itemView.setOnClickListener { onClick(routine) }
    }

    override fun getItemCount() = routines.size
}
