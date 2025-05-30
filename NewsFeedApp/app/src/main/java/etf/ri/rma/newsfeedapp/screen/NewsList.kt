package etf.ri.rma.newsfeedapp.screen

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.navigation.NavController
import etf.ri.rma.newsfeedapp.model.NewsItem
import etf.ri.rma.newsfeedapp.screen.MessageCard
import etf.ri.rma.newsfeedapp.screen.StandardNewsCard
import etf.ri.rma.newsfeedapp.screen.FeaturedNewsCard
@Composable
fun NewsList(newsItems : List<NewsItem>, chosenCategory: String, navController: NavController){
    LazyColumn(modifier = Modifier.testTag("news_list") ) {
        if (newsItems.isEmpty()) {
            item {
                MessageCard(chosenCategory)
            }
        } else {
            items(newsItems, key = { it.uuid }) { newsItem ->
                if (newsItem.isFeatured) {
                    FeaturedNewsCard(newsItem, navController = navController)
                } else {
                    StandardNewsCard(newsItem, navController = navController)
                }
            }
        }
    }
}
