package com.example.munchbox.payment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.munchbox.MunchBoxApp
import com.example.munchbox.R
import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import org.json.JSONObject

var PUBLISH_KEY = "pk_test_51NT9kzHFo5HGXVWfSvxwHGG8TUNzxzp8E9Q8VsuhrW3VOL3xAJ2miba5gERq2YPImMHt3ONb3I0Izf4H2y3TLkZG00JfF8NlnR"
var SECRET_KEY = "sk_test_51NT9kzHFo5HGXVWft1sGRJIvoIJ1ZKRxR2oBFF1pImKy32e6uE9V9tGTOtGTLWqlkEJkU2w9IKpssGn6nVi6slBZ00Pc6JOdn7"
var customerId = ""
var ephemeralKey = ""
var paymentIntentClientSecret = ""

class PaymentActivity : ComponentActivity() {
    lateinit var paymentSheet: PaymentSheet
    lateinit var customerConfig: PaymentSheet.CustomerConfiguration
    lateinit var returnIntent: Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        val b = intent.extras
//        val amount_print = b?.getString("amount")
////        Toast.makeText(this, amount_print, Toast.LENGTH_LONG).show()
        val amount = "1000"
        val numMeals = 1
        // send requests
        createCustomer(amount)
        PaymentConfiguration.init(this, PUBLISH_KEY)
        paymentSheet = PaymentSheet(this, ::onPaymentSheetResult)

        returnIntent = Intent()

        setContent {
            MealPaymentScreen(
                numMeals = numMeals,
                price = "$" + amount,
                onPayButtonClicked = {
                    paymentFlow()
                },
                onCancelButtonClicked = {
                    setResult(RESULT_CANCELED, returnIntent);
                    finish()
                }
            )
        }

    }
    private fun createCustomer(amount: String) {
        val url = "https://api.stripe.com/v1/customers"

        val stringRequest = object: StringRequest(
            Method.POST, url,
            Response.Listener { response ->
                val obj = JSONObject(response)
                customerId = obj.getString("id")
                createEphemeralKey(customerId, amount)
            },
            Response.ErrorListener {  })
        {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer $SECRET_KEY"
                return headers
            }
        }
        val queue = Volley.newRequestQueue(this)
        queue.add(stringRequest)
    }

    private fun createEphemeralKey(customerId: String, amount: String) {
        val url = "https://api.stripe.com/v1/ephemeral_keys"

        val stringRequest = object: StringRequest(
            Method.POST, url,
            Response.Listener { response ->
                val obj = JSONObject(response)
                ephemeralKey = obj.getString("id")
                createPaymentIntent(customerId, amount)
            },
            Response.ErrorListener {  })
        {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer $SECRET_KEY"
                headers["Stripe-Version"] = "2022-11-15"
                return headers
            }
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["customer"] = customerId
                return params
            }
        }
        val queue = Volley.newRequestQueue(this)
        queue.add(stringRequest)
    }

    private fun createPaymentIntent(customerId: String, amount: String) {
        val url = "https://api.stripe.com/v1/payment_intents"

        val stringRequest = object: StringRequest(
            Method.POST, url,
            Response.Listener { response ->
                val obj = JSONObject(response)
                paymentIntentClientSecret = obj.getString("client_secret")
            },
            Response.ErrorListener {  })
        {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer $SECRET_KEY"
                return headers
            }
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["customer"] = customerId
                params["currency"] = "CAD"
                params["amount"] = amount
                params["automatic_payment_methods[enabled]"] = "true"
                return params
            }
        }
        val queue = Volley.newRequestQueue(this)
        queue.add(stringRequest)
    }

    private fun paymentFlow() {
        customerConfig = PaymentSheet.CustomerConfiguration(
            customerId,
            ephemeralKey
        )
        paymentSheet.presentWithPaymentIntent(
            paymentIntentClientSecret,
            PaymentSheet.Configuration(
                merchantDisplayName = "MunchBox",
                customer = customerConfig,
                allowsDelayedPaymentMethods = true),
        )
    }

    private fun onPaymentSheetResult(paymentSheetResult: PaymentSheetResult) {
        when(paymentSheetResult) {
            is PaymentSheetResult.Canceled -> {
                print("Canceled")
            }
            is PaymentSheetResult.Failed -> {
                print("Error: ${paymentSheetResult.error}")
            }
            is PaymentSheetResult.Completed -> {
                setResult(RESULT_OK, returnIntent);
                print("Completed")
                finish()
            }
        }
    }
}