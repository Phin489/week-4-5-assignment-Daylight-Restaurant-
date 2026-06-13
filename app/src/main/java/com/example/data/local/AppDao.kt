package com.example.data.local

import androidx.room.*
import com.example.model.MenuItem
import com.example.model.Reservation
import com.example.model.User
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): User?
    
    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    suspend fun getUserById(userId: Int): User?

    @Query("SELECT * FROM menu_items")
    fun getAllMenuItems(): Flow<List<MenuItem>>
    
    @Query("SELECT * FROM menu_items WHERE name LIKE '%' || :query || '%'")
    fun searchMenuItems(query: String): Flow<List<MenuItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMenuItem(item: MenuItem)

    @Delete
    suspend fun deleteMenuItem(item: MenuItem)

    @Update
    suspend fun updateMenuItem(item: MenuItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReservation(res: Reservation)

    @Query("SELECT * FROM reservations WHERE userId = :userId ORDER BY date DESC, time DESC")
    fun getReservationsByUser(userId: Int): Flow<List<Reservation>>

    @Query("SELECT * FROM reservations ORDER BY date DESC, time DESC")
    fun getAllReservations(): Flow<List<Reservation>>

    @Query("UPDATE reservations SET status = :status WHERE id = :resId")
    suspend fun updateReservationStatus(resId: Int, status: String)
}
