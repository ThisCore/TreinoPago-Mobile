package com.example.treinopago.ui.screens

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.treinopago.AppDestinations
import com.example.treinopago.ViewModels.BillingViewModel
import com.example.treinopago.services.dtos.BillingDTO
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.temporal.ChronoUnit

@RequiresApi(Build.VERSION_CODES.O)
private fun determineDueDatePresentation(
    dueDateString: String,
    reminderSent: Boolean,
    originalStatus: String,
    defaultColor: Color,
    errorColor: Color,
    upcomingColor: Color,
    paidColor: Color
): Pair<String, Color> {
    var displayDate: String
    var dateColor: Color

    try {
        val parsedDate = LocalDate.parse(dueDateString.take(10), DateTimeFormatter.ISO_LOCAL_DATE)
        displayDate = parsedDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))

        if (reminderSent) {
            dateColor = paidColor
        } else {
            val today = LocalDate.now()
            val daysUntilDue = ChronoUnit.DAYS.between(today, parsedDate)

            dateColor = when {
                originalStatus.equals("OVERDUE", ignoreCase = true) || daysUntilDue < 0 -> errorColor
                daysUntilDue <= 7 -> upcomingColor
                else -> defaultColor
            }
        }
    } catch (e: DateTimeParseException) {
        displayDate = dueDateString
        dateColor = defaultColor
    }
    return Pair(displayDate, dateColor)
}


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BillingsScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    billingViewModel: BillingViewModel = viewModel()
) {
    val allBillings by billingViewModel.billings.observeAsState(initial = emptyList())
    val isLoading by billingViewModel.isLoading.observeAsState(initial = false)
    val errorMessage by billingViewModel.error.observeAsState(initial = null)
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        billingViewModel.fetchAllBillings()
    }

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            billingViewModel.clearError()
        }
    }

    val (pendingBillings, paidBillings) = remember(allBillings) {
        allBillings.partition { !it.reminderSent }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cobranças") },
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
            if (isLoading && allBillings.isEmpty()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (allBillings.isEmpty() && !isLoading) {
                Text(
                    text = "Nenhuma cobrança encontrada.",
                    modifier = Modifier.align(Alignment.Center),
                    fontSize = 18.sp
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(top = 16.dp, bottom = 72.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (pendingBillings.isNotEmpty()) {
                        item {
                            Text(
                                "Pendentes",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                        items(pendingBillings, key = { billing -> "pending-${billing.id}" }) { billing ->
                            BillingItem(
                                billing = billing,
                                onClick = {
                                    navController.navigate("${AppDestinations.BILLING_DETAIL_SCREEN}/${billing.id}")
                                }
                            )
                        }
                    } else if (!isLoading) {
                        item {
                            Text(
                                "Nenhuma cobrança pendente.",
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    if (pendingBillings.isNotEmpty() && paidBillings.isNotEmpty()) {
                        item {
                            Divider(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp))
                        }
                    }

                    if (paidBillings.isNotEmpty()) {
                        item {
                            Text(
                                "Enviadas",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                        items(paidBillings, key = { billing -> "paid-${billing.id}" }) { billing ->
                            BillingItem(
                                billing = billing,
                                onClick = {
                                    navController.navigate("${AppDestinations.BILLING_DETAIL_SCREEN}/${billing.id}")
                                }
                            )
                        }
                    } else if (!isLoading && pendingBillings.isNotEmpty()) {
                        item {
                            Text(
                                "Nenhuma cobrança faturada.",
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    if (isLoading && allBillings.isNotEmpty()) {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BillingItem(
    billing: BillingDTO,
    onClick: () -> Unit
) {
    val defaultDateColor = LocalContentColor.current
    val errorDateColor = MaterialTheme.colorScheme.error
    val upcomingDateColor = Color(0xFFFFA726)
    val paidDateColor = Color.Gray

    val (displayDueDate, dueDateColor) = remember(billing.dueDate, billing.reminderSent, billing.status) {
        determineDueDatePresentation(
            dueDateString = billing.dueDate,
            reminderSent = billing.reminderSent,
            originalStatus = billing.status,
            defaultColor = defaultDateColor,
            errorColor = errorDateColor,
            upcomingColor = upcomingDateColor,
            paidColor = paidDateColor
        )
    }

    val statusText: String
    val statusColor: Color

    if (billing.reminderSent) {
        statusText = "Enviado"
        statusColor = Color(0xFF4CAF50)
    } else {
        statusText = if (billing.status.equals("OVERDUE", ignoreCase = true) ||
            billing.status.equals("VENCIDO", ignoreCase = true) ) {
            billing.status.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        } else {
            "Pendente"
        }
        statusColor = if (billing.status.equals("OVERDUE", ignoreCase = true) ||
            billing.status.equals("VENCIDO", ignoreCase = true)) {
            MaterialTheme.colorScheme.error
        } else {
            MaterialTheme.colorScheme.secondary
        }
    }


    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            val clientName = billing.client?.name ?: "Cliente não disponível"
            val planName = billing.client?.plan?.name ?: "Plano não disponível"

            Text(
                text = "Cliente: $clientName",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                maxLines = 1
            )
            Text(text = "Plano: $planName")
            Text(text = "Valor: R$ ${String.format("%.2f", billing.amount)}")
            Text(
                text = "Vencimento: $displayDueDate",
                color = dueDateColor
            )
            Text(
                text = "Status: $statusText",
                color = statusColor
            )
        }
    }
}
