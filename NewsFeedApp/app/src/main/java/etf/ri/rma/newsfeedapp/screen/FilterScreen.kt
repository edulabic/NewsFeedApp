package etf.ri.rma.newsfeedapp.screen

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FilterChip(chosenCategory: String, onCategoryChosen: (String) -> Unit) {
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

