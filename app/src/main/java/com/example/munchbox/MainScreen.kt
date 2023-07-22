package com.example.munchbox

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
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
import com.example.munchbox.controller.DayOfWeek
import com.example.munchbox.controller.Meal
import com.example.munchbox.data.DataSource
import com.example.munchbox.data.DataSource.lazeez
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
import com.example.munchbox.ui.MuncherViewModel
import com.example.munchbox.ui.NumberOfMealsScreen
import com.example.munchbox.ui.RestaurantHubScreen
import com.example.munchbox.ui.RestaurantViewModel
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
    muncherViewModel: MuncherViewModel = viewModel(),
    restaurantViewModel: RestaurantViewModel = viewModel(),
    navController: NavHostController = rememberNavController()
) {
    /**
     * Instantiating the StorageService Class
     * Eg. Access storage services via storageService.orderService
     */
    val storageServices = StorageServices(FirebaseFirestore.getInstance())


    val context = LocalContext.current

    val uiState by muncherViewModel.uiState.collectAsState()
    // TODO setup another uistate for restaruant

    // Get current back stack entry
    val backStackEntry by navController.currentBackStackEntryAsState()
    // Get the name of the current screen
    val currentScreen = OrderScreen.valueOf(
        backStackEntry?.destination?.route ?: OrderScreen.Login.name
    )

    /**
     * coroutineScope for api calls by onClicks functions
     */
    val coroutineScope = rememberCoroutineScope()

    //TODO: we need to pop the prev stack when we get here since we don't want to be able to backtrack on this page
    LaunchedEffect(Unit){muncherViewModel.updateMuncherState("temp_user_id")}

    /**
     * Logic to navigate to and from payment activity
     */

    val intent = Intent(context, PaymentActivity::class.java)

    val stripeLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            navController.navigate(OrderScreen.AfterPayment.name)
            for(meal in muncherViewModel.uiState.value.orderUiState.unorderedMeals){
                coroutineScope.launch {
                    // TODO: replace userID with userID of current user
                    storageServices.orderService().createDBOrder(
                        userID = "temp_user_id",
                        mealID = meal.mealID,
                        restaurantID = meal.restaurantID,
                        pickUpDate = uiState.orderUiState.unorderedSelectedPickupDay[meal]!!.date,
                        orderPickedUp = false,
                    )
                }
            }
            coroutineScope.launch{muncherViewModel.updateMuncherState("temp_user_id")}
        }
        if (result.resultCode == Activity.RESULT_CANCELED) {
            muncherViewModel.clearUnorderedMeals()
            cancelOrderAndNavigateToStart(navController)
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


                MealOrderSummaryScreen(
                    orderUiState = uiState.orderUiState,
                    storageServices = storageServices,
                    onConfirmButtonClicked = {
                        //update meals
                        //TODO: we need to change the filter since meals.days is the days the meal is available. need to check db for field that reps the meal pickup date
                        //TODO: all we do here is delete order and meal from database
                        coroutineScope.launch {
                            muncherViewModel.updateConfirmedMeals(uiState.orderUiState.meals, uiState.orderUiState.selectedToPickUpDay)
                        }

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
                        muncherViewModel.setQuantity(numMeals, price)
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
                    restaurants = uiState.availableRestaurants,
                    orderInfo = uiState.orderUiState,
                    numMealsRequired = uiState.orderUiState.currentOrderQuantity,
//                    quantityOptions = DataSource.quantityOptions,
                    onCancelButtonClicked = {
                        muncherViewModel.clearUnorderedMeals()
                        cancelOrderAndNavigateToStart(navController)
                    },
                    onSubmit = { newOrderedMeals : Array<Meal?> ->
                        var mealDayPairs : MutableMap<Meal, DayOfWeek> = mutableMapOf()
                        var mealList : List<Meal> = listOf()
                        for ((index, meal) in newOrderedMeals.withIndex()) {
                            if (meal != null) {
                                mealDayPairs[meal] = DayOfWeek.values()[index]
                                mealList = mealList.plus(meal)
                            }
                        }
                        muncherViewModel.setUnorderedMeals(mealDayPairs, mealList)
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
                    orderUiState = muncherViewModel.uiState.value.orderUiState,
                    storageServices = storageServices,
                    onNextButtonClicked = {
                        intent.putExtra("amount", muncherViewModel.uiState.value.orderUiState.currentOrderPrice)
                        intent.putExtra("numMeals", muncherViewModel.uiState.value.orderUiState.currentOrderQuantity)
                        stripeLauncher.launch(intent)
                    },
                    onCancelButtonClicked = {
                        /**
                         * TODO: BUG - previous orders will be deleted on cancel need to adjust this logic everywhere this is used.
                         * Might need to be fixed once we have a database and store data properly
                        **/
                        muncherViewModel.clearUnorderedMeals()
                        cancelOrderAndNavigateToStart(navController)
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
                    numMeals = muncherViewModel.uiState.value.orderUiState.currentOrderQuantity,
                    price = muncherViewModel.uiState.value.orderUiState.currentOrderPrice,
                    onCancelButtonClicked = {
                        muncherViewModel.clearUnorderedMeals()
                        cancelOrderAndNavigateToStart(navController)
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
                RestaurantHubScreen(
                    storageServices = storageServices,
                    orderUiState = restaurantViewModel.uiState.value,
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
    navController: NavHostController
) {
    navController.popBackStack(OrderScreen.MealOrderSummary.name, inclusive = false)
}

