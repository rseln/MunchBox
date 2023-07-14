package com.example.munchbox.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.munchbox.R
import com.example.munchbox.data.DataSource

/**
 * Composable that allows the user to select the desired cupcake quantity and expects
 * [onNextButtonClicked] lambda that expects the selected quantity and triggers the navigation to
 * next screen
 */
@Composable
fun AfterPaymentScreen(
    isPaymentSuccess: Boolean?,
    onConfirmButtonClicked: () -> Unit,
    modifier: Modifier = Modifier
){
    var message = "";

    if (isPaymentSuccess == true) {
        message = "Order Purchase Complete"
    }
    if (isPaymentSuccess == false) {
        message = "Order Purchase Cancelled"
    }

    Column(
        modifier = modifier,

    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small))
        ) {
            Spacer(modifier = Modifier.height(30.dp))

            if (isPaymentSuccess != null) {
                Image(
                    painter = painterResource(id = R.drawable.cashier),
                    contentDescription = "cashier image",
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

            Text(
                text = message,
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(20.dp))

            if (isPaymentSuccess != null) {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onConfirmButtonClicked
                ) {
                    Text(stringResource(R.string.confirm))
                }
            }
        }
    }
}

@Preview
@Composable
fun AfterPaymentPreview(){
    AfterPaymentScreen(
        isPaymentSuccess = true,
        onConfirmButtonClicked = { },
        modifier = Modifier.fillMaxSize().padding(dimensionResource(R.dimen.padding_medium))
    )
}