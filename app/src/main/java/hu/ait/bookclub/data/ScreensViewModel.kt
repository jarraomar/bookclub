package hu.ait.bookclub.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

class ScreensViewModel {

    interface GoogleBooksService {
        // Existing function for trending books
        @GET("volumes")
        suspend fun searchBooks(
            @Query("q") query: String,
            @Query("maxResults") maxResults: Int,
            @Query("orderBy") orderBy: String
        ): retrofit2.Response<SearchResults>

        // New function for book search
        @GET("volumes")
        suspend fun searchBooksByQuery(
            @Query("q") query: String,
            @Query("maxResults") maxResults: Int
        ): retrofit2.Response<SearchResults>
    }


    private val service: GoogleBooksService = Retrofit.Builder()
        .baseUrl("https://www.googleapis.com/books/v1/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(GoogleBooksService::class.java)

    suspend fun searchBooks(query: String, maxResults: Int = 10, orderBy: String = "relevance"): List<BookItem> {
        val response = service.searchBooks(query, maxResults, orderBy)
        return if (response.isSuccessful) {
            response.body()?.items ?: emptyList()
        } else {
            emptyList()
        }
    }

    //Api responses
    data class SearchResults(
        val items: List<BookItem>
    )

    data class BookItem(
        val id: String,
        val volumeInfo: VolumeInfo
    )

    data class VolumeInfo(
        val title: String,
        val authors: List<String>?,
        val imageLinks: ImageLinks?,
    )

    data class ImageLinks(
        val thumbnail: String?,
    )
}