package com.example.treinopago.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.semantics.error


import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.treinopago.services.dtos.PlanResponse
import com.example.treinopago.ui.theme.TreinoPagoTheme
import com.example.treinopago.ViewModels.PlanViewModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanDetailScreen(
    navController: NavController,
    planId: String,
    modifier: Modifier = Modifier,
    planViewModel: PlanViewModel = viewModel()
) {
    var planName by remember { mutableStateOf("") }
    var planDescription by remember { mutableStateOf("") }
    var planPriceText by remember { mutableStateOf("") }
    var selectedRecurrence by remember { mutableStateOf(BillingFrequency.MONTHLY) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current

    val selectedPlan by planViewModel.selectedPlan.observeAsState()
    val isLoading by planViewModel.isLoading.observeAsState(initial = false)
    val updateSuccess by planViewModel.planUpdateSuccess.observeAsState(initial = false)
    val deletionSuccess by planViewModel.planDeletionSuccess.observeAsState(initial = false)
    val errorMessage by planViewModel.error.observeAsState(initial = null)

    LaunchedEffect(planId) {
        planViewModel.fetchPlanById(planId)
    }

    LaunchedEffect(selectedPlan) {
        selectedPlan?.let { plan ->
            planName = plan.name
            planDescription = plan.description ?: ""
            planPriceText = String.format(Locale.US, "%.2f", plan.price)
            selectedRecurrence = plan.recurrence
        }
    }

    LaunchedEffect(updateSuccess) {
        if (updateSuccess) {
            Toast.makeText(context, "Plano atualizado com sucesso!", Toast.LENGTH_SHORT).show()
            navController.popBackStack()
            planViewModel.resetPlanUpdateStatus()
            planViewModel.fetchAllPlans()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            planViewModel.clearSelectedPlan()
        }
    }

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            Toast.makeText(context, "Erro: $it", Toast.LENGTH_LONG).show()
            planViewModel.clearError()
        }
    }

    LaunchedEffect(deletionSuccess) {
        if (deletionSuccess) {
            Toast.makeText(context, "Plano excluído com sucesso!", Toast.LENGTH_SHORT).show()
            navController.popBackStack()
            planViewModel.resetPlanDeletionStatus()
            planViewModel.fetchAllPlans()
        }
    }

    if (showDeleteDialog && selectedPlan != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Confirmar Exclusão") },
            text = { Text("Tem certeza de que deseja excluir o plano \"${selectedPlan!!.name}\"? Esta ação não pode ser desfeita.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        planViewModel.deleteCurrentPlan(planId)
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
                title = { Text(if (selectedPlan != null) "Editar Plano" else "Carregando Plano...") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Voltar")
                    }
                },
                actions = {
                    if (selectedPlan != null && !isLoading) {
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(
                                Icons.Filled.Delete,
                                contentDescription = "Excluir Plano",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        if (isLoading && selectedPlan == null) {
            Box(modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (selectedPlan == null && !isLoading) {
            Box(modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding), contentAlignment = Alignment.Center) {
                Text("Não foi possível carregar os detalhes do plano.")
            }
        } else {
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
                    value = planName,
                    onValueChange = { planName = it },
                    label = { Text("Nome do Plano") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                )
                OutlinedTextField(
                    value = planDescription,
                    onValueChange = { planDescription = it },
                    label = { Text("Descrição (Opcional)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 80.dp),
                    enabled = !isLoading
                )
                OutlinedTextField(
                    value = planPriceText,
                    onValueChange = { planPriceText = it.filter { char -> char.isDigit() || char == '.' || char == ',' } },
                    label = { Text("Preço (ex: 99.90)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                )


                var recurrenceExpanded by remember { mutableStateOf(false) }
                val recurrenceOptions = BillingFrequency.values()

                ExposedDropdownMenuBox(
                    expanded = recurrenceExpanded,
                    onExpandedChange = { recurrenceExpanded = !recurrenceExpanded && !isLoading },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = selectedRecurrence.name,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Frequência de Cobrança") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = recurrenceExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        enabled = !isLoading
                    )
                    ExposedDropdownMenu(
                        expanded = recurrenceExpanded,
                        onDismissRequest = { recurrenceExpanded = false }
                    ) {
                        recurrenceOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option.name.replace("_", " ").lowercase()
                                    .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() })
                                },
                                onClick = {
                                    selectedRecurrence = option
                                    recurrenceExpanded = false
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f, fill = false))

                if (isLoading && selectedPlan != null) {
                    CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp))
                } else {
                    Button(
                        onClick = {
                            val priceDouble = planPriceText.replace(",", ".").toDoubleOrNull()

                            if (planName.isBlank() || priceDouble == null || priceDouble <= 0) {
                                Toast.makeText(context, "Nome e preço são obrigatórios e válidos.", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            planViewModel.updateExistingPlan(
                                planId = planId,
                                name = planName.takeIf { it != selectedPlan?.name },
//                                description = planDescription.takeIf { it != selectedPlan?.description },
                                price = priceDouble.takeIf { it != selectedPlan?.price },
                                recurrence = selectedRecurrence.takeIf { it != selectedPlan?.recurrence }
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        enabled = selectedPlan != null && !isLoading
                    ) {
                        Text("Salvar Alterações")
                    }
                }

                if (isLoading && (updateSuccess || deletionSuccess || selectedPlan != null)) {
                    CircularProgressIndicator(modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 8.dp))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PlanDetailScreenPreview() {
    TreinoPagoTheme {
        PlanDetailScreen(
            navController = rememberNavController(),
            planId = "preview_id"
        )
    }
}