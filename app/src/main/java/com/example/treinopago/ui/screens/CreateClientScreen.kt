package com.example.treinopago.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday // Ícone para o campo de data
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.treinopago.ui.theme.TreinoPagoTheme
import java.text.SimpleDateFormat
import java.util.*

// Função para formatar Long (timestamp) para String (data)
fun Long.toFormattedDateString(): String {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return dateFormat.format(Date(this))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateClientScreen(
    navController: NavController,
    modifier: Modifier = Modifier
    // TODO adicionar um callback aqui para salvar o cliente, ex:
    // onSaveClient: (name: String, email: String, startDate: Long) -> Unit
) {
    var clientName by remember { mutableStateOf("") }
    var clientEmail by remember { mutableStateOf("") }

    // Inicializa com a data de hoje
    var selectedStartDateMillis by remember { mutableStateOf(Calendar.getInstance().timeInMillis) }
    val showDatePickerDialog = remember { mutableStateOf(false) }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Adicionar Novo Cliente") },
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
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = clientName,
                onValueChange = { clientName = it },
                label = { Text("Nome Completo") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = clientEmail,
                onValueChange = { clientEmail = it },
                label = { Text("Email") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = selectedStartDateMillis.toFormattedDateString(),
                onValueChange = { /* Não editável diretamente */ },
                label = { Text("Data de Início") },
                readOnly = true,
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Filled.CalendarToday,
                        contentDescription = "Selecionar Data",
                        modifier = Modifier.clickable { showDatePickerDialog.value = true }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePickerDialog.value = true }
            )

            if (showDatePickerDialog.value) {
                val datePickerState = rememberDatePickerState(
                    initialSelectedDateMillis = selectedStartDateMillis,
                    // É possível definir limites de ano aqui se necessário
                    // yearRange = (2020..Calendar.getInstance().get(Calendar.YEAR))
                )
                DatePickerDialog(
                    onDismissRequest = { showDatePickerDialog.value = false },
                    confirmButton = {
                        TextButton(onClick = {
                            datePickerState.selectedDateMillis?.let {
                                selectedStartDateMillis = it
                            }
                            showDatePickerDialog.value = false
                        }) {
                            Text("OK")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDatePickerDialog.value = false }) {
                            Text("Cancelar")
                        }
                    }
                ) {
                    DatePicker(state = datePickerState)
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    // TODO: Validar os dados e salvar o cliente
                    // Ex: onSaveClient(clientName, clientEmail, selectedStartDateMillis)
                    navController.navigateUp()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Salvar Cliente")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CreateClientScreenPreview() {
    TreinoPagoTheme {
        CreateClientScreen(navController = rememberNavController())
    }
}