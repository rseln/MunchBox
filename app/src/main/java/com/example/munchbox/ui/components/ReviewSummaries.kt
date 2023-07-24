package com.example.munchbox.ui.components

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.munchbox.data.OrderUiState
import com.example.munchbox.data.StorageServices

@Composable
fun ReviewSummaries(
    orderUiState: OrderUiState,
    storageServices: StorageServices,
    modifier: Modifier = Modifier,
) {
    Row (
        modifier = modifier
    ){
        Box(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            Text(
                text = "Review Summary",
                style = MaterialTheme.typography.headlineMedium,
            )
        }
    }
    val unorderedSelectedPickupDayList = orderUiState.unorderedSelectedPickupDay.toList().sortedBy { it.second.id }
    for((meal,pickUpDate) in unorderedSelectedPickupDayList) {
        if (meal != null){
            Column(modifier = modifier) {
                Spacer(modifier = Modifier.height(18.dp))
                Text(
                    text = pickUpDate.str,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier,
                )
                Spacer(modifier = Modifier.height(13.dp))
                Log.d("HELLO FROM ORDER SUMMARY3", meal.mealID)
                OrderSummaryCard(meal = meal, storageServices = storageServices, confirmDisabled = true)
                Spacer(modifier = Modifier.height(13.dp))
            }
        }
    }
}