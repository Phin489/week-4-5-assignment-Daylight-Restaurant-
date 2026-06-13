package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reservations")
data class Reservation(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val date: String,
    val time: String,
    val guestCount: Int,
    val status: String // Pending, Confirmed, Cancelled
)

data class ReservationWithUser(
    val reservation: Reservation,
    val userName: String,
    val userEmail: String,
    val userPhone: String
)
