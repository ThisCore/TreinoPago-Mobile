package com.example.treinopago.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.semantics.error

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.isEmpty
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.treinopago.services.dtos.ClientResponse
import com.example.treinopago.ui.theme.TreinoPagoTheme
import com.example.treinopago.ViewModels.ClientViewModel
import com.example.treinopago.ViewModels.PlanViewModel
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientDetailScreen(
    navController: NavController,
    clientId: String,
    modifier: Modifier = Modifier,
    clientViewModel: ClientViewModel = viewModel(),
    planViewModel: PlanViewModel = viewModel()
) {
    var clientName by remember { mutableStateOf("") }
    var clientEmail by remember { mutableStateOf("") }
    val availablePlans by clientViewModel.availablePlans.observeAsState(emptyList())
    var selectedPlanId by remember { mutableStateOf<String?>(null) }
    var expandedPlanDropdown by remember { mutableStateOf(false) }
    var selectedPlanText by remember { mutableStateOf("") }
    val plansList by planViewModel.plans.observeAsState(initial = emptyList())
    var showDeleteDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val selectedClient by clientViewModel.selectedClient.observeAsState()
    val isLoading by clientViewModel.isLoading.observeAsState(initial = false)
    val updateSuccess by clientViewModel.clientUpdateSuccess.observeAsState(initial = false)
    val deletionSuccess by clientViewModel.clientDeletionSuccess.observeAsState(initial = false)
    val errorMessage by clientViewModel.error.observeAsState(initial = null)


    LaunchedEffect(clientId) {
        clientViewModel.fetchClientById(clientId)
        planViewModel.fetchAllPlans()
    }

    LaunchedEffect(selectedClient, plansList) {
        selectedClient?.let { client ->
            clientName = client.name
            clientEmail = client.email ?: ""
            selectedPlanId = client.planId

            if (client.planId != null && plansList.isNotEmpty()) {
                val plan = plansList.find { it.id == client.planId }
                selectedPlanText = plan?.name ?: "Plano não encontrado"
            } else if (client.planId == null) {
                selectedPlanText = "Nenhum plano associado"
            } else {
                if (plansList.isEmpty() && client.planId != null) {
                    selectedPlanText = "Carregando detalhes do plano..."
                }
            }
        }
        if (selectedClient == null) {
            selectedPlanText = ""
        }
    }

    LaunchedEffect(updateSuccess) {
        if (updateSuccess) {
            Toast.makeText(context, "Cliente atualizado com sucesso!", Toast.LENGTH_SHORT).show()
            navController.popBackStack()
            clientViewModel.resetClientUpdateStatus()
            clientViewModel.fetchAllClients()
        }
    }

    LaunchedEffect(deletionSuccess) {
        if (deletionSuccess) {
            Toast.makeText(context, "Cliente excluído com sucesso!", Toast.LENGTH_SHORT).show()
            navController.popBackStack()
            clientViewModel.resetClientDeletionStatus()
            clientViewModel.fetchAllClients()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            clientViewModel.clearSelectedClient()
        }
    }

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            Toast.makeText(context, "Erro: $it", Toast.LENGTH_LONG).show()
            clientViewModel.clearError()
        }
    }

    if (showDeleteDialog && selectedClient != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Confirmar Exclusão") },
            text = { Text("Tem certeza de que deseja excluir o cliente \"${selectedClient!!.name}\"? Esta ação não pode ser desfeita.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        clientViewModel.deleteClient(clientId)
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Excluir")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (selectedClient != null) "Detalhes do Cliente" else "Carregando Cliente...") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Voltar")
                    }
                },
                actions = {
                    if (selectedClient != null && !isLoading) {
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(
                                Icons.Filled.Delete,
                                contentDescription = "Excluir Cliente",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        if (isLoading && selectedClient == null) {
            Box(modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (selectedClient == null && !isLoading) {
            Box(modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding), contentAlignment = Alignment.Center) {
                Text("Não foi possível carregar os dados do cliente.")
            }
        } else if (selectedClient != null) {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(16.dp)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = clientName,
                    onValueChange = { clientName = it },
                    label = { Text("Nome do Cliente") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                )
                OutlinedTextField(
                    value = clientEmail,
                    onValueChange = { clientEmail = it },
                    label = { Text("Email") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                )

                ExposedDropdownMenuBox(
                    expanded = expandedPlanDropdown,
                    onExpandedChange = { expandedPlanDropdown = !expandedPlanDropdown },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = selectedPlanText,
                        onValueChange = {  },
                        label = { Text("Plano") },
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedPlanDropdown)
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedPlanDropdown,
                        onDismissRequest = { expandedPlanDropdown = false }
                    ) {
                        if (plansList.isEmpty()) {
                            DropdownMenuItem(
                                text = { Text(if (clientViewModel.isLoading.value == true) "Carregando planos..." else "Nenhum plano disponível") }, // Supondo isLoadingPlans no ViewModel
                                onClick = { },
                                enabled = false
                            )
                        } else {
                            plansList.forEach { plan ->
                                DropdownMenuItem(
                                    text = { Text(plan.name) },
                                    onClick = {
                                        selectedPlanText = plan.name
                                        selectedPlanId = plan.id
                                        expandedPlanDropdown = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp))
                } else {
                    Button(
                        onClick = {
                            if (clientName.isBlank()) {
                                Toast.makeText(context, "Nome do cliente é obrigatório.", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            clientViewModel.updateExistingClient(
                                clientId = clientId,
                                name = clientName,
                                email = clientEmail,
                                planId = selectedPlanId
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        enabled = !isLoading && selectedClient != null
                    ) {
                        Text("Salvar Alterações")
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ClientDetailScreenPreview() {
    TreinoPagoTheme {
        ClientDetailScreen(
            navController = rememberNavController(),
            clientId = "preview_client_id"
        )
    }
}