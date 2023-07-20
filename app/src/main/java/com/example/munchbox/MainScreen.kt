package com.example.munchbox

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.munchbox.controller.Meal
import com.example.munchbox.data.DataSource
import com.example.munchbox.data.DataSource.campusPizza
import com.example.munchbox.data.DataSource.campusPizzaMeal
import com.example.munchbox.data.DataSource.campusPizzaMeal2
import com.example.munchbox.data.DataSource.lazeez
import com.example.munchbox.data.DataSource.lazeezMeal
import com.example.munchbox.data.DataSource.lazeezMeal2
import com.example.munchbox.data.DataSource.shawaramaPlus
import com.example.munchbox.data.DataSource.shawarmaPlusMeal
import com.example.munchbox.data.DataSource.shawarmaPlusMeal2
import com.example.munchbox.data.OrderUiState
import com.example.munchbox.data.StorageServices
import com.example.munchbox.payment.MealPaymentScreen
import com.example.munchbox.payment.PaymentActivity
import com.example.munchbox.ui.AfterPaymentScreen
import com.example.munchbox.ui.ChooseFighterScreen
import com.example.munchbox.ui.LoginScreen
import com.example.munchbox.ui.MealOrderSummaryScreen
import com.example.munchbox.ui.MealReviewScreen
import com.example.munchbox.ui.MealSelectionScreen
import com.example.munchbox.ui.NumberOfMealsScreen
import com.example.munchbox.ui.OrderViewModel
import com.example.munchbox.ui.RestaurantHubScreen
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch


/**
 * enum values that represent the screens in the app
 */

// note: Maybe we should rename to "Pages" to be more inclusive of login (low priority tho)
enum class OrderScreen(@StringRes val title: Int) {
    Login(title = R.string.login),
    MealOrderSummary(title = R.string.app_name),
    NumberOfMeals(title = R.string.app_name),
    MealSelect(title = R.string.meal_select),
    MealReview(title = R.string.meal_review),
    AfterPayment(title = R.string.after_payment),
    MealPayment(title = R.string.meal_payment),
    RestaurantHub(title = R.string.restaurant_hub),
    ChooseFighter(title = R.string.choose_fighter),
}

