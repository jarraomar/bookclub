package hu.ait.bookclub.data

data class DiscussionPost(
    var uid: String = "",
    var author: String = "",
    var title: String = "",
    var body: String = "",
)

data class DiscussionWithId(
    val postId: String,
    val post: DiscussionPost
)