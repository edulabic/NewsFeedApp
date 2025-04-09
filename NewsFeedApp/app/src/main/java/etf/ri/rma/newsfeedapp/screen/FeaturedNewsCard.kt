package etf.ri.rma.newsfeedapp.screen

import androidx.compose.runtime.Composable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row

import etf.ri.rma.newsfeedapp.model.NewsItem
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
@Composable
fun FeaturedNewsCard(newsItem: NewsItem) {
    Column(modifier = Modifier.padding(8.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .background(Color.Gray),
            contentAlignment = Alignment.Center
        ) {
            Text("BANNER IMAGE")
        }
        Text(newsItem.title, fontWeight = FontWeight.Bold)
        Text(newsItem.snippet, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text("${newsItem.source} • ${newsItem.publishedDate}", style = MaterialTheme.typography.bodySmall)
    }
}
