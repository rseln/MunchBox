package com.example.munchbox.ui

// uses time picker code from https://www.geeksforgeeks.org/time-picker-in-android-using-jetpack-compose/

import android.app.TimePickerDialog
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.munchbox.controller.DietaryOption
import java.util.Calendar
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.example.munchbox.R
import java.util.regex.Pattern

val dietaryOptions = setOf(
    Pair(0, "Halal"),
    Pair(1, "Vegetarian"),
    Pair(2, "Kosher"),
    Pair(3, "Vegan"),
    Pair(4, "Gluten Free"),
    Pair(5, "Peanut Free"),
    Pair(6, "Meat")
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun RestaurantCreationScreen() {
    val mContext = LocalContext.current

    // VALUES TO SEND
    val restaurantName = remember { mutableStateOf(TextFieldValue()) }
    val restaurantAddress = remember { mutableStateOf(TextFieldValue()) }
    val mTimeStart = remember { mutableStateOf("") }
    val mTimeEnd = remember { mutableStateOf("") }
    val restaurantCuisine = remember { mutableStateOf(TextFieldValue()) }
    var selectedOptions by remember { mutableStateOf(setOf<Pair<Int, String>>()) }
    val restaurantPhoneNumber = remember {mutableStateOf(TextFieldValue())}
    val restaurantEmail = remember {mutableStateOf(TextFieldValue())}

    // helper state vars
    var emailError by remember { mutableStateOf((false))}
    var phoneError by remember { mutableStateOf((false))}


    // Declaring and initializing a calendar
    val mCalendarStart = Calendar.getInstance()
    val mCalendarEnd = Calendar.getInstance()
    val mHourStart = mCalendarStart[Calendar.HOUR_OF_DAY]
    val mMinuteStart = mCalendarStart[Calendar.MINUTE]
    val mHourEnd = mCalendarEnd[Calendar.HOUR_OF_DAY]
    val mMinuteEnd = mCalendarEnd[Calendar.MINUTE]

    // Creating TimePickerDialogs for RestaurantHours
    val mTimePickerDialogStart = TimePickerDialog(
        mContext,
        {_, mHour : Int, mMinute: Int ->
            mTimeStart.value = "$mHourStart:$mMinuteStart"
        }, mHourStart, mMinuteStart, false
    )
    val mTimePickerDialogEnd = TimePickerDialog(
        mContext,
        {_, mHour : Int, mMinute: Int ->
            mTimeEnd.value = "$mHour:$mMinute"
        }, mHourEnd, mMinuteEnd, false
    )
    Column(
        //horizontalAlignment = Alignment.CenterHorizontally
    modifier = Modifier
        .verticalScroll(rememberScrollState())
        .padding(dimensionResource(R.dimen.padding_medium))
    ) {
        Text(text = stringResource(R.string.app_name), fontSize = 30.sp)
        Text(text = stringResource(R.string.signup_restaurant_title), fontSize = 20.sp)
        Spacer(modifier = Modifier.padding(10.dp))


        // RESTAURANT NAME - done
        Text(stringResource(R.string.signup_restaurant_name),
            //modifier = Modifier.align(Alignment.Start)
        )
        TextField(value = restaurantName.value, onValueChange = {restaurantName.value = it}, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.padding(20.dp))

        // RESTAURANT ADDRESS - done
        Text(stringResource(R.string.signup_restaurant_address))
        TextField(value = restaurantAddress.value, onValueChange = {restaurantAddress.value = it}, Modifier.fillMaxWidth(),)
        Spacer(modifier = Modifier.padding(20.dp))

        // RESTAURANT HOURS
        Text(stringResource(R.string.signup_restaurant_hours))
        Button(onClick = { mTimePickerDialogStart.show() }, colors = ButtonDefaults.buttonColors(containerColor = Color(0XFF0F9D58))) {
            Text(text = stringResource(R.string.signup_restaurant_hours_start), color = Color.White)
        }
        Text(text = "Selected Start Time: ${mTimeStart.value}")
        Button(onClick = { mTimePickerDialogEnd.show() }, colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) {
            Text(text = stringResource(R.string.signup_restaurant_hours_end), color = Color.White)
        }
        Text(text = "Selected End Time: ${mTimeEnd.value}")
        Spacer(modifier = Modifier.padding(20.dp))

        // CUISINE
        Text(text = stringResource(R.string.signup_restaurant_cuisine))
        TextField(value = restaurantCuisine.value, onValueChange = {restaurantCuisine.value = it}, Modifier.fillMaxWidth(),)
        Spacer(modifier = Modifier.padding(20.dp))

        // DIETARY OPTIONS
        Text(text = stringResource(R.string.signup_restaurant_dietary_options))
        FlowRow(
            modifier = Modifier.fillMaxWidth()
        ) {
            for (option in dietaryOptions) {
                FilterChip(
                    selected = selectedOptions.contains(option),
                    label = {Text(option.second)},
                    onClick = {
                        if (selectedOptions.contains(option)) {
                            selectedOptions = selectedOptions.minus(option)
                        }
                        else {
                            selectedOptions = selectedOptions.plus(option)
                        }
                    },
                    leadingIcon = {
                        if (selectedOptions.contains(option)) {
                            Icon(
                                imageVector = Icons.Filled.Done,
                                contentDescription = "Localized Description",
                                modifier = Modifier.size(FilterChipDefaults.IconSize)
                            )
                        }
                    }
                )
                Spacer(modifier = Modifier.size(4.dp, 4.dp))
            }
        }
        Spacer(modifier = Modifier.padding(20.dp))

        // PHONE NUMBER
        Text(text = stringResource(R.string.signup_restaurant_phone))
        TextField(
            value = restaurantPhoneNumber.value,
            onValueChange = {
                restaurantPhoneNumber.value = it
                phoneError = !isValidPhone(it.text)
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            isError = phoneError,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.padding(20.dp))

        // EMAIL - done
        Text(text = stringResource(R.string.signup_restaurant_email))
        TextField(
            value = restaurantEmail.value,
            onValueChange = {
                // taken from https://stackoverflow.com/questions/65641875/jetpack-compose-textfield-inputfilter-to-have-only-currency-regex-inputs
                restaurantEmail.value = it
                emailError = !isValidEmail(it.text)
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            isError = emailError,
            modifier = Modifier.fillMaxWidth(),)
        Spacer(modifier = Modifier.padding(20.dp))

        // FINISH
        Button(
            onClick = { /*TODO*/ },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(text = stringResource(R.string.signup_restaurant_finish))
        }
        // GO BACK
        Button(
            onClick = { /*TODO*/ },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(text = stringResource(R.string.back_button))
        }
    }
}

fun isValidPhone(phoneStr: String?) = Pattern.compile(
    "^[\\+]?[(]?[0-9]{3}[)]?[-\\s\\.]?[0-9]{3}[-\\s\\.]?[0-9]{4,6}\$")
    .matcher(phoneStr).find()
fun isValidEmail(emailStr: String?) = Pattern.compile(
    "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE)
    .matcher(emailStr).find()


@Preview
@Composable
fun PreviewRestaurantCreationScreen() {
    RestaurantCreationScreen()
}