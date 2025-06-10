package etf.ri.rma.newsfeedapp.data.network.api

import etf.ri.rma.newsfeedapp.data.network.NewsResponse
import etf.ri.rma.newsfeedapp.model.NewsItem
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApiService{
    @GET("news/top")
    suspend fun getTopStories(
        @Query("api_token") apiKey: String = "Jty4AzGDU4uF8ImaRbXhdpqZ1XcQuE5rLxhhLl6H",
        @Query("categories") category: String,
        @Query("locale") locale: String = "us",

        ): NewsResponse

    @GET("news/similar")
    suspend fun getSimilarStories(
        @Query("uuid") uuid: String,
        @Query("api_token") apiKey: String = "Jty4AzGDU4uF8ImaRbXhdpqZ1XcQuE5rLxhhLl6H"
    ): NewsResponse

    @GET("news/similar")
    suspend fun getSimilarWithSource(
        @Query("uuid") uuid: String,
        @Query("source") source: String,
        @Query("api_token") apiKey: String = "Jty4AzGDU4uFF8ImaRbXhdpqZ1XcQuE5rLxhhLl6H"
    ): NewsResponse

}