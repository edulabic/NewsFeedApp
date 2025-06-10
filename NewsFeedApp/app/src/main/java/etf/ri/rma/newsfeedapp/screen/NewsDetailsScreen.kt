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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import etf.ri.rma.newsfeedapp.data.NewsRepository
import etf.ri.rma.newsfeedapp.model.NewsItem
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.abs
import kotlinx.coroutines.launch
import coil.compose.AsyncImage
import android.util.Log
import etf.ri.rma.newsfeedapp.data.network.ImagaDAO
import etf.ri.rma.newsfeedapp.data.network.exception.InvalidImageURLException
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("SimpleDateFormat")
@Composable
fun NewsDetailsScreen(
    navController: NavController,
    newsId: String?
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val newsList by produceState<List<NewsItem>>(emptyList()) {
        value = NewsRepository.getAllCachedNews()
        if (newsId != null && value.find { it.uuid == newsId } == null) {
        }
    }

    val currentNews: NewsItem? = remember(newsId, newsList) {
        newsList.find { it.uuid == newsId }
    }

    var imageTags by remember { mutableStateOf<List<String>>(emptyList()) }
    var isLoadingTags by remember { mutableStateOf(false) }
    var tagsError by remember { mutableStateOf<String?>(null) }

    var similarNews by remember { mutableStateOf<List<NewsItem>>(emptyList()) }
    var isLoadingSimilar by remember { mutableStateOf(false) }
    var similarError by remember { mutableStateOf<String?>(null) }

    var nextNewsUrl by remember { mutableStateOf<String?>(null) }
    var isLoadingNextNews by remember { mutableStateOf(false) }
    var nextNewsError by remember { mutableStateOf<String?>(null) }

    if (currentNews == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            if (newsList.isEmpty()) {
                CircularProgressIndicator()
                Text("Učitavanje vijesti...")
            } else {
                Text("Vijest nije pronađena.")
            }
        }
        return
    }

    LaunchedEffect(currentNews.uuid) {
        if (!currentNews.imageUrl.isNullOrEmpty() && currentNews.imageTags.isEmpty()) {
            isLoadingTags = true
            tagsError = null
            try {
                val imagaDAO = ImagaDAO()
                val tags = imagaDAO.getTags(currentNews.imageUrl)
                imageTags = tags
                NewsRepository.getAllCachedNews().find { it.uuid == currentNews.uuid }?.imageTags?.addAll(tags)
                Log.d("NewsDetailsScreen", "Fetched image tags for ${currentNews.title}: $tags")
            } catch (e: InvalidImageURLException) {
                tagsError = "Greška pri učitavanju tagova: ${e.message}"
                Log.e("NewsDetailsScreen", "Invalid image URL for ${currentNews.title}: ${e.message}")
            } catch (e: Exception) {
                tagsError = "Neočekivana greška pri učitavanju tagova: ${e.message}"
                Log.e("NewsDetailsScreen", "Unexpected error fetching image tags for ${currentNews.title}: ${e.message}")
            } finally {
                isLoadingTags = false
            }
        } else if (!currentNews.imageUrl.isNullOrEmpty() && currentNews.imageTags.isNotEmpty()) {
            imageTags = currentNews.imageTags
        } else {
            imageTags = emptyList()
        }
    }

    LaunchedEffect(currentNews.uuid) {
        isLoadingSimilar = true
        similarError = null
        try {
            val similar = NewsRepository.getSimilarStories(currentNews.uuid)
            similarNews = similar
            Log.d("NewsDetailsScreen", "Fetched similar news for ${currentNews.title}: ${similar.map { it.title }}")
        } catch (e: Exception) {
            similarError = "Greška pri učitavanju sličnih vijesti: ${e.message}"
            Log.e("NewsDetailsScreen", "Error fetching similar news for ${currentNews.title}: ${e.message}")
        } finally {
            isLoadingSimilar = false
        }
    }

    val relatedNews = remember(currentNews.uuid, newsList) {
        val formatter = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val currentNewsDate = try { formatter.parse(currentNews.publishedDate) } catch (e: Exception) { null }

        if (currentNewsDate == null) {
            emptyList()
        } else {
            newsList
                .filter { it.category == currentNews.category && it.uuid != currentNews.uuid }
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
            Text(
                currentNews.title,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.testTag("details_title")
            )
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                currentNews.snippet,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.testTag("details_snippet")
            )
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                "Kategorija: ${currentNews.category}",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.testTag("details_category")
            )
            Text(
                "Izvor: ${currentNews.source}",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.testTag("details_source")
            )
            Text(
                "Datum objave: ${currentNews.publishedDate}",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.testTag("details_date")
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (!currentNews.imageUrl.isNullOrEmpty()) {
                AsyncImage(
                    model = currentNews.imageUrl,
                    contentDescription = "Slika vijesti",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .testTag("details_image"),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            if (!currentNews.imageUrl.isNullOrEmpty()) {
                Text(
                    "Tagovi slike:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))

                when {
                    isLoadingTags -> {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    tagsError != null -> {
                        Text(
                            tagsError!!,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                    imageTags.isNotEmpty() -> {
                        Text(
                            imageTags.joinToString(", "),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.testTag("details_image_tags")
                        )
                    }
                    else -> {
                        Text(
                            "Nema dostupnih tagova",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            Text(
                "Slične vijesti:",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            when {
                isLoadingSimilar -> {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp)
                    )
                }
                similarError != null -> {
                    Text(
                        similarError!!,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                similarNews.isNotEmpty() -> {
                    similarNews.forEachIndexed { index, newsItem ->
                        Text(
                            newsItem.title,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary,
                            textDecoration = TextDecoration.Underline,
                            modifier = Modifier
                                .testTag("similar_news_title_${index + 1}")
                                .clickable {
                                    navController.navigate("/details/${newsItem.uuid}") {
                                        popUpTo("/details/{uuid}") {
                                            inclusive = true
                                        }
                                        launchSingleTop = true
                                    }
                                }
                                .padding(vertical = 4.dp)
                        )
                    }
                }
                else -> {
                    Text(
                        "Nema sličnih vijesti",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (relatedNews.isNotEmpty()) {
                Text(
                    "Povezane vijesti iz iste kategorije",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
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
                                navController.navigate("/details/${newsItem.uuid}") {
                                    popUpTo("/details/{uuid}") {
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
                onClick = {
                    Log.d("OpenNextNews", "Kliknuto na dugme")
                    coroutineScope.launch {
                        isLoadingNextNews = true
                        nextNewsError = null
                        nextNewsUrl = null
                        try {
                            val url = NewsRepository.getFirstSimilarNewsUrlFromSource(
                                currentNews.uuid,
                                currentNews.source
                            )
                            Log.d("OpenNextNews", "URL iz repozitorija: $url")

                            if (!url.isNullOrBlank() && url.startsWith("http")) {
                                nextNewsUrl = url
                                withContext(Dispatchers.Main) {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                    context.startActivity(intent)
                                }
                            } else {
                                nextNewsError = "Nije pronađena vijest iz istog izvora"
                            }
                        } catch (e: Exception) {
                            nextNewsError = "Greška: ${e.message}"
                            Log.e("OpenNextNews", "Greška prilikom otvaranja: ${e.message}")
                        } finally {
                            isLoadingNextNews = false
                        }
                    }
                }
                ,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("open_next_button"),
                enabled = !isLoadingNextNews
            ) {
                if (isLoadingNextNews) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Učitavanje...")
                } else {
                    Text("Otvori sljedeću")
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

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