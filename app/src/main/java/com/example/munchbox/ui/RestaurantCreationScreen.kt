package com.example.munchbox.ui


import android.app.TimePickerDialog
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.media.Image
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Calendar
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role.Companion.Image
import com.example.munchbox.R
import com.google.firebase.storage.ktx.component1
import com.google.firebase.storage.ktx.component2
import com.google.firebase.storage.ktx.component3
import com.example.munchbox.data.RestaurantStorageService
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
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
    Log.d("tag", "test Call")


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

    // submission / restaurant creation
    val storageService = RestaurantStorageService(FirebaseFirestore.getInstance())

    @Composable
    fun SubmitComposable() {
        //https://stackoverflow.com/questions/64116377/how-to-call-kotlin-coroutine-in-composable-function-callbacks
        // Returns a scope that's cancelled when F is removed from composition
        val coroutineScope = rememberCoroutineScope()


        val sendRequestRestaurant: () -> Unit = {
            coroutineScope.launch {
                val restID = storageService.createDBRestaurant(
                    restaurantName.value.text
                )
            }
        }

        Button(
            onClick = {
                sendRequestRestaurant()
            },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(text = stringResource(R.string.signup_restaurant_finish))
        }

    }

    // taken from https://github.com/Kiran-Bahalaskar/Pick-Image-From-Gallery-With-Jetpack-Compose/blob/master/app/src/main/java/com/kiranbahalaskar/pickimagefromgallery/MainActivity.kt
    @Composable
    fun PickImageFromGallery() {
        var imageURI by remember { mutableStateOf<Uri?>(null)}
        val context = LocalContext.current
        val bitmap = remember { mutableStateOf<Bitmap?>(null)}
        val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.PickVisualMedia()) {
                uri: Uri? -> imageURI = uri
        }


        Column {
            if (imageURI != null ) {
                Text(text = "Image Selected: ${File(imageURI!!.path).name}")
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Button(onClick = { launcher.launch(
            PickVisualMediaRequest(
                //Here we request only photos. Change this to .ImageAndVideo if
                //you want videos too.
                //Or use .VideoOnly if you only want videos.
                mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly))
        }) {
            Text(text = "Upload Image")
        }
    }

    // Declaring and initializing a calendar
    val mCalendarStart = Calendar.getInstance()
    val mCalendarEnd = Calendar.getInstance()
    val mHourStart = mCalendarStart[Calendar.HOUR_OF_DAY]
    val mMinuteStart = mCalendarStart[Calendar.MINUTE]
    val mHourEnd = mCalendarEnd[Calendar.HOUR_OF_DAY]
    val mMinuteEnd = mCalendarEnd[Calendar.MINUTE]

    val mTimeStartFORMATTED = remember { mutableStateOf("") }
    val mTimeEndFORMATTED = remember { mutableStateOf("") }

    // uses time picker code from https://www.geeksforgeeks.org/time-picker-in-android-using-jetpack-compose/
    // Creating TimePickerDialogs for RestaurantHours
    val mTimePickerDialogStart = TimePickerDialog(
        mContext,
        {_, mHour : Int, mMinute: Int ->
            mTimeStart.value = "$mHour:$mMinute"
            mTimeStartFORMATTED.value = convertTo12Hours(mTimeStart.value);
        }, mHourStart, mMinuteStart, false
    )
    val mTimePickerDialogEnd = TimePickerDialog(
        mContext,
        {_, mHour : Int, mMinute: Int ->
            mTimeEnd.value = "$mHour:$mMinute"
            mTimeEndFORMATTED.value = convertTo12Hours(mTimeEnd.value);
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


        // RESTAURANT NAME
        Text(stringResource(R.string.signup_restaurant_name),
            //modifier = Modifier.align(Alignment.Start)
        )
        TextField(value = restaurantName.value, onValueChange = {restaurantName.value = it}, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.padding(20.dp))

        // RESTAURANT ADDRESS
        Text(stringResource(R.string.signup_restaurant_address))
        TextField(value = restaurantAddress.value, onValueChange = {restaurantAddress.value = it}, Modifier.fillMaxWidth(),)
        Spacer(modifier = Modifier.padding(20.dp))

        // RESTAURANT HOURS
        Text(stringResource(R.string.signup_restaurant_hours))
        Button(onClick = { mTimePickerDialogStart.show() }, colors = ButtonDefaults.buttonColors(containerColor = Color(0XFF0F9D58))) {
            Text(text = stringResource(R.string.signup_restaurant_hours_start), color = Color.White)
        }
        Text(text = "Selected Start Time: ${mTimeStartFORMATTED.value}")

        Button(onClick = { mTimePickerDialogEnd.show() }, colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) {
            Text(text = stringResource(R.string.signup_restaurant_hours_end), color = Color.White)
        }
        Text(text = "Selected End Time: ${mTimeEndFORMATTED.value}")
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

        // EMAIL
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

        // PHOTO
        Text(text = stringResource(R.string.signup_restaurant_image))


        Spacer(modifier = Modifier.padding(20.dp))

        // FINISH
        SubmitComposable()

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

// taken from  https://stackoverflow.com/questions/6907968/how-to-convert-24-hr-format-time-in-to-12-hr-format
fun convertTo12Hours(militaryTime: String): String{
    //in => "14:00:00"
    //out => "02:00 PM"
    val inputFormat = SimpleDateFormat("hh:mm", Locale.getDefault())
    val outputFormat = SimpleDateFormat("h:mm aa", Locale.getDefault())
    val date = inputFormat.parse(militaryTime)
    return outputFormat.format(date)
}
// taken from https://github.com/Kiran-Bahalaskar/Pick-Image-From-Gallery-With-Jetpack-Compose/blob/master/app/src/main/java/com/kiranbahalaskar/pickimagefromgallery/MainActivity.kt
//@Composable
//fun PickImageFromGallery() {
//    var imageURI by remember { mutableStateOf<Uri?>(null)}
//    val context = LocalContext.current
//    val bitmap = remember { mutableStateOf<Bitmap?>(null)}
//
//    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.PickVisualMedia()) {
//        uri: Uri? -> imageURI = uri
//    }
//
//    Button(onClick = { launcher.launch(
//        PickVisualMediaRequest(
//            mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly))
//    }) {
//        Text(text = "Upload Image")
//    }
//
//    Column {
//        if (imageURI != null ) {
//            Text(text = "Image Selected: ${File(imageURI!!.path).name}")
//        }
//    }
//    Spacer(modifier = Modifier.height(12.dp))
//}


@Preview
@Composable
fun PreviewRestaurantCreationScreen() {
    RestaurantCreationScreen()
}