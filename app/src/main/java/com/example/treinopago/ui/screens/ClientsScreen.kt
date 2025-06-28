package com.example.treinopago.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.isEmpty
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.treinopago.AppDestinations
import com.example.treinopago.ViewModels.ClientViewModel
import com.example.treinopago.ui.theme.TreinoPagoTheme

// Modelo de dados simples para um cliente
data class Client(val id: String, val name: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientsListScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    clientViewModel: ClientViewModel = viewModel()
    // Você pode adicionar um callback para quando o FAB for clicado,
    // onNavigateToCreateClient: () -> Unit
) {
    val clients by clientViewModel.clients.observeAsState(initial = emptyList())
    val isLoading by clientViewModel.isLoading.observeAsState(initial = false)
    val errorMessage by clientViewModel.error.observeAsState(initial = null)

    LaunchedEffect(key1 = Unit) {
        clientViewModel.fetchAllClients()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Meus Clientes") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                navController.navigate(AppDestinations.CREATE_CLIENT_SCREEN)
            }) {
                Icon(Icons.Filled.Add, contentDescription = "Adicionar novo cliente")
            }
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (errorMessage != null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Erro: $errorMessage", color = MaterialTheme.colorScheme.error)
                }
            } else if (clients.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Nenhum cliente encontrado.")
                }
            } else {
                LazyColumn(

                ) {
                    items(clients) { client ->
                        ClientItem(Client(client.id, client.name), modifier = Modifier.padding(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun ClientItem(client: Client, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = client.name,
                style = MaterialTheme.typography.titleMedium
            )
            // TODO dicionar mais detalhes ou botões aqui (ver detalhes, editar, etc.)
            // Ex: IconButton(onClick = { /* Ver detalhes do cliente */ }) { Icon(Icons.Filled.Info, "Detalhes")}
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ClientsListScreenPreview() {
    TreinoPagoTheme {
        ClientsListScreen(navController = rememberNavController())
    }
}

@Preview(showBackground = true)
@Composable
fun ClientItemPreview() {
    TreinoPagoTheme {
        ClientItem(Client("1", "Nome do Cliente de Teste"))
    }
}