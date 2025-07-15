package com.example.treinopago.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.treinopago.ViewModels.SettingsViewModel
import com.example.treinopago.ViewModels.ThemeViewModel
import com.example.treinopago.ui.theme.TreinoPagoTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    themeViewModel: ThemeViewModel,
    modifier: Modifier = Modifier
) {
    val settingsViewModel = viewModel<SettingsViewModel>()

    val currentPixKey by settingsViewModel.pixKey.observeAsState("")
    var pixKeyInput by remember { mutableStateOf("") }
    val isLoading by settingsViewModel.isLoading.observeAsState(initial = false)
    val updateSuccess by settingsViewModel.updateSuccess.observeAsState(initial = false)
    val errorMessage by settingsViewModel.error.observeAsState(initial = null)
    val context = LocalContext.current

    // Estado do tema
    val isDarkTheme by themeViewModel.isDarkTheme.collectAsState()

    LaunchedEffect(Unit) {
        settingsViewModel.fetchPixKey()
    }

    LaunchedEffect(currentPixKey) {
        if (!currentPixKey.isNullOrBlank()) {
            pixKeyInput = currentPixKey!!
        }
    }

    LaunchedEffect(updateSuccess) {
        if (updateSuccess) {
            Toast.makeText(context, "Chave PIX salva com sucesso!", Toast.LENGTH_SHORT).show()
            settingsViewModel.clearUpdateSuccess()
            navController.popBackStack()
        }
    }

    LaunchedEffect(errorMessage) {
        errorMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            settingsViewModel.clearError()
        }
    }

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
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Seção de Tema
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isDarkTheme) Icons.Filled.DarkMode else Icons.Filled.LightMode,
                            contentDescription = "Tema",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "Modo Noturno",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = if (isDarkTheme) "Tema escuro ativado" else "Tema claro ativado",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Switch(
                            checked = isDarkTheme,
                            onCheckedChange = { themeViewModel.toggleTheme() }
                        )
                    }
                }
            }

            // Seção de Chave PIX
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Configurações de Pagamento",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    OutlinedTextField(
                        value = pixKeyInput,
                        onValueChange = { pixKeyInput = it },
                        label = { Text("Chave PIX") },
                        placeholder = { Text("Digite sua chave PIX") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading,
                        singleLine = true,
                        isError = !settingsViewModel.validatePixKey(pixKeyInput) && pixKeyInput.isNotBlank()
                    )

                    if (!settingsViewModel.validatePixKey(pixKeyInput) && pixKeyInput.isNotBlank()) {
                        Text(
                            text = "Formato de chave PIX inválido",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Button(
                        onClick = {
                            settingsViewModel.updatePixKey(pixKeyInput)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading && pixKeyInput.isNotBlank() && settingsViewModel.validatePixKey(pixKeyInput)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Text("Salvar Chave PIX")
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    TreinoPagoTheme {
        // SettingsScreen(navController = rememberNavController())
    }
}