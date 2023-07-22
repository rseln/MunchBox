package com.example.munchbox.ui.components


import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.munchbox.R
import com.example.munchbox.controller.Meal
import com.example.munchbox.controller.Restaurant
import com.example.munchbox.data.StorageServices
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch


@Composable
fun OrderSummaryCard(meal: Meal,
                     storageServices: StorageServices,
                     confirmDisabled: Boolean,
                     onConfirmButtonClick: () -> Unit = {},
                     modifier: Modifier = Modifier.fillMaxWidth()){
    // TODO: Replace restaurant name with the actual restaurant name from DB
    var restaurantName by remember { mutableStateOf<String>("") }

    LaunchedEffect(Unit){
        restaurantName = storageServices.restaurantService().getRestaurantByID(meal.restaurantID)?.name ?: ""
    }
    MealCard(
        storageServices = storageServices,
        restaurantName = restaurantName, // TODO: replace this with the actual restaurant name
        allMeals = setOf(meal),
        onAdd = { null },
        onSelectOption = { null },
        selectedOptions = meal.options,
        availableOptions = meal.options,
        added = true,
        disabled = true,
        confirmDisabled = confirmDisabled,
        onConfirmButtonClick = onConfirmButtonClick,
        modifier = modifier
            .fillMaxWidth()
    )
}
@Preview
@Composable
fun PreviewOrderSummaryCard(){
    val lazeez = Restaurant("1", "Lazeez", setOf(), R.drawable.lazeez)
//    lazeez.addMeals(setOf(Meal("lazeez_meal", "lazeez_id", "lazeez", setOf(DietaryOption.HALAL), setOf(DayOfWeek.SUNDAY))))

    val storageService = StorageServices(FirebaseFirestore.getInstance())
    for (meal in lazeez.meals) {
        OrderSummaryCard(meal, storageService,false, {}, Modifier.fillMaxWidth())
    }
}


