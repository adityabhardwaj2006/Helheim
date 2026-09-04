package com.aditya.expensetracker.presentation.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.FabPosition
import androidx.compose.material3.MaterialTheme
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
import com.aditya.expensetracker.presentation.Screens.AboutAppScreen
import com.aditya.expensetracker.presentation.Screens.EditProfileScreen
import com.aditya.expensetracker.presentation.Screens.HelpSupportScreen
import com.aditya.expensetracker.presentation.Screens.HistoryScreen
import com.aditya.expensetracker.presentation.Screens.HomeScreen
import com.aditya.expensetracker.presentation.Screens.NotificationScreen
import com.aditya.expensetracker.presentation.Screens.ChatScreen
import com.aditya.expensetracker.presentation.Screens.PaymentMethodsScreen
import com.aditya.expensetracker.presentation.Screens.PrivacyPolicyScreen
import com.aditya.expensetracker.presentation.Screens.ProfileScreen
import com.aditya.expensetracker.presentation.Screens.RateUsScreen
import com.aditya.expensetracker.presentation.Screens.SignUpScreen
import com.aditya.expensetracker.presentation.Screens.StatisticsScreen
import com.aditya.expensetracker.presentation.Screens.utils.AddIncome
import com.aditya.expensetracker.presentation.Screens.utils.AddSaving
import com.aditya.expensetracker.presentation.Screens.utils.AddScreen
import com.aditya.expensetracker.presentation.Screens.utils.CustomBottomNavigation
import com.aditya.expensetracker.presentation.Screens.utils.MorphingFab
import com.aditya.expensetracker.presentation.components.HomeSkeleton
import com.example.expensemanager.ui.login.LoginScreen
import com.aditya.expensetracker.presentation.components.shimmer
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.height
import com.aditya.expensetracker.data.ViewModel.NotificationViewModel
import com.aditya.expensetracker.data.ViewModel.TransactionViewModel
import com.aditya.expensetracker.data.ViewModel.TransactionVm
import com.aditya.expensetracker.presentation.Screens.UpdatePassword

enum class SheetType { NONE, INCOME, EXPENSE, SAVING }

@Composable
fun App(
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel = hiltViewModel(),
    notificationViewModel : NotificationViewModel = hiltViewModel(),
    transactionViewModel: TransactionVm = hiltViewModel()
) {
    val navController = rememberNavController()
    var currentSheet by remember { mutableStateOf(SheetType.NONE) }
    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()

    if (isLoggedIn == null) {
        // Full screen skeleton loader
        HomeSkeleton()
        return
    }

    val startDestination = if (isLoggedIn == true) {
        SubNavigation.MainHomeScreen
    } else {
        SubNavigation.LoginSignUpScreen
    }
    
    when (currentSheet) {
        SheetType.INCOME -> AddIncome(navController = navController,onDismiss = { currentSheet = SheetType.NONE })
        SheetType.EXPENSE -> AddScreen(navController = navController,onDismiss = { currentSheet = SheetType.NONE })
        SheetType.SAVING -> AddSaving(navController = navController, onDismiss = { currentSheet = SheetType.NONE })
        SheetType.NONE -> {}
    }
    
    val currentEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentEntry?.destination?.route
    val showBottomBarAndFab = currentRoute != Routes.LoginScreen::class.qualifiedName
            && currentRoute != Routes.SignUpScreen::class.qualifiedName
            && currentRoute != Routes.UpdatePassword::class.qualifiedName
            && currentRoute != Routes.EditProfile::class.qualifiedName
            && currentRoute != Routes.PrivacyPolicy::class.qualifiedName
            && currentRoute != Routes.HelpSupport::class.qualifiedName
            && currentRoute != Routes.AboutApp::class.qualifiedName
            && currentRoute != Routes.RateUs::class.qualifiedName
            && currentRoute != Routes.Notification::class.qualifiedName
            && currentRoute != Routes.PaymentMethods::class.qualifiedName
            && currentRoute != Routes.Chat::class.qualifiedName
    Scaffold(
        floatingActionButton = {
            AnimatedVisibility(
                visible = showBottomBarAndFab,
                enter = slideInVertically(
                    initialOffsetY = { it }
                ) + fadeIn(),
                exit = slideOutVertically(
                    targetOffsetY = { it }
                ) + fadeOut()
            ) {
                Box(
                    modifier = Modifier.offset(y = 85.dp)
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
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButtonPosition = FabPosition.Center,
        bottomBar = {
                AnimatedVisibility(
                    visible = showBottomBarAndFab,
                    enter = slideInVertically(
                        initialOffsetY = {it}
                    )+ fadeIn(),
                    exit = slideOutVertically(
                        targetOffsetY = {it}
                    )+ fadeOut()
                ) {
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
                            navController.navigate(Routes.HomeUi) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
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
                .padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            navigation<SubNavigation.MainHomeScreen>(
                startDestination = Routes.HomeUi
            ) {
                composable<Routes.HomeUi> {
                    HomeScreen(navController,
                        authViewModel = authViewModel,
                        notificationViewModel = notificationViewModel,
                        transactionVm = transactionViewModel)
                }
                composable<Routes.Profile> {
                    ProfileScreen(navController,authViewModel)
                }
                composable<Routes.EditProfile> {
                    EditProfileScreen(navController, authViewModel = authViewModel)
                }
                composable<Routes.Statistics> {
                    StatisticsScreen(navController, transactionVm = transactionViewModel)
                }
                composable<Routes.History> {
                    HistoryScreen(navController = navController, transactionVm = transactionViewModel)
                }
                composable<Routes.UpdatePassword> {
                    UpdatePassword(
                        navController = navController,
                        authViewModel = authViewModel
                    )
                }
                composable<Routes.PrivacyPolicy> {
                    PrivacyPolicyScreen(navController)
                }
                composable<Routes.HelpSupport> {
                    HelpSupportScreen(navController)
                }
                composable<Routes.AboutApp> {
                    AboutAppScreen(navController)
                }
                composable<Routes.RateUs> {
                    RateUsScreen(navController)
                }
                composable<Routes.PaymentMethods> {
                    PaymentMethodsScreen(navController)
                }
                composable<Routes.Notification> {
                    NotificationScreen(navController,
                        viewModel = notificationViewModel)
                }
                composable<Routes.Chat> {
                    ChatScreen(navController)
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
