package hu.ait.bookclub.ui.screen.discussion

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import hu.ait.bookclub.data.DiscussionPost
import hu.ait.bookclub.data.DiscussionWithId
import hu.ait.bookclub.ui.screen.discussion.writediscussion.WriteDiscussionViewModel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

sealed interface WriteDiscussionScreenState {
    object Init : WriteDiscussionScreenState

    data class Success(val postList: List<DiscussionWithId>) : WriteDiscussionScreenState
    data class Error(val error: String?) : WriteDiscussionScreenState
}


class DiscussionScreenViewModel : ViewModel() {

    var currentUserId: String
    init {
        currentUserId = Firebase.auth.currentUser!!.uid
    }

    fun postsList() = callbackFlow {
        val snapshotListener =
            FirebaseFirestore.getInstance().collection(WriteDiscussionViewModel.COLLECTION_POSTS)
                .addSnapshotListener() { snapshot, e ->
                    val response = if (snapshot != null) {
                        val postList = snapshot.toObjects(DiscussionPost::class.java)
                        val postWithIdList = mutableListOf<DiscussionWithId>()

                        postList.forEachIndexed { index, post ->
                            postWithIdList.add(DiscussionWithId(snapshot.documents[index].id, post))
                        }

                        WriteDiscussionScreenState.Success(
                            postWithIdList
                        )
                    } else {
                        WriteDiscussionScreenState.Error(e?.message.toString())
                    }

                    trySend(response) // emit this value through the flow
                }
        awaitClose {
            snapshotListener.remove()
        }
    }

}