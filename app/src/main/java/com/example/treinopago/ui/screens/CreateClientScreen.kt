package com.example.treinopago.ui.screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.error
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.treinopago.ViewModels.ClientViewModel
import com.example.treinopago.ui.theme.TreinoPagoTheme
import java.text.SimpleDateFormat
import java.util.*

fun Long.toFormattedDateString(): String {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return dateFormat.format(Date(this))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateClientScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    clientViewModel: ClientViewModel = viewModel()
) {
    var clientName by remember { mutableStateOf("") }
    var clientEmail by remember { mutableStateOf("") }
    var selectedStartDateMillis by remember { mutableStateOf(Calendar.getInstance().timeInMillis) }
    val showDatePickerDialog = remember { mutableStateOf(false) }

    val context = LocalContext.current

    val isLoading by clientViewModel.isLoading.observeAsState(initial = false)
    val creationSuccess by clientViewModel.creationSuccess.observeAsState(initial = false)
    val errorMessage by clientViewModel.error.observeAsState(initial = null)

    LaunchedEffect(creationSuccess) {
        if (creationSuccess) {
            Toast.makeText(context, "Cliente criado com sucesso!", Toast.LENGTH_SHORT).show()
            navController.navigateUp()
            clientViewModel.resetCreationStatus()
        }
    }

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            Toast.makeText(context, "Erro: $it", Toast.LENGTH_LONG).show()
            clientViewModel.clearError()
        }
    }

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
                isError = false,
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
                isError = false,
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
                        modifier = Modifier.clickable { if (!isLoading) showDatePickerDialog.value = true }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { if (!isLoading) showDatePickerDialog.value = true }
            )

            if (showDatePickerDialog.value) {
                val datePickerState = rememberDatePickerState(
                    initialSelectedDateMillis = selectedStartDateMillis,
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

            if (isLoading) {
                CircularProgressIndicator()
            } else {
                Button(
                    onClick = {
                        // TODO: Adicionar validação dos campos antes de enviar
                        if (clientName.isNotBlank() && clientEmail.isNotBlank()) {
                            clientViewModel.createNewClient(
                                name = clientName,
                                email = clientEmail,
                                startDate = selectedStartDateMillis
                            )
                        } else {
                            Toast.makeText(context, "Por favor, preencha todos os campos.", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                ) {
                    Text("Salvar Cliente")
                }
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