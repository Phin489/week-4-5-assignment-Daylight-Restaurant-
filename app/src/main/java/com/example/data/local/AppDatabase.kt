package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.model.MenuItem
import com.example.model.Reservation
import com.example.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [User::class, MenuItem::class, Reservation::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appDao(): AppDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "daylight_database_v2"
                )
                .fallbackToDestructiveMigration()
                .addCallback(DatabaseCallback())
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
    
    private class DatabaseCallback : RoomDatabase.Callback() {
        override fun onCreate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    val dao = database.appDao()
                    // Default admin
                    dao.insertUser(User(name = "Admin", email = "admin", phone = "000000", passRaw = "admin123", isAdmin = true))
                    // Test user
                    dao.insertUser(User(name = "Test User", email = "test@test.com", phone = "111111", passRaw = "test123", isAdmin = false))
                    
                    // Default menu items
                    dao.insertMenuItem(MenuItem(name = "Nyama Choma Combo", price = 1500.0, imageUrl = "https://images.unsplash.com/photo-1544025162-d76694265947?auto=format&fit=crop&w=400&q=80", category = "Grill", description = "Delicious grilled meat served with kachumbari."))
                    dao.insertMenuItem(MenuItem(name = "Kienyeji Chicken", price = 1200.0, imageUrl = "https://images.unsplash.com/photo-1604908176997-125f25cc6f3d?auto=format&fit=crop&w=400&q=80", category = "Local Staples", description = "Traditional free-range chicken cooked in rich stew."))
                    dao.insertMenuItem(MenuItem(name = "Ugali & Sukuma Wiki", price = 350.0, imageUrl = "https://images.unsplash.com/photo-1512621776951-a57141f2eefd?auto=format&fit=crop&w=400&q=80", category = "Local Staples", description = "The ultimate Kenyan classic served hot."))
                    dao.insertMenuItem(MenuItem(name = "Grilled Tilapia", price = 1000.0, imageUrl = "https://images.unsplash.com/photo-1543339308-43e59d6b73a6?auto=format&fit=crop&w=400&q=80", category = "Seafood", description = "Whole grilled tilapia from Lake Victoria."))
                    dao.insertMenuItem(MenuItem(name = "Pilau Beef", price = 800.0, imageUrl = "https://images.unsplash.com/photo-1512058564366-18510be2db19?auto=format&fit=crop&w=400&q=80", category = "Swahili Dishes", description = "Spiced rice dish with tender beef chunks."))
                    dao.insertMenuItem(MenuItem(name = "Bhajia", price = 300.0, imageUrl = "https://images.unsplash.com/photo-1541592106381-b31e9677c0e5?auto=format&fit=crop&w=400&q=80", category = "Sides", description = "Crispy sliced potatoes deep fried in seasoned gram flour."))
                }
            }
        }
    }
}
