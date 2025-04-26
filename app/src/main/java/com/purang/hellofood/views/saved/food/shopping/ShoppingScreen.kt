package com.purang.hellofood.views.saved.food.shopping

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.purang.hellofood.R
import com.purang.hellofood.utils.FirebaseUserManager
import com.purang.hellofood.viewmodels.FoodLogViewModel
import com.purang.hellofood.viewmodels.GeminiViewModel
import com.purang.hellofood.views.saved.food.FoodItemUI

@Composable
fun ShoppingScreen(
    navController: NavController,
    foodLogViewModel: FoodLogViewModel = hiltViewModel()
) {
    val shoppingList by foodLogViewModel.shoppingList.collectAsState(emptyList())
    val userId = FirebaseUserManager.userId.toString()

    LaunchedEffect(Unit) {
        foodLogViewModel.fetchShoppingList(userId)
    }

    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Shopping List",
            modifier = Modifier.padding(bottom = 8.dp),
            fontWeight = FontWeight.Bold
        )

        LazyColumn {
            itemsIndexed(
                items = shoppingList
            ) { _ , item ->
                FoodItemUI(
                    item = item,
                    onClickItem = {

                    },
                    onLongClickItem = {
                        foodLogViewModel.deleteFoodItemShoppingToFireStore(it, userId)
                    }
                )
            }
        }
    }
}