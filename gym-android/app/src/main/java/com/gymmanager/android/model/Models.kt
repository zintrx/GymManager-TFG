package com.gymmanager.android.model

import java.util.Date

data class LoginRequest(
    val username: String,
    val password: String
)

data class LoginResponse(
    val id: Long? = null,
    val success: Boolean,
    val message: String,
    val username: String?,
    val role: String?,
    val email: String?,
    val telefono: String?,
    val dni: String?,
    val avatarUrl: String?,
    val cuotaMensual: Double?,
    val clienteId: Long? = null
)

data class Publicacion(
    val id: Long? = null,
    val contenido: String,
    val autor: String,
    val fecha: String? = null,
    val likes: Int = 0,
    val usuarioId: Long? = null
)

data class Compra(
    val id: Long? = null,
    val producto: String,
    val precio: Double,
    val usuarioId: Long? = null,
    val fecha: String? = null
)

data class CompraRequest(
    val producto: String,
    val precio: Double,
    val usuarioId: Long
)

data class UpdateUserRequest(
    val username: String,
    val email: String? = null,
    val telefono: String? = null,
    val dni: String? = null,
    val password: String? = null,
    val avatarUrl: String? = null,
    val role: String? = null
)

data class Cliente(
    val id: Long? = null,
    val nombre: String,
    val apellidos: String,
    val dni: String,
    val telefono: String?,
    val email: String?,
    val estado: String? = "ACTIVO"
)

data class Pago(
    val id: Long? = null,
    val cliente: Cliente,
    val concepto: String,
    val monto: Double,
    val fechaPago: Date?
)

data class Rutina(
    val id: Long? = null,
    val nombreRutina: String,
    val descripcion: String,
    val fechaAsignacion: Date?
)

data class Ejercicio(
    val id: Long? = null,
    val nombre: String,
    val series: Int,
    val repeticiones: Int,
    val peso: Double
)

data class Actividad(
    val id: Long? = null,
    val titulo: String,
    val descripcion: String?,
    val fechaHora: String?,
    val sala: String?,
    val instructor: String?,
    val cupoMaximo: Int = 20,
    val cupoActual: Int = 0
)

data class Reserva(
    val id: Long? = null,
    val actividad: Actividad,
    val fechaReserva: String? = null
)

data class HistorialProgreso(
    val id: Long? = null,
    val usuario: LoginResponse? = null, // Simplified for now
    val ejercicio: Ejercicio? = null,
    val seriesCompletadas: Int,
    val repeticionesRealizadas: Int,
    val pesoUtilizado: Double,
    val notas: String? = null,
    val fechaRegistro: String? = null
)
