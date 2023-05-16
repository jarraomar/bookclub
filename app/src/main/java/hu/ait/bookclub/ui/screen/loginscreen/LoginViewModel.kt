package hu.ait.bookclub.ui.screen.loginscreen

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.auth.User
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await

sealed interface LoginUiState {
    object Init : LoginUiState
    object Loading : LoginUiState
    object LoginSuccess : LoginUiState
    object RegisterSuccess : LoginUiState
    data class Error(val error: String?) : LoginUiState
}
class LoginViewModel() : ViewModel() {
    var loginUiState: LoginUiState by mutableStateOf(LoginUiState.Init)
    var userId: String? by mutableStateOf(null)
    private var userData: User? = null

    private val db = Firebase.firestore
    private lateinit var auth: FirebaseAuth

    init {
        auth = Firebase.auth
    }

    suspend fun registerUser(email: String, password: String) {
        loginUiState = LoginUiState.Loading
        delay(2000)

        try {
            auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener {
                loginUiState = LoginUiState.RegisterSuccess
            }.addOnFailureListener {
                loginUiState = LoginUiState.Error(it.message)
            }
        } catch (e: java.lang.Exception) {
            loginUiState = LoginUiState.Error(e.message)
        }
    }

    suspend fun loginUser(email: String, password: String) : AuthResult? {
        loginUiState = LoginUiState.Loading

        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()

            loginUiState = LoginUiState.LoginSuccess

            result
        } catch (e: java.lang.Exception) {
            loginUiState = LoginUiState.Error(e.message)

            null
        }
    }

    suspend fun getReadingList(userId: String): List<String>? {
        return try {
            val user = auth.currentUser
            val document = db.collection("users").document(userId).get().await()
            val userData = document.toObject<hu.ait.bookclub.data.User>()

            Log.d("LoginViewModel", "User: $user")
            Log.d("LoginViewModel", "User UID: ${user?.uid}")

            if (user?.uid == userId && userData != null) {
                // User data retrieved successfully
                userData.readingList
            } else {
                // Invalid user or user data is null
                null
            }
        } catch (e: Exception) {
            // Handle any errors
            null
        }
    }

    suspend fun addBookToReadingList(userId: String, bookTitle: String) {
        val documentRef = db.collection("users").document(userId)
        db.runTransaction { transaction ->
            val documentSnapshot = transaction.get(documentRef)
            val readingList = documentSnapshot.toObject<hu.ait.bookclub.data.User>()?.readingList ?: emptyList()
            transaction.update(documentRef, "readingList", readingList + bookTitle)
        }.await()
    }

}