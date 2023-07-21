package com.example.munchbox.ui.components


import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.munchbox.R
import com.example.munchbox.controller.Meal
import com.example.munchbox.controller.Order
import com.example.munchbox.controller.Restaurant
import com.example.munchbox.data.StorageServices
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date


@Composable
fun OrderSummaryCard(meal: Meal,
                     order: Order,
                     storageServices: StorageServices,
                     confirmDisabled: Boolean,
                     onConfirmButtonClick: () -> Unit = {},
                     modifier: Modifier = Modifier.fillMaxWidth()){
    // TODO: Replace restaurant name with the actual restaurant name from DB
    MealCard(
        storageServices = storageServices,
        restaurantName = "Temporary Lazeez", // TODO: replace this with the actual restaurant name
        allMeals = setOf(meal),
        order = order,
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

    val order = Order("-1", "-1",  "-1", "-1", Date(), false)
    val storageService = StorageServices(FirebaseFirestore.getInstance())
    for (meal in lazeez.meals) {
        OrderSummaryCard(meal, order, storageService,false, {}, Modifier.fillMaxWidth())
    }
}


