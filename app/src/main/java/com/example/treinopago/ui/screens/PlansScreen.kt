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
import com.example.treinopago.ui.theme.TreinoPagoTheme

// Modelo de dados simples para um plano
data class Plan(val id: String, val name: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlansListScreen(
    navController: NavController,
    onNavigateToCreatePlan: () -> Unit,
    modifier: Modifier = Modifier
) {
    val plans = remember {
        mutableStateListOf(
            Plan("1", "Plano Básico Mensal"),
            Plan("2", "Plano Premium Anual"),
            Plan("3", "Plano Trimestral com Desconto")
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Meus Planos") },
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
            FloatingActionButton(onClick = onNavigateToCreatePlan) {
                Icon(Icons.Filled.Add, contentDescription = "Criar novo plano")
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
            if (plans.isEmpty()) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text("Nenhum plano cadastrado ainda.")
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(plans) { plan ->
                        PlanItem(plan = plan)
                    }
                }
            }
        }
    }
}

@Composable
fun PlanItem(plan: Plan, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = plan.name,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PlansListScreenPreview() {
    TreinoPagoTheme {
        PlansListScreen(
            navController = rememberNavController(),
            onNavigateToCreatePlan = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PlanItemPreview() {
    TreinoPagoTheme {
        PlanItem(Plan("1", "Plano de Teste"))
    }
}