package com.example.munchbox.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.munchbox.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealPaymentScreen(
    numMeals: Int,
    price: String,
    onCancelButtonClicked: () -> Unit = {},
    onPayButtonClicked: () -> Unit = {}
) {
    // State variables to hold the entered payment details
    val cardNumber = remember { mutableStateOf("") }
    val cardHolderName = remember { mutableStateOf("") }
    val expirationDate = remember { mutableStateOf("") }
    val cvv = remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Cart: $numMeals meal(s) - $price",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = cardNumber.value,
            onValueChange = { cardNumber.value = it },
            label = { Text("Card Number") }
        )
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = cardHolderName.value,
            onValueChange = { cardHolderName.value = it },
            label = { Text("Cardholder Name") }
        )
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = expirationDate.value,
            onValueChange = { expirationDate.value = it },
            label = { Text("Expiration Date") }
        )
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = cvv.value,
            onValueChange = { cvv.value = it },
            label = { Text("CVV") }
        )

        Spacer(modifier = Modifier.height(16.dp))
        Row (modifier = Modifier.align(Alignment.CenterHorizontally)) {
            OutlinedButton(
                modifier = Modifier
                    .weight(1f)
                    .height(IntrinsicSize.Min)
                    .padding(start = 8.dp),
                onClick = onCancelButtonClicked
            ) {
                Text(stringResource(R.string.cancel))
            }

            Spacer(modifier = Modifier.width(16.dp))

            Button(
                modifier = Modifier
                    .weight(1f)
                    .height(IntrinsicSize.Min)
                    .padding(start = 8.dp),
                onClick = onPayButtonClicked
            ) {
                Text(stringResource(R.string.pay))
            }
        }

    }
}



@Preview
@Composable
fun PreviewMealPaymentScreen() {
    MealPaymentScreen(5, "$42")
}