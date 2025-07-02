package com.example.treinopago.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.treinopago.ViewModels.PlanViewModel
import com.example.treinopago.services.dtos.PlanResponse
import com.example.treinopago.ui.theme.TreinoPagoTheme
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlansListScreen(
    navController: NavController,
    onNavigateToCreatePlan: () -> Unit,
    modifier: Modifier = Modifier,
    planViewModel: PlanViewModel = viewModel()
) {
    val plansFromApi by planViewModel.plans.observeAsState(initial = emptyList())
    val isLoading by planViewModel.isLoading.observeAsState(initial = false)
    val errorMessage by planViewModel.error.observeAsState(initial = null)

    LaunchedEffect(key1 = Unit) {
        planViewModel.fetchAllPlans()
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
                },
                 actions = {
                     IconButton(onClick = { planViewModel.fetchAllPlans() }, enabled = !isLoading) {
                         Icon(Icons.Filled.Refresh, contentDescription = "Atualizar Planos")
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
        ) {
            if (isLoading) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    CircularProgressIndicator()
                }
            } else if (errorMessage != null) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Erro ao carregar planos: $errorMessage \n\nToque para tentar novamente.",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.clickable { planViewModel.fetchAllPlans() }
                    )
                }
            } else if (plansFromApi.isEmpty()) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Text("Nenhum plano cadastrado ainda.")
                }
            } else {
                LazyColumn(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(plansFromApi, key = { plan -> plan.id }) { plan ->
                        PlanApiItem(
                            plan = plan,
                            onClick = {
                                // TODO: Definir ação ao clicar em um plano
                                // Ex: navController.navigate("plan_details/${plan.id}")
                                println("Plano clicado: ${plan.name}")
                            }
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun PlanApiItem(
    plan: PlanResponse,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currencyFormatter = remember { NumberFormat.getCurrencyInstance(Locale("pt", "BR")) }

    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = plan.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            plan.description?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = currencyFormatter.format(plan.price),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                plan.durationDescription?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            if (!plan.isActive) {
                Text(
                    "PLANO INATIVO",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PlansListScreenWithApiPreview() {
    TreinoPagoTheme {
        PlansListScreen(
            navController = rememberNavController(),
            onNavigateToCreatePlan = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PlanApiItemPreview() {
    TreinoPagoTheme {
        PlanApiItem(
            plan = PlanResponse(
                id = "prev_1",
                name = "Plano de Teste Premium",
                description = "Descrição detalhada do plano de teste para o preview.",
                price = 149.99,
                durationDays = null,
                durationDescription = "Mensal",
                isActive = true
            ),
            onClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PlanApiItemInactivePreview() {
    TreinoPagoTheme {
        PlanApiItem(
            plan = PlanResponse(
                id = "prev_2",
                name = "Plano Antigo",
                description = "Este plano não está mais ativo.",
                price = 79.00,
                durationDays = null,
                durationDescription = "Trimestral",
                isActive = false
            ),
            onClick = {}
        )
    }
}