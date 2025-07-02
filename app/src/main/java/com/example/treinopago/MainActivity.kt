package com.example.treinopago

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.treinopago.ui.screens.BillingDetailScreen
import com.example.treinopago.ui.screens.BillingsScreen
import com.example.treinopago.ui.screens.ClientDetailScreen
import com.example.treinopago.ui.screens.ClientsListScreen
import com.example.treinopago.ui.screens.CreateClientScreen
import com.example.treinopago.ui.screens.CreatePlanScreen
import com.example.treinopago.ui.screens.PlanDetailScreen
import com.example.treinopago.ui.screens.PlansListScreen
import com.example.treinopago.ui.theme.TreinoPagoTheme


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

}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TreinoPagoTheme {
                AppNavigation()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation() {
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
    }
}

@Composable
fun MainScreen(navController: NavController, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "TreinoPago",
            style = TextStyle(
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.padding(top = 32.dp, bottom = 48.dp)
        )

        Button(
            onClick = { navController.navigate(AppDestinations.PLANS_LIST_SCREEN) },
            modifier = Modifier.widthIn(min = 200.dp)
        ) {
            Text("Planos")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { navController.navigate(AppDestinations.CLIENTS_LIST_SCREEN) },
            modifier = Modifier.widthIn(min = 200.dp)
        ) {
            Text("Clientes")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { navController.navigate(AppDestinations.BILLINGS_SCREEN) },
            modifier = Modifier.widthIn(min = 200.dp)
        ) {
            Text("Cobranças")
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