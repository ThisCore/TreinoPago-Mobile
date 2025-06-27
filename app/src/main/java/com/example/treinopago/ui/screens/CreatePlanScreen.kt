package com.example.treinopago.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.treinopago.ui.theme.TreinoPagoTheme

// Enum para as opções de frequência
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
    modifier: Modifier = Modifier
    // onSavePlan: (name: String, value: String, frequency: BillingFrequency) -> Unit
) {
    var planName by remember { mutableStateOf("") }
    var planValue by remember { mutableStateOf("") }
    var selectedFrequency by remember { mutableStateOf(BillingFrequency.MONTHLY) }
    var isFrequencyDropdownExpanded by remember { mutableStateOf(false) }

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

            // Dropdown de Frequência usando ExposedDropdownMenuBox
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

            Button(
                onClick = {
                    // TODO: Validar e salvar
                    navController.navigateUp()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Salvar Plano")
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