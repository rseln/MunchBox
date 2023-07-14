package com.example.munchbox.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.munchbox.ui.components.SelectCard

@Composable
fun ChooseFighterScreen(
    onMunchButtonClick: () -> Unit,
    onRestaurantButtonClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
    ){
        SelectCard(
            headerText = "Are you a muncher?",
            buttonText = "I'm a munch",
            onClick = { onMunchButtonClick() })
        Spacer(modifier = Modifier.height(24.dp))
        SelectCard(
            headerText = "Do you box munches?",
            buttonText = "I'm a munch-boxer",
            onClick = { onRestaurantButtonClick() })
    }
}

