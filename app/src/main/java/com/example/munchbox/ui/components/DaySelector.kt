package com.example.munchbox.ui.components

import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.example.munchbox.controller.DayOfWeek

@Composable
fun DayTabs(
    days: Array<DayOfWeek>,
    selectedTabIndex: Int,
    onTabClick: (DayOfWeek) -> Unit
) {
    ScrollableTabRow(
        selectedTabIndex = selectedTabIndex,
        edgePadding = 0.dp,
    ) {
        days.forEachIndexed { tabIndex, day ->
            Tab(
                selected = selectedTabIndex == tabIndex,
                onClick = { onTabClick(day) },
                text = { Text(day.str) }
            )
        }
    }
}
