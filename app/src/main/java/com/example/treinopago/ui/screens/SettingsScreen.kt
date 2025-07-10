package com.example.treinopago.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.treinopago.ui.theme.TreinoPagoTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    var pixKey by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configurações") },
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
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            OutlinedTextField(
                value = pixKey,
                onValueChange = { pixKey = it },
                label = { Text("Chave PIX") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Salvar")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    TreinoPagoTheme {
        SettingsScreen(navController = rememberNavController())
    }
}
