package etf.ri.rma.newsfeedapp.data

import android.net.Uri
import etf.ri.rma.newsfeedapp.data.network.ImagaDAO
import etf.ri.rma.newsfeedapp.data.network.NewsDAO
import etf.ri.rma.newsfeedapp.data.network.exception.InvalidImageURLException
import etf.ri.rma.newsfeedapp.data.network.exception.InvalidUUIDException
import etf.ri.rma.newsfeedapp.model.NewsItem
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import android.util.Log
object NewsRepository {
    private val dao = NewsDAO()
    private val cache = mutableMapOf<String, NewsItem>()
    private val categoryLastFetch = mutableMapOf<String, Long>()

    private val initialNewsItems = listOf(
        NewsItem(
            uuid = "be025a89-b068-4b7d-9c64-b4f558b46065",
            title = "Phillies’ Romano fans Braves’ White with bases loaded to end game",
            snippet = "Sign in to Sportsnet\n\nSign In Sign Up\n\nFirst Name {...}",
            imageUrl = "https://www.sportsnet.ca/sn_favicon.ico",
            category = "sports",
            isFeatured = false,
            source = "sportsnet.ca",
            publishedDate = "29-05-2025",
            imageTags = arrayListOf()
        ),
        NewsItem(
            uuid="783b0d47-f15f-49a8-b9fb-6a5b2d03d89e",
            title="Amber Rose Opens Up About Why She's Advocating for Tory Lanez's Freedom",
            snippet="Amber Rose appeared on NewsNation’s CUOMO with Chris Cuomo this week, where she opened up about her vocal support for Tory Lanez, who is currently serving a 1...",
            imageUrl="https://www.vladtv.com/images/size_fs/video-311245.jpg?ver=1748558809",
            category="sports",
            isFeatured=false,
            source="sportsnet.ca",
            publishedDate="29-05-2025",
            imageTags=arrayListOf()
        ),
        NewsItem(
            uuid="718b1e2e-333e-49f1-8203-a5a14cb8c3f1",
            title="New quantum material bends electrons, unlocks zero-loss electricity",
            snippet="Physicists at Rice University have now taken a major step in that direction.\n\nLed by Ming Yi and Emilia Morosan, the team developed a new quantum material with ...",
            imageUrl="https://cms.interestingengineering.com/wp-content/uploads/2025/05/Untitled-design-2025-05-30T043658.134.jpg",
            category="tech",
            isFeatured=true,
            source="interestingengineering.com",
            publishedDate="29-05-2025",
            imageTags=arrayListOf()
        ),
        NewsItem(
            uuid="e1f2cba9-2486-4bb6-a8ae-25b00ae750d0",
            title="Sempra (SRE) Gets Green Light for Port Arthur LNG Phase 2 Export",
            snippet="Sempra's Port Arthur LNG Phase 2 project receives a significant boost with a new DOE permit.\n\nAnalysts suggest a modest upside potential for Sempra (SRE, Financ...",
            imageUrl="https://static.gurufocus.com/logos/0C000008ZC.png?20",
            category="business",
            isFeatured=false,
            source="gurufocus.com",
            publishedDate="29-05-2025",
            imageTags=arrayListOf()
        ),
        NewsItem(
            uuid="a67e699e-88e8-4cd8-9498-41cc1def5108",
            title="Vegan Calamari That’s Healthy and Full of Flavor",
            snippet="When most of us see calamari on a restaurant menu, it’s unlikely we think of the sea creature it was made from. But the clue is actually right there in the na...",
            imageUrl="https://vegnews.com/media/W1siZiIsIjQyMDcyL1ZlZ05ld3MuQ2FsYW1hcmkuSGFwcGllZS5qcGciXSxbInAiLCJjcm9wX3Jlc2l6ZWQiLCIxMDc3eDU2NSsxKzc5IiwiMTIwMHg2MzBeIix7ImZvcm1hdCI6ImpwZyJ9XSxbInAiLCJvcHRpbWl6ZSJdXQ/VegNews.Calamari.Happiee.jpg?sha=6fa8a4f00d2d68ae",
            category="food",
            isFeatured=false,
            source="vegnews.com",
            publishedDate="29-05-2025",
            imageTags=arrayListOf()
        ),
        NewsItem(
            uuid="249d681b-a5e5-4dc7-a636-02dc5e149e2a",
            title="'A bunch of stories that are worth telling': new book details North Bay's history",
            snippet="A retired North Bay journalist has published a book about the city's history, right in time for 100th anniversary celebrations.\n\nPeter Fleming Handley is the au...",
            imageUrl="https://i.cbc.ca/1.7547324.1748544327!/fileImage/httpImage/image.jpg_gen/derivatives/16x9_1180/vis-a-vis.jpg?im=Resize%3D620",
            category="general",
            isFeatured=false,
            source="cbc.ca",
            publishedDate="30-05-2025",
            imageTags=arrayListOf()
        ),
        NewsItem(
            uuid="4b74a528-7e48-48fe-9705-6120c3990e70",
            title="Ancient dinosaur fossil may offer clues for future cancer treatments",
            snippet="The findings raise the possibility that ancient tumours could help unravel cancer’s molecular origins and potentially inform future treatments.\n\nAncient cells...",
            imageUrl="https://cms.interestingengineering.com/wp-content/uploads/2025/05/iStock-173758906.jpg",
            category="science",
            isFeatured = false,
            source="interestingengineering.com",
            publishedDate="29-05-2025",
            imageTags=arrayListOf()
        ),
        NewsItem(
            uuid="b55bfcd4-3b8d-42a5-aef8-2149339d50be",
            title="Invictus Fitness",
            snippet="General Warm-Up\n\nTwo rounds of:\n\n250 Meter Row\n\n10 Goblet Hold Alternating Lateral Lunges\n\n10 Goblet Hold Good Mornings\n\nand then…\n\nTwo rounds with an empty b...",
            imageUrl="https://invictusfitness.com/wp-content/uploads/2015/11/logo.png",
            category="health",
            isFeatured=false,
            source="crossfitinvictus.com",
            publishedDate="29-05-2025",
            imageTags=arrayListOf()
        ),
        NewsItem(
            uuid="1dcb5198-5005-4dbf-a52c-d1b8bebe9220",
            title="WATCH: Bureaucrat caught admitting to 'bending the rules' to give illegals benefits * WorldNetDaily * by Cristina Laila, the Gateway Pundit",
            snippet="The O’Keefe Media Group on Thursday released undercover video of a Nevada Department of Health and Human Services (DHHS) Division of Welfare and Supportive Se...",
            imageUrl="https://www.wnd.com/wp-content/uploads/2025/05/nevada-illegals-omg.jpg",
            category="politics",
            isFeatured=true,
            source="wnd.com",
            publishedDate="29-05-2025",
            imageTags=arrayListOf()
        ),
        NewsItem(
            uuid="40290076-a8b6-49e9-9815-bbc4bccbaefd",
            title="SIHOT and Dingus Automate Tour Operator Bookings with Real-Time PMS Integration",
            snippet="Now, reservations from Dingus are automatically linked to specific contracts within SIHOT, allowing the system to distinguish between allotment-based and genera...",
            imageUrl="https://hospitalitytech.com/images/v/16_x_9_480/2025-05/unnamed_9.jpg",
            category="travel",
            isFeatured=false,
            source="hospitalitytech.com",
            publishedDate="29-05-2025",
            imageTags=arrayListOf()
        )
    )

