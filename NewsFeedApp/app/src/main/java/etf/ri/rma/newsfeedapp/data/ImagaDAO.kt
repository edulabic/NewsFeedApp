package etf.ri.rma.newsfeedapp.data

import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Headers
import retrofit2.http.Header
import retrofit2.Response

interface ImaggaApiService {
    @GET("v2/tags")
    suspend fun getTags(
        @Query("image_url") imageUrl: String,
        @Header("Authorization") authHeader: String
    ): Response<ImaggaResponse>
}
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
class InvalidImageURLException(message: String) : Exception(message)

object ImageDAO {
    private const val BASE_URL = "https://api.imagga.com/"
    private const val AUTH_KEY = "acc_9423c9d71b1e36e:7fa4c117596c3f705f24b7e9012caf1a"

    private val retrofit by lazy {
        retrofit2.Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
            .build()
    }

    private val api by lazy {
        retrofit.create(ImaggaApiService::class.java)
    }

    suspend fun getTags(imageURL: String): List<String> {
        if (!imageURL.startsWith("http")) {
            throw InvalidImageURLException("Neispravan URL slike: $imageURL")
        }

        val credentials = android.util.Base64.encodeToString(AUTH_KEY.toByteArray(), android.util.Base64.NO_WRAP)
        val authHeader = "Basic $credentials"

        val response = api.getTags(imageURL, authHeader)

        if (!response.isSuccessful) {
            throw InvalidImageURLException("Neuspješan zahtjev: ${response.code()}")
        }

        val tags = response.body()?.result?.tags ?: throw InvalidImageURLException("Nema tagova u odgovoru.")
        return tags.mapNotNull { it.tag["en"] }
    }
}

