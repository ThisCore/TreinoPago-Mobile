package com.example.treinopago.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.treinopago.ViewModels.BillingViewModel
import com.example.treinopago.services.dtos.BillingDTO

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BillingDetailScreen(
    navController: NavController,
    billingId: String?,
    modifier: Modifier = Modifier,
    billingViewModel: BillingViewModel = viewModel()
) {
    val selectedBilling by billingViewModel.selectedBilling.observeAsState()
    val isLoading by billingViewModel.isLoading.observeAsState(initial = false)
    val errorMessage by billingViewModel.error.observeAsState(initial = null)
    val context = LocalContext.current

    LaunchedEffect(billingId) {
        if (billingId != null) {
            billingViewModel.fetchBillingById(billingId)
        } else {
            Toast.makeText(context, "ID da cobrança inválido.", Toast.LENGTH_LONG).show()
            navController.popBackStack()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            billingViewModel.clearSelectedBilling()
        }
    }

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            billingViewModel.clearError()
            if (selectedBilling == null && !isLoading) {
                navController.popBackStack()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (selectedBilling != null) "Detalhes da Cobrança" else "Carregando...") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Voltar")
                    }
                }
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (selectedBilling != null) {
                BillingDetailContent(billing = selectedBilling!!)
            } else if (billingId != null) {
                Text("Cobrança não encontrada ou erro ao carregar.", modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}

@Composable
fun BillingDetailContent(billing: BillingDTO) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Detalhes da Cobrança", fontSize = 22.sp, fontWeight = FontWeight.SemiBold)
        Divider(modifier = Modifier.padding(vertical = 8.dp))

        InfoRow("ID da Cobrança:", billing.id)
        InfoRow("Cliente:", billing.client.name)
        InfoRow("Plano:", billing.client.plan.name)
        InfoRow("Valor:", "R$ ${String.format("%.2f", billing.amount)}")
        InfoRow("Data de Vencimento:", billing.dueDate)


    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.4f))
        Text(text = value, modifier = Modifier.weight(0.6f))
    }
}
