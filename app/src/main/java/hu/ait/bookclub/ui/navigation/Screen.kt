package hu.ait.bookclub.ui.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Main : Screen("main")
    object ReadingList : Screen("reading_list")
    object Discussion : Screen("discussion")

    object WriteDiscussion : Screen("writediscussion")
}