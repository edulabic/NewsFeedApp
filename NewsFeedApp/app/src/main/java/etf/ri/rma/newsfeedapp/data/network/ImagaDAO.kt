package etf.ri.rma.newsfeedapp.data.network

import android.util.Base64
import etf.ri.rma.newsfeedapp.data.network.api.ImagaApiService
import etf.ri.rma.newsfeedapp.data.network.exception.InvalidImageURLException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

data class ImaggaResponse(
    val result: Result?
)

data class Result(
    val tags: List<Tag>
)

data class Tag(
    val tag: Map<String, String>,
    val confidence: Double
)

class ImagaDAO {
    private val BASE_URL = "https://api.imagga.com/"
    private val AUTH_KEY = "acc_4efbb1285383ceb:a5fe69d730d03e3b45d23328194e83c3"
    private val cache = mutableMapOf<String, List<String>>()

    private var externalApi: ImagaApiService? = null

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val defaultApi by lazy {
        retrofit.create(ImagaApiService::class.java)
    }

    constructor(apiService: ImagaApiService) {
        this.externalApi = apiService
    }

    constructor()

    fun setApiService(apiService: ImagaApiService) {
        this.externalApi = apiService
    }

    private val api: ImagaApiService
        get() = externalApi ?: defaultApi

    suspend fun getTags(imageURL: String): List<String> {
        if (!imageURL.startsWith("http")) {
            throw InvalidImageURLException("Neispravan URL slike: $imageURL")
        }

        if (cache.containsKey(imageURL)) {
            return cache[imageURL]!!
        }

        val credentials = Base64.encodeToString(AUTH_KEY.toByteArray(), Base64.NO_WRAP)
        val authHeader = "Basic $credentials"

        val response = api.getTags(imageURL, authHeader)

        if (!response.isSuccessful) {
            throw InvalidImageURLException("Neuspješan zahtjev: ${response.code()}")
        }

        val tags = response.body()?.result?.tags ?: throw InvalidImageURLException("Nema tagova u odgovoru.")
        val tagList = tags.mapNotNull { it.tag["en"] }

        cache[imageURL] = tagList

        return tagList
    }
}
