package com.example.munchbox.ui

/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.munchbox.R

/**
 * Composable for the login page.
 * [onLoginButtonClicked] lambda for navigation.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginButtonClicked: () -> Unit = {},
) {
    val context = LocalContext.current
//    var restaurant by remember() {
//        mutableStateOf<Restaurant?>(null)
//    }
//    var restaurants by remember() {
//        mutableStateOf<List<Restaurant>?>(null)
//    }
//    val storageService = RestaurantStorageService(FirebaseFirestore.getInstance())
//
//    LaunchedEffect(Unit){
//        val restID = storageService.createDBRestaurant("Anees House")
//        val fetchedRestaurant = storageService.getRestaurantByID(restID)
//        val allRestaurants = storageService.getAllRestaurants()
//        restaurant = fetchedRestaurant
//        restaurants = allRestaurants
//        storageService.deleteRestaurantByID("lazeez")
//    }
//
//    if (restaurant != null) {
//        // Use the restaurantName variable in your UI or business logic
//        Log.d("IM SHITTING", restaurant!!.name)
//    }
//
//    if (restaurants != null) {
//        // Use the restaurantName variable in your UI or business logic
//        for(r in restaurants!!) {
//            Log.d("IM SHITTING2", r!!.name)
//        }
//    }


    Box(modifier = Modifier.fillMaxSize()) {
        ClickableText(
            onClick = {  /*TODO*/ },
            text = buildAnnotatedString {
                append("New user? ")
                withStyle(style = SpanStyle(
                    fontSize = 14.sp,
                    fontFamily = FontFamily.Default,
                    textDecoration = TextDecoration.Underline,
                    color = Color.Blue)) {
                    append("Sign up here with email.")
                }
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(20.dp)
        )
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(120.dp))

        val username = remember { mutableStateOf(TextFieldValue()) }
        val password = remember { mutableStateOf(TextFieldValue()) }

        Text(text = "Login", style = TextStyle(fontSize = 50.sp, fontFamily = FontFamily.SansSerif))

        Spacer(modifier = Modifier.height(20.dp))
        TextField(
            label = { Text(text = "Username") },
            value = username.value,
            onValueChange = { username.value = it })

        Spacer(modifier = Modifier.height(20.dp))
        TextField(
            label = { Text(text = "Password") },
            value = password.value,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            onValueChange = { password.value = it })

        Spacer(modifier = Modifier.height(20.dp))
        Box(modifier = Modifier.padding(40.dp, 0.dp, 40.dp, 0.dp)) {
            Button(
                onClick = {
                    if(loginVerification(username, password, context)) {
                        onLoginButtonClicked()
                    }
                          },
                shape = RoundedCornerShape(50.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(text = "Login")
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        Box(modifier = Modifier.padding(40.dp, 0.dp, 40.dp, 0.dp)) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth(0.8F)
                    .padding(8.dp)
            ) {
                IconButton( onClick = { /*TODO*/ }){
                    Icon(
                        painter = painterResource(id = R.drawable.ic_google),
                        contentDescription = "Google Login",
                        tint= Color.Unspecified
                    )
                }
                IconButton( onClick = { /*TODO*/ }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_facebook),
                        contentDescription = "Facebook Login",
                        tint= Color.Unspecified
                    )
                }
                IconButton( onClick = { /*TODO*/ }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_twitter),
                        contentDescription = "Twitter Login",
                        tint= Color.Unspecified
                    )
                }
            }
        }
    }
}

fun loginVerification(
    username: MutableState<TextFieldValue>,
    password:  MutableState<TextFieldValue>,
    context: Context): Boolean {

    return if(username.value.text.isNotEmpty() and password.value.text.isNotEmpty()) {
        true
    } else {
        Toast.makeText(context, "Please enter valid credentials.", Toast.LENGTH_SHORT).show()
        false
    }
}

@Preview
@Composable
fun LoginPreview(){
    LoginScreen(onLoginButtonClicked = {})
}