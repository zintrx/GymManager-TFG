package com.gymmanager.android.model

data class ClaseGym(
    val id: String,
    val title: String,
    val emoji: String,
    val time: String,
    val duration: String,
    val room: String,
    val instructor: String,
    val capacity: Int,
    val booked: Int,
    val color: String,
    val bg: String,
    var isReserved: Boolean = false
)
