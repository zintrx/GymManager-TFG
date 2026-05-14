package com.gymmanager.android.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gymmanager.android.R
import com.gymmanager.android.model.Compra

class ShopAdapter(private val compras: List<Compra>) : RecyclerView.Adapter<ShopAdapter.ShopViewHolder>() {

    class ShopViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvProduct: TextView = view.findViewById(R.id.tvShopProduct)
        val tvDate: TextView = view.findViewById(R.id.tvShopDate)
        val tvPrice: TextView = view.findViewById(R.id.tvShopPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShopViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_shop, parent, false)
        return ShopViewHolder(view)
    }

    override fun onBindViewHolder(holder: ShopViewHolder, position: Int) {
        val compra = compras[position]
        holder.tvProduct.text = compra.producto
        holder.tvPrice.text = "${compra.precio}€"
        
        // Format date: 2026-04-30T13:32:25 -> 30 Abr
        holder.tvDate.text = compra.fecha?.split("T")?.get(0) ?: "Recién"
    }

    override fun getItemCount() = compras.size
}
