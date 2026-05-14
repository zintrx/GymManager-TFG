package com.gymmanager.android.adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gymmanager.android.databinding.ItemClaseBinding
import com.gymmanager.android.model.Actividad

class ClasesAdapter(
    private var classes: List<Actividad>,
    private var reservedIds: Set<Long> = emptySet(),
    private val onReserveClick: (Actividad, Boolean) -> Unit
) : RecyclerView.Adapter<ClasesAdapter.ClaseViewHolder>() {

    inner class ClaseViewHolder(val binding: ItemClaseBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClaseViewHolder {
        val binding = ItemClaseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ClaseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ClaseViewHolder, position: Int) {
        val cls = classes[position]
        val isReserved = reservedIds.contains(cls.id)
        
        with(holder.binding) {
            tvEmoji.text = "🏋️" // Default emoji for now
            tvTitle.text = cls.titulo
            tvTime.text = cls.fechaHora?.substringAfter("T")?.substring(0, 5) ?: "00:00"
            tvInstructor.text = cls.instructor ?: "Instructor"
            tvAforoLabel.text = "${cls.cupoActual}/${cls.cupoMaximo}"
            
            val pct = if (cls.cupoMaximo > 0) (cls.cupoActual.toFloat() / cls.cupoMaximo * 100).toInt() else 0
            pbAforo.progress = pct
            
            if (isReserved) {
                btnReserve.text = "Cancelar"
                btnReserve.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#33ef4444"))
                btnReserve.setTextColor(Color.parseColor("#ef4444"))
            } else {
                btnReserve.text = "Reservar"
                btnReserve.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#BBFF00"))
                btnReserve.setTextColor(Color.BLACK)
            }

            btnReserve.setOnClickListener { onReserveClick(cls, isReserved) }
        }
    }

    override fun getItemCount() = classes.size

    fun updateData(newClasses: List<Actividad>, newReservedIds: Set<Long>) {
        classes = newClasses
        reservedIds = newReservedIds
        notifyDataSetChanged()
    }
}
