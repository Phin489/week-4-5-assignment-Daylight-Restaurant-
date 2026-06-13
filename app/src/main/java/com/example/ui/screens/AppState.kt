package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.model.MenuItem
import com.example.model.Reservation
import com.example.model.User
import kotlinx.coroutines.flow.Flow

// MVI State
data class AppState(
    val currentUser: User? = null,
    val menuItems: List<MenuItem> = emptyList(),
    val reservations: List<Reservation> = emptyList(),
    val cart: Map<MenuItem, Int> = emptyMap()
)
