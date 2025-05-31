package etf.ri.rma.newsfeedapp.data

import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApi{
    @GET("news/top")
    suspend fun getTopStories(
        @Query("api_token") apiKey: String,
        @Query("categories") category: String,
        @Query("locale") locale: String = "us",

    ): NewsResponse
}