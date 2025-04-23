package com.purang.hellofood.views.saved.food

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.purang.hellofood.R
import com.purang.hellofood.models.FoodItem
import com.purang.hellofood.models.FoodLog
import com.purang.hellofood.ui.theme.greenFoodColor2
import com.purang.hellofood.utils.FirebaseUserManager
import com.purang.hellofood.viewmodels.FoodLogViewModel
import com.purang.hellofood.viewmodels.GeminiViewModel
import com.purang.hellofood.views.loading.LoadingState

@Composable
fun FoodScreen(
    geminiViewModel: GeminiViewModel = hiltViewModel(),
    foodLogViewModel: FoodLogViewModel = hiltViewModel(),
    navController: NavController,
    responseText : String
) {
    val responseTextHilt by geminiViewModel.responseText.collectAsState(initial = "")
    val userId = FirebaseUserManager.userId.toString()
    val context = LocalContext.current

    val itemList = remember {
        mutableStateListOf<FoodItem?>(null)
    }

    //var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(responseTextHilt) {

        Log.d("FoodScreen", "Parsing started")
        //itemList.clear()

        val parsedItems = responseTextHilt.split(":").last().split("/")
        Log.e("paredItems", "$parsedItems")
        parsedItems.forEach { item ->
            Log.e("paredItems1", item.toString())
            val parts = item.split(",").map { it.trim() }
            Log.e("paredItems2", parts.toString())
            if (parts.size >= 2) {
                val name = parts[0]
                val description = parts.filterIndexed { index, _ -> index > 0 }.joinToString(", ")
                itemList.add(
                    FoodItem(
                        foodId = "0",
                        userId = userId,
                        photoUrl = null,
                        foodName = name,
                        foodDescription = description
                    )
                )
            }
        }

        LoadingState.hide()
    }

    Column (
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = "Recommended Food",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 10.dp, bottom = 18.dp, start = 16.dp)
        )

        when {
            itemList.isEmpty() -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Empty Data",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            else -> {
                LazyColumn (
                    modifier = Modifier.fillMaxWidth().wrapContentHeight()
                        .padding(bottom = 56.dp)
                ) {
                    itemsIndexed(itemList) { _, item ->
                        if (item != null) {
                            FoodItemUI(
                                item = item,
                                onClickItem = {
                                    foodLogViewModel.setShoppingExistence(true)
                                    foodLogViewModel.saveShoppingItemToFireStore(it, userId)
                                    Toast.makeText(context, "추가되었습니다!", Toast.LENGTH_SHORT).show()
                                },
                                onLongClickItem = {//delete
                                    //foodLogViewModel.
                                    foodLogViewModel.deleteFoodItemShoppingToFireStore(it, userId)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FoodItemUI(
    item : FoodItem,
    onClickItem : (FoodItem) -> Unit,
    onLongClickItem : (FoodItem) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .background(Color.White, shape = RoundedCornerShape(12.dp)) // 둥근 모서리 적용
            //.border(1.dp, Color.Gray, shape = RoundedCornerShape(12.dp)) // 테두리 추가
            .combinedClickable(
                onClick = {
                    onClickItem(item)
                },
                onLongClick = {
                    onLongClickItem(item)
                },
            ),
    ) {
        Row(
            modifier = Modifier.padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(Color.White, RoundedCornerShape(12.dp))
            ) {
                AsyncImage(
                    model = R.drawable.vegetables_fr_m_icon,
                    contentDescription = "item image",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column (
                modifier = Modifier
                    .padding(start = 16.dp)
            ) {
                Text(
                    text = item.foodName.toString(),
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    fontSize = 16.sp
                )

                Text(
                    text = item.foodDescription.toString(),
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    fontSize = 16.sp
                )

                Text(
                    text = "+ Add to shopping list",
                    color = greenFoodColor2,
                    fontSize = 14.sp
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(16.dp))
}