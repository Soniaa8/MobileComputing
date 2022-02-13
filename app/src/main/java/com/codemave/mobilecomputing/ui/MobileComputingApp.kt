package com.codemave.mobilecomputing.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.codemave.mobilecomputing.ui.home.Home
import com.codemave.mobilecomputing.ui.login.Login
import com.codemave.mobilecomputing.ui.login.Help
import com.codemave.mobilecomputing.ui.login.Help2
import com.codemave.mobilecomputing.ui.login.Help3
import com.codemave.mobilecomputing.ui.payment.Payment
import com.codemave.mobilecomputing.ui.payment.Edit

@Composable
fun MobileComputingApp(
    appState: MobileComputingAppState = rememberMobileComputingAppState()
) {
    NavHost(
        navController = appState.navController,
        startDestination = "login"
    ) {
        composable(route = "login") {
            Login(navController = appState.navController)
        }
        composable(route = "Help") {
            Help(
                navController = appState.navController,
                onBackPress = appState::navigateBack
            )
        }
        composable(route = "Help2") {
            Help2(
                navController = appState.navController,
                onBackPress = appState::navigateBack
            )
        }
        composable(route = "Help3") {
            Help3(
                navController = appState.navController,
                onBackPress = appState::navigateBack
            )
        }
        composable(route = "home") {
            Home(
                navController = appState.navController,
                onBackPress = appState::navigateBack
            )
        }
        composable(route = "payment") {
            Payment(onBackPress = appState::navigateBack)
        }
        composable(route = "edit/{paymentId}") {
            entry ->Edit(onBackPress = appState::navigateBack,
            entry.arguments?.getString("paymentId")?:""
            )
        }

    }
}