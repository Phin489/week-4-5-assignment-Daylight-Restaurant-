package com.example.ui.screens

import com.example.data.AppRepository
import com.example.data.local.AppDao
import com.example.model.MenuItem
import com.example.model.Reservation
import com.example.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class FakeDao : AppDao {
    private val users = mutableListOf<User>()
    
    override suspend fun insertUser(user: User) {
        users.add(user)
    }

    override suspend fun getUserByEmail(email: String): User? {
        return users.find { it.email == email }
    }

    override suspend fun getUserById(userId: Int): User? {
        return users.find { it.id == userId }
    }

    override fun getAllMenuItems(): Flow<List<MenuItem>> = flowOf(emptyList())
    override fun searchMenuItems(query: String): Flow<List<MenuItem>> = flowOf(emptyList())
    override suspend fun insertMenuItem(item: MenuItem) {}
    override suspend fun deleteMenuItem(item: MenuItem) {}
    override suspend fun updateMenuItem(item: MenuItem) {}
    override suspend fun insertReservation(res: Reservation) {}
    override fun getReservationsByUser(userId: Int): Flow<List<Reservation>> = flowOf(emptyList())
    override fun getAllReservations(): Flow<List<Reservation>> = flowOf(emptyList())
    override suspend fun updateReservationStatus(resId: Int, status: String) {}
}

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {
    
    private lateinit var viewModel: MainViewModel
    private lateinit var fakeDao: FakeDao
    private lateinit var repository: AppRepository
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakeDao = FakeDao()
        repository = AppRepository(fakeDao)
        viewModel = MainViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `login with correct credentials updates user state`() = runTest {
        val user = User(id = 1, name = "Test", email = "test@test.com", phone = "123", passRaw = "pass", isAdmin = false)
        fakeDao.insertUser(user)

        var loginSuccess = false
        viewModel.login("test@test.com", "pass") { success ->
            loginSuccess = success
        }
        
        testDispatcher.scheduler.advanceUntilIdle()
        
        assertTrue(loginSuccess)
        assertEquals(user, viewModel.uiState.value.currentUser)
    }

    @Test
    fun `login with incorrect credentials fails`() = runTest {
        var loginSuccess = true
        viewModel.login("wrong@test.com", "pass") { success ->
            loginSuccess = success
        }
        
        testDispatcher.scheduler.advanceUntilIdle()
        
        assertFalse(loginSuccess)
        assertNull(viewModel.uiState.value.currentUser)
    }
}
