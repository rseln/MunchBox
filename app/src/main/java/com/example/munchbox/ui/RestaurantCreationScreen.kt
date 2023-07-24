package com.example.munchbox.ui


import android.app.TimePickerDialog
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
\import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Calendar
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.example.munchbox.OrderScreen
import com.example.munchbox.R
import com.example.munchbox.controller.Restaurant
import com.example.munchbox.data.StorageServices
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.UUID
import java.util.regex.Pattern

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun RestaurantCreationScreen(
    navController: NavController
) {
    val mContext = LocalContext.current

    // VALUES TO SEND
    val restaurantName = remember { mutableStateOf(TextFieldValue()) }
    val mTimeStart = remember { mutableStateOf("") }
    val mTimeEnd = remember { mutableStateOf("") }
    val restaurantCuisine = remember { mutableStateOf(TextFieldValue()) }
    val restaurantPhoneNumber = remember {mutableStateOf(TextFieldValue())}
    val restaurantEmail = remember {mutableStateOf(TextFieldValue())}
    val imageURL = remember {mutableStateOf("")}


    // helper state vars
    var emailError by remember { mutableStateOf((false))}
    var phoneError by remember { mutableStateOf((false))}

    // submission / restaurant creation
    val storageServices = StorageServices(FirebaseFirestore.getInstance())

    @Composable
    fun SubmitComposable() {
        //https://stackoverflow.com/questions/64116377/how-to-call-kotlin-coroutine-in-composable-function-callbacks
        // Returns a scope that's cancelled when F is removed from composition
        val coroutineScope = rememberCoroutineScope()

        val sendRequestRestaurant: () -> Unit = {
            coroutineScope.launch {
                val rest: Restaurant? = storageServices.restaurantService().createDBRestaurant(
                    restaurantName.value.text,
                    imageURL.value
                )
            }
        }

        Button(
            enabled = (!emailError && !phoneError && restaurantName.value.text != ""),
            onClick = {
                sendRequestRestaurant()
                navController.navigate(OrderScreen.RestaurantHub.name)
            },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(text = stringResource(R.string.signup_restaurant_finish))
        }

    }

    // general form taken from https://github.com/Kiran-Bahalaskar/Pick-Image-From-Gallery-With-Jetpack-Compose/blob/master/app/src/main/java/com/kiranbahalaskar/pickimagefromgallery/MainActivity.kt
    // auto uploads image and updates form's download URL for rest creation
    @Composable
    fun PickImageFromGallery() {

        var imageUri by remember { mutableStateOf<Uri?>(null) }
        val context = LocalContext.current
        val bitmap = remember { mutableStateOf<Bitmap?>(null) }

        val launcher =
            rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
                imageUri = uri
            }

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            imageUri?.let {
                if (Build.VERSION.SDK_INT < 28) {
                    bitmap.value = MediaStore.Images
                        .Media.getBitmap(context.contentResolver, it)
                } else {
                    val source = ImageDecoder.createSource(context.contentResolver, it)
                    bitmap.value = ImageDecoder.decodeBitmap(source)
                }

                bitmap.value?.let { btm ->
                    val baos = ByteArrayOutputStream()
                    btm.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                    val data = baos.toByteArray()

                    // Create a reference to "mountains.jpg"
                    var photoID = UUID.randomUUID().toString()
                    val imageRef = Firebase.storage.reference.child("$photoID.jpg")

                    var uploadTask = imageRef.putBytes(data)
                    uploadTask.addOnFailureListener {
                        // Handle unsuccessful uploads
                        Log.e("FIRESTORE ERROR", it.toString())

                    }
//                    .addOnSuccessListener { taskSnapshot ->
//                        // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
//                        // ...
//                    }

                    val urlTask = uploadTask.continueWithTask { task ->
                        if (!task.isSuccessful) {
                            task.exception?.let {
                                Log.e("FIRESTORE ERROR", it.toString())
                                throw it
                            }
                        }
                        imageRef.downloadUrl
                    }.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val downloadUri = task.result
                            imageURL.value = downloadUri.toString()
                        } else {
                            // Handle failures
                            // ...
                            Log.e("FIRESTORE ERROR", "Unsuccessful image DownloadURL retrieve")
                        }
                    }
                    imageUri = null
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (imageURL.value != "") {
                Text(text = "Image Submitted Successfully")
            }
            else {
                Button(onClick = { launcher.launch("image/*") }) {
                    Text(text = "Pick Image")
                }
            }
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
        Text(
            stringResource(R.string.signup_restaurant_name),
            //modifier = Modifier.align(Alignment.Start)
        )
        TextField(
            value = restaurantName.value,
            onValueChange = { restaurantName.value = it },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.padding(20.dp))


        // RESTAURANT HOURS
        Text(stringResource(R.string.signup_restaurant_hours))
        Spacer(modifier = Modifier.padding(10.dp))

        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = { mTimePickerDialogStart.show() },
                //modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.signup_restaurant_hours_start),
                    color = Color.White
                )
            }
            Spacer(modifier = Modifier.padding(10.dp))
            Button(
                onClick = { mTimePickerDialogEnd.show() },
                //modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(R.string.signup_restaurant_hours_end), color = Color.White)
            }
        }

        if (mTimeStart.value != "") {
            Spacer(modifier = Modifier.padding(10.dp))
            Text(text = "Selected Start Time: ${mTimeStartFORMATTED.value}")
        }

        if (mTimeEnd.value != "") {
            Text(text = "Selected End Time: ${mTimeEndFORMATTED.value}")
        }
        Spacer(modifier = Modifier.padding(10.dp))


        // CUISINE
        Text(text = stringResource(R.string.signup_restaurant_cuisine))
        TextField(value = restaurantCuisine.value, onValueChange = {restaurantCuisine.value = it}, Modifier.fillMaxWidth(),)
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
        PickImageFromGallery()

        Spacer(modifier = Modifier.padding(20.dp))

        // FINISH
        SubmitComposable()

        // GO BACK
        Button(
            onClick = { navController.navigate(OrderScreen.ChooseFighter.name) },
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


