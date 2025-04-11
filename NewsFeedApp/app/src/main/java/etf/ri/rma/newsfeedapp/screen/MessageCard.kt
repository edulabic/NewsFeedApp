package etf.ri.rma.newsfeedapp.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MessageCard(chosenCategory: String) {
    Card( modifier = Modifier
        .padding(8.dp)
        .fillMaxWidth()){
        Text("Nema pronađenih vijesti u kategoriji $chosenCategory",
            Modifier.padding(8.dp))
    }
}


