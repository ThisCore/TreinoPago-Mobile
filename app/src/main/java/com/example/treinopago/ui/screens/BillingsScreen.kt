package com.example.treinopago.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.treinopago.AppDestinations
import com.example.treinopago.ui.theme.TreinoPagoTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Modelo de dados simples para uma cobrança
data class Billing(
    val id: String,
    val clientName: String,
    val planName: String,
    val amount: Double,
    val dueDate: Long,
    val isPaid: Boolean = false
)

// Função para formatar Long (timestamp) para String (data)
fun Long.toFormattedDateForBilling(): String {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return dateFormat.format(Date(this))
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BillingsScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    // Lista de exemplo de cobranças
    val allBillings = remember {
        mutableStateListOf(
            Billing("1", "Ana Silva", "Plano Mensal", 99.90, System.currentTimeMillis() + (7 * 24 * 60 * 60 * 1000), false),
            Billing("2", "Carlos Souza", "Plano Trimestral", 250.00, System.currentTimeMillis() - (10 * 24 * 60 * 60 * 1000), true),
            Billing("3", "Beatriz Lima", "Plano Anual", 1000.00, System.currentTimeMillis() + (30 * 24 * 60 * 60 * 1000L), false),
            Billing("4", "Ricardo Alves", "Plano Mensal", 99.90, System.currentTimeMillis() - (5 * 24 * 60 * 60 * 1000), true),
            Billing("5", "Ana Silva", "Plano Mensal", 99.90, System.currentTimeMillis() - (40 * 24 * 60 * 60 * 1000L), true)
        )
    }

    val futureBillings = allBillings.filter { !it.isPaid }.sortedBy { it.dueDate }
    val paidBillings = allBillings.filter { it.isPaid }.sortedByDescending { it.dueDate }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Minhas Cobranças") },
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
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            // Seção de Cobranças Futuras
            BillingSection(
                title = "Cobranças Futuras",
                billings = futureBillings,
                emptyListMessage = "Nenhuma cobrança futura.",
                onBillingClick = { billing ->
                    navController.navigate("${AppDestinations.BILLING_DETAIL_SCREEN}/${billing.id}")
                }
            )

            Divider(modifier = Modifier.padding(vertical = 16.dp))

            // Seção de Cobranças Feitas
            BillingSection(
                title = "Cobranças Feitas",
                billings = paidBillings,
                emptyListMessage = "Nenhuma cobrança foi realizada ainda.",
                onBillingClick = { billing ->
                    navController.navigate("${AppDestinations.BILLING_DETAIL_SCREEN}/${billing.id}")
                }
            )
        }
    }
}

@Composable
fun BillingSection(
    title: String,
    billings: List<Billing>,
    emptyListMessage: String,
    onBillingClick: (Billing) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        if (billings.isEmpty()) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Text(emptyListMessage, style = MaterialTheme.typography.bodyMedium)
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.heightIn(max = 300.dp) // Limita a altura para evitar rolagem excessiva da página
            ) {
                items(billings) { billing ->
                    BillingItem(
                        billing = billing,
                        onClick = { onBillingClick(billing) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BillingItem(
    billing: Billing,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = billing.clientName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${billing.planName} - R$${String.format(Locale.getDefault(), "%.2f", billing.amount)}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Text(
                text = if (billing.isPaid) "Paga em: ${billing.dueDate.toFormattedDateForBilling()}"
                else "Vence em: ${billing.dueDate.toFormattedDateForBilling()}",
                style = MaterialTheme.typography.bodySmall,
                color = if (billing.isPaid) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
            )
        }
    }
}

@Preview(showBackground = true, heightDp = 800)
@Composable
fun BillingsScreenPreview() {
    TreinoPagoTheme {
        BillingsScreen(navController = rememberNavController())
    }
}

@Preview(showBackground = true)
@Composable
fun BillingItemPaidPreview() {
    TreinoPagoTheme {
        BillingItem(
            billing = Billing("1", "Cliente Pago", "Plano Exemplo", 100.0, System.currentTimeMillis(), true),
            onClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun BillingItemFuturePreview() {
    TreinoPagoTheme {
        BillingItem(
            billing = Billing("2", "Cliente Futuro", "Plano Teste", 50.50, System.currentTimeMillis() + 86400000, false),
            onClick = {}
        )
    }
}