package com.example.treinopago.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.treinopago.ui.theme.TreinoPagoTheme
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BillingDetailScreen(
    navController: NavController,
    billingId: String?,
    modifier: Modifier = Modifier
    // billingViewModel: BillingViewModel = viewModel()
) {
    val sampleBillings = remember {
        listOf(
            Billing("1", "Ana Silva", "Plano Mensal", 99.90, System.currentTimeMillis() + (7 * 24 * 60 * 60 * 1000), false),
            Billing("2", "Carlos Souza", "Plano Trimestral", 250.00, System.currentTimeMillis() - (10 * 24 * 60 * 60 * 1000), true),
            Billing("3", "Beatriz Lima", "Plano Anual", 1000.00, System.currentTimeMillis() + (30 * 24 * 60 * 60 * 1000L), false),
            Billing("4", "Ricardo Alves", "Plano Mensal", 99.90, System.currentTimeMillis() - (5 * 24 * 60 * 60 * 1000), true),
            Billing("5", "Ana Silva", "Plano Mensal", 99.90, System.currentTimeMillis() - (40 * 24 * 60 * 60 * 1000L), true)
        )
    }
    val billing = sampleBillings.find { it.id == billingId }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalhes da Cobrança") },
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
        if (billing == null) {
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Cobrança não encontrada.")
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            DetailItem(label = "Cliente:", value = billing.clientName)
            DetailItem(label = "Plano:", value = billing.planName)
            DetailItem(label = "Valor:", value = "R$${String.format(Locale.getDefault(), "%.2f", billing.amount)}")

            val dateLabel = if (billing.isPaid) "Data do Pagamento:" else "Data de Vencimento:"
            DetailItem(label = dateLabel, value = billing.dueDate.toFormattedDateForBilling())

            DetailItem(label = "Status:", value = if (billing.isPaid) "Paga" else "Pendente")

            Spacer(modifier = Modifier.weight(1f))

            if (!billing.isPaid) {
                Button(
                    onClick = {
                        // TODO: Implementar lógica para marcar como paga
                        println("Marcar como paga: ${billing.id}")
                        // Exemplo: navController.navigateUp() após marcar
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Marcar como Paga")
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            OutlinedButton(
                onClick = {
                    // TODO: Implementar lógica para gerar 2ª via ou outra ação
                    println("Gerar 2ª via: ${billing.id}")
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (billing.isPaid) "Ver Recibo" else "Gerar 2ª Via Boleto")
            }
        }
    }
}

@Composable
fun DetailItem(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 12.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}


@Preview(showBackground = true)
@Composable
fun BillingDetailScreenPaidPreview() {
    TreinoPagoTheme {
        BillingDetailScreen(
            navController = rememberNavController(),
            billingId = "2"
        )
    }
}

@Preview(showBackground = true)
@Composable
fun BillingDetailScreenPendingPreview() {
    TreinoPagoTheme {
        BillingDetailScreen(
            navController = rememberNavController(),
            billingId = "1"
        )
    }
}

@Preview(showBackground = true)
@Composable
fun BillingDetailScreenNotFoundPreview() {
    TreinoPagoTheme {
        BillingDetailScreen(
            navController = rememberNavController(),
            billingId = "nonexistent"
        )
    }
}