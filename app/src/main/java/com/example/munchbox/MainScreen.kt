package com.example.munchbox

import android.annotation.SuppressLint
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
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
import com.example.munchbox.data.DataSource.lazeez
import com.example.munchbox.data.DataSource.lazeezMeal
import com.example.munchbox.data.DataSource.pickUpOptions
import com.example.munchbox.data.DataSource.shawaramaPlus
import com.example.munchbox.data.DataSource.shawarmaPlusMeal
import com.example.munchbox.ui.LoginScreen
import com.example.munchbox.ui.MealOrderSummaryScreen
import com.example.munchbox.ui.MealPaymentScreen
import com.example.munchbox.ui.MealReviewScreen
import com.example.munchbox.ui.MealSelectionScreen
import com.example.munchbox.ui.NumberOfMealsScreen
import com.example.munchbox.ui.OrderViewModel




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
    MealPayment(title = R.string.meal_payment),
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
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
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

    lazeez.addMeals(setOf(lazeezMeal))
    shawaramaPlus.addMeals(setOf(shawarmaPlusMeal))
    campusPizza.addMeals(setOf(campusPizzaMeal))


    Scaffold(
        topBar = {
            MunchBoxAppBar(
                currentScreen = currentScreen,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() }
            )
        }
    ) { innerPadding ->
        val uiState by viewModel.uiState.collectAsState()

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
                        navController.navigate(OrderScreen.MealOrderSummary.name) {
                            popUpTo(OrderScreen.Login.name) {
                                inclusive = true
                            }
                        }
                    }
                )
            }
            composable(route = OrderScreen.MealOrderSummary.name) {
                /**
                 * the view models meals can be set in meal selection for now it is pointing to a fake dataset
                 * same with the pickup options
                 * **/

                viewModel.setMeals(meals = uiState.meals.toList())
                viewModel.setPickupOptions(pickupOptions = pickUpOptions)
                MealOrderSummaryScreen(
                    orderUiState = uiState,
                    onNextButtonClicked = {
                        navController.navigate(OrderScreen.NumberOfMeals.name)
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(dimensionResource(R.dimen.padding_medium))
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
                    restaurants = setOf(lazeez, campusPizza, shawaramaPlus),
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
                        .padding(dimensionResource(R.dimen.padding_medium))
                )
            }
            composable(route = OrderScreen.MealReview.name) {
                MealReviewScreen(
                    orderUiState = uiState,
                    onNextButtonClicked = {
                        navController.navigate(OrderScreen.MealPayment.name)
                    },
                    onCancelButtonClicked = {
                        orderedMeals = listOf()
                        viewModel.setMeals(listOf())
                        cancelOrderAndNavigateToStart(viewModel, navController)
                    },
                    modifier = Modifier.fillMaxHeight()
                )
            }
            composable(route = OrderScreen.MealPayment.name) {
                MealPaymentScreen(
                    viewModel.uiState.value.quantity,
                    viewModel.uiState.value.price,
                    onCancelButtonClicked = {
                        orderedMeals = listOf()
                        viewModel.setMeals(listOf())
                        cancelOrderAndNavigateToStart(viewModel, navController)
                    },
                    onPayButtonClicked = {
                        navController.navigate(OrderScreen.MealOrderSummary.name)
                    } // TODO: Set this function to go to the Hub (and later, add some kind of actual confirmation)
                )
            }
        }
    }
}

/**
 * Resets the [OrderUiState] and pops up to [OrderScreeen.Start]
 */
private fun cancelOrderAndNavigateToStart(
    viewModel: OrderViewModel,
    navController: NavHostController
) {
    viewModel.resetOrder()
    navController.popBackStack(OrderScreen.MealOrderSummary.name, inclusive = false)
}

