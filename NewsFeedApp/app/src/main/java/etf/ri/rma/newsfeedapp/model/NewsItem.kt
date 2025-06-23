package etf.ri.rma.newsfeedapp.model

import androidx.room.*
/*data class NewsItem(
    val uuid: String,
    val title: String,
    val snippet: String,
    val imageUrl: String?,
    val category: String?,
    val isFeatured: Boolean,
    val source: String,
    val publishedDate: String,
    var imageTags: ArrayList<String> = arrayListOf()
)*/


@Entity(tableName = "News")
data class NewsItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val uuid: String,
    val title: String,
    val snippet: String,
    val imageUrl: String?,
    val category: String?,
    val isFeatured: Boolean,
    val source: String,
    val publishedDate: String
) {
    @Ignore
    var imageTags: ArrayList<String> = arrayListOf()
}

@Entity(tableName = "Tags")
data class TagEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val value: String
)

@Entity(
    tableName = "NewsTags",
    primaryKeys = ["newsId", "tagId"],
    foreignKeys = [
        ForeignKey(entity = NewsItem::class, parentColumns = ["id"], childColumns = ["newsId"]),
        ForeignKey(entity = TagEntity::class, parentColumns = ["id"], childColumns = ["tagId"])
    ],
    indices = [Index("newsId"), Index("tagId")]
)
data class NewsTagsCrossRef(
    val newsId: Long,
    val tagId: Long
)

data class NewsWithTags(
    @Embedded val news: NewsItem,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = NewsTagsCrossRef::class,
            parentColumn = "newsId",
            entityColumn = "tagId"
        )
    )
    val tags: List<TagEntity>
)

data class TagWithNews(
    @Embedded val tag: TagEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = NewsTagsCrossRef::class,
            parentColumn = "tagId",
            entityColumn = "newsId"
        )
    )
    val newsList: List<NewsItem>
)