/**
 * Composable that displays the topBar and displays back button if back navigation is possible.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MunchBoxAppBar(
    currentScreen: OrderScreen,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = { Text(stringResource(currentScreen.title)) },
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_button)
                    )
                }
            }
        }
    )
}

@SuppressLint("StateFlowValueCalledInComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MunchBoxApp(
    viewModel: OrderViewModel = viewModel(),
    navController: NavHostController = rememberNavController()
) {
    /**
     * Instantiating the StorageService Class
     * Eg. Access storage services via storageService.orderService
     */
    val storageServices = StorageServices(FirebaseFirestore.getInstance())


    val context = LocalContext.current

    val uiState by viewModel.uiState.collectAsState()
    // Get current back stack entry
    val backStackEntry by navController.currentBackStackEntryAsState()
    // Get the name of the current screen
    val currentScreen = OrderScreen.valueOf(
        backStackEntry?.destination?.route ?: OrderScreen.Login.name
    )

    /**
     * State variables for order summaries etc.
     */
    var orderedMeals by remember { mutableStateOf(listOf<Meal>()) }

    lazeez.addMeals(setOf(lazeezMeal, lazeezMeal2))
    shawaramaPlus.addMeals(setOf(shawarmaPlusMeal, shawarmaPlusMeal2))
    campusPizza.addMeals(setOf(campusPizzaMeal, campusPizzaMeal2))

    /**
     * coroutineScope for api calls by onClicks functions
     */
    val coroutineScope = rememberCoroutineScope()

    /**
     * Logic to navigate to and from payment activity
     */

    val intent = Intent(context, PaymentActivity::class.java)

    val stripeLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            navController.navigate(OrderScreen.AfterPayment.name)
            for(meal in orderedMeals){
                coroutineScope.launch {
                    // TODO: replace userID with userID of current user
                    storageServices.orderService().createDBOrder(
                        userID = "temp_user_id",
                        mealID = meal.mealID,
                        restaurantID = meal.restaurantID,
                        pickUpDate = uiState.selectedToPickUpDay[meal]!!.date,
                        orderPickedUp = false,
                    )
                }
            }
        }
        if (result.resultCode == Activity.RESULT_CANCELED) {
            orderedMeals = listOf()
            viewModel.setMeals(listOf())
            cancelOrderAndNavigateToStart(viewModel, navController)
        }
    }


    Scaffold(
        topBar = {
            MunchBoxAppBar(
                currentScreen = currentScreen,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() }
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = OrderScreen.Login.name,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(route = OrderScreen.Login.name) {
                LoginScreen(
                    onLoginButtonClicked = {
                        // Need to write a function to do actual verification later!!!
                        // Just nav to next page for now
                        navController.navigate(OrderScreen.ChooseFighter.name) {
                            popUpTo(OrderScreen.Login.name) {
                                inclusive = true
                            }
                        }
                    }
                )
            }
            composable(route = OrderScreen.ChooseFighter.name) {
                ChooseFighterScreen(
                    onMunchButtonClick = {
                        navController.navigate(OrderScreen.MealOrderSummary.name)
                    },
                    onRestaurantButtonClick = {
                        navController.navigate(OrderScreen.RestaurantHub.name)
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .fillMaxWidth()
                        .padding(25.dp)
                )
            }
            composable(route = OrderScreen.MealOrderSummary.name) {
                /**
                 * the view models meals can be set in meal selection for now it is pointing to a fake dataset
                 * same with the pickup options
                 * **/

                //TODO: we need to pop the prev stack when we get here since we don't want to be able to backtrack on this page
                viewModel.setMeals(meals = uiState.meals.toList())
//                viewModel.setPickupOptions(pickupOptions = pickUpOptions)

                MealOrderSummaryScreen(
                    orderUiState = uiState,
                    storageServices = storageServices,
                    onConfirmButtonClicked = {
                        //update meals
                        //TODO: we need to change the filter since meals.days is the days the meal is available. need to check db for field that reps the meal pickup date
                        uiState.meals = uiState.meals.filter { meal : Meal -> !meal.days.contains(DataSource.currentDay) }.toSet()
                        viewModel.setMeals(meals = uiState.meals.toList())

                        //refresh page
                        navController.popBackStack(OrderScreen.MealOrderSummary.name,true)
                        navController.navigate(OrderScreen.MealOrderSummary.name)
                    },
                    onNextButtonClicked = {
                        navController.navigate(OrderScreen.NumberOfMeals.name)
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .fillMaxWidth()
                        .padding(25.dp)
                )
            }
            composable(route = OrderScreen.NumberOfMeals.name) {
                NumberOfMealsScreen(
                    quantityOptions = DataSource.quantityOptions,
                    onNextButtonClicked = { numMeals, price ->
                        viewModel.setQuantity(numMeals, price)
                        navController.navigate(OrderScreen.MealSelect.name)
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(dimensionResource(R.dimen.padding_medium))
                )
            }
            composable(route = OrderScreen.MealSelect.name) {
                MealSelectionScreen(
                    storageServices = storageServices,
                    restaurants = setOf(lazeez, campusPizza, shawaramaPlus),
                    orderInfo = uiState,
                    numMealsRequired = uiState.quantity,
//                    quantityOptions = DataSource.quantityOptions,
                    onCancelButtonClicked = {
                        orderedMeals = listOf()
                        viewModel.setMeals(listOf())
                        cancelOrderAndNavigateToStart(viewModel, navController)
                    },
                    onSubmit = { newOrderedMeals : Array<Meal?> ->
                        orderedMeals = listOf()
                        for (meal in newOrderedMeals) {
                            if (meal == null)
                                continue
                            orderedMeals = orderedMeals.plus(meal)
                        }

                        viewModel.setMeals(orderedMeals)
                        navController.navigate(OrderScreen.MealReview.name)
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .fillMaxWidth()
                        .padding(25.dp)
                )
            }
            composable(route = OrderScreen.MealReview.name) {
                MealReviewScreen(
                    orderUiState = uiState,
                    storageServices = storageServices,
                    onNextButtonClicked = {
                        intent.putExtra("amount", uiState.price)
                        intent.putExtra("numMeals", uiState.quantity)
                        stripeLauncher.launch(intent)
                    },
                    onCancelButtonClicked = {
                        /**
                         * TODO: BUG - previous orders will be deleted on cancel need to adjust this logic everywhere this is used.
                         * Might need to be fixed once we have a database and store data properly
                        **/
                        orderedMeals = listOf()
                        viewModel.setMeals(listOf())
                        cancelOrderAndNavigateToStart(viewModel, navController)
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .fillMaxWidth()
                        .padding(25.dp)
                )
            }
            composable(route = OrderScreen.AfterPayment.name) {
                AfterPaymentScreen(
                    onConfirmButtonClicked = {
                        navController.popBackStack(OrderScreen.MealOrderSummary.name, inclusive = false)
                    },
                    modifier = Modifier.fillMaxHeight()
                )
            }
            composable(route = OrderScreen.MealPayment.name) {
                MealPaymentScreen(
                    numMeals = viewModel.uiState.value.quantity,
                    price = viewModel.uiState.value.price,
                    onCancelButtonClicked = {
                        orderedMeals = listOf()
                        viewModel.setMeals(listOf())
                        cancelOrderAndNavigateToStart(viewModel, navController)
                    },
                    onPayButtonClicked = {
                        navController.navigate(OrderScreen.MealOrderSummary.name)
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .fillMaxWidth()
                        .padding(25.dp)
                    // TODO: Set this function to go to the Hub (and later, add some kind of actual confirmation)
                    //TODO: orderedMeals create an order document for each of the orders to be accessed in the DB when payment is confirmed
                )
            }
            composable(route = OrderScreen.RestaurantHub.name) {
                RestaurantHubScreen(orderUiState = viewModel.uiState.value,
                    restaurant = lazeez, // TODO: Change this to the actual restaurant logged in instead of always lazeez
                    modifier = Modifier
                        .fillMaxSize()
                        .fillMaxWidth()
                        .padding(25.dp))
            }
        }
    }
}

/**
 * Resets the [OrderUiState] and pops up to [OrderScreen.Start]
 */
private fun cancelOrderAndNavigateToStart(
    viewModel: OrderViewModel,
    navController: NavHostController
) {
    viewModel.resetOrder()
    navController.popBackStack(OrderScreen.MealOrderSummary.name, inclusive = false)
}

