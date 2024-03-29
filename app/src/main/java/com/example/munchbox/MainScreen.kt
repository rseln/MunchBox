package com.example.munchbox

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.icu.util.Calendar
import android.util.Log
import android.widget.Toast
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.example.munchbox.controller.DayOfWeek
import com.example.munchbox.controller.DietaryOption
import com.example.munchbox.controller.Meal
import com.example.munchbox.data.DataSource
import com.example.munchbox.data.OrderUiState
import com.example.munchbox.data.StorageServices
import com.example.munchbox.login.LoginScreen
import com.example.munchbox.payment.MealPaymentScreen
import com.example.munchbox.payment.PaymentActivity
import com.example.munchbox.signup.SignUpScreen
import com.example.munchbox.ui.AfterPaymentScreen
import com.example.munchbox.ui.ChooseFighterScreen
import com.example.munchbox.ui.MealOrderSummaryScreen
import com.example.munchbox.ui.MealReviewScreen
import com.example.munchbox.ui.MealSelectionScreen
import com.example.munchbox.ui.MuncherViewModel
import com.example.munchbox.ui.NumberOfMealsScreen
import com.example.munchbox.ui.RestaurantCreationScreen
import com.example.munchbox.ui.RestaurantHubScreen
import com.example.munchbox.ui.RestaurantViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import java.util.Date


/**
 * enum values that represent the screens in the app
 */

// note: Maybe we should rename to "Pages" to be more inclusive of login (low priority tho)
enum class OrderScreen(@StringRes val title: Int) {
    Signup(title = R.string.signup),
    Login(title = R.string.login),
    MealOrderSummary(title = R.string.app_name),
    NumberOfMeals(title = R.string.app_name),
    MealSelect(title = R.string.meal_select),
    MealReview(title = R.string.meal_review),
    AfterPayment(title = R.string.after_payment),
    MealPayment(title = R.string.meal_payment),
    RestaurantHub(title = R.string.restaurant_hub),
    RestaurantCreation(title = R.string.restaurant_signup),
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

    val muncherUiState by muncherViewModel.uiState.collectAsState()
    val restaurantUiState by restaurantViewModel.uiState.collectAsState()

    // Get current back stack entry
    val backStackEntry by navController.currentBackStackEntryAsState()
    // Get the name of the current screen
    val currentScreen = OrderScreen.valueOf(
        backStackEntry?.destination?.route ?: OrderScreen.Signup.name
    )

    /**
     * coroutineScope for api calls by onClicks functions
     */
    val coroutineScope = rememberCoroutineScope()

    //TODO: we need to pop the prev stack when we get here since we don't want to be able to backtrack on this page
    /**
     * Logic to navigate to and from payment activity
     */

    val intent = Intent(context, PaymentActivity::class.java)

