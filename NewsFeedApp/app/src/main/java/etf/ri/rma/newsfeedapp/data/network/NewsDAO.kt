package etf.ri.rma.newsfeedapp.data.network

import com.google.gson.annotations.SerializedName
import etf.ri.rma.newsfeedapp.data.network.api.NewsApiService
import etf.ri.rma.newsfeedapp.data.network.exception.InvalidUUIDException
import etf.ri.rma.newsfeedapp.model.NewsItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

data class NewsResponse(
    @SerializedName("data") val data: List<NewsItemApi>
)

data class NewsItemApi(
    @SerializedName("uuid") val uuid: String,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String?,
    @SerializedName("published_at") val publishedAt: String,
    @SerializedName("category") val category: String,
    @SerializedName("url") val url: String?,
    @SerializedName("image_url") val imageUrl: String?
)

object RetrofitInstance {
    private const val BASE_URL = "https://api.thenewsapi.com/v1/"

    val api: NewsApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NewsApiService::class.java)
    }
}

class NewsDAO {
    private val cachedCategoryStories = mutableMapOf<String, Pair<Long, List<NewsItem>>>()
    private val cacheTimeoutMillis = 30_000L

    private val apiKey = "Jty4AzGDU4uF8ImaRbXhdpqZ1XcQuE5rLxhhLl6H"
    private var externalApi: NewsApiService? = null

    constructor(apiService: NewsApiService) {
        this.externalApi = apiService
    }

    constructor()

    fun setApiService(apiService: NewsApiService) {
        this.externalApi = apiService
    }

    private val api: NewsApiService
        get() = externalApi ?: RetrofitInstance.api

    suspend fun getTopStoriesByCategory(category: String): List<NewsItem> {
        val allowedCategories = listOf(
            "general", "science", "sports", "business",
            "health", "entertainment", "tech", "politics",
            "food", "travel"
        )

        require(category in allowedCategories) { "Invalid category" }

        val now = System.currentTimeMillis()
        val cached = cachedCategoryStories[category]

        if (cached != null && now - cached.first < cacheTimeoutMillis) {
            return cached.second
        }

        val response = api.getTopStories(apiKey, category)
        val newStories = response.data.map { it.toModel() }

        cachedCategoryStories[category] = now to newStories

        return newStories
    }

    fun getAllStories(): List<NewsItem> {
        return cachedCategoryStories.values.flatMap { it.second }
    }


    private val similarCache: MutableMap<String, List<NewsItem>> = mutableMapOf()

    suspend fun getSimilarStories(uuid: String): List<NewsItem> {
        if (!uuid.matches(Regex("^[0-9a-fA-F-]{36}$"))) {
            throw InvalidUUIDException("Neispravan uuid!")
        }

        val cached = similarCache[uuid]
        if (cached != null) {
            return cached
        }

        val response = api.getSimilarStories(uuid)
        val newsList = response.data.map { it.toModel() }
        similarCache[uuid] = newsList
        return newsList
    }


    suspend fun getSimilarWithSource(uuidnews: String, source: String): String? = withContext(
        Dispatchers.IO){
        if (!uuidnews.matches(Regex("^[0-9a-fA-F-]{36}$"))) {
            throw InvalidUUIDException("Neispravan uuid!")
        }

        try {
            val response = api.getSimilarWithSource(uuidnews, source)

            if (response.data.isNotEmpty()) {
                response.data[0].url
            } else {
                null
            }
        } catch (e: IndexOutOfBoundsException) {
            println("Nema vijesti : $uuidnews, source: $source")
            null
        } catch (e: Exception) {
            println("Greska: ${e.message}")
            null
        }
    }

}

fun NewsItemApi.toModel(): NewsItem {
    val extractedCategory = this.category?.split(",")?.firstOrNull()?.trim() ?: "unknown"

    val extractedDate = try {
        val zonedDateTime = ZonedDateTime.parse(this.publishedAt)
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        zonedDateTime.format(formatter)
    } catch (e: DateTimeParseException) {
        "unknown"
    }

    return NewsItem(
        uuid = this.uuid,
        title = this.title,
        snippet = this.description ?: "No description available",
        imageUrl = this.imageUrl,
        category = extractedCategory,
        isFeatured = false,
        source = this.url ?: "",
        publishedDate = extractedDate,
        imageTags = arrayListOf()
    )
}