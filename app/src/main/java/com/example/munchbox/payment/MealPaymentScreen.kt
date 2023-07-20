package com.example.munchbox.payment

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.munchbox.R
import com.example.munchbox.data.StorageServices
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

@Composable
fun MealPaymentScreen(
    numMeals: Int,
    price: String,
    onCancelButtonClicked: () -> Unit = {},
    onPayButtonClicked: () -> Unit = {},
    modifier : Modifier = Modifier
) {
    val parsedPrice = "$" + price.slice(0..1) + "." + price.substring(2)
    Column(
        modifier = modifier
            .fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(30.dp))

        Image(
            painter = painterResource(id = R.drawable.checkout),
            contentDescription = "checkout image",
            contentScale = ContentScale.FillWidth,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(35.dp))

        Text(
            text = "Cart: $numMeals meal(s) - $parsedPrice",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp),
            onClick = onPayButtonClicked
        ) {
            Text(stringResource(R.string.pay))
        }

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp),
            onClick = onCancelButtonClicked
        ) {
            Text(stringResource(R.string.cancel))
        }
    }
}

@Preview
@Composable
fun PreviewMealPaymentScreen() {
    MealPaymentScreen(5, "$42")
}