package com.example.treinopago.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.treinopago.AppDestinations
import com.example.treinopago.ui.theme.TreinoPagoTheme

// Modelo de dados simples para um cliente
data class Client(val id: String, val name: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientsListScreen(
    navController: NavController,
    modifier: Modifier = Modifier
    // Você pode adicionar um callback para quando o FAB for clicado,
    // onNavigateToCreateClient: () -> Unit
) {
    val clients = remember {
        mutableStateListOf(
            Client("1", "Ana Silva"),
            Client("2", "Carlos Souza"),
            Client("3", "Beatriz Lima"),
            Client("4", "Ricardo Alves")
        )
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
                .padding(16.dp)
        ) {
            if (clients.isEmpty()) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text("Nenhum cliente cadastrado ainda.")
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(clients) { client ->
                        ClientItem(client = client)
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