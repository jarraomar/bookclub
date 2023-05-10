package hu.ait.bookclub.ui.screen.mainpage

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import hu.ait.bookclub.data.Book
import hu.ait.bookclub.ui.navigation.Screen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

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
                        color = Color(0xFF705E5C),
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
                            Text(text = title, fontFamily = FontFamily.Serif, color = Color(0xFF705E5C))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Top Trending Books",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF705E5C),
                    fontFamily = FontFamily.Serif
                )

                Spacer(modifier = Modifier.height(16.dp))

                TopTrendingBooks()

            }
        }
    )
}


@Composable
fun TopTrendingBooks() {
    val retrofit = remember {
        Retrofit.Builder()
            .baseUrl("https://www.googleapis.com/books/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    val service = retrofit.create(MainScreenViewModel.GoogleBooksService::class.java)

    var books by remember { mutableStateOf(listOf<Book>()) }

    var errorMessage by remember { mutableStateOf("") }

    // Fetch the top 10 trending books
    LaunchedEffect(true) {
        val response = withContext(Dispatchers.IO) {
            service.searchBooks("bestseller", 5, "relevance")
        }
        if (response.isSuccessful) {
            val bookItems = response.body()?.items ?: emptyList()
            books = bookItems.map { bookItem ->
                Book(
                    id = bookItem.id,
                    title = bookItem.volumeInfo.title,
                    author = bookItem.volumeInfo.authors?.joinToString(", ") ?: "",
                    imageUrl = bookItem.volumeInfo.imageLinks?.thumbnail ?: "",
                )
            }
        } else {
            errorMessage = response.message()
        }
    }

    if (errorMessage.isNotEmpty()) {
        Text(errorMessage)
    } else {

        LazyColumn {
            items(books.size) { index ->
                val book = books[index]

                Text(text = book.title, fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Text(text = book.author, fontFamily = FontFamily.Serif, fontSize = 14.sp)
                BookCoverImage(url = book.imageUrl)
            }
        }
    }
}

@Composable
fun BookCoverImage(url: String) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(url)
            .crossfade(true)
            .build(),
        contentDescription = "Image",
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(100.dp)
            .clip(RectangleShape)
    )
}