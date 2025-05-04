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
import androidx.navigation.NavController
import etf.ri.rma.newsfeedapp.data.NewsData
import android.icu.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
@Composable
fun NewsFeedScreen(navController: NavController) {
    val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle
    val appliedCategory = savedStateHandle?.get<String>("filter_category")
    val appliedDateRange = savedStateHandle?.get<String>("filter_date_range")
    val unwantedWords = savedStateHandle?.get<List<String>>("filter_unwanted_words") ?: emptyList()


    var chosenCategory by remember { mutableStateOf(appliedCategory ?: "Sve") }

    val newsList = NewsData.getAllNews()

    val filteredByCategory = when (chosenCategory) {
        "Politika" -> newsList.filter { it.category == "Politika" }
        "Sport" -> newsList.filter { it.category == "Sport" }
        "Nauka/tehnologija" -> newsList.filter { it.category == "Nauka/tehnologija" }
        "Kultura" -> newsList.filter { it.category == "Kultura" }
        else -> newsList
    }

    val filteredByDate = appliedDateRange?.takeIf { it.contains(";") }?.let { range ->
        val (startStr, endStr) = range.split(";")
        val formatter = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val startDate = formatter.parse(startStr)
        val endDate = formatter.parse(endStr)
        filteredByCategory.filter {
            val newsDate = formatter.parse(it.publishedDate)
            newsDate != null && startDate != null && endDate != null &&
                    !newsDate.before(startDate) && !newsDate.after(endDate)
        }
    } ?: filteredByCategory

    val filteredNews = filteredByDate.filter { newsItem ->
        unwantedWords.none { word ->
            newsItem.title.contains(word, ignoreCase = true) ||
                    newsItem.snippet.contains(word, ignoreCase = true)
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        FilterChips(
            navController = navController,
            chosenCategory = chosenCategory,

            onCategoryChosen = {
                chosenCategory = it
                navController.currentBackStackEntry?.savedStateHandle?.set("filter_category", it)
            }
        )
        NewsList(newsItems = filteredNews, chosenCategory = chosenCategory, navController = navController)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FilterChips(
    navController: NavController,
    chosenCategory: String,
    onCategoryChosen: (String) -> Unit
) {
    val categories = listOf("Sve", "Politika", "Sport", "Nauka/tehnologija", "Kultura", "Više filtera ...")
    val tags = listOf("filter_chip_all", "filter_chip_pol", "filter_chip_spo", "filter_chip_sci", "filter_chip_none", "filter_chip_more")

    FlowRow(modifier = Modifier.padding(8.dp)) {
        categories.forEachIndexed { index, category ->
            FilterChip(
                selected = chosenCategory == category,
                onClick = {
                    if (category == "Više filtera ...") {
                        navController.navigate("/filters") {
                            navController.currentBackStackEntry?.savedStateHandle?.set("filter_category", chosenCategory)
                        }
                    } else {
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