package com.example.munchbox.login

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
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.munchbox.OrderScreen
import com.example.munchbox.R
import com.example.munchbox.controller.User
import com.example.munchbox.data.ClientID.ServerClient
import com.example.munchbox.data.StorageServices
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.GoogleAuthProvider
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

/**
 * Composable for the login page.
 * [onLoginButtonClicked] lambda for navigation.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: SignInViewModel = hiltViewModel(),
) {

    val storageServices = StorageServices(FirebaseFirestore.getInstance())
    val googleSignInState = viewModel.googleState.value

    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) {
            val account = GoogleSignIn.getSignedInAccountFromIntent(it.data)
            try {
                val result = account.getResult(ApiException::class.java)
                val credentials = GoogleAuthProvider.getCredential(result.idToken, null)
                viewModel.googleSignIn(credentials)
            } catch (it: ApiException) {
                print(it)
            }
        }

    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val state = viewModel.signInState.collectAsState(initial = null)

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(120.dp))

        Text(text = "Login", style = TextStyle(fontSize = 50.sp, fontFamily = FontFamily.SansSerif))

        Spacer(modifier = Modifier.height(20.dp))
        TextField(
            label = { Text(text = "Email") },
            value = email,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            onValueChange = {email = it })

        Spacer(modifier = Modifier.height(20.dp))
        TextField(
            label = { Text(text = "Password") },
            value = password,
            onValueChange = { password = it },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                val image = if (passwordVisible)
                    Icons.Filled.Visibility
                else Icons.Filled.VisibilityOff

                // Localized description for accessibility services
                val description = if (passwordVisible) "Hide password" else "Show password"

                // Toggle button to hide or display password
                IconButton(onClick = {passwordVisible = !passwordVisible}){
                    Icon(imageVector  = image, description)
                }
            }
        )

        Spacer(modifier = Modifier.height(20.dp))
        Box(modifier = Modifier.padding(40.dp, 0.dp, 40.dp, 0.dp)) {
            Button(
                onClick = {
                    scope.launch {
                        viewModel.loginUser(email, password)
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

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            if (state.value?.isLoading == true) {
                CircularProgressIndicator()
            }

        }
        Spacer(modifier = Modifier.height(100.dp))
        Box(modifier = Modifier.padding(40.dp, 0.dp, 40.dp, 0.dp)) {
            OutlinedButton(
                onClick = {
                    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .requestIdToken(ServerClient)
                        .build()

                    val googleSignInClient = GoogleSignIn.getClient(context, gso)

                    launcher.launch(googleSignInClient.signInIntent)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .padding(start = 55.dp, end = 55.dp),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.5.dp, Color.DarkGray),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.White,
                    contentColor = Color.DarkGray
                )
            ) {
            Row (
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center

            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_google),
                    contentDescription = "",
                    modifier = Modifier.size(35.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Sign in with Google",
                    style = TextStyle(fontSize = 15.sp, fontFamily = FontFamily.SansSerif),
                    modifier = Modifier.fillMaxWidth()
                )
            } }
        }
        Spacer(modifier = Modifier.height(160.dp))
        Text(
            modifier = Modifier
                .padding(15.dp)
                .clickable {
                    navController.navigate(OrderScreen.Signup.name)
                },
            text = "Don't have an account? Sign Up!",
            //fontWeight = FontWeight.Bold,
            color = Color.Blue, fontFamily = FontFamily.SansSerif
        )

        LaunchedEffect(key1 = state.value?.isSuccess) {
            scope.launch {
                if (state.value?.isSuccess?.isNotEmpty() == true) {
                    val success = state.value?.isSuccess
                    Toast.makeText(context, "$success", Toast.LENGTH_SHORT).show()
                    val IDuser = Firebase.auth.currentUser?.uid
                    val userType: String? = IDuser?.let {
                        storageServices.userService().getTypeByUserID(
                            it
                        )
                    }
                    if (userType == "Muncher") {
                        navController.navigate(OrderScreen.MealOrderSummary.name)
                    }
                    else if (userType == "Restaurant") {
                        navController.navigate(OrderScreen.RestaurantHub.name)
                    }
                    else {
                        navController.navigate(OrderScreen.ChooseFighter.name)
                    }

                }
            }
        }
        LaunchedEffect(key1 = state.value?.isError) {
            scope.launch {
                if (state.value?.isError?.isNotEmpty() == true) {
                    val error = state.value?.isError
                    //Toast.makeText(context, "$error", Toast.LENGTH_SHORT).show()
                }
            }
        }

        LaunchedEffect(key1 = googleSignInState.success) {
            scope.launch {
                if(googleSignInState.success != null) {
                    Toast.makeText(context, "Sign In Success", Toast.LENGTH_SHORT).show()
                    val IDuser = Firebase.auth.currentUser?.uid
                    val userType: String? = IDuser?.let {
                        storageServices.userService().getTypeByUserID(
                            it
                        )
                    }
                    if (userType == "Muncher") {
                        navController.navigate(OrderScreen.MealOrderSummary.name)
                    }
                    else if (userType == "Restaurant") {
                        navController.navigate(OrderScreen.RestaurantHub.name)
                    }
                    else {
                        navController.navigate(OrderScreen.ChooseFighter.name)
                    }


                }
            }
        }

        LaunchedEffect(key1 = googleSignInState.error) {
            scope.launch {
                if(googleSignInState.error != null) {
                    val error = googleSignInState.error
                    //Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                }
            }
        }

    }
}

