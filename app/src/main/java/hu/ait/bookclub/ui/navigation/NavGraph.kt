package hu.ait.bookclub.ui.navigation


import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import hu.ait.bookclub.ui.screen.loginscreen.LoginScreen
import hu.ait.bookclub.ui.screen.mainpage.MainScreen

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(onLoginSuccess = {
                // navigate to the main messages screen
                navController.navigate(Screen.Main.route)
            })
        }

        composable(Screen.Main.route) {
            MainScreen()
        }

    }
}