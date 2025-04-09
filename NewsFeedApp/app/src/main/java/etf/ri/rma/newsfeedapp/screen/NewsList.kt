package etf.ri.rma.newsfeedapp.screen

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import etf.ri.rma.newsfeedapp.model.NewsItem
import etf.ri.rma.newsfeedapp.screen.MessageCard
import etf.ri.rma.newsfeedapp.screen.StandardNewsCard
import etf.ri.rma.newsfeedapp.screen.FeaturedNewsCard
@Composable
fun NewsList(newsItems : List<NewsItem>){
    if(newsItems.isEmpty()){
        MessageCard()
    }
    else {
        LazyColumn(modifier = Modifier.testTag("news_list")) {
            items(newsItems, key = { it.id }) { newsItem ->
                if (newsItem.isFeatured) {
                    FeaturedNewsCard(newsItem)
                } else {
                    StandardNewsCard(newsItem)
                }
            }
        }
    }

}