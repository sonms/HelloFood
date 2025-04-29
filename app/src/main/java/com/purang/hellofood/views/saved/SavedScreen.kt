package com.purang.hellofood.views.saved

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.purang.hellofood.R
import com.purang.hellofood.ui.theme.TextColorGray
import com.purang.hellofood.ui.theme.blueExercise2
import com.purang.hellofood.ui.theme.greenFoodColor2
import com.purang.hellofood.ui.theme.purpleRestColor2
import com.purang.hellofood.ui.theme.redPersonalColor2
import com.purang.hellofood.ui.theme.yellow
import com.purang.hellofood.utils.FirebaseUserManager
import com.purang.hellofood.viewmodels.FoodLogViewModel
import com.purang.hellofood.viewmodels.GeminiViewModel
import com.purang.hellofood.views.loading.LoadingState
import com.purang.hellofood.views.saved.food.FoodScreen
import com.purang.hellofood.views.saved.recipe.RecipeScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SavedScreen(
    navController: NavController,
    foodLogViewModel: FoodLogViewModel = hiltViewModel(),
    viewModel : GeminiViewModel = hiltViewModel()
) {
    val pagerState = rememberPagerState(pageCount = { 2 }) // 2개의 페이지 (캘린더, 통계)
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    //현재 달의 총 칼로리, 단백질, 지방, 탄수화물, 비타민
    val userId = FirebaseUserManager.userId.toString()
    //val foodLogs by foodLogViewModel.foodLogs.collectAsState()
    val responseText by viewModel.responseText.collectAsState()
    val summary by foodLogViewModel.nutritionSummary.collectAsState()

    val isExistShoppingData by foodLogViewModel.shoppingExistence.collectAsState()
    val isExistRecipeData by foodLogViewModel.recipeExistence.collectAsState()

    LaunchedEffect (Unit) {
        LoadingState.show()
        foodLogViewModel.fetchMonthlyNutrition(userId)
        Log.e("summary", summary.toString())
    }
    LaunchedEffect(summary) {
        Log.d("Summary 업데이트됨", summary.toString())
        viewModel.sendMessageWithText(
            prompt = "Check and analyze the monthly food intake nutrient data : $summary and recommend 5 foods such as fruits, vegetables, and meats that are good to eat, separated by commas, in the following format: name, and recommended nutrients. However, never use special characters and be sure to follow the format. After each recommended food, indicate the end by inserting the / symbol when recommending the next food."
        )
    }


    /*val personalData by PreferenceDataStore.getUserData(context).collectAsState(initial = null)
    val exerciseData by PreferenceDataStore.getExerciseTime(context).collectAsState(initial = 0)
    val sleepData by PreferenceDataStore.getSleep(context).collectAsState(initial = 0)
    val restData by PreferenceDataStore.getRest(context).collectAsState(initial = 0)*/






    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        //Top Stat (스크롤됨)
        item {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                // 상단 메뉴
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "backScreen",
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    IconButton(onClick = {
                        navController.navigate("recipe")
                    }) {
                        Icon(
                            painter = if (isExistRecipeData) {
                                painterResource(R.drawable.menu_book_add)
                            } else{
                                painterResource(R.drawable.outline_menu_book_24)
                            },
                            tint = Color.Unspecified, //xml에서 지정한 값
                            contentDescription = "menu book - recipe"
                        )
                    }

                    IconButton(onClick = { navController.navigate("shopping") }) {
                        Icon(
                            painter = if (isExistShoppingData) {
                                painterResource(R.drawable.shopping_cart_add_icon)
                            } else{
                                painterResource(R.drawable.baseline_shopping_cart_24)
                            },
                            tint = Color.Unspecified, //xml에서 지정한 값
                            contentDescription = "shopping cart"
                        )
                    }
                }

                // top stat
                Box(
                    modifier = Modifier
                        .background(Color.White, RoundedCornerShape(8.dp))
                        .padding(top = 12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "My (Month)",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        StatusProgress("Calories", summary.calories, max = 72000f, redPersonalColor2)
                        StatusProgress("Carbs", summary.carbohydrates, 9000f, greenFoodColor2)
                        StatusProgress("Proteins",summary.proteins, 1950f, blueExercise2)
                        StatusProgress("Fats", summary.fats, 2100f, purpleRestColor2)
                        StatusProgress("Vitamins",summary.vitamins, 3000f, yellow)
                    }
                }
            }
        }

        //Sticky Header - 탭(TabRow) (스크롤되지 않음)
        stickyHeader {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White) // 배경 추가 (스크롤 시 겹치지 않도록)
            ) {
                val tabTitles = listOf("Foods", "Recipes")

                TabRow(
                    selectedTabIndex = pagerState.currentPage,
                    modifier = Modifier.fillMaxWidth(),
                    indicator = {} // 기본 indicator 제거
                ) {
                    tabTitles.forEachIndexed { index, title ->
                        Tab(
                            selected = pagerState.currentPage == index,
                            onClick = {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                            },
                            text = {
                                Text(
                                    title,
                                    color = if (pagerState.currentPage == index) greenFoodColor2 else Color.Gray
                                )
                            }
                        )
                    }
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillParentMaxHeight()
            ) {
                SavedViewPager(viewModel, foodLogViewModel, pagerState, navController, responseText)
            }
        }
    }
}

@Composable
fun StatusProgress(
    name: String,
    value: Float,
    max: Float,
    color: Color,
) {
    var progress by remember { mutableFloatStateOf(0f) }

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 1500, easing = FastOutSlowInEasing)
    )

    LaunchedEffect(value, max) {
        delay(500)
        progress = (value / max).coerceIn(0f, 1f)  // 0~1 범위로 제한
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = name,
            fontSize = 14.sp,
            color = TextColorGray
        )

        Spacer(modifier = Modifier.weight(1f))

        Box(
            modifier = Modifier
                .width(200.dp)
                .height(8.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.LightGray)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(200.dp * animatedProgress)
                    .background(color, RoundedCornerShape(16.dp))
                    .animateContentSize()
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = "${(progress * 100).toInt()}%",
            fontSize = 12.sp,
            color = color
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SavedViewPager(
    viewModel: GeminiViewModel = hiltViewModel(),
    foodLogViewModel: FoodLogViewModel = hiltViewModel(),
    pagerState: PagerState,
    navController: NavController,
    responseText : String
) {
    HorizontalPager(
        modifier = Modifier.wrapContentHeight(),
        state = pagerState
    ) { page ->
        when (page) {
            0 -> FoodScreen(viewModel, foodLogViewModel, navController, responseText) // 캘린더 화면
            1 -> RecipeScreen(navController, viewModel, foodLogViewModel) // 통계 화면
        }
    }
}