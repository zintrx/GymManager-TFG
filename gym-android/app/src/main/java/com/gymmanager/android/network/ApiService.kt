package com.gymmanager.android.network

import com.gymmanager.android.model.*
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @POST("api/auth/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @POST("api/auth/register")
    fun register(@Body request: RegisterRequest): Call<LoginResponse>

    @retrofit2.http.PUT("api/auth/update")
    fun updateProfile(@Body request: UpdateUserRequest): Call<LoginResponse>

    @GET("api/clientes")
    fun getClientes(): Call<List<Cliente>>
    
    @POST("api/clientes")
    fun createCliente(@Body cliente: Cliente): Call<Cliente>

    @GET("api/pagos")
    fun getPagos(): Call<List<Pago>>

    @GET("api/rutinas/cliente/{clienteId}")
    fun getRutinasByCliente(@Path("clienteId") clienteId: Long): Call<List<Rutina>>

    @GET("api/pagos/status/{username}")
    fun getPaymentStatus(@Path("username") username: String): Call<Map<String, Any>>

    @GET("api/rutinas/usuario/{username}")
    fun getRutinasByUsername(@Path("username") username: String): Call<List<Rutina>>

    @GET("api/ejercicios/rutina/{rutinaId}")
    fun getEjerciciosByRutina(@Path("rutinaId") rutinaId: Long): Call<List<Ejercicio>>

    // --- Social ---
    @GET("api/publicaciones")
    fun getPublicaciones(): Call<List<Publicacion>>

    @POST("api/publicaciones")
    fun createPublicacion(@Body publicacion: Publicacion): Call<Publicacion>

    // --- Store ---
    @POST("api/compras")
    fun realizarCompra(@Body request: CompraRequest): Call<Compra>

    @GET("api/compras/usuario/{usuarioId}")
    fun getComprasByUsuario(@Path("usuarioId") usuarioId: Long): Call<List<Compra>>

    // --- Activities & Reservations ---
    @GET("api/actividades")
    fun getActividades(): Call<List<Actividad>>

    @POST("api/reservas/actividad/{actividadId}/usuario/{usuarioId}")
    fun realizarReserva(
        @Path("actividadId") actividadId: Long,
        @Path("usuarioId") usuarioId: Long
    ): Call<Reserva>

    @retrofit2.http.DELETE("api/reservas/actividad/{actividadId}/usuario/{usuarioId}")
    fun cancelarReserva(
        @Path("actividadId") actividadId: Long,
        @Path("usuarioId") usuarioId: Long
    ): Call<Void>

    @GET("api/reservas/usuario/{usuarioId}")
    fun getReservasByUsuario(@Path("usuarioId") usuarioId: Long): Call<List<Reserva>>

    // --- Progress Tracking ---
    @POST("api/progreso")
    fun guardarProgreso(@Body progreso: HistorialProgreso): Call<HistorialProgreso>

    @retrofit2.http.DELETE("api/rutinas/{id}")
    fun deleteRutina(@Path("id") id: Long): Call<Void>
}

