package com.example.treinopago

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.treinopago.ui.screens.CreatePlanScreen
import com.example.treinopago.ui.screens.PlansListScreen
import com.example.treinopago.ui.theme.TreinoPagoTheme


object AppDestinations {
    const val MAIN_SCREEN = "main_screen"
    const val PLANS_LIST_SCREEN = "plans_list_screen"
    const val CREATE_PLAN_SCREEN = "create_plan_screen"
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
                modifier = Modifier.fillMaxSize()
            )
        }
        composable(AppDestinations.CREATE_PLAN_SCREEN) {
            CreatePlanScreen(
                navController = navController,
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
            onClick = { /* TODO: navController.navigate("clients_screen") */ },
            modifier = Modifier.widthIn(min = 200.dp)
        ) {
            Text("Clientes")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { /* TODO: navController.navigate("billings_screen") */ },
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