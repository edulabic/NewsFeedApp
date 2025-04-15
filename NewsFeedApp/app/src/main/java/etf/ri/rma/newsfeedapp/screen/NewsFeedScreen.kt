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
import java.time.LocalDate
import java.time.format.DateTimeFormatter

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
    var chosenDate by remember {mutableStateOf("")}
    val filteredDate = when (chosenDate) {
        "U ovom mjesecu" -> filteredNews.filter {
            val datum = pretvoriUDatum(it.publishedDate)
            datum.month == LocalDate.now().month && datum.year == LocalDate.now().year
        }
        "U ovoj godini" -> filteredNews.filter {
            val datum = pretvoriUDatum(it.publishedDate)
            datum.year == LocalDate.now().year
        }
        else -> filteredNews
    }

    Column(modifier= Modifier.fillMaxSize().padding(16.dp)){
        FilterChips(chosenCategory=chosenCategory, onCategoryChosen={ chosenCategory = it })
        FilterDate(chosenDate = chosenDate, onDateChosen = {chosenDate = it})
        NewsList(newsItems = filteredDate, chosenCategory=chosenCategory, chosenDate=chosenDate)
    }
}

fun pretvoriUDatum(date: String): LocalDate {
    return LocalDate.parse(date, DateTimeFormatter.ofPattern("dd.MM.yyyy"))
}


fun substringDefault(date: String):String {
    val substring = date.substring(5, 7)
    return substring
}

fun substringMonth(date: String):String {
    val substring = date.substring(3, 5)
    return substring
}

fun pretvoriDate(date:String):String {
    val localDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("dd.MM.yyyy"))
    return localDate.toString()
}



@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FilterDate(chosenDate: String, onDateChosen: (String)-> Unit){
    val options = listOf("U ovom mjesecu", "U ovoj godini")
    val tags = listOf("filter_chip_date_month", "filter_chip_date_year")
    FlowRow(modifier = Modifier.padding(16.dp)){
        options.forEachIndexed { index, option ->
            FilterChip(
                selected = chosenDate == option,
                onClick = {
                    if(chosenDate != option){
                        onDateChosen(option)
                    }
                },
                label = { Text(option) },
                modifier = Modifier
                    .padding(end = 8.dp)
                    .testTag(tags[index])
            )

        }
    }

}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FilterChips(chosenCategory: String, onCategoryChosen: (String) -> Unit) {
    val categories = listOf("Sve", "Politika", "Sport", "Nauka/tehnologija", "Kultura")
    val tags = listOf("filter_chip_all", "filter_chip_pol", "filter_chip_spo", "filter_chip_sci", "filter_chip_none")
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

