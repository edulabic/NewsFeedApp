package etf.ri.rma.newsfeedapp.data

import etf.ri.rma.newsfeedapp.model.NewsItem
import kotlinx.coroutines.runBlocking

object NewsRepository {
    private val dao = NewsDAO(apiKey = "Jty4AzGDU4uF8ImaRbXhdpqZ1XcQuE5rLxhhLl6H")
    private val cache = mutableListOf<NewsItem>()
    private val categoryLastFetch = mutableMapOf<String, Long>()
    private val fetchedCategories = mutableSetOf<String>()

    init {
        cache.addAll(runBlocking { NewsData.getAllNews() })
    }

    fun getAllCachedNews(): List<NewsItem> = cache

    suspend fun getNewsByCategory(category: String): List<NewsItem> {
        val now = System.currentTimeMillis()
        val lastFetched = categoryLastFetch[category] ?: 0
        val within30Sec = now - lastFetched < 30_000

        val allFromCategory = cache.filter { it.category == category }

        if (within30Sec) {
            return allFromCategory
        }

        try {
            val newItems = dao.getTopStoriesByCategory(category)
            categoryLastFetch[category] = now

            val featuredItems = mutableListOf<NewsItem>()

            for (item in newItems) {
                val existing = cache.find { it.uuid == item.uuid }
                if (existing != null) {
                    val updated = existing.copy(isFeatured = true)
                    cache.remove(existing)
                    cache.add(0, updated)
                    featuredItems.add(updated)
                } else {
                    val fresh = item.copy(isFeatured = true)
                    cache.add(0, fresh)
                    featuredItems.add(fresh)
                }
                if (featuredItems.size == 3) break
            }

            val rest = cache.filter {
                it.category == category && it !in featuredItems
            }.map {
                it.copy(isFeatured = false)
            }

            return featuredItems + rest

        } catch (e: Exception) {
            println(" Greška prilikom dohvaćanja vijesti iz kategorije '$category': ${e.message}")
            return allFromCategory
        }
    }

}