    val stripeLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            navController.navigate(OrderScreen.AfterPayment.name)
            val userID = Firebase.auth.currentUser?.uid ?: ""
            for(meal in muncherViewModel.uiState.value.orderUiState.unorderedMeals){
                coroutineScope.launch {
                    val orderPickupDate = muncherUiState.orderUiState.unorderedSelectedPickupDay[meal]
                    val newAmountOrdered = meal.amountOrdered[orderPickupDate]!!.plus(1)
                    val newTotalOrders = meal.totalOrders.plus(1)
                    storageServices.orderService().createDBOrder(
                        userID = userID,
                        mealID = meal.mealID,
                        restaurantID = meal.restaurantID,
                        pickUpDate =  orderPickupDate!!.date,
                        orderPickedUp = false,
                    )
                    storageServices.restaurantService().updateMeal(
                        mealID = meal.mealID,
                        restaurantID = meal.restaurantID,
                        amountOrdered = mapOf(orderPickupDate to newAmountOrdered),
                        orderCount = newTotalOrders
                    )
                }
            }
            coroutineScope.launch{
                muncherViewModel.updateMuncherState(userID)
            }
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
                canNavigateBack = if ((currentScreen == OrderScreen.ChooseFighter) ||
                                    (currentScreen == OrderScreen.RestaurantHub) ||
                                    (currentScreen == OrderScreen.MealOrderSummary)) {
                    false
                }
                else {
                    navController.previousBackStackEntry != null
                },
                navigateUp = { navController.navigateUp() }
            )
        }
    ) { innerPadding ->
        //val uiState by viewModel.uiState.collectAsState()
        NavHost(
            navController = navController,
            startDestination = OrderScreen.Login.name,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(route = OrderScreen.Signup.name) {
                SignUpScreen(navController)
            }

            composable(route = OrderScreen.Login.name) {
                LoginScreen(navController)
            }

            composable(route = OrderScreen.RestaurantCreation.name) {
                RestaurantCreationScreen(navController)
            }
            composable(route = OrderScreen.ChooseFighter.name) {
                ChooseFighterScreen(
                    onMunchButtonClick = {
                        coroutineScope.launch {
                            val user = Firebase.auth.currentUser
                            if (user != null) {
                                user.email?.let { it1 ->
                                    storageServices.userService().createDBUser(
                                        userID = user.uid,
                                        email = it1,
                                        type = "Muncher",
                                        restaurantID = null
                                    )
                                }
                            }
                        }
                        navController.navigate(OrderScreen.MealOrderSummary.name)
                    },
                    onRestaurantButtonClick = {
                        coroutineScope.launch {
                            val user = Firebase.auth.currentUser
                            if (user != null) {
                                user.email?.let { it1 ->
                                    storageServices.userService().createDBUser(
                                        userID = user.uid,
                                        email = it1,
                                        type = "Restaurant",
                                        restaurantID = null
                                    )
                                }
                            }
                        }
                        navController.navigate(OrderScreen.RestaurantCreation.name)
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
                val userID = Firebase.auth.currentUser?.uid ?: ""
                var displayButton by remember {mutableStateOf(false)}
                LaunchedEffect(Unit){
                    muncherViewModel.updateMuncherState(userID)
                    displayButton = true
                }

                MealOrderSummaryScreen(
                    orderUiState = muncherUiState.orderUiState,
                    storageServices = storageServices,
                    onConfirmButtonClicked = {
                        coroutineScope.launch {
                            muncherViewModel.updateConfirmedOrders(muncherUiState.orderUiState.orders)
                        }

                        //refresh page
                        navController.popBackStack(OrderScreen.MealOrderSummary.name,true)
                        navController.navigate(OrderScreen.MealOrderSummary.name)
                    },
                    onNextButtonClicked = {
                        navController.navigate(OrderScreen.NumberOfMeals.name)
                    },
                    displayButton = displayButton,
                    onSignOutButtonClicked = {
                        val mAuth = FirebaseAuth.getInstance()
                        mAuth.signOut()
                        Toast.makeText(context, "Sign Out Successful", Toast.LENGTH_SHORT).show()
                        navController.navigate(OrderScreen.Login.name) {
                            popUpTo(OrderScreen.Login.name) {
                                inclusive = true
                            }
                        }
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
                    restaurants = muncherUiState.availableRestaurants,
                    orderInfo = muncherUiState.orderUiState,
                    numMealsRequired = muncherUiState.orderUiState.currentOrderQuantity,
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
                )
            }
            composable(route = OrderScreen.RestaurantHub.name) {
                val userID = Firebase.auth.currentUser?.uid ?: ""
                LaunchedEffect(Unit){restaurantViewModel.updateRestaurantState(userID)}
                RestaurantHubScreen(
                    storageServices = storageServices,
                    restaurant = restaurantUiState.restaurant,
                    updateViewModel = {
                        coroutineScope.launch{restaurantViewModel.updateRestaurantState(userID)}
                    },
                    createNewMeal = { selectedDays : Set<DayOfWeek>, selectedOptions : Set<DietaryOption> ->
                        coroutineScope.launch {
                            storageServices.restaurantService().addMealToRestaurant(restaurantUiState.restaurant.restaurantID, selectedOptions, selectedDays)
                            restaurantViewModel.updateRestaurantState(userID)
                        }
                    },
                    cancelMeal = { meal : Meal ->
                        coroutineScope.launch {
                            val latestOrder = storageServices.orderService().getRestaurantsLatestOrder(meal.restaurantID)
                            if (latestOrder!=null){
                                val calendar = Calendar.getInstance()
                                calendar.time = latestOrder.pickUpDate
                                calendar.add(Calendar.DAY_OF_MONTH, 1)
                                storageServices.restaurantService().updateMeal(restaurantUiState.restaurant.restaurantID, meal.mealID, null, null, calendar.time)
                            }
                            else{
                                storageServices.restaurantService().updateMeal(restaurantUiState.restaurant.restaurantID, meal.mealID, null, null, Date())
                            }

                            restaurantViewModel.updateRestaurantState(userID)
                        }
                    },
                    orderUiState = muncherViewModel.uiState.value.orderUiState,
                    onSignOutButtonClicked = {
                        val mAuth = FirebaseAuth.getInstance()
                        mAuth.signOut()
                        Toast.makeText(context, "Sign Out Successful", Toast.LENGTH_SHORT).show()
                        navController.navigate(OrderScreen.Login.name) {
                            popUpTo(OrderScreen.Login.name) {
                                inclusive = true
                            }
                        }
                    },
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