    init {
        runBlocking {
            val imagaDAO = ImagaDAO()
            coroutineScope {
                initialNewsItems.forEach { newsItem ->
                    launch {
                        newsItem.imageUrl?.let { url ->
                            try {
                                val tags = imagaDAO.getTags(url)
                                newsItem.imageTags.addAll(tags)
                                Log.d("NewsRepository", "Fetched tags for ${newsItem.title}: $tags")
                            } catch (e: InvalidImageURLException) {
                                Log.e("NewsRepository", "Error fetching tags for '${newsItem.title}' (URL: $url): ${e.message}")
                            } catch (e: Exception) {
                                Log.e("NewsRepository", "An unexpected error occurred for '${newsItem.title}' (URL: $url): ${e.message}")
                            }
                        }
                    }
                }
            }

            initialNewsItems.forEach { cache[it.uuid] = it }
            Log.d("NewsRepository", "Initial news items loaded into cache.")
        }
    }

    fun getAllCachedNews(): List<NewsItem> = cache.values.toList()

    suspend fun getNewsByCategory(category: String): List<NewsItem> {
        val now = System.currentTimeMillis()
        val lastFetched = categoryLastFetch[category] ?: 0
        val within30Sec = now - lastFetched < 30_000

        val currentCachedCategoryNews = cache.values.filter { it.category == category }.toMutableList()

        if (within30Sec) {
            Log.d("NewsRepository", "Returning cached news for category $category (within 30s timeout)")
            return currentCachedCategoryNews.map { it.copy(isFeatured = false) }
        }

        try {
            val newItemsFromApi = dao.getTopStoriesByCategory(category)
            categoryLastFetch[category] = now
            val resultListForDisplay = mutableListOf<NewsItem>()

            coroutineScope {
                newItemsFromApi.forEach { apiItem ->
                    launch {
                        apiItem.imageUrl?.let { url ->
                            try {
                                if (apiItem.imageTags.isEmpty()) {
                                    val tags = ImagaDAO().getTags(url)
                                    apiItem.imageTags.addAll(tags)
                                    Log.d("NewsRepository", "Fetched API item tags for ${apiItem.title}: $tags")
                                }
                            } catch (e: InvalidImageURLException) {
                                Log.e("NewsRepository", "Error fetching API item tags for '${apiItem.title}' (URL: $url): ${e.message}")
                            } catch (e: Exception) {
                                Log.e("NewsRepository", "An unexpected error occurred for API item '${apiItem.title}' (URL: $url): ${e.message}")
                            }
                        }
                    }
                }
            }

            val featuredCandidates = newItemsFromApi.take(3).map { it.copy(isFeatured = true) }
            resultListForDisplay.addAll(featuredCandidates)

            val nonFeaturedNewItems = newItemsFromApi.drop(3).filter { apiItem ->
                !featuredCandidates.any { it.uuid == apiItem.uuid }
            }.map { it.copy(isFeatured = false) }
            resultListForDisplay.addAll(nonFeaturedNewItems)

            resultListForDisplay.forEach { cache[it.uuid] = it }
            Log.d("NewsRepository", "Fetched new news for category $category. Total in cache: ${cache.size}")

            return resultListForDisplay.sortedByDescending { it.publishedDate }
        } catch (e: Exception) {
            Log.e("NewsRepository", "Greška prilikom dohvaćanja vijesti iz kategorije '$category': ${e.message}")
            return currentCachedCategoryNews.map { it.copy(isFeatured = false) }
        }
    }

