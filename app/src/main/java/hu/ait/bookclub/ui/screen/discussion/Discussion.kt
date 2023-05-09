package hu.ait.bookclub.ui.screen.discussion

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import hu.ait.bookclub.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Discussion(navController: NavController){
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Discussion") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate(Screen.Main.route) }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) {

    }
}