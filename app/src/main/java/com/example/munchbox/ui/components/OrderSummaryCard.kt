package com.example.munchbox.ui.components


import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.munchbox.R
import com.example.munchbox.controller.Restaurant
import com.example.munchbox.data.OrderUiState
import com.example.munchbox.ui.theme.Typography



@Composable
fun OrderSummaryCard(order: OrderUiState){

    Card(
        shape = MaterialTheme.shapes.medium,

    ){
        Image(
            painter = painterResource(id = R.drawable.lazeez),
            contentDescription = "Contact profile picture",
            modifier = Modifier
                .height(175.dp)
                .width(350.dp),
            contentScale = ContentScale.FillBounds
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = order.restaurant.name,
            style = Typography.headlineMedium,
            modifier = Modifier.padding(start = 16.dp),
        )
        Spacer(modifier = Modifier.height(16.dp))
    }


}
@Preview
@Composable
fun PreviewOrderSummaryCard(){
    val lazeez = Restaurant("Lazeez")
    OrderSummaryCard(OrderUiState(restaurant = lazeez))
}


