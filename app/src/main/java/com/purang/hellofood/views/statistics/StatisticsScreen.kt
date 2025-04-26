package com.purang.hellofood.views.statistics

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.purang.hellofood.ui.theme.blueExercise1
import com.purang.hellofood.ui.theme.blueExercise2
import com.purang.hellofood.ui.theme.blueExercise3
import com.purang.hellofood.ui.theme.greenFoodColor1
import com.purang.hellofood.ui.theme.greenFoodColor2
import com.purang.hellofood.ui.theme.greenFoodColor3
import com.purang.hellofood.ui.theme.purpleRestColor1
import com.purang.hellofood.ui.theme.purpleRestColor2
import com.purang.hellofood.ui.theme.purpleRestColor3
import com.purang.hellofood.ui.theme.redPersonalColor1
import com.purang.hellofood.ui.theme.redPersonalColor2
import com.purang.hellofood.ui.theme.redPersonalColor3
import com.purang.hellofood.utils.FirebaseUserManager
import com.purang.hellofood.utils.PreferenceDataStore
import com.purang.hellofood.viewmodels.FoodLogViewModel
import com.purang.hellofood.viewmodels.GeminiViewModel
import com.purang.hellofood.views.camera.analysis.GeminiUiState
import com.purang.hellofood.views.loading.LoadingState
import kotlin.math.floor

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun StatisticsScreen (
    navController: NavController,
    geminiViewModel: GeminiViewModel = hiltViewModel(),
    foodLogViewModel: FoodLogViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val userId = FirebaseUserManager.userId.toString()

    val personalData by PreferenceDataStore.getUserData(context).collectAsState(initial = null)
    val exerciseData by PreferenceDataStore.getExerciseTime(context).collectAsState(initial = 0)
    val sleepData by PreferenceDataStore.getSleep(context).collectAsState(initial = 0)
    val restData by PreferenceDataStore.getRest(context).collectAsState(initial = 0)

    val foodData by foodLogViewModel.foodLogs.collectAsState(initial = emptyList())
    val waterData by PreferenceDataStore.getWater(context).collectAsState(initial = 0)
    //Exercise, Personal, Food, Rest

    val responseText by geminiViewModel.responseText.collectAsState()
    val uiState by geminiViewModel.uiState.collectAsState()
    val summary by foodLogViewModel.nutritionSummary.collectAsState()

    when (uiState) {
        is GeminiUiState.Loading -> LoadingState.show()
        is GeminiUiState.Success -> {
            //Text(text = responseText)
            //Log.e("SuccessAnaly", responseFoodLog.toString())
            LoadingState.hide()
            //geminiViewModel.fetchUIState(GeminiUiState.Error(""))
        }
        is GeminiUiState.Error -> {
            Text("Error : $responseText")
            LoadingState.hide()
            //Text("Error : $responseText")
        }

        else -> {
            GeminiUiState.Initial
        }
    }

    val personalDescription = responseText.split("/").firstOrNull() ?: ""
    val exerciseDescription = responseText.split("/").getOrNull(1) ?: ""
    val foodDescription = responseText.split("/").getOrNull(2) ?: ""
    val restDescription = responseText.split("/").getOrNull(3) ?: ""

    LaunchedEffect(Unit) {
        foodLogViewModel.fetchMonthlyNutrition(userId)
        if (uiState == GeminiUiState.Initial) {
            LoadingState.show()
            geminiViewModel.sendMessageWithText(
                prompt = "Command: Status Analysis\n" +
                        "Task: Provide improvement suggestions based on the given information. The given information is " +
                        "Personal (BMI : ${personalData?.bmi})\n" +
                        "Exercise (Active min : ${exerciseData})\n" +
                        "Food (calories : ${foodData.sumOf {it.calories?.toInt() ?: 0 }}, water : ${waterData}L)\n" +
                        "Rest (sleep Hour : ${sleepData}, Rest Hour : ${restData}). When answering, use the title of each information above, \n" +
                        "Separate each with a space and provide 4 improvement suggestions. Never use special characters. When improvements to each data are completed and improvements to the next data are written, insert the / symbol."
            )
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()) // 스크롤 가능하게 설정
            .padding(16.dp)
    ) {
        /*Row(
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
        }*/
        Personal(
            data = personalData,
            personalDescription,
            onClick = {
                navController.navigate("personal")
            }
        ) //불러오기

        Spacer(modifier = Modifier.height(16.dp))

        Exercise(
            description = exerciseDescription,
            weight = personalData?.weight ?: 0,
            min = exerciseData ?: 0,
            onClick = {
                navController.navigate("exercise")
            }
        ) //평균 값으로

        Spacer(modifier = Modifier.height(16.dp))

        Food(
            foodData.sumOf {it.calories?.toInt() ?: 0 },
            water = waterData.toDouble(),
            description = foodDescription,
            onClick = {
                navController.navigate("food")
            }
        ) //평균 값으로

        Spacer(modifier = Modifier.height(16.dp))

        Rest(
            sleep = sleepData,
            rest = restData,
            description = restDescription,
            onClick = {
                navController.navigate("rest")
            }
        ) //평균 값으로
    }
}

