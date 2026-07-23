package com.aditya.expensetracker.presentation.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.aditya.expensetracker.presentation.Screens.HomeScreen
import com.aditya.expensetracker.presentation.Screens.ProfileScreen
import com.aditya.expensetracker.presentation.Screens.SettingScreen
import com.aditya.expensetracker.presentation.Screens.StatisticsScreen
import com.aditya.expensetracker.presentation.Screens.utils.AddScreen
import com.aditya.expensetracker.presentation.Screens.utils.CustomBottomNavigation
import com.aditya.expensetracker.presentation.Screens.utils.MorphingFab

@Composable
fun App(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    var showBottomSheet by remember{mutableStateOf(false)}
    if(showBottomSheet){
        AddScreen(onDismiss = {showBottomSheet = false})
    }
    
    val currentEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentEntry?.destination?.route

    Scaffold(
        floatingActionButton = {
            Box(
                modifier = Modifier.offset(y = 90.dp)
            ) {
                MorphingFab(
                    onSavingClick = {
                        showBottomSheet = true
                    },
                    onExpenseClick = {
                        showBottomSheet = true
                    },
                    onPaymentClick = {
                        showBottomSheet = true
                    }
                )
            }
        },
        containerColor = Color.White,
        floatingActionButtonPosition = FabPosition.Center,
        bottomBar = {
            CustomBottomNavigation(
                currentRoute = currentRoute,
                onProfileClick = {
                    if (currentRoute != Routes.Profile::class.qualifiedName) {
                        navController.navigate(Routes.Profile) { launchSingleTop = true }
                    }
                },
                onStatisticsClick = {
                    if (currentRoute != Routes.Statistics::class.qualifiedName) {
                        navController.navigate(Routes.Statistics) { launchSingleTop = true }
                    }
                },
                onHomeClick = {
                    if (currentRoute != Routes.HomeUi::class.qualifiedName) {
                        navController.navigate(Routes.HomeUi) { launchSingleTop = true }
                    }
                }
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = SubNavigation.MainHomeScreen,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            navigation<SubNavigation.MainHomeScreen>(
                startDestination = Routes.HomeUi
            ) {
                composable<Routes.HomeUi> {
                    HomeScreen(navController)
                }
                composable<Routes.Profile> {
                    ProfileScreen(navController)
                }
                composable<Routes.Statistics> {
                    StatisticsScreen(navController)
                }
                composable<Routes.Setting> {
                    SettingScreen()
                }
            }
        }
    }
}
