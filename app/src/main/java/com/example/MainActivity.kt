package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.data.AppRepository
import com.example.data.local.AppDatabase
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val dao = AppDatabase.getDatabase(applicationContext).appDao()
        val repo = AppRepository(dao)
        
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val factory = object : ViewModelProvider.Factory {
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            @Suppress("UNCHECKED_CAST")
                            return MainViewModel(repo) as T
                        }
                    }
                    val viewModel: MainViewModel = viewModel(factory = factory)
                    val navController = rememberNavController()

                    NavHost(navController = navController, startDestination = "home") {
                        composable("login") { LoginScreen(viewModel, navController) }
                        composable("signup") { SignUpScreen(viewModel, navController) }
                        composable("home") { HomeScreen(viewModel, navController) }
                        composable("menu") { MenuScreen(viewModel, navController) }
                        composable("cart") { CartScreen(viewModel, navController) }
                        composable("order_confirmation") { OrderConfirmationScreen(navController) }
                        composable("reservation") { ReservationScreen(viewModel, navController) }
                        composable("confirmation") { ConfirmationScreen(viewModel, navController) }
                        composable("admin_dashboard") { AdminDashboard(viewModel, navController) }
                    }
                }
            }
        }
    }
}
