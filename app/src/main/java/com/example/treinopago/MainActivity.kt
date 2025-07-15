package com.example.treinopago

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.treinopago.ViewModels.ThemeViewModel
import com.example.treinopago.ui.screens.BillingDetailScreen
import com.example.treinopago.ui.screens.BillingsScreen
import com.example.treinopago.ui.screens.ClientDetailScreen
import com.example.treinopago.ui.screens.ClientsListScreen
import com.example.treinopago.ui.screens.CreateClientScreen
import com.example.treinopago.ui.screens.CreatePlanScreen
import com.example.treinopago.ui.screens.PlanDetailScreen
import com.example.treinopago.ui.screens.PlansListScreen
import com.example.treinopago.ui.screens.SettingsScreen
import com.example.treinopago.ui.theme.TreinoPagoTheme
import com.example.treinopago.ui.theme.PrimaryBlue
import com.example.treinopago.ui.theme.SecondaryGreen
import com.example.treinopago.ui.theme.AccentOrange


object AppDestinations {
    const val MAIN_SCREEN = "main_screen"
    const val PLANS_LIST_SCREEN = "plans_list_screen"
    const val CREATE_PLAN_SCREEN = "create_plan_screen"
    const val CLIENTS_LIST_SCREEN = "clients_list_screen"
    const val CREATE_CLIENT_SCREEN = "create_client_screen"
    const val CLIENT_DETAIL_SCREEN = "client_detail_screen"
    const val CLIENT_ID_ARG = "clientId"
    val CLIENT_DETAIL_ROUTE_WITH_ARG = "$CLIENT_DETAIL_SCREEN/{$CLIENT_ID_ARG}"
    const val BILLINGS_SCREEN = "billings_screen"
    const val BILLING_DETAIL_SCREEN = "billing_detail_screen"
    const val BILLING_ID_ARG = "billingId"
    val BILLING_DETAIL_ROUTE_WITH_ARG = "$BILLING_DETAIL_SCREEN/{$BILLING_ID_ARG}"
    const val PLAN_DETAIL_SCREEN = "plan_detail_screen"
    const val PLAN_ID_ARG = "planId"
    val PLAN_DETAIL_ROUTE_WITH_ARG = "$PLAN_DETAIL_SCREEN/{$PLAN_ID_ARG}"
    const val SETTINGS_SCREEN = "settings_screen"

}

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeViewModel: ThemeViewModel = viewModel()
            val isDarkTheme by themeViewModel.isDarkTheme.collectAsState()

            TreinoPagoTheme(darkTheme = isDarkTheme) {
                AppNavigation(themeViewModel = themeViewModel)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(themeViewModel: ThemeViewModel) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = AppDestinations.MAIN_SCREEN) {
        composable(AppDestinations.MAIN_SCREEN) {
            MainScreen(
                navController = navController,
                modifier = Modifier.fillMaxSize()
            )
        }
        composable(AppDestinations.PLANS_LIST_SCREEN) {
            PlansListScreen(
                navController= navController,
                onNavigateToCreatePlan = {
                    navController.navigate(AppDestinations.CREATE_PLAN_SCREEN)
                },
                onNavigateToPlanDetail = { planId ->
                    navController.navigate("${AppDestinations.PLAN_DETAIL_SCREEN}/$planId")
                },
                modifier = Modifier.fillMaxSize()
            )
        }
        composable(AppDestinations.CREATE_PLAN_SCREEN) {
            CreatePlanScreen(
                navController = navController,
                modifier = Modifier.fillMaxSize()
            )
        }
        composable(
            route = AppDestinations.PLAN_DETAIL_ROUTE_WITH_ARG,
            arguments = listOf(navArgument(AppDestinations.PLAN_ID_ARG) {
                type = NavType.StringType
                nullable = false
            })
        ) { backStackEntry ->
            val planId = backStackEntry.arguments?.getString(AppDestinations.PLAN_ID_ARG)
            if (planId == null) {
                Text("Erro: ID do plano não encontrado.")
                LaunchedEffect(Unit) {
                    navController.popBackStack()
                }
            } else {
                PlanDetailScreen(
                    navController = navController,
                    planId = planId,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
        composable(AppDestinations.CLIENTS_LIST_SCREEN) {
            ClientsListScreen(
                navController = navController,
                onNavigateToClientDetail = { clientId ->
                    navController.navigate("${AppDestinations.CLIENT_DETAIL_SCREEN}/$clientId")
                },
                modifier = Modifier.fillMaxSize()
            )
        }
        composable(AppDestinations.CREATE_CLIENT_SCREEN) {
            CreateClientScreen(
                navController = navController,
                modifier = Modifier.fillMaxSize()
            )
        }
        composable(
            route = AppDestinations.CLIENT_DETAIL_ROUTE_WITH_ARG,
            arguments = listOf(navArgument(AppDestinations.CLIENT_ID_ARG) {
                type = NavType.StringType
                nullable = false
            })
        ) { backStackEntry ->
            val clientId = backStackEntry.arguments?.getString(AppDestinations.CLIENT_ID_ARG)
            if (clientId == null) {
                Text("Erro: ID do cliente não encontrado.")
                LaunchedEffect(Unit) {
                    navController.popBackStack()
                }
            } else {
                ClientDetailScreen(
                    navController = navController,
                    clientId = clientId,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
        composable(AppDestinations.BILLINGS_SCREEN) {
            BillingsScreen(
                navController = navController,
                modifier = Modifier.fillMaxSize()
            )
        }

        composable(
            route = AppDestinations.BILLING_DETAIL_ROUTE_WITH_ARG,
            arguments = listOf(navArgument(AppDestinations.BILLING_ID_ARG) {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            val billingId = backStackEntry.arguments?.getString(AppDestinations.BILLING_ID_ARG)
            BillingDetailScreen(
                navController = navController,
                billingId = billingId,
                modifier = Modifier.fillMaxSize()
            )
        }

        composable(AppDestinations.SETTINGS_SCREEN) {
            SettingsScreen(
                navController = navController,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
fun MainScreen(navController: NavController, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.surfaceVariant
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Header com logo/título
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 48.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Filled.FitnessCenter,
                        contentDescription = "Logo",
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "TreinoPago",
                        style = MaterialTheme.typography.displayMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Botões de navegação modernos
            ModernNavigationButton(
                text = "Planos",
                icon = Icons.Filled.Assignment,
                description = "Gerencie planos de treino",
                color = PrimaryBlue,
                onClick = { navController.navigate(AppDestinations.PLANS_LIST_SCREEN) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            ModernNavigationButton(
                text = "Clientes",
                icon = Icons.Filled.People,
                description = "Cadastro e controle de alunos",
                color = SecondaryGreen,
                onClick = { navController.navigate(AppDestinations.CLIENTS_LIST_SCREEN) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            ModernNavigationButton(
                text = "Cobranças",
                icon = Icons.Filled.AttachMoney,
                description = "Controle financeiro",
                color = AccentOrange,
                onClick = { navController.navigate(AppDestinations.BILLINGS_SCREEN) }
            )
        }

        // Botão de configurações moderno
        Card(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(24.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            IconButton(
                onClick = { navController.navigate(AppDestinations.SETTINGS_SCREEN) },
                modifier = Modifier.padding(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = "Configurações",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun ModernNavigationButton(
    text: String,
    icon: ImageVector,
    description: String,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Card(
                modifier = Modifier.size(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = text,
                        modifier = Modifier.size(24.dp),
                        tint = color
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Navegar",
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    TreinoPagoTheme {
        MainScreen(navController = rememberNavController())
    }
}