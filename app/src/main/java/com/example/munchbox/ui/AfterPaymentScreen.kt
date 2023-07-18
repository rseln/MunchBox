package com.example.munchbox.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.munchbox.R

@Composable
fun AfterPaymentScreen(
    onConfirmButtonClicked: () -> Unit,
    modifier: Modifier = Modifier
){
    Column(
        modifier = modifier,

    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small))
        ) {
            Spacer(modifier = Modifier.height(30.dp))

            Image(
                painter = painterResource(id = R.drawable.cashier),
                contentDescription = "cashier image"
            )

            Spacer(modifier = Modifier.height(30.dp))

            Text(
                text = "Thank you! Order purchase complete.",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(16.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))


            Button(
                modifier = Modifier.width(200.dp),
                onClick = onConfirmButtonClicked
            ) {
                Text(stringResource(R.string.confirm))
            }
        }
    }
}

@Preview
@Composable
fun AfterPaymentPreview(){
    AfterPaymentScreen(
        onConfirmButtonClicked = { },
        modifier = Modifier.fillMaxSize().padding(dimensionResource(R.dimen.padding_medium))
    )
}