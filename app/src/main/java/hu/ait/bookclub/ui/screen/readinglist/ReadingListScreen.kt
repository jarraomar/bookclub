package hu.ait.bookclub.ui.screen.readinglist

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.firebase.auth.FirebaseAuth
import hu.ait.bookclub.data.Book
import hu.ait.bookclub.data.ScreensViewModel
import hu.ait.bookclub.ui.navigation.Screen
import hu.ait.bookclub.ui.screen.loginscreen.LoginViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReadingList(navController: NavController) {
    var showTextButton by remember { mutableStateOf(false) }
    var editValue by remember { mutableStateOf("") }
    var bookList by remember { mutableStateOf(listOf<String>()) }
    var showReadingListContent by remember { mutableStateOf(true) }
    var selectedBookIndex by remember { mutableStateOf(-1) }

    val loginViewModel: LoginViewModel = viewModel()

    LaunchedEffect(Unit) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            val readingList = loginViewModel.getReadingList(userId)
            if (readingList != null) {
                bookList = readingList
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Reading List") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate(Screen.Main.route) }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    showTextButton = true
                    showReadingListContent = false
                }
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add")
            }
        }
    ) {
        Column(modifier = Modifier.padding(top = 50.dp)) {
            Spacer(modifier = Modifier.height(16.dp))
            if (showReadingListContent) {
                ReadingListContent(
                    bookList = bookList,
                    onBookClick = { index, editedBookName ->
                        selectedBookIndex = index
                        showTextButton = true
                        showReadingListContent = false
                        editValue = editedBookName
                    },
                    onBookDelete = { index ->
                        val updatedBookList = bookList.toMutableList()
                        updatedBookList.removeAt(index)
                        bookList = updatedBookList
                    }
                )
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    if (showTextButton) {
                        Card(
                            modifier = Modifier.padding(16.dp),
                            content = {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    TextField(
                                        value = editValue,
                                        onValueChange = { editValue = it },
                                        label = { Text("Insert Book Name Here") },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Button(
                                            onClick = {
                                                if (editValue.isNotBlank()) {
                                                    if (selectedBookIndex != -1) {
                                                        bookList = bookList.toMutableList().apply {
                                                            set(selectedBookIndex, editValue)
                                                        }
                                                        selectedBookIndex = -1
                                                    } else {
                                                        bookList = bookList + editValue
                                                    }
                                                    editValue = ""

                                                    val userId = FirebaseAuth.getInstance().currentUser?.uid
                                                    if (userId != null) {
                                                        loginViewModel.saveReadingList(userId, bookList)
                                                    }

                                                }
                                                showReadingListContent = true
                                            },
                                            modifier = Modifier.padding(end = 8.dp)
                                        ) {
                                            Text("Enter Book to Reading List")
                                        }
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun ReadingListContent(bookList: List<String>, onBookClick: (Int, String) -> Unit, onBookDelete: (Int) -> Unit) {
    val retrofit = remember {
        Retrofit.Builder()
            .baseUrl("https://www.googleapis.com/books/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    val service = retrofit.create(ScreensViewModel.GoogleBooksService::class.java)

    val coroutineScope = rememberCoroutineScope()
    var books by remember { mutableStateOf(listOf<Book>()) }
    var showDialog by remember { mutableStateOf(false) }
    var editedBookName by remember { mutableStateOf("") }
    val selectedIndex by remember { mutableStateOf(0) }

    val loginViewModel: LoginViewModel = viewModel()

    LaunchedEffect(bookList) {
        // Fetch book information for each book in the list
        coroutineScope.launch {
            val bookInfoDeferreds = bookList.map { bookTitle ->
                async { service.searchBooks(bookTitle, 1, "relevance") }
            }
            val bookInfoResponses = bookInfoDeferreds.awaitAll()

            books = bookInfoResponses.mapIndexedNotNull { index, response ->
                if (response.isSuccessful) {
                    val bookItems = response.body()?.items ?: emptyList()
                    if (bookItems.isNotEmpty()) {
                        val bookItem = bookItems[0]
                        val imageUrl = bookItem.volumeInfo.imageLinks?.thumbnail ?: ""
                        val updatedImageUrl = if (imageUrl.isNotEmpty() && imageUrl != bookItem.volumeInfo.imageLinks?.thumbnail) {
                            // Book image URL has changed, force refresh the image by appending a timestamp query parameter
                            "$imageUrl?timestamp=${System.currentTimeMillis()}"
                        } else {
                            imageUrl
                        }
                        Book(
                            id = bookItem.id,
                            title = bookItem.volumeInfo.title,
                            author = bookItem.volumeInfo.authors?.joinToString(", ") ?: "",
                            imageUrl = updatedImageUrl,
                        )
                    } else {
                        null
                    }
                } else {
                    null
                }
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        itemsIndexed(books) { index, book ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clickable {
                        showDialog = true
                        onBookClick(index, book.title)
                    }
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ListBookCoverImage(url = book.imageUrl)
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 16.dp)
                    ) {
                        Text(text = book.title, fontWeight = FontWeight.Bold)
                        Text(text = book.author)
                    }
                    IconButton(
                        onClick = {
                            // Delete button click logic here
                            val updatedBookList = books.toMutableList()
                            updatedBookList.removeAt(index)
                            books = updatedBookList
                            onBookDelete(index)

                            val userId = FirebaseAuth.getInstance().currentUser?.uid
                            if (userId != null) {
                                loginViewModel.saveReadingList(userId, bookList)
                            }

                        }
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = Color.Red
                        )
                    }
                }
            }


        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Edit Book Name") },
            text = {
                TextField(
                    value = editedBookName,
                    onValueChange = { editedBookName = it },
                    label = { Text("New Book Name") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        val updatedBookName = editedBookName
                        val selectedBook = books[selectedIndex]
                        val updatedBook = selectedBook.copy(title = updatedBookName)

                        val updatedBookList = books.toMutableList().apply {
                            set(selectedIndex, updatedBook)
                        }
                        books = updatedBookList

                        coroutineScope.launch {
                            val response = service.searchBooks(updatedBookName, 1, "relevance")
                            if (response.isSuccessful) {
                                val bookItems = response.body()?.items ?: emptyList()
                                if (bookItems.isNotEmpty()) {
                                    val bookItem = bookItems[0]
                                    val imageUrl = bookItem.volumeInfo.imageLinks?.thumbnail ?: ""
                                    val updatedImageUrl = if (imageUrl.isNotEmpty() && imageUrl != bookItem.volumeInfo.imageLinks?.thumbnail) {
                                        // Book image URL has changed, force refresh the image by appending a timestamp query parameter
                                        "$imageUrl?timestamp=${System.currentTimeMillis()}"
                                    } else {
                                        imageUrl
                                    }
                                    val updatedBookWithDetails = updatedBook.copy(
                                        author = bookItem.volumeInfo.authors?.joinToString(", ") ?: "",
                                        imageUrl = updatedImageUrl
                                    )

                                    val updatedBooks = books.toMutableList()
                                    updatedBooks[selectedIndex] = updatedBookWithDetails
                                    books = updatedBooks
                                }
                            }
                        }

                        showDialog = false
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}


@Composable
fun ListBookCoverImage(url: String) {
    Log.d("BookCoverImage", "Loading image from URL: $url")
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






