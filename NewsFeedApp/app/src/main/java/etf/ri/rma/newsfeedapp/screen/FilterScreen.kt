package etf.ri.rma.newsfeedapp.screen

import android.icu.text.SimpleDateFormat
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import java.util.*

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun FilterScreen(navController: NavController) {
    var selectedCategory by remember { mutableStateOf("Sve") }

    val categories = listOf(
        "Sve" to "filter_chip_all",
        "Politika" to "filter_chip_pol",
        "Sport" to "filter_chip_spo",
        "Nauka/tehnologija" to "filter_chip_sci"
    )

    var dateRangeText by remember { mutableStateOf("Odaberite opseg datuma") }
    val datePickerState = rememberDateRangePickerState()
    val openDialog = remember { mutableStateOf(false) }
    var unwantedWord by remember { mutableStateOf("") }
    var unwantedWords by remember { mutableStateOf(listOf<String>()) }


    if (openDialog.value) {
        DatePickerDialog(
            onDismissRequest = { openDialog.value = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        openDialog.value = false
                        val start = datePickerState.selectedStartDateMillis?.let {
                            SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date(it))
                        }
                        val end = datePickerState.selectedEndDateMillis?.let {
                            SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date(it))
                        }
                        dateRangeText = if (start != null && end != null) "$start;$end" else "Odaberite opseg datuma"
                    },
                    enabled = datePickerState.selectedEndDateMillis != null
                ) {
                    Text("Potvrdi")
                }
            },
            dismissButton = {
                TextButton(onClick = { openDialog.value = false }) {
                    Text("Odustani")
                }
            }
        ) {
            DateRangePicker(
                state = datePickerState,
                modifier = Modifier.weight(1f)
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text("Filtriraj po kategoriji:")

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { (name, tag) ->
                    FilterChip(
                        selected = selectedCategory == name,
                        onClick = { selectedCategory = name },
                        label = { Text(name) },
                        modifier = Modifier.testTag(tag)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Filtriraj po datumu:")

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = dateRangeText,
                    modifier = Modifier.testTag("filter_daterange_display")
                )

                Button(
                    onClick = { openDialog.value = true },
                    modifier = Modifier.testTag("filter_daterange_button")
                ) {
                    Text("Odaberi")
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))

        Text("Neželjene riječi u vijestima:")

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            TextField(
                value = unwantedWord,
                onValueChange = { unwantedWord = it },
                label = { Text("Unesi riječ") },
                modifier = Modifier
                    .weight(1f)
                    .testTag("filter_unwanted_input")
            )

            Button(
                onClick = {
                    val wordLower = unwantedWord.trim().lowercase()
                    if (wordLower.isNotEmpty() && unwantedWords.none { it.equals(wordLower, ignoreCase = true) }) {
                        unwantedWords = unwantedWords + wordLower
                        unwantedWord = ""
                    }
                },
                modifier = Modifier.testTag("filter_unwanted_add_button")
            ) {
                Text("Dodaj")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Column(modifier = Modifier.testTag("filter_unwanted_list")) {
            unwantedWords.forEach { word ->
                Text(text = word)
            }
        }

        Button(
            onClick = {
                navController.previousBackStackEntry?.savedStateHandle?.set("filter_category", selectedCategory)
                navController.previousBackStackEntry?.savedStateHandle?.set("filter_date_range", dateRangeText)
                navController.previousBackStackEntry?.savedStateHandle?.set("filter_unwanted_words", unwantedWords)
                navController.popBackStack()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp)
                .testTag("filter_apply_button")
        ) {
            Text("Primijeni filtere")
        }
    }
}
