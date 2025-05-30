package etf.ri.rma.newsfeedapp.dto

import com.google.gson.annotations.SerializedName
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

interface NewsApi{
    @GET("news/top")
    suspend fun getTopStories(
        @Query("api_token") apiKey: String,
        @Query("categories") category: String,
        @Query("locale") locale: String = "us"
    ): NewsResponse
}

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


