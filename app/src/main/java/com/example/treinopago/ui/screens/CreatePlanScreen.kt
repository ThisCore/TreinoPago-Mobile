package com.example.treinopago.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.example.treinopago.ui.theme.TreinoPagoTheme
import com.example.treinopago.ViewModels.PlanViewModel

enum class BillingFrequency(val displayName: String) {
    WEEKLY("Semanal"),
    MONTHLY("Mensal"),
    QUARTERLY("Trimestral"),
    SEMI_ANNUALLY("Semestral"),
    ANNUALLY("Anual")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePlanScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    planViewModel: PlanViewModel = viewModel()
) {
    var planName by remember { mutableStateOf("") }
    var planValue by remember { mutableStateOf("") }
    var selectedFrequency by remember { mutableStateOf(BillingFrequency.MONTHLY) }
    var isFrequencyDropdownExpanded by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val isLoading by planViewModel.isLoading.observeAsState(initial = false)
    val creationSuccess by planViewModel.planCreationSuccess.observeAsState(initial = false)
    val errorMessage by planViewModel.error.observeAsState(initial = null)

    LaunchedEffect(creationSuccess) {
        if (creationSuccess) {
            Toast.makeText(context, "Plano criado com sucesso!", Toast.LENGTH_SHORT).show()
            navController.navigateUp()
            planViewModel.resetPlanCreationStatus()
        }
    }

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            Toast.makeText(context, "Erro: $it", Toast.LENGTH_LONG).show()
            planViewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Criar Novo Plano") },
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
                value = planName,
                onValueChange = { planName = it },
                label = { Text("Nome do Plano") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = planValue,
                onValueChange = { planValue = it.filter { char -> char.isDigit() || char == '.' || char == ',' } },
                label = { Text("Valor (Ex: 99.90)") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.fillMaxWidth()
            )

            ExposedDropdownMenuBox(
                expanded = isFrequencyDropdownExpanded,
                onExpandedChange = { isFrequencyDropdownExpanded = it },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = selectedFrequency.displayName,
                    onValueChange = { },
                    label = { Text("Frequência de Cobrança") },
                    readOnly = true,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = isFrequencyDropdownExpanded)
                    },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = isFrequencyDropdownExpanded,
                    onDismissRequest = { isFrequencyDropdownExpanded = false },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    BillingFrequency.entries.forEach { frequency ->
                        DropdownMenuItem(
                            text = { Text(frequency.displayName) },
                            onClick = {
                                selectedFrequency = frequency
                                isFrequencyDropdownExpanded = false
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp))
            } else {
                Button(
                    onClick = {
                        val priceDouble = planValue.replace(",", ".").toDoubleOrNull()

                        if (planName.isBlank()) {
                            Toast.makeText(context, "Nome do plano é obrigatório.", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        if (priceDouble == null || priceDouble <= 0) {
                            Toast.makeText(context, "Preço inválido.", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        planViewModel.createNewPlan(
                            name = planName,
                            description = null,
                            price = priceDouble,
                            recurrence = selectedFrequency
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    enabled = !isLoading
                ) {
                    Text("Salvar Plano")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CreatePlanScreenPreview() {
    TreinoPagoTheme {
        CreatePlanScreen(navController = rememberNavController())
    }
}