@Composable
fun Personal(
    data : PreferenceDataStore.UserData?,
    description : String,
    onClick : () -> Unit = {}
) {
    Column (
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(10.dp)
            .clickable {
                onClick()
            }
    ) {
        //제목, icon
        Row (
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Personal",
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.weight(1f))

            Icon (
                painter = painterResource(R.drawable.personal_icon),
                contentDescription = "personal icon",
                tint = redPersonalColor2,
            )
        }

        Row (
            modifier = Modifier.fillMaxWidth().padding(bottom = 15.dp)
        ) {
            //몸무게, 키, bmi
            Column (
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text (
                    text = data?.weight?.toString() ?: "0",
                    fontSize = 24.sp,
                    color = redPersonalColor2,
                    modifier = Modifier.padding(bottom = 2.dp)
                )

                Text (
                    text = "Weight",
                    fontSize = 14.sp,
                    color = TextColorGray,
                    modifier = Modifier.padding(bottom = 2.dp)
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally

            ) {
                Text (
                    text = data?.height?.toString() ?: "0",
                    fontSize = 24.sp,
                    color = redPersonalColor2,
                    modifier = Modifier.padding(bottom = 2.dp)
                )

                Text (
                    text = "Height",
                    fontSize = 14.sp,
                    color = TextColorGray,
                    modifier = Modifier.padding(bottom = 2.dp)
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text (
                    text = data?.bmi.toString(),
                    fontSize = 24.sp,
                    color = redPersonalColor2,
                    modifier = Modifier.padding(bottom = 2.dp)
                )

                Text (
                    text = "BMI",
                    fontSize = 14.sp,
                    color = TextColorGray,
                    modifier = Modifier.padding(bottom = 2.dp)
                )
            }
        }

        Box (
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .background(redPersonalColor3, RoundedCornerShape(8.dp))
        ) {
            Text(
                text = description,
                color = redPersonalColor1,
                modifier = Modifier.padding(12.dp)
            )
        }
    }
}

@Composable
fun Exercise(
    description: String,
    weight: Int,
    min: Int,
    onClick : () -> Unit = {}
) {
    var showTooltip by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(10.dp)
            .clickable {
                onClick()
            }
    ) {
        // 제목, icon
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Exercise",
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.weight(1f))

            Icon(
                painter = painterResource(R.drawable.baseline_fitness_center_24),
                contentDescription = "exercise icon",
                tint = blueExercise2,
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 15.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Active Min 텍스트를 포함한 Column
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = min.toString(),
                    fontSize = 24.sp,
                    color = blueExercise2,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Text(
                    text = "Active Min",
                    fontSize = 14.sp,
                    color = TextColorGray,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    /*val caloriesBurned = (5 * 3.5 * weight * min / 1000 * 5)
                    val formattedCalories = ((caloriesBurned * 100).toInt() / 100.0).toString()*/
                    Text(
                        text = ((5 * 3.5 * weight * min / 1000 * 5) * 100 / 100.0).toString(),
                        fontSize = 24.sp,
                        color = blueExercise2
                    )

                    Spacer(modifier = Modifier.width(5.dp))

                    // 툴팁 아이콘 버튼
                    Box (
                        modifier = Modifier.size(24.dp)
                    ) {
                        IconButton(onClick = { showTooltip = !showTooltip }) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Calorie Info",
                                tint = TextColorGray
                            )
                        }
                    }
                }

                Text(
                    text = "Burned Calories",
                    fontSize = 14.sp,
                    color = TextColorGray,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }


        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .background(blueExercise3, RoundedCornerShape(8.dp))
        ) {
            Text(
                text = description,
                color = blueExercise1,
                modifier = Modifier.padding(12.dp)
            )
        }
    }

    if (showTooltip) {
        Box(
            modifier = Modifier
                .width(IntrinsicSize.Max)
                .background(Color.LightGray, shape = RoundedCornerShape(8.dp))
                .padding(8.dp)
        ) {
            Text(
                text = "This is not accurate as it is calculated based on average exercise intensity.",
                color = Color.Black,
                fontSize = 12.sp
            )
        }
    }
}


