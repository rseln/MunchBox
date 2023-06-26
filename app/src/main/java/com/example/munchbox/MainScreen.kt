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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.munchbox.controller.DayOfWeek
import com.example.munchbox.controller.DietaryOption
import com.example.munchbox.controller.Meal
import com.example.munchbox.controller.Restaurant
import com.example.munchbox.data.DataSource
import com.example.munchbox.ui.MealPaymentScreen
import com.example.munchbox.ui.LoginScreen
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

    var restaurant = Restaurant("Lazeez", setOf())
    val vegeMeal = Meal(setOf(DietaryOption.VEGE, DietaryOption.GF, DietaryOption.HALAL), restaurant, setOf(
        DayOfWeek.SUNDAY, DayOfWeek.SATURDAY))
    val meatMeal = Meal(setOf(DietaryOption.HALAL, DietaryOption.MEAT), restaurant, setOf(DayOfWeek.SUNDAY, DayOfWeek.SATURDAY))
    val allMeals = setOf<Meal>(vegeMeal, meatMeal)
    restaurant.addMeals(allMeals)
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
                        navController.navigate(OrderScreen.NumberOfMeals.name) {
                            popUpTo(OrderScreen.Login.name) {
                                inclusive = true
                            }
                        }
                    }
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
                    restaurants = setOf(restaurant),
//                    quantityOptions = DataSource.quantityOptions,
                    onNextButtonClicked = {
                        navController.navigate(OrderScreen.MealReview.name)
                    },
                    onCancelButtonClicked = {
                        cancelOrderAndNavigateToStart(viewModel, navController)
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
                        cancelOrderAndNavigateToStart(viewModel, navController)
                    },
                    onPayButtonClicked = { } // TODO: Set this function to go to the Hub (and later, add some kind of actual confirmation)
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
    navController.popBackStack(OrderScreen.NumberOfMeals.name, inclusive = false)
}

