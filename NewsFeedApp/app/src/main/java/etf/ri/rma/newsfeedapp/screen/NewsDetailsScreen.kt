package etf.ri.rma.newsfeedapp.screen

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import etf.ri.rma.newsfeedapp.data.NewsData
import etf.ri.rma.newsfeedapp.model.NewsItem
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("SimpleDateFormat")
@Composable
fun NewsDetailsScreen(
    navController: NavController,
    newsId: String?
) {

    val newsList = remember { NewsData.getAllNews() }

    val currentNews = remember(newsId) { newsList.find { it.id == newsId } }

    if (currentNews == null) {
        Text("Vijest nije pronađena.")

        LaunchedEffect(Unit) {
            navController.popBackStack()
        }
        return
    }

    val relatedNews = remember(currentNews.id, newsList) {
        val formatter = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

        val currentNewsDate = try { formatter.parse(currentNews.publishedDate) } catch (e: Exception) { null }

        if (currentNewsDate == null) {
            emptyList()
        } else {
            newsList
                .filter { it.category == currentNews.category && it.id != currentNews.id }
                .mapNotNull { newsItem ->
                    try {
                        val newsDate = formatter.parse(newsItem.publishedDate)
                        if (newsDate != null) Pair(newsItem, abs(currentNewsDate.time - newsDate.time)) else null
                    } catch (e: Exception) {
                        null
                    }
                }

                .sortedWith(compareBy<Pair<NewsItem, Long>> { it.second }
                    .thenBy { it.first.title }
                )

                .take(2)

                .map { it.first }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalji vijesti") },
                navigationIcon = {

                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Nazad")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {

            Text(currentNews.title, style = MaterialTheme.typography.headlineMedium, modifier = Modifier.testTag("details_title"))
            Spacer(modifier = Modifier.height(8.dp))
            Text(currentNews.snippet, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.testTag("details_snippet"))
            Spacer(modifier = Modifier.height(8.dp))
            Text("Kategorija: ${currentNews.category}", style = MaterialTheme.typography.bodySmall, modifier = Modifier.testTag("details_category"))
            Text("Izvor: ${currentNews.source}", style = MaterialTheme.typography.bodySmall, modifier = Modifier.testTag("details_source"))
            Text("Datum objave: ${currentNews.publishedDate}", style = MaterialTheme.typography.bodySmall, modifier = Modifier.testTag("details_date"))

            Spacer(modifier = Modifier.height(24.dp))

            if (relatedNews.isNotEmpty()) {
                Text("Povezane vijesti iz iste kategorije", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))

                relatedNews.forEachIndexed { index, newsItem ->
                    Text(
                        newsItem.title,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        textDecoration = TextDecoration.Underline,
                        modifier = Modifier
                            .testTag("related_news_title_${index + 1}")
                            .clickable {
                                navController.navigate("/details/${newsItem.id}") {
                                    popUpTo("/details/{newsId}") {
                                        inclusive = true
                                    }
                                    launchSingleTop = true
                                }
                            }
                            .padding(vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("details_close_button")
            ) {
                Text("Zatvori detalje")
            }
        }
    }
}