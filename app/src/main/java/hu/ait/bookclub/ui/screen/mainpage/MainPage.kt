package hu.ait.bookclub.ui.screen.mainpage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Yellow
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import hu.ait.bookclub.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Welcome to your Library!",
                        fontFamily = FontFamily.Serif,
                        fontSize = 30.sp,
                    )
                },
                
            )
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 24.dp)
                    .background(Color(0xFFF2E2CE))
            ) {
                Text(
                    text = "Tabs",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF705E5C),
                    fontFamily = FontFamily.Serif
                )

                Spacer(modifier = Modifier.height(16.dp))

                val tabs = listOf(
                    "Reading List",
                    "Bookshelf",
                    "Discussion"
                )

                var selectedTabIndex by remember { mutableStateOf(3) }

                TabRow(
                    selectedTabIndex = selectedTabIndex,
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = {
                                selectedTabIndex = index;
                                when(selectedTabIndex) {
                                    0 -> {
                                        // Show reading list section
                                        navController.navigate(Screen.ReadingList.route)
                                    }
                                    1 -> {
                                        // Show bookshelf section
                                        navController.navigate(Screen.Bookshelf.route)
                                    }
                                    2 -> {
                                        // Show discussion section
                                        navController.navigate(Screen.Discussion.route)
                                    }
                                }
                            }
                        ) {
                            Text(text = title)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    )
}