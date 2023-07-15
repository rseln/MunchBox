package com.example.munchbox.ui

// uses time picker code from https://www.geeksforgeeks.org/time-picker-in-android-using-jetpack-compose/

import android.app.TimePickerDialog
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestaurantCreationScreen() {
    val mContext = LocalContext.current

    val restaurantName = remember { mutableStateOf(TextFieldValue()) }
    val restaurantAddress = remember { mutableStateOf(TextFieldValue()) }

    // Declaring and initializing a calendar
    val mCalendarStart = Calendar.getInstance()
    val mCalendarEnd = Calendar.getInstance()
    val mHourStart = mCalendarStart[Calendar.HOUR_OF_DAY]
    val mMinuteStart = mCalendarStart[Calendar.MINUTE]
    val mHourEnd = mCalendarEnd[Calendar.HOUR_OF_DAY]
    val mMinuteEnd = mCalendarEnd[Calendar.MINUTE]
    // Value for storing time as a string
    val mTimeStart = remember { mutableStateOf("") }
    val mTimeEnd = remember { mutableStateOf("") }
    // Creating a TimePicker dialod
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

    val restaurantPhoneNumber = remember {mutableStateOf(TextFieldValue())}
    val restaurantEmail = remember {mutableStateOf(TextFieldValue())}

    Column(
        //horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // RESTAURANT NAME - done
        Text("Restaurant Name",
            //modifier = Modifier.align(Alignment.Start)
        )
        TextField(value = restaurantName.value, onValueChange = {restaurantName.value = it})

        Spacer(modifier = Modifier.padding(20.dp))

        // RESTAURANT ADDRESS - done
        Text(text = "Address")
        TextField(value = restaurantAddress.value, onValueChange = {restaurantAddress.value = it})

        Spacer(modifier = Modifier.padding(20.dp))

        // RESTAURANT HOURS
        Text(text = "Restaurant Hours")
        Button(onClick = { mTimePickerDialogStart.show() }, colors = ButtonDefaults.buttonColors(containerColor = Color(0XFF0F9D58))) {
            Text(text = "Select Opening Time", color = Color.White)
        }
        Text(text = "Selected Start Time: ${mTimeStart.value}")

        Button(onClick = { mTimePickerDialogEnd.show() }, colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) {
            Text(text = "Select Closing Time", color = Color.White)
        }
        Text(text = "Selected End Time: ${mTimeEnd.value}")

        Spacer(modifier = Modifier.padding(20.dp))

        // CUISINE
        Text(text = "Cuisine")

        Spacer(modifier = Modifier.padding(20.dp))

        // DIETARY OPTIONS
        Text(text = "Dietary Offerings")

        Spacer(modifier = Modifier.padding(20.dp))

        // PHONE NUMBER
        Text(text = "Business Phone Number")
        TextField(
            value = restaurantPhoneNumber.value,
            {restaurantPhoneNumber.value = it},
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
        )
        Spacer(modifier = Modifier.padding(20.dp))

        // EMAIL
        Text(text = "Business Email")
        TextField(value = restaurantEmail.value, onValueChange = {restaurantEmail.value = it}, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email))
        Spacer(modifier = Modifier.padding(20.dp))

        // PAYMENT
        Text(text = "Link Payment Account ")
        // TODO
        Spacer(modifier = Modifier.padding(20.dp))

        // FINISH
        Button(onClick = { /*TODO*/ }) {
            Text(text = "Finish")
        }
    }
}

@Preview
@Composable
fun PreviewRestaurantCreationScreen() {
    RestaurantCreationScreen()
}