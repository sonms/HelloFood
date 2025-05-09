package com.purang.hellofood.views.camera.analysis

import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.purang.hellofood.BottomNavItem
import com.purang.hellofood.R
import com.purang.hellofood.models.FoodLog
import com.purang.hellofood.ui.theme.TextColorGray
import com.purang.hellofood.ui.theme.blueExercise1
import com.purang.hellofood.ui.theme.blueExercise2
import com.purang.hellofood.ui.theme.blueExercise3
import com.purang.hellofood.ui.theme.greenFoodColor1
import com.purang.hellofood.ui.theme.greenFoodColor2
import com.purang.hellofood.ui.theme.greenFoodColor3
import com.purang.hellofood.ui.theme.mintColor4
import com.purang.hellofood.ui.theme.purpleRestColor1
import com.purang.hellofood.ui.theme.purpleRestColor2
import com.purang.hellofood.ui.theme.purpleRestColor3
import com.purang.hellofood.ui.theme.redPersonalColor1
import com.purang.hellofood.ui.theme.redPersonalColor2
import com.purang.hellofood.ui.theme.redPersonalColor3
import com.purang.hellofood.ui.theme.textYellow
import com.purang.hellofood.ui.theme.yellow
import com.purang.hellofood.utils.FirebaseUserManager
import com.purang.hellofood.viewmodels.FoodLogViewModel
import com.purang.hellofood.viewmodels.GeminiViewModel
import com.purang.hellofood.views.loading.LoadingState
import com.purang.hellofood.views.schedule.edit.CategoryChip
import kotlinx.coroutines.delay
import java.time.LocalDate

sealed class GeminiUiState {
    data object Initial : GeminiUiState()
    data object Loading : GeminiUiState()
    data object Success : GeminiUiState()
    data class Error(val errorMessage: String) : GeminiUiState()
}

@OptIn(ExperimentalLayoutApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AnalysisScreen ( //음식 분석 정보 확인
    navController: NavController,
    viewModel: GeminiViewModel,
    foodLogViewModel: FoodLogViewModel = hiltViewModel()
) {
    val dataList = listOf("Calories", "Protein", "Vitamin", "Fat")
    val responseFoodLog by viewModel.responseFoodLog.collectAsState()
    val selectedImage by viewModel.selectedImageUri.collectAsState()
    //val userId = FirebaseUserManager.userId.toString()

    val uiState by viewModel.uiState.collectAsState()

    when (uiState) {
        is GeminiUiState.Loading -> LoadingState.show()
        is GeminiUiState.Success -> {
            //Text(text = responseText)
            Log.e("SuccessAnaly", responseFoodLog.toString())
            LoadingState.hide()
            viewModel.fetchUIState(GeminiUiState.Initial)
        }
        is GeminiUiState.Error -> Text("Error : $responseFoodLog")

        else -> {
            GeminiUiState.Initial
        }
    }

    LaunchedEffect(Unit) {
        viewModel.clearResponse()
    }

    Scaffold(
        bottomBar = {
            BottomAppBar(
                modifier = Modifier.wrapContentHeight(),
                contentColor = Color.White,
                tonalElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Transparent)
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = {
                            navController.navigate(BottomNavItem.Camera.screenRoute) {
                                popUpTo(navController.graph.startDestinationId) { inclusive = false }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        modifier = Modifier.weight(1f), // 동일한 크기로 설정
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
                    ) {
                        Text(text = "Cancel", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    }

                    Spacer(modifier = Modifier.width(8.dp)) // 버튼 사이 간격 조정

                    Button(
                        onClick = {
                            responseFoodLog?.let { foodLog ->
                                val userId = FirebaseUserManager.userId.toString()
                                foodLogViewModel.saveFoodLogToFireStore(navController, foodLog, userId)
                            }
                            navController.navigate(BottomNavItem.Camera.screenRoute) {
                                popUpTo(navController.graph.startDestinationId) { inclusive = false }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = greenFoodColor2)
                    ) {
                        Text(
                            text = "Save Analysis",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())  // 스크롤 가능하게 유지
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
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
            }

            // 이미지 표시 영역
            Column(
                modifier = Modifier
                    .wrapContentSize()
                    .background(Color.White, RoundedCornerShape(16.dp))
            ) {
                Box(
                    modifier = Modifier
                        .background(Color.White, RoundedCornerShape(16.dp))
                        .fillMaxWidth()
                        .padding(16.dp)
                        .aspectRatio(16f / 9f)  // 비율 유지
                        .clip(RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    if (selectedImage != null) {
                        AsyncImage(
                            model = selectedImage,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_image_24),
                                tint = TextColorGray,
                                contentDescription = "select image"
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "No image selected",
                                color = TextColorGray,
                                fontSize = 16.sp
                            )
                        }
                    }
                }

                Row (
                    modifier = Modifier.fillMaxWidth()
                ){
                    Text(
                        text = LocalDate.now().toString(),
                        color = TextColorGray,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(16.dp),
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        text = responseFoodLog?.foodName.toString(),
                        color = TextColorGray,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(16.dp),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // body - 영양소 설명 창
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Text(
                    text = "Nutritional Analysis",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(bottom = 8.dp),
                    color = Color.Black
                )

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly, // 가로 간격 균등 배치
                    verticalArrangement = Arrangement.spacedBy(8.dp) // 세로 간격 조정
                ) {
                    dataList.forEach { category ->
                        AnalysisBoxUI(
                            category = category,
                            value = when(category) {
                                "Calories" -> {
                                    responseFoodLog?.calories?.toInt() ?: 0
                                }

                                "Protein" -> {
                                    responseFoodLog?.proteins?.toInt() ?: 0
                                }

                                "Vitamin" -> {
                                    responseFoodLog?.vitamin?.toInt() ?: 0
                                }

                                "Fat" -> {
                                    responseFoodLog?.fats?.toInt() ?: 0
                                }

                                else -> {
                                    responseFoodLog?.fats?.toInt() ?: 0
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth(0.5f)
                                .padding(4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    val allNutrients =  (responseFoodLog?.proteins?.toInt() ?: 0) + (responseFoodLog?.vitamin?.toInt() ?:0) + (responseFoodLog?.fats?.toInt() ?:0)
                    //val calorieRatio = if (allNutrients.toFloat() != 0f) (responseFoodLog?.calories ?: 0f) / allNutrients * 100f else 0f
                    val proteinRatio = if (allNutrients.toFloat() != 0f) (responseFoodLog?.proteins ?: 0f) / allNutrients * 100f else 0f
                    val vitaminRatio = if (allNutrients.toFloat() != 0f) (responseFoodLog?.vitamin ?: 0f) / allNutrients * 100f else 0f
                    val fatRatio = if (allNutrients.toFloat() != 0f) (responseFoodLog?.fats ?: 0f) / allNutrients * 100f else 0f

                    //AnalysisStatusProgress(name = "Calories", value = calorieRatio, color = blueExercise2)
                    AnalysisStatusProgress(name = "Protein", value = proteinRatio, color = greenFoodColor2)
                    AnalysisStatusProgress(name = "Vitamin", value = vitaminRatio, color = yellow)
                    AnalysisStatusProgress(name = "Fat", value = fatRatio, color = purpleRestColor2)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            // Meal Rating Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Text(
                    text = "Meal Rating",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = responseFoodLog?.foodDescription.toString(),
                    fontSize = 16.sp,
                    color = TextColorGray
                )
            }
        }
    }


}

@Composable
fun AnalysisBoxUI(
    category: String,
    value : Int,
    modifier : Modifier,
) {
    val color = when (category) {
        "Calories" -> blueExercise3
        "Protein" -> greenFoodColor3
        "Vitamin" -> yellow
        "Fat" -> purpleRestColor3
        else -> {
            redPersonalColor2
        }
    }

    val textColor = when (category) {
        "Calories" -> blueExercise1
        "Protein" -> greenFoodColor1
        "Vitamin" -> textYellow
        "Fat" -> purpleRestColor1
        else -> {
            redPersonalColor1
        }
    }

    val valueData = when (category) {
        "Calories" -> "${value}Kcal"
        "Protein" -> "${value}g"
        "Vitamin" -> "${value}mg"
        "Fat" -> "${value}g"
        else -> {
            "$value"
        }
    }

    Box(
        modifier = modifier.background(
            color,
            RoundedCornerShape(8.dp)
        )
    ) {
        Column (
            modifier = Modifier.padding(16.dp)
        ){
            Text(
                text = category,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.Black
            )

            Text(
                text = valueData,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = textColor
            )
        }
    }
}

@Composable
fun AnalysisStatusProgress(name: String, value: Float, color: Color) {
    var progress by remember { mutableFloatStateOf(0f) }

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 1500, easing = FastOutSlowInEasing)
    )

    LaunchedEffect(Unit) {
        delay(500)
        progress = value / 100f
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = name,
            fontSize = 14.sp,
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(80.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Box(
            modifier = Modifier
                .weight(1f)
                .height(8.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.LightGray)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(animatedProgress)
                    .background(color, RoundedCornerShape(16.dp))
                    .animateContentSize()
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = "${(animatedProgress * 100).toInt()}%", // 현재 진행된 값 반영
            fontSize = 14.sp,
            color = TextColorGray,
            fontWeight = FontWeight.Bold
        )
    }
}

