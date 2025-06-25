package etf.ri.rma.newsfeedapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import etf.ri.rma.newsfeedapp.data.NewsDatabase
import etf.ri.rma.newsfeedapp.data.NewsRepository
import etf.ri.rma.newsfeedapp.screen.FilterScreen
import etf.ri.rma.newsfeedapp.screen.NewsDetailsScreen
import etf.ri.rma.newsfeedapp.screen.NewsFeedScreen
import etf.ri.rma.newsfeedapp.ui.theme.NewsFeedAppTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = NewsDatabase.getDatabase(this)
        NewsRepository.initialize(database)

        setContent {
            NewsFeedAppTheme {
                val navController = rememberNavController()
                AppNavGraph(navController = navController, newsDatabase = database)
            }
        }
    }
}
@Composable
fun AppNavGraph(navController: NavController, newsDatabase: NewsDatabase) {
    NavHost(navController = navController as NavHostController, startDestination = "/home") {
        composable("/home") {
            NewsFeedScreen(navController)
        }
        composable("/filters") {
            FilterScreen(navController)
        }
        composable(
            route = "/details/{uuid}",
            arguments = listOf(navArgument("uuid") { type = NavType.StringType })
        ) { backStackEntry ->
            val newsId = backStackEntry.arguments?.getString("uuid")
            NewsDetailsScreen(navController = navController, newsId = newsId)
        }
    }
}



@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
NewsFeedAppTheme {
   // NewsFeedScreen("/home")
    val navController = rememberNavController()
   // AppNavGraph(navController)
    }
}