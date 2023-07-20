package com.example.munchbox.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Button


@Preview(
    showBackground = true,
    showSystemUi = true,
    name  = "Selection Card Preview"
)
@Composable
fun SelectCardContainer() {
    SelectCard(headerText = "Hello", buttonText = "Click Me", onClick = { })
}
@Composable
fun SelectCard(headerText: String,
               subHeaderText : String = "",
               buttonText: String,
               onClick : () -> Unit,
               modifier: Modifier = Modifier) {
    ElevatedCard(
        modifier = modifier,
        shape = MaterialTheme.shapes.large)
    {
        Column( modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),) {
            Text(
                text = headerText,
                style = MaterialTheme.typography.headlineMedium,
            )
            Spacer(modifier = Modifier.height(8.dp))
            if ( subHeaderText != "") {
                Text(
                    text = subHeaderText,
                    style = MaterialTheme.typography.labelMedium,
                )
            }
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = { onClick() },
            ) {
                Text(
                    text = buttonText,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}