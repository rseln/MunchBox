package com.example.munchbox.signup

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultRegistryOwner
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.munchbox.OrderScreen
import com.example.munchbox.R
import com.example.munchbox.data.ClientID
import com.example.munchbox.data.StorageServices
import com.example.munchbox.login.LoginScreen
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.lang.IllegalStateException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen (
    navController: NavController,
    viewModel: SignUpViewModel = hiltViewModel()
) {

    val googleSignInState = viewModel.googleState.value
    val storageServices = StorageServices(FirebaseFirestore.getInstance())

    val launchergoogle =
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
    val state = viewModel.signUpState.collectAsState(initial = null )

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(120.dp))
        Text(
            modifier = Modifier.padding(bottom = 10.dp),
            text = "Create Account",
            style = TextStyle(fontSize = 50.sp, fontFamily = FontFamily.SansSerif)
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Enter your credentials to register.",
            fontWeight = FontWeight.Medium,
            fontSize = 15.sp, color = Color.Gray,
            fontFamily = FontFamily.SansSerif,
        )
        TextField(
            value = email,
            onValueChange = {email = it },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            placeholder = {
                Text(text = "Email")
            }
        )

        Spacer(modifier = Modifier.height(20.dp))
        TextField(
            value = password,
            onValueChange = {password = it },
            singleLine = true,
            placeholder = {
                Text(text = "Password")
            },
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
                        viewModel.registerUser(email, password)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(50.dp)
            ) {
                Text(
                    text = "Sign Up",
                    color = Color.White,
                )
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            if (state.value?.isLoading == true) {
                CircularProgressIndicator()
            }
        }
        Text(
            modifier = Modifier
                .padding(
                    top = 80.dp,
                ),
            text = "Or connect with",
            fontWeight = FontWeight.Medium, color = Color.Gray
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp), horizontalArrangement = Arrangement.Center
        ) {
            IconButton(onClick = {
                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .requestIdToken(ClientID.ServerClient)
                    .build()

                val googleSignInClient = GoogleSignIn.getClient(context, gso)

                launchergoogle.launch(googleSignInClient.signInIntent)
            }) {
                Icon(
                    modifier = Modifier.size(50.dp),
                    painter = painterResource(id = R.drawable.ic_google),
                    contentDescription = "Google Icon", tint = Color.Unspecified
                )
            }
        }
    }

    LaunchedEffect(key1 = state.value?.isSuccess) {
        scope.launch {
            if (state.value?.isSuccess?.isNotEmpty() == true) {
                val success = state.value?.isSuccess
                Toast.makeText(context, "$success", Toast.LENGTH_SHORT).show()
                //navController.popBackStack()
                navController.navigate(OrderScreen.ChooseFighter.name)

            }
        }
    }
    LaunchedEffect(key1 = state.value?.isError) {
        scope.launch {
            if (state.value?.isError?.isNotBlank() == true) {
                val error = state.value?.isError
                //Toast.makeText(context, "$error", Toast.LENGTH_SHORT).show()
            }
        }
    }
    LaunchedEffect(key1 = googleSignInState.success) {
        scope.launch {
            if(googleSignInState.success != null) {
                Toast.makeText(context, "Sign In Successful", Toast.LENGTH_SHORT).show()
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
