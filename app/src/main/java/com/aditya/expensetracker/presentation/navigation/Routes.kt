package com.aditya.expensetracker.presentation.navigation

import kotlinx.serialization.Serializable


sealed class SubNavigation{
    @Serializable
    object LoginSignUpScreen : SubNavigation()

    @Serializable
    object MainHomeScreen : SubNavigation()

}
sealed class Routes{
    @Serializable
    object HomeUi : Routes()

    @Serializable
    object Profile : Routes()

    @Serializable
    object Statistics : Routes()

    @Serializable
    object Setting : Routes()

}
