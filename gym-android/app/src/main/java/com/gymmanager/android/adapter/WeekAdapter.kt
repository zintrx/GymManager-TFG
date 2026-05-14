package com.gymmanager.android.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gymmanager.android.R
import com.gymmanager.android.databinding.ItemDayBinding
import java.util.*

data class DayItem(val date: Date, var isSelected: Boolean = false)

class WeekAdapter(
    private val days: List<DayItem>,
    private val onDaySelected: (DayItem) -> Unit
) : RecyclerView.Adapter<WeekAdapter.DayViewHolder>() {

    inner class DayViewHolder(val binding: ItemDayBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        val binding = ItemDayBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DayViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        val day = days[position]
        val calendar = Calendar.getInstance()
        calendar.time = day.date
        
        val dayNames = arrayOf("DO", "LU", "MA", "MI", "JU", "VI", "SA")
        holder.binding.tvDayName.text = dayNames[calendar.get(Calendar.DAY_OF_WEEK) - 1]
        holder.binding.tvDayNum.text = calendar.get(Calendar.DAY_OF_MONTH).toString()

        if (day.isSelected) {
            holder.binding.dayContainer.setBackgroundResource(R.drawable.bg_btn_reserve)
            holder.binding.tvDayName.setTextColor(Color.BLACK)
            holder.binding.tvDayNum.setTextColor(Color.BLACK)
        } else {
            holder.binding.dayContainer.setBackgroundResource(0)
            holder.binding.tvDayName.setTextColor(Color.parseColor("#94A3B8"))
            holder.binding.tvDayNum.setTextColor(Color.WHITE)
        }

        holder.binding.root.setOnClickListener {
            days.forEach { it.isSelected = false }
            day.isSelected = true
            notifyDataSetChanged()
            onDaySelected(day)
        }
    }

    override fun getItemCount() = days.size
}
