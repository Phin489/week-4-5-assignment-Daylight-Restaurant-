package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.model.MenuItem
import com.example.model.Reservation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboard(viewModel: MainViewModel, navController: NavController) {
    val state by viewModel.uiState.collectAsState()
    
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Reservations", "Menu Items")
    
    // Add/Edit Menu Item Dialog State
    var showDialog by remember { mutableStateOf(false) }
    var editingMenuItem by remember { mutableStateOf<MenuItem?>(null) }
    
    var menuName by remember { mutableStateOf("") }
    var menuPrice by remember { mutableStateOf("") }
    var menuCategory by remember { mutableStateOf("General") }
    var menuDesc by remember { mutableStateOf("") }

    val openDialogForAdd = {
        editingMenuItem = null
        menuName = ""
        menuPrice = ""
        menuCategory = "General"
        menuDesc = ""
        showDialog = true
    }

    val openDialogForEdit = { item: MenuItem ->
        editingMenuItem = item
        menuName = item.name
        menuPrice = item.price.toString()
        menuCategory = item.category
        menuDesc = item.description
        showDialog = true
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Admin Dashboard") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            if (selectedTabIndex == 1) {
                FloatingActionButton(
                    onClick = openDialogForAdd,
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Menu Item")
                }
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            TabRow(selectedTabIndex = selectedTabIndex) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title) }
                    )
                }
            }
            
            when (selectedTabIndex) {
                0 -> {
                    // Reservations Tab
                    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                        Text("Daily Reservations", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        if (state.reservations.isEmpty()) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("No reservations yet.")
                            }
                        } else {
                            LazyColumn {
                                items(state.reservations) { res ->
                                    AdminReservationCard(res, viewModel)
                                    Spacer(modifier = Modifier.height(12.dp))
                                }
                            }
                        }
                    }
                }
                1 -> {
                    // Menu Items Tab
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        item {
                            Text("Menu Management", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                        
                        items(state.menuItems) { item ->
                            AdminMenuItemCard(
                                item = item,
                                onEdit = { openDialogForEdit(item) },
                                onDelete = { viewModel.deleteMenuItem(item) }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
        
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text(if (editingMenuItem == null) "Add Menu Item" else "Edit Menu Item") },
                text = {
                    Column {
                        OutlinedTextField(
                            value = menuName, 
                            onValueChange = { menuName = it }, 
                            label = { Text("Name") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = menuPrice, 
                            onValueChange = { menuPrice = it }, 
                            label = { Text("Price (KES)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = menuCategory, 
                            onValueChange = { menuCategory = it }, 
                            label = { Text("Category") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = menuDesc, 
                            onValueChange = { menuDesc = it }, 
                            label = { Text("Description") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        val price = menuPrice.toDoubleOrNull() ?: 0.0
                        if (editingMenuItem == null) {
                            viewModel.addMenuItem(menuName, price, menuCategory, menuDesc)
                        } else {
                            viewModel.updateMenuItem(editingMenuItem!!.id, menuName, price, menuCategory, menuDesc)
                        }
                        showDialog = false
                    }) { Text("Save") }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) { Text("Cancel") }
                }
            )
        }
    }
}

@Composable
fun AdminMenuItemCard(item: MenuItem, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(item.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(item.category, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(4.dp))
                Text("KES ${String.format("%,.0f", item.price)}", fontWeight = FontWeight.Medium)
            }
            Row {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary)
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
fun AdminReservationCard(res: Reservation, viewModel: MainViewModel) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Date: ${res.date} | Time: ${res.time}", fontWeight = FontWeight.Bold)
            Text("Guests: ${res.guestCount} | Status: ${res.status}")
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { viewModel.updateReservation(res.id, "Confirmed") }) { Text("Confirm") }
                OutlinedButton(onClick = { viewModel.updateReservation(res.id, "Cancelled") }) { Text("Cancel") }
            }
        }
    }
}
