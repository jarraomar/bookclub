package hu.ait.bookclub.ui.screen.discussion.writediscussion

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import hu.ait.bookclub.data.DiscussionPost

sealed interface WriteDiscussionState {
    object Init : WriteDiscussionState
    object LoadingPostUpload : WriteDiscussionState
    object PostUploadSuccess : WriteDiscussionState
    data class ErrorDuringPostUpload(val error: String?) : WriteDiscussionState

}

class WriteDiscussionViewModel : ViewModel() {
    companion object {
        const val COLLECTION_POSTS = "posts"
    }

    var writeDiscussionUIState: WriteDiscussionState by mutableStateOf(WriteDiscussionState.Init)
    private lateinit var auth: FirebaseAuth

    init {
        //auth = FirebaseAuth.getInstance()
        auth = Firebase.auth
    }
    fun uploadPost(title: String, postBody: String) {
        writeDiscussionUIState = WriteDiscussionState.LoadingPostUpload

        val myPost = DiscussionPost(
            uid = auth.currentUser!!.uid,
            author = auth.currentUser!!.email!!,
            title = title,
            body = postBody
        )

        val postsCollection = FirebaseFirestore.getInstance().collection(COLLECTION_POSTS)

        postsCollection.add(myPost).addOnSuccessListener {
            writeDiscussionUIState = WriteDiscussionState.PostUploadSuccess
        }.addOnFailureListener{
            writeDiscussionUIState = WriteDiscussionState.ErrorDuringPostUpload(it.message)
        }
    }


}