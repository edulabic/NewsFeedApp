package etf.ri.rma.newsfeedapp.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import etf.ri.rma.newsfeedapp.data.NewsData

@Composable
fun NewsFeedScreen(){
    var chosenCategory by remember{ mutableStateOf("Sve")}
    val newsList = NewsData.getAllNews()
    val filteredNews = when (chosenCategory){
        "Politika" -> newsList.filter{it.category =="Politika"}
        "Sport" -> newsList.filter {it.category == "Sport"}
        "Nauka/tehnologija" ->newsList.filter{it.category =="Nauka/tehnologija"}
        "Kultura" -> newsList.filter {it.category == "Kultura"}
        else -> newsList
    }
    Column(modifier= Modifier.fillMaxSize().padding(16.dp)){
        FilterChips(chosenCategory=chosenCategory, onCategoryChosen={ chosenCategory = it })
        NewsList(newsItems = filteredNews, chosenCategory=chosenCategory)}
}


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FilterChips(chosenCategory: String, onCategoryChosen: (String) -> Unit) {
    val categories = listOf("Sve", "Politika", "Sport", "Nauka/tehnologija", "Kultura", "Više filtera ...")
    val tags = listOf("filter_chip_all", "filter_chip_pol", "filter_chip_spo", "filter_chip_sci", "filter_chip_none", "filter_chip_more")
    FlowRow(modifier = Modifier.padding(8.dp)) {
        categories.forEachIndexed { index, category ->
            FilterChip(
                selected = chosenCategory == category,
                onClick = {
                    if (chosenCategory != category) {
                        onCategoryChosen(category)
                    }
                },
                label = { Text(category) },
                modifier = Modifier
                    .padding(end = 8.dp)
                    .testTag(tags[index])
            )
        }
    }
}

