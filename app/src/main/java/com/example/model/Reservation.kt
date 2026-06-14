package com.example.model

data class Reservation(
    val id: String = "",
    val userId: String = "",
    val date: String = "",
    val time: String = "",
    val guestCount: Int = 0,
    val status: String = "" // Pending, Confirmed, Cancelled
)

data class ReservationWithUser(
    val reservation: Reservation,
    val userName: String,
    val userEmail: String,
    val userPhone: String
)
