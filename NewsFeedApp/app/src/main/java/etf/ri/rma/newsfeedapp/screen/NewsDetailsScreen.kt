package etf.ri.rma.newsfeedapp.screen

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
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
import java.util.Date
import java.util.Locale
import kotlin.math.abs
import kotlinx.coroutines.launch
import coil.compose.AsyncImage
import android.util.Log
import androidx.compose.ui.platform.LocalContext
import etf.ri.rma.newsfeedapp.data.NewsDatabase
import etf.ri.rma.newsfeedapp.data.network.ImagaDAO
import etf.ri.rma.newsfeedapp.data.network.exception.InvalidImageURLException
import etf.ri.rma.newsfeedapp.model.TagEntity
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("SimpleDateFormat")
@Composable
fun NewsDetailsScreen(
    navController: NavController,
    newsId: String?
) {
    val context = LocalContext.current
    val newsDatabase = NewsDatabase.getDatabase(context)


    val coroutineScope = rememberCoroutineScope()

    Log.d("NewsDetailsScreen", "Received newsId: $newsId")

    val newsList by produceState<List<NewsItem>>(emptyList()) {
        value = NewsRepository.getAllCachedNews()
        Log.d("NewsDetailsScreen", "NewsList updated, size: ${value.size}")
    }

    val currentNews: NewsItem? = remember(newsId, newsList) {
        val foundNews = newsList.find { it.uuid == newsId }
        Log.d("NewsDetailsScreen", "Finding currentNews: ${foundNews?.title}")
        foundNews
    }

    var imageTags by remember { mutableStateOf<List<String>>(emptyList()) }
    var isLoadingTags by remember { mutableStateOf(false) }
    var tagsError by remember { mutableStateOf<String?>(null) }

    var similarNews by remember { mutableStateOf<List<NewsItem>>(emptyList()) }
    var isLoadingSimilar by remember { mutableStateOf(false) }
    var similarError by remember { mutableStateOf<String?>(null) }

    if (newsList.isEmpty() || currentNews == null) {
        if (newsList.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
                Text("Učitavanje vijesti...")
            }
        } else {
            Text("Vijest nije pronađena.")
        }
        return
    }
    LaunchedEffect(currentNews.uuid) {
        Log.d("NewsDetailsScreen", "Fetching tags for: ${currentNews.title}")

        if (!currentNews.imageUrl.isNullOrEmpty()) {
            if (currentNews.imageTags.isNotEmpty()) {
                Log.d("NewsDetailsScreen", "Using existing tags: ${currentNews.imageTags.size}")
                imageTags = currentNews.imageTags.map { it.value }
            } else {
                isLoadingTags = true
                tagsError = null

                try {
                    val isOnline = NetworkUtils.hasInternetAccess(context)
                    Log.d("NewsDetailsScreen", "Network available: $isOnline")

                    if (isOnline) {
                        Log.d("NewsDetailsScreen", "Fetching tags from API...")
                        val imagaDAO = ImagaDAO()
                        val tagsFromApi = imagaDAO.getTags(currentNews.imageUrl)
                        Log.d("NewsDetailsScreen", "Tags from API: $tagsFromApi")
                        imageTags = tagsFromApi

                        try {
                            val dao = newsDatabase.savedNewsDAO()
                            dao.saveNews(currentNews)
                            val savedNews = dao.getNewsByUuid(currentNews.uuid)
                            if (savedNews != null) {
                                dao.addTags(tagsFromApi, savedNews.id.toInt())
                                Log.d("NewsDetailsScreen", "Tags saved to database")
                            } else {
                                Log.e("NewsDetailsScreen", "News not found in DB after save")
                            }
                        } catch (dbError: Exception) {
                            Log.e("NewsDetailsScreen", "Error saving to database: ${dbError.message}")
                        }

                    } else {
                        val dao = newsDatabase.savedNewsDAO()
                        val savedNews = dao.getNewsByUuid(currentNews.uuid)
                        val tagsFromDb = savedNews?.let {
                            dao.getTags(it.id.toInt())
                        } ?: emptyList()

                        Log.d("NewsDetailsScreen", "Tags from DB: $tagsFromDb")
                        imageTags = tagsFromDb
                        tagsError = if (tagsFromDb.isEmpty()) {
                            "Nema sačuvanih tagova (offline)"
                        } else {
                            "Korišteni tagovi iz baze (offline)"
                        }
                    }

                } catch (e: Exception) {
                    Log.e("NewsDetailsScreen", "Error fetching tags from API: ${e.message}")
                    if (e.message?.contains("coroutine scope left") != true) {
                        try {
                            val dao = newsDatabase.savedNewsDAO()
                            val savedNews = dao.getNewsByUuid(currentNews.uuid)
                            val tagsFromDb = savedNews?.let {
                                dao.getTags(it.id.toInt())
                            } ?: emptyList()

                            Log.d("NewsDetailsScreen", "Fallback - Tags from DB: $tagsFromDb")
                            imageTags = tagsFromDb
                            tagsError = if (tagsFromDb.isEmpty()) {
                                "Nema sačuvanih tagova"
                            } else {
                                "Korišteni tagovi iz baze (offline)"
                            }
                        } catch (dbError: Exception) {
                            Log.e("NewsDetailsScreen", "Error fetching tags from DB: ${dbError.message}")
                            tagsError = "Greška pri učitavanju tagova iz baze"
                        }
                    }
                } finally {
                    isLoadingTags = false
                }
            }
        } else {
            Log.d("NewsDetailsScreen", "News has no image URL, loading tags from DB...")
            val dao = newsDatabase.savedNewsDAO()
            val savedNews = dao.getNewsByUuid(currentNews.uuid)
            val tagsFromDb = savedNews?.let {
                dao.getTags(it.id.toInt())
            } ?: emptyList()

            Log.d("NewsDetailsScreen", "Tags from DB: $tagsFromDb")
            imageTags = tagsFromDb
            tagsError = if (tagsFromDb.isEmpty()) {
                "Nema sačuvanih tagova (offline)"
            } else {
                "Korišteni tagovi iz baze (offline)"
            }
        }
    }


    LaunchedEffect(currentNews.uuid) {
        isLoadingSimilar = true
        similarError = null
        try {
            val similar = NewsRepository.getSimilarStories(currentNews.uuid)
            similarNews = similar
        } catch (e: Exception) {
            similarError = "Greška pri učitavanju sličnih vijesti: ${e.message}"
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
                .sortedWith(compareBy<Pair<NewsItem, Long>> { it.second }.thenBy { it.first.title })
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
                Text("Tagovi slike:", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                when {
                    isLoadingTags -> CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    imageTags.isNotEmpty() -> Text(
                        imageTags.joinToString(", "),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.testTag("details_image_tags")
                    )
                    tagsError != null -> Text(
                        tagsError!!,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                    else -> Text(
                        "Nema dostupnih tagova",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            Text("Slične vijesti:", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            when {
                isLoadingSimilar -> CircularProgressIndicator(modifier = Modifier.size(24.dp))
                similarError != null -> Text(similarError!!, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
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
                                        popUpTo("/details/{uuid}") { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                                .padding(vertical = 4.dp)
                        )
                    }
                }
                else -> Text("Nema sličnih vijesti", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (relatedNews.isNotEmpty()) {
                Text("Povezane vijesti iz iste kategorije", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
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
                                    popUpTo("/details/{uuid}") { inclusive = true }
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
object NetworkUtils {
    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                    networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                    networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
        } else {
            @Suppress("DEPRECATION")
            val activeNetwork = connectivityManager.activeNetworkInfo
            activeNetwork?.isConnected == true
        }
    }

    fun hasInternetAccess(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

            networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                    networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
        } else {
            @Suppress("DEPRECATION")
            val activeNetwork = connectivityManager.activeNetworkInfo
            activeNetwork?.isConnectedOrConnecting == true
        }
    }
}