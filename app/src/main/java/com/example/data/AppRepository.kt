package com.example.data

import com.example.data.local.AppDao
import com.example.model.MenuItem
import com.example.model.Reservation
import com.example.model.User
import kotlinx.coroutines.flow.Flow
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AppRepository(private val dao: AppDao) {
    
    private val firestore by lazy { FirebaseFirestore.getInstance() }

    suspend fun login(email: String, pass: String): User? {
        val user = dao.getUserByEmail(email)
        if (user != null && user.passRaw == pass) {
            return user
        }
        return null
    }

    suspend fun register(user: User): Boolean {
        if (dao.getUserByEmail(user.email) != null) return false
        dao.insertUser(user)
        return true
    }

    suspend fun getUser(id: Int): User? = dao.getUserById(id)

    val menuItems = dao.getAllMenuItems()
    
    fun searchMenu(query: String) = dao.searchMenuItems(query)

    suspend fun addMenu(item: MenuItem) = dao.insertMenuItem(item)
    
    suspend fun updateMenu(item: MenuItem) = dao.updateMenuItem(item)
    
    suspend fun deleteMenu(item: MenuItem) = dao.deleteMenuItem(item)
    
    suspend fun addReservation(res: Reservation) {
        dao.insertReservation(res)
        // Sync to Firebase
        try {
            val firestoreRes = hashMapOf(
                "userId" to res.userId,
                "date" to res.date,
                "time" to res.time,
                "guestCount" to res.guestCount,
                "status" to res.status
            )
            firestore.collection("reservations").add(firestoreRes).await()
        } catch (e: Exception) {
            // Ignore for now if offline
        }
    }
    
    fun getUserReservations(userId: Int): Flow<List<Reservation>> = dao.getReservationsByUser(userId)
    
    fun getAllReservations(): Flow<List<Reservation>> = dao.getAllReservations()
    
    suspend fun updateReservationStatus(id: Int, status: String) {
        dao.updateReservationStatus(id, status)
        // A complete sync would also need string ID mappings, but this sets up the connection.
    }
}
