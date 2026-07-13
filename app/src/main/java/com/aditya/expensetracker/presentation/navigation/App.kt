package com.aditya.expensetracker.presentation.navigation

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.aditya.expensetracker.R
import com.aditya.expensetracker.presentation.Screens.HomeScreen

data class BottomItem(
    val title: String,
    val icon: Int,
    val route: Routes
)
@Composable
fun App(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val bottomList = listOf(
        BottomItem(
            title = "Statistics",
            icon = R.drawable.statistics_svgrepo_com,
            route = Routes.HomeUi
        ),
        BottomItem(
            title = "Home",
            icon = R.drawable.home_svgrepo_com,
            route = Routes.HomeUi
        ),
        BottomItem(
            title = "Home",
            icon = Icons.Default.Home,
            route = Routes.HomeUi
        )
    )

    Scaffold(
        bottomBar = BottomAppBar(
            Row(modifier.fillMaxWidth()){

            }
        ) { }
    ) {

    }
    
    NavHost(
        navController = navController,
        startDestination = SubNavigation.MainHomeScreen,
        modifier = Modifier.fillMaxSize()
    ) {
        navigation<SubNavigation.MainHomeScreen>(
            startDestination = Routes.HomeUi
        ) {
            composable<Routes.HomeUi>{
                HomeScreen()
            }
            composable<Routes.Profile>{
//                ProfileScreen()
            }
            composable<Routes.Statistics>{
//                StatisticsScreen()
            }
        }
    }
}