    suspend fun getSimilarStories(uuid: String): List<NewsItem> {
        if (!uuid.matches(Regex("^[0-9a-fA-F\\-]{36}$"))) {
            throw InvalidUUIDException("Invalid UUID format: $uuid")
        }

        try {
            val similarFromApi = dao.getSimilarStories(uuid)
            if (similarFromApi.isNotEmpty()) {
                coroutineScope {
                    similarFromApi.forEach { newsItem ->
                        launch {
                            newsItem.imageUrl?.let { url ->
                                try {
                                    if (newsItem.imageTags.isEmpty()) {
                                        val tags = ImagaDAO().getTags(url)
                                        newsItem.imageTags.addAll(tags)
                                        Log.d("NewsRepository", "Fetched similar API item tags for ${newsItem.title}: $tags")
                                    }
                                } catch (e: InvalidImageURLException) {
                                    Log.e("NewsRepository", "Error fetching similar API item tags for '${newsItem.title}' (URL: $url): ${e.message}")
                                } catch (e: Exception) {
                                    Log.e("NewsRepository", "An unexpected error occurred for similar API item '${newsItem.title}' (URL: $url): ${e.message}")
                                }
                            }
                        }
                    }
                }
                similarFromApi.forEach { cache[it.uuid] = it }
                return similarFromApi
            }
        } catch (e: Exception) {
            Log.e("NewsRepository", "Error fetching similar stories from API for UUID $uuid: ${e.message}")

        }

        val reference = cache[uuid] ?: return emptyList()
        val sameCategory = cache.values.filter { it.category == reference.category && it.uuid != uuid }
        return sameCategory.take(2).toList()
    }


    suspend fun getFirstSimilarNewsUrlFromSource(uuidNews: String, source: String): String? {
        if (!uuidNews.matches(Regex("^[0-9a-fA-F\\-]{36}$"))) {
            throw InvalidUUIDException("Invalid UUID format: $uuidNews")
        }

        return try {
            dao.getSimilarWithSource(uuidNews, source)
        } catch (e: InvalidUUIDException) {
            Log.e("NewsRepository", "Invalid UUID: ${e.message}")
            null
        } catch (e: Exception) {
            Log.e("NewsRepository", "Error in getFirstSimilarNewsUrlFromSource: ${e.message}")
            null
        }
    }
}