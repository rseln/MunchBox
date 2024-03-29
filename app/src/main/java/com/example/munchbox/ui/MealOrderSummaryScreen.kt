package com.example.munchbox.ui

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialogDefaults.shape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.munchbox.controller.Order
import com.example.munchbox.R
import com.example.munchbox.controller.Meal
import com.example.munchbox.data.ClientID
import com.example.munchbox.data.DataSource.currentDay
import com.example.munchbox.data.OrderUiState
import com.example.munchbox.data.StorageServices
import com.example.munchbox.ui.components.OrderSummaries
import com.example.munchbox.ui.components.OrderSummaryCard
import com.example.munchbox.ui.components.SelectCard
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import kotlinx.coroutines.launch

@Composable
fun MealOrderSummaryScreen(
    orderUiState: OrderUiState,
    storageServices: StorageServices,
    modifier: Modifier = Modifier,
    onConfirmButtonClicked: () -> Unit = {},
    onNextButtonClicked: () -> Unit = {},
    displayButton: Boolean,
    onSignOutButtonClicked: () -> Unit = {},
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .verticalScroll(scrollState),
    ){
        if(displayButton){
            Row(modifier = Modifier.align( Alignment.CenterHorizontally)){
                SelectCard(headerText = "Let's munch.",
                    buttonText = "Order Lunch",
                    onClick = { onNextButtonClicked() })
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
        val fmt = SimpleDateFormat("yyyyMMdd")
        if (orderUiState.orders.any { order: Order -> fmt.format(order.pickUpDate).equals(fmt.format(Date())) &&
                    !order.orderPickedUp }) {
            OrdersAvailable(orderUiState, storageServices, onConfirmButtonClicked, Modifier)
        }
        Spacer(modifier = Modifier.height(32.dp))
        if (orderUiState.orders.isNotEmpty()) {
            OrderSummaries(
                orderUiState = orderUiState,
                storageServices = storageServices,
                modifier = Modifier,
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.padding_medium)),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.Bottom
        ) {

            Button(
                onClick = { onSignOutButtonClicked() },
                shape = RoundedCornerShape(5.dp)) {
                Text(text = "Sign Out")
            }}
        }

}

@Composable
fun OrdersAvailable(orderUiState: OrderUiState,
                    storageService: StorageServices,
                    onConfirmButtonClicked: () -> Unit = {},
                    modifier: Modifier = Modifier) {
    Row {
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ){
            Text(
                text = "Available for Pickup Today",
                style = MaterialTheme.typography.headlineMedium,
            )
        }
    }

    Column(modifier = modifier) {

        val fmt = SimpleDateFormat("yyyyMMdd")
        val ordersToday = orderUiState.orders.filter {
                order: Order -> fmt.format(order.pickUpDate).equals(fmt.format(Date())) &&
                !order.orderPickedUp
        }

        for(order in ordersToday) {
            val meal = orderUiState.orderToMeal[order]

            if (meal != null) {
                OrderSummaryCard(meal = meal,
                    order = order,
                    storageService,
                    false,
                    onConfirmButtonClicked,
                    modifier = Modifier.fillMaxWidth())
            }
        }
    }
}


@Composable
@Preview
fun PreviewMealsAvailableScreen(){
    /** To show the cards go into interactive mode **/
    val viewModel: OrderViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()
    val storageService = StorageServices(FirebaseFirestore.getInstance())

//    viewModel.setMeals(meals = allMeals.toList())
//    viewModel.setPickupOptions(pickupOptions = pickUpOptions)
//    MealOrderSummaryScreen(orderUiState = uiState, storageService)
}