package etf.ri.rma.newsfeedapp.data

import com.google.gson.annotations.SerializedName
import etf.ri.rma.newsfeedapp.model.NewsItem
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

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

    val api: NewsApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NewsApi::class.java)
    }
}

class NewsDAO(private val apiKey: String) {

    private val cachedStories = mutableListOf<NewsItemApi>()

    suspend fun getTopStoriesByCategory(category: String): List<NewsItem> {
        val allowedCategories = listOf(
            "general", "science", "sports", "business",
            "health", "entertainment", "tech", "politics",
            "food", "travel"
        )

        require(category in allowedCategories) { "Invalid category" }

        val response = RetrofitInstance.api.getTopStories(apiKey, category)
        val top3 = response.data.take(3)
        cachedStories.addAll(top3)
        return top3.map { it.toModel() }
    }

    fun getAllStories(): List<NewsItem> {
        return cachedStories.map { it.toModel() }
    }

    suspend fun getSimilarStories(uuid: String): List<NewsItem> {
        if (!uuid.matches(Regex("^[0-9a-fA-F\\-]{36}$"))) {
            throw InvalidUUIDException("Invalid UUID format: $uuid")
        }

        val reference = cachedStories.find { it.uuid == uuid } ?: return emptyList()
        val sameCategory =
            cachedStories.filter { it.category == reference.category && it.uuid != uuid }
        return sameCategory.take(2).map { it.toModel() }
    }
}
    fun NewsItemApi.toModel(): NewsItem {
    return NewsItem(
        uuid = this.uuid,
        title = this.title,
        snippet = this.description ?: "No description available",
        imageUrl = this.imageUrl,
        category = this.category,
        isFeatured = false,
        source = this.url ?: "",
        publishedDate = this.publishedAt,
        imageTags = arrayListOf()
    )
}

class InvalidUUIDException(message: String) : Exception(message)
