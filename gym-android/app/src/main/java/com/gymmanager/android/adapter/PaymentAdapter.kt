package com.gymmanager.android.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gymmanager.android.R
import com.gymmanager.android.model.Pago
import java.text.SimpleDateFormat
import java.util.*

class PaymentAdapter(private val payments: List<Pago>) : RecyclerView.Adapter<PaymentAdapter.PaymentViewHolder>() {

    class PaymentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvClient: TextView = view.findViewById(R.id.tvPaymentClient)
        val tvConcept: TextView = view.findViewById(R.id.tvPaymentConcept)
        val tvAmount: TextView = view.findViewById(R.id.tvPaymentAmount)
        val tvDate: TextView = view.findViewById(R.id.tvPaymentDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_payment, parent, false)
        return PaymentViewHolder(view)
    }

    override fun onBindViewHolder(holder: PaymentViewHolder, position: Int) {
        val payment = payments[position]
        holder.tvClient.text = "${payment.cliente.nombre} ${payment.cliente.apellidos}"
        holder.tvConcept.text = payment.concepto
        holder.tvAmount.text = "${payment.monto}€"
        
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        holder.tvDate.text = if (payment.fechaPago != null) sdf.format(payment.fechaPago) else "N/A"
    }

    override fun getItemCount() = payments.size
}
