package com.gymmanager.android.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gymmanager.android.R
import com.gymmanager.android.model.Cliente

class ClientAdapter(private val clients: List<Cliente>) : RecyclerView.Adapter<ClientAdapter.ClientViewHolder>() {

    class ClientViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvClientName)
        val tvDni: TextView = view.findViewById(R.id.tvClientDni)
        val tvStatus: TextView = view.findViewById(R.id.tvStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClientViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_client, parent, false)
        return ClientViewHolder(view)
    }

    override fun onBindViewHolder(holder: ClientViewHolder, position: Int) {
        val client = clients[position]
        holder.tvName.text = "${client.nombre} ${client.apellidos}"
        holder.tvDni.text = "DNI: ${client.dni}"
        holder.tvStatus.text = client.estado
    }

    override fun getItemCount() = clients.size
}
