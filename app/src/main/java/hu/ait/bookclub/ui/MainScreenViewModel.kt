package hu.ait.bookclub.ui

import android.app.appsearch.SearchResult
import com.google.android.gms.common.api.Response
import retrofit2.http.GET
import retrofit2.http.Query

class MainScreenViewModel {


    interface GoogleBooksService {
        @GET("volumes")
        suspend fun searchBooks(
            @Query("q") query: String,
            @Query("maxResults") maxResults: Int,
            @Query("orderBy") orderBy: String
        ): retrofit2.Response<hu.ait.bookclub.ui.MainScreenViewModel.SearchResults>
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