package com.example.munchbox.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.munchbox.R
import com.example.munchbox.controller.Restaurant
import com.example.munchbox.ui.components.MealCard
import com.example.munchbox.ui.components.OrderSummaryCard


data class Order(val restaurant: Restaurant)

@Composable
fun OrdersAvaialable(orders:List<Order>){

}

@Composable
fun OrderSummaries(){

}

@Composable
fun MealOrderSummaryScreen(
    onCancelButtonClicked: () -> Unit = {},
    onNextButtonClicked: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedValue by rememberSaveable { mutableStateOf("") }
    Column() {
        Button(
            onClick= onNextButtonClicked
        ) {
            Text("Order for next week")
        }

        Text("Orders Available for Pickup")


        Text("Order Summaries")
    }

//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(dimensionResource(R.dimen.padding_medium)),
//        horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_medium)),
//        verticalAlignment = Alignment.Bottom
//    ){
//        OutlinedButton(modifier = Modifier.weight(1f), onClick = onCancelButtonClicked) {
//            Text(stringResource(R.string.cancel))
//        }
//        Button(
//            modifier = Modifier.weight(1f),
//            // the button is enabled when the user makes a selection
////            enabled = selectedValue.isNotEmpty(),
//            onClick = onNextButtonClicked
//        ) {
//            Text(stringResource(R.string.next))
//        }
//    }
}


@Composable
@Preview
fun PreviewMealsAvailableScreen(){
    MealOrderSummaryScreen()
}