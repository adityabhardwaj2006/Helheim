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
    object EditProfile : Routes()

    @Serializable
    object Statistics : Routes()

    @Serializable
    object History : Routes()

    @Serializable
    object LoginScreen : Routes()

    @Serializable
    object SignUpScreen : Routes()

    @Serializable
    object UpdatePassword : Routes()

    @Serializable
    object PrivacyPolicy : Routes()


}
