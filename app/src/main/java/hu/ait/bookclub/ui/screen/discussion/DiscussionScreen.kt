package hu.ait.bookclub.ui.screen.discussion

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.ArrowBack

import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

import androidx.navigation.NavController

import hu.ait.bookclub.data.DiscussionPost
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
        Column(modifier = androidx.compose.ui.Modifier.padding(contentPadding)) {
            if (postListState.value == WriteDiscussionScreenState.Init) {
                Text(text = "Initializing..")
            } else if (postListState.value is WriteDiscussionScreenState.Success) {
                //Text(text = "Messages number: " +
                //        "${(postListState.value as MainScreenUIState.Success).postList.size}")

                LazyColumn() {
                    items((postListState.value as WriteDiscussionScreenState.Success).postList){
                        PostCard(post = it.post,
                        onRemoveItem = {
                            writeDiscussionScreensViewModel.deletePost(it.postId)
                        },
                        currentUserId = writeDiscussionScreensViewModel.currentUserId)
                    }
                }
            }
        }
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

@Composable
fun PostCard(
    post: DiscussionPost,
    onRemoveItem: () -> Unit = {},
    currentUserId: String = ""
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 10.dp
        ),
        modifier = Modifier.padding(5.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(10.dp)
        ) {
            Row(
                modifier = Modifier.padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                ) {
                    Text(
                        text = post.title,
                    )
                    Text(
                        text = post.body,
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(text = post.author)

                    if (currentUserId.equals(post.uid)) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "Delete",
                            modifier = Modifier.clickable {
                                onRemoveItem()
                            },
                            tint = Color.Red
                        )
                    }
                }
            }

        }
    }
}
