package hu.ait.bookclub.ui.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Main : Screen("main")
    object ReadingList : Screen("reading_list")
    object Bookshelf : Screen("bookshelf")
    object Discussion : Screen("discussion")
}