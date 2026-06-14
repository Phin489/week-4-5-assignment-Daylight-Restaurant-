package com.example.data

import com.example.model.MenuItem
import com.example.model.Reservation
import com.example.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.UUID

class AppRepository {
    
    private val firestore by lazy { FirebaseFirestore.getInstance() }

    private val _menuItems = MutableStateFlow<List<MenuItem>>(emptyList())
    val menuItems = _menuItems.asStateFlow()
    
    init {
        firestore.collection("menu_items")
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    val items = snapshot.documents.mapNotNull { it.toObject(MenuItem::class.java) }
                    _menuItems.value = items
                }
            }
    }

    suspend fun login(email: String, pass: String): User? {
        return try {
            val query = firestore.collection("users")
                .whereEqualTo("email", email)
                .get()
                .await()
            
            val user = query.documents.firstOrNull()?.toObject(User::class.java)
            if (user != null && user.passRaw == pass) {
                user
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun register(user: User): Boolean {
        return try {
            val query = firestore.collection("users")
                .whereEqualTo("email", user.email)
                .get()
                .await()
                
            if (!query.isEmpty) return false
            
            val newId = UUID.randomUUID().toString()
            val userToSave = user.copy(id = newId)
            firestore.collection("users").document(newId).set(userToSave).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun getUser(id: String): User? {
        return firestore.collection("users").document(id).get().await().toObject(User::class.java)
    }

    fun searchMenu(query: String): Flow<List<MenuItem>> {
        val filtered = MutableStateFlow<List<MenuItem>>(emptyList())
        firestore.collection("menu_items")
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    val items = snapshot.documents.mapNotNull { it.toObject(MenuItem::class.java) }
                    filtered.value = items.filter { it.name.contains(query, ignoreCase = true) || it.category.contains(query, ignoreCase = true) }
                }
            }
        return filtered.asStateFlow()
    }

    suspend fun addMenu(item: MenuItem) {
        val newId = UUID.randomUUID().toString()
        val itemToSave = item.copy(id = newId)
        firestore.collection("menu_items").document(newId).set(itemToSave).await()
    }
    
    suspend fun updateMenu(item: MenuItem) {
        if (item.id.isNotEmpty()) {
            firestore.collection("menu_items").document(item.id).set(item).await()
        }
    }
    
    suspend fun deleteMenu(item: MenuItem) {
        if (item.id.isNotEmpty()) {
            firestore.collection("menu_items").document(item.id).delete().await()
        }
    }
    
    suspend fun addReservation(res: Reservation) {
        val newId = UUID.randomUUID().toString()
        val resToSave = res.copy(id = newId)
        firestore.collection("reservations").document(newId).set(resToSave).await()
    }
    
    fun getUserReservations(userId: String): Flow<List<Reservation>> {
        val reservations = MutableStateFlow<List<Reservation>>(emptyList())
        firestore.collection("reservations")
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    reservations.value = snapshot.documents.mapNotNull { it.toObject(Reservation::class.java) }
                }
            }
        return reservations.asStateFlow()
    }
    
    fun getAllReservations(): Flow<List<Reservation>> {
        val reservations = MutableStateFlow<List<Reservation>>(emptyList())
        firestore.collection("reservations")
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    reservations.value = snapshot.documents.mapNotNull { it.toObject(Reservation::class.java) }
                }
            }
        return reservations.asStateFlow()
    }
    
    suspend fun updateReservationStatus(id: String, status: String) {
        firestore.collection("reservations").document(id).update("status", status).await()
    }
}
