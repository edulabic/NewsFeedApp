package etf.ri.rma.newsfeedapp.data

import androidx.room.*
import etf.ri.rma.newsfeedapp.model.*

@Dao
interface SavedNewsDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNews(news: NewsItem): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTag(tag: TagEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNewsTagCrossRef(crossRef: NewsTagsCrossRef)

    @Transaction
    @Query("SELECT * FROM News")
    suspend fun getAllNewsWithTags(): List<NewsWithTags>

    @Transaction
    @Query("SELECT * FROM Tags")
    suspend fun getAllTagsWithNews(): List<TagWithNews>

    @Query("SELECT * FROM News WHERE uuid = :uuid LIMIT 1")
    suspend fun getNewsByUuid(uuid: String): NewsItem?

    @Transaction
    @Query("SELECT * FROM News WHERE id = :newsId LIMIT 1")
    suspend fun getNewsWithTagsById(newsId: Long): NewsWithTags?

    @Query("SELECT * FROM Tags WHERE value = :tagValue LIMIT 1")
    suspend fun getTagByValue(tagValue: String): TagEntity?

    suspend fun saveNews(news: NewsItem): Boolean {
        val existingNews = getNewsByUuid(news.uuid)
        if (existingNews != null) return false
        insertNews(news)
        return true
    }

    /*suspend fun allNews(): List<NewsItem> {
        val newsWithTags = getAllNewsWithTags()
        return newsWithTags.map { newsWithTag ->
            val newsItem = newsWithTag.news
            newsItem.imageTags = ArrayList(newsWithTag.tags.map { it.value })
            newsItem
        }
    }*/

    suspend fun addTags(tags: List<String>, newsId: Int): Int {
        var newTagsCount = 0
        for (tagValue in tags) {
            val existingTag = getTagByValue(tagValue)
            val tagId = if (existingTag != null) {
                existingTag.id
            } else {
                val newTag = TagEntity(value = tagValue)
                val insertedId = insertTag(newTag)
                newTagsCount++
                insertedId
            }
            insertNewsTagCrossRef(NewsTagsCrossRef(newsId.toLong(), tagId))
        }
        return newTagsCount
    }

    suspend fun getTags(newsId: Int): List<String> {
        val newsWithTags = getNewsWithTagsById(newsId.toLong())
        return newsWithTags?.tags?.map { it.value } ?: emptyList()
    }


    @Transaction
    @Query("SELECT * FROM News")
    suspend fun getNewsWithTags(): List<NewsWithTags>
    fun NewsWithTags.toNewsItemWithTags(): NewsItem {
        return news.apply {
            imageTags = tags
        }
    }

    suspend fun allNews(): List<NewsItem> {
        return getNewsWithTags().map { it.toNewsItemWithTags() }
    }

    suspend fun getNewsWithCategory(category: String): List<NewsItem> {
        return getAllNewsWithTags()
            .filter { it.news.category == category }
            .map { it.toNewsItemWithTags() }
    }

    suspend fun getSimilarNews(tags: List<String>): List<NewsItem> {
        return getAllNewsWithTags()
            .filter { it.tags.any { tag -> tags.contains(tag.value) } }
            .map { it.toNewsItemWithTags() }
            .sortedByDescending { it.publishedDate }
    }


}
