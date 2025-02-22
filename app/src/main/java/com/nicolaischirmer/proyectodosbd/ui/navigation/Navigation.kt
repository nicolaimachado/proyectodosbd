package com.nicolaischirmer.proyectodosbd.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.nicolaischirmer.proyectodosbd.data.AuthManager
import com.nicolaischirmer.proyectodosbd.data.FirestoreManager
import com.nicolaischirmer.proyectodosbd.ui.screen.ForgotPasswordScreen
import com.nicolaischirmer.proyectodosbd.ui.screen.LoginScreen
import com.nicolaischirmer.proyectodosbd.ui.screen.ScreenDetalle
import com.nicolaischirmer.proyectodosbd.ui.screen.ScreenInicio
import com.nicolaischirmer.proyectodosbd.ui.screen.SignUpScreen

@Composable
fun Navegacion(auth: AuthManager) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val firestore = FirestoreManager(auth, context)

    NavHost(
        navController = navController,
        startDestination = login
    ) {
        composable<login> {
            LoginScreen(
                auth,
                { navController.navigate(signUp) },
                {
                    navController.navigate(screenInicio) {
                        popUpTo(login) { inclusive = true }
                    }
                },
                { navController.navigate(forgotPassword) }
            )
        }
        composable<signUp> {
            SignUpScreen(
                auth
            ) { navController.popBackStack() }
        }

        composable<forgotPassword> {
            ForgotPasswordScreen(
                auth
            ) {
                navController.navigate(login) {
                    popUpTo(login) { inclusive = true }
                }
            }
        }

        composable<screenInicio> {
            ScreenInicio(
                auth,
                firestore,
                {
                    navController.navigate(login) {
                        popUpTo(screenInicio) { inclusive = true }
                    }
                },
                { id ->
                    navController.navigate(screenDetalle(id))
                }
            )
        }

        composable<screenDetalle> { backStackEntry ->
            val detalle = backStackEntry.toRoute<screenDetalle>()
            val id = detalle.id
            ScreenDetalle(id, auth, firestore) {
                navController.navigate(login) {
                    popUpTo(screenInicio) { inclusive = true }
                }
            }
        }
    }
}