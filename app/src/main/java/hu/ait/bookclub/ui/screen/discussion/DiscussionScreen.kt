package hu.ait.bookclub.ui.screen.discussion

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import hu.ait.bookclub.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Discussion(
    navController: NavController,
    onWriteNewPostClick: () -> Unit = {},
    writeDiscussionScreensViewModel: DiscussionScreenViewModel = viewModel()){
    val snackbarHostState = remember { SnackbarHostState() }
    val postListState = writeDiscussionScreensViewModel.postsList().collectAsState(
        initial = WriteDiscussionScreenState.Init)
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
        },
        floatingActionButton = {
            MainFloatingActionButton(
                onWriteNewPostClick = onWriteNewPostClick,
                snackbarHostState = snackbarHostState
            )}
    ) {
        contentPadding ->
        Column(modifier = androidx.compose.ui.Modifier.padding(contentPadding)) {}
    }
}


@Composable
fun MainFloatingActionButton(
    onWriteNewPostClick: () -> Unit = {},
    snackbarHostState: SnackbarHostState
) {
    val coroutineScope = rememberCoroutineScope()

    FloatingActionButton(
        onClick = {
            onWriteNewPostClick()
        },
        containerColor = MaterialTheme.colorScheme.secondary,
        shape = RoundedCornerShape(16.dp),
    ) {
        Icon(
            imageVector = Icons.Rounded.Add,
            contentDescription = "Add",
            tint = Color.White,
        )
    }
}