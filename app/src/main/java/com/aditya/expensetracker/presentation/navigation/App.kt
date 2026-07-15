package com.aditya.expensetracker.presentation.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
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

data class BottomItem(
    val title: String,
    val icon: ImageVector,
    val route: Routes?
)
@Composable
fun App(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    var showBottomSheet by remember{mutableStateOf(false)}
    if(showBottomSheet){
        AddScreen(onDismiss = {showBottomSheet = false})
    }
    val bottomList = listOf(

        BottomItem(
            title = "Profile",
            icon = Icons.Default.Person,
            route = Routes.Profile
        ),
        BottomItem(
            title = "Statistics",
            icon = Icons.Default.AutoGraph,
            route = Routes.Statistics
        ),
        BottomItem(
            title = "Add",
            icon = Icons.Default.Add,
            route = null
        ),
        BottomItem(
            title = "Home",
            icon = Icons.Default.Home,
            route = Routes.HomeUi
        ),

        BottomItem(
            title = "Setting",
            icon = Icons.Default.Settings,
            route = Routes.Setting
        )
    )
    val currentEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentEntry?.destination?.route

    Scaffold(

        bottomBar = {



            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.surface,
                contentPadding = PaddingValues(0.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    bottomList.forEach { item ->
                        NavigationBarItem(
                            selected = item.route!=null&&item.route::class.qualifiedName == currentRoute,
                            onClick = {
                                if(item.route==null){
                                    showBottomSheet = true
                                }else{
                                    if(item.route::class.qualifiedName != currentRoute){
                                        navController.navigate(item.route) {
                                            launchSingleTop = true
                                        }
                                    }

                                }


                            },
                            icon = {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.title,
                                    modifier = Modifier.size(24.dp)
                                )
                            },
                            label = { Text(text = item.title) }
                        )
                    }
                }
            }


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
                    HomeScreen()
                }
                composable<Routes.Profile> {
                    ProfileScreen()
                }
                composable<Routes.Statistics> {
                    StatisticsScreen()
                }
                composable<Routes.Setting> {
                    SettingScreen()
                }

//                navigation<SubNavigation.LoginSignUpScreen>(
//                    startDestination = Routes.HomeUi
//                ) {
//
//                }

            }
        }
    }
}
