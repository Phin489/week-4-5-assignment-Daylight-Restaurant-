package com.example.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppRepository
import com.example.model.MenuItem
import com.example.model.Reservation
import com.example.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel(private val repo: AppRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(AppState())
    val uiState: StateFlow<AppState> = _uiState

    init {
        viewModelScope.launch {
            repo.menuItems.collect { items ->
                _uiState.value = _uiState.value.copy(menuItems = items)
            }
        }
    }

    fun login(email: String, pass: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val user = repo.login(email.trim(), pass)
            if (user != null) {
                _uiState.value = _uiState.value.copy(currentUser = user)
                observeUserReservations(user.id)
                if (user.isAdmin) observeAllReservations()
                onResult(true)
            } else {
                onResult(false)
            }
        }
    }

    fun logout() {
        _uiState.value = _uiState.value.copy(currentUser = null, reservations = emptyList())
    }

    fun register(name: String, email: String, phone: String, pass: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = repo.register(User(name = name, email = email, phone = phone, passRaw = pass))
            onResult(success)
        }
    }
    
    private fun observeUserReservations(userId: String) {
        viewModelScope.launch {
            repo.getUserReservations(userId).collect { res ->
                _uiState.value = _uiState.value.copy(reservations = res)
            }
        }
    }

    private fun observeAllReservations() {
        viewModelScope.launch {
            repo.getAllReservations().collect { res ->
                _uiState.value = _uiState.value.copy(reservations = res)
            }
        }
    }

    fun addReservation(date: String, time: String, guests: Int) {
        val user = _uiState.value.currentUser ?: return
        viewModelScope.launch {
            repo.addReservation(Reservation(userId = user.id, date = date, time = time, guestCount = guests, status = "Confirmed"))
        }
    }
    
    fun updateReservation(id: String, status: String) {
        viewModelScope.launch {
            repo.updateReservationStatus(id, status)
        }
    }
    
    fun addMenuItem(name: String, price: Double, category: String, desc: String) {
        viewModelScope.launch {
            repo.addMenu(MenuItem(name = name, price = price, imageUrl = "", category = category, description = desc))
        }
    }
    
    fun updateMenuItem(id: String, name: String, price: Double, category: String, desc: String, imageUrl: String = "") {
        viewModelScope.launch {
            repo.updateMenu(MenuItem(id = id, name = name, price = price, category = category, description = desc, imageUrl = imageUrl))
        }
    }
    
    fun deleteMenuItem(item: MenuItem) {
        viewModelScope.launch {
            repo.deleteMenu(item)
        }
    }

    fun addToCart(item: MenuItem) {
        val currentCart = _uiState.value.cart.toMutableMap()
        currentCart[item] = (currentCart[item] ?: 0) + 1
        _uiState.value = _uiState.value.copy(cart = currentCart)
    }

    fun removeFromCart(item: MenuItem) {
        val currentCart = _uiState.value.cart.toMutableMap()
        val count = currentCart[item] ?: 0
        if (count > 1) {
            currentCart[item] = count - 1
        } else {
            currentCart.remove(item)
        }
        _uiState.value = _uiState.value.copy(cart = currentCart)
    }

    fun clearCart() {
        _uiState.value = _uiState.value.copy(cart = emptyMap())
    }
}