@Composable
fun Food(
    calories : Int,
    water : Double,
    description : String,
    onClick : () -> Unit = {}
) {
    Column (
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(10.dp)
            .clickable {
                onClick()
            }
    ) {
        //제목, icon
        Row (
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Food",
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.weight(1f))

            Icon (
                painter = painterResource(R.drawable.food_icon),
                contentDescription = "food icon",
                tint = greenFoodColor2,
            )
        }

        Row (
            modifier = Modifier.fillMaxWidth().padding(bottom = 15.dp)
        ) {
            //칼로리, 물, 평균 todo
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text (
                    text = calories.toString(),
                    fontSize = 24.sp,
                    color = greenFoodColor2,
                    modifier = Modifier.padding(bottom = 2.dp)
                )

                Text (
                    text = "Calories",
                    fontSize = 14.sp,
                    color = TextColorGray,
                    modifier = Modifier.padding(bottom = 2.dp)
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text (
                    text = "${water}L",
                    fontSize = 24.sp,
                    color = greenFoodColor2,
                    modifier = Modifier.padding(bottom = 2.dp)
                )

                Text (
                    text = "Water",
                    fontSize = 14.sp,
                    color = TextColorGray,
                    modifier = Modifier.padding(bottom = 2.dp)
                )
            }
        }

        Box (
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .background(greenFoodColor3, RoundedCornerShape(8.dp))
        ) {
            Text(
                text = description,
                color = greenFoodColor1,
                modifier = Modifier.padding(12.dp)
            )
        }
    }
}

@Composable
fun Rest(
    sleep : Int,
    rest : Int,
    description : String,
    onClick : () -> Unit = {}
) {
    Column (
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(10.dp)
            .clickable {
                onClick()
            }
    ) {
        //제목, icon
        Row (
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Rest",
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.weight(1f))

            Icon (
                painter = painterResource(R.drawable.rest_icon),
                contentDescription = "rest icon",
                tint = purpleRestColor2,
            )
        }

        Row (
            modifier = Modifier.fillMaxWidth().padding(bottom = 15.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text (
                    text = sleep.toString(),
                    fontSize = 24.sp,
                    color = purpleRestColor2,
                    modifier = Modifier.padding(bottom = 2.dp)
                )

                Text (
                    text = "Sleep",
                    fontSize = 14.sp,
                    color = TextColorGray,
                    modifier = Modifier.padding(bottom = 2.dp)
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text (
                    text = rest.toString(),
                    fontSize = 24.sp,
                    color = purpleRestColor2,
                    modifier = Modifier.padding(bottom = 2.dp)
                )

                Text (
                    text = "Rest",
                    fontSize = 14.sp,
                    color = TextColorGray,
                    modifier = Modifier.padding(bottom = 2.dp)
                )
            }
        }

        Box (
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .background(purpleRestColor3, RoundedCornerShape(8.dp))
        ) {
            Text(
                text = description,
                color = purpleRestColor1,
                modifier = Modifier.padding(12.dp)
            )
        }
    }
}