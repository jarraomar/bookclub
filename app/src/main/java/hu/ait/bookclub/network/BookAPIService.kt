package hu.ait.bookclub.network

import okhttp3.MediaType.Companion.toMediaType
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query


private const val BASE_URL = "https://www.googleapis.com/books/v1/"


private val retrofit = Retrofit.Builder()
    .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
    .baseUrl(BASE_URL)
    .build()


object BookAPi{
    val retrofitService: BookAPIService by lazy {
        retrofit.create(BookAPIService::class.java)
    }
}


interface BookAPIService{

}