package com.aditya.expensetracker.presentation.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.hilt.navigation.compose.hiltViewModel
import com.aditya.expensetracker.data.ViewModel.AuthViewModel
import com.aditya.expensetracker.presentation.Screens.EditProfileScreen
import com.aditya.expensetracker.presentation.Screens.HistoryScreen
import com.aditya.expensetracker.presentation.Screens.HomeScreen
import com.aditya.expensetracker.presentation.Screens.ProfileScreen
import com.aditya.expensetracker.presentation.Screens.SignUpScreen
import com.aditya.expensetracker.presentation.Screens.StatisticsScreen
import com.aditya.expensetracker.presentation.Screens.utils.AddIncome
import com.aditya.expensetracker.presentation.Screens.utils.AddSaving
import com.aditya.expensetracker.presentation.Screens.utils.AddScreen
import com.aditya.expensetracker.presentation.Screens.utils.CustomBottomNavigation
import com.aditya.expensetracker.presentation.Screens.utils.MorphingFab
import com.example.expensemanager.ui.login.LoginScreen
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment

enum class SheetType { NONE, INCOME, EXPENSE, SAVING }

@Composable
fun App(
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    var currentSheet by remember { mutableStateOf(SheetType.NONE) }
    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()

    if (isLoggedIn == null) {
        // Loading state
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val startDestination = if (isLoggedIn == true) {
        SubNavigation.MainHomeScreen
    } else {
        SubNavigation.LoginSignUpScreen
    }
    
    when (currentSheet) {
        SheetType.INCOME -> AddIncome(onDismiss = { currentSheet = SheetType.NONE })
        SheetType.EXPENSE -> AddScreen(onDismiss = { currentSheet = SheetType.NONE })
        SheetType.SAVING -> AddSaving(onDismiss = { currentSheet = SheetType.NONE })
        SheetType.NONE -> {}
    }
    
    val currentEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentEntry?.destination?.route
    val showBottomBarAndFab = currentRoute != Routes.LoginScreen::class.qualifiedName
            && currentRoute != Routes.SignUpScreen::class.qualifiedName
    Scaffold(
        floatingActionButton = {
            if (showBottomBarAndFab) {
                Box(
                    modifier = Modifier.offset(y = 90.dp)
                ) {
                    MorphingFab(
                        onSavingClick = {
                            currentSheet = SheetType.SAVING
                        },
                        onExpenseClick = {
                            currentSheet = SheetType.EXPENSE
                        },
                        onPaymentClick = {
                            currentSheet = SheetType.INCOME
                        }
                    )
                }
            }
        },
        containerColor = Color.White,
        floatingActionButtonPosition = FabPosition.Center,
        bottomBar = {
            if (showBottomBarAndFab) {
                CustomBottomNavigation(
                    currentRoute = currentRoute,
                    onProfileClick = {
                        if (currentRoute != Routes.Profile::class.qualifiedName) {
                            navController.navigate(Routes.Profile) {
                                popUpTo(navController.graph.findStartDestination().id){
                                    saveState = true
                                }

                                launchSingleTop = true
                                restoreState = true

                            }
                        }
                    },
                    onStatisticsClick = {
                        if (currentRoute != Routes.Statistics::class.qualifiedName) {
                            navController.navigate(Routes.Statistics) {
                                popUpTo(navController.graph.findStartDestination().id){
                                    saveState = true
                                }

                                launchSingleTop = true
                                restoreState = true

                            }
                        }
                    },
                    onHomeClick = {
                        if (currentRoute != Routes.HomeUi::class.qualifiedName) {
                            navController.navigate(Routes.HomeUi) {
                                popUpTo(navController.graph.findStartDestination().id){
                                    saveState = true
                                }

                                launchSingleTop = true
                                restoreState = true

                            }
                        }
                    },
                    onHistoryClick = {
                        if (currentRoute != Routes.History::class.qualifiedName) {
                            navController.navigate(Routes.History) {
                                popUpTo(navController.graph.findStartDestination().id){
                                    saveState = true
                                }

                                launchSingleTop = true
                                restoreState = true

                            }
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(bottom = innerPadding.calculateBottomPadding())
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
                composable<Routes.EditProfile> {
                    EditProfileScreen(navController)
                }
                composable<Routes.Statistics> {
                    StatisticsScreen(navController)
                }
                composable<Routes.History> {
                    HistoryScreen(navController = navController)
                }
            }
            navigation< SubNavigation.LoginSignUpScreen>(
                startDestination = Routes.LoginScreen
            ){
                composable<Routes.LoginScreen> {
                    LoginScreen(navController = navController)
                }
                composable<Routes.SignUpScreen> {
                    SignUpScreen(navController)
                }
            }
        }
    }
}
