package com.purang.hellofood.views.account

import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.purang.hellofood.R
import com.purang.hellofood.ui.theme.TextColorGray
import com.purang.hellofood.ui.theme.blueColor1
import com.purang.hellofood.ui.theme.blueExercise2
import com.purang.hellofood.ui.theme.purpleRestColor2
import com.purang.hellofood.ui.theme.redPersonalColor2
import com.purang.hellofood.utils.PreferenceDataStore
import kotlinx.coroutines.delay

@Composable
fun AccountScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val personalData by PreferenceDataStore.getUserData(context).collectAsState(initial = null)
    val exerciseData by PreferenceDataStore.getExerciseTime(context).collectAsState(initial = null)
    val sleepData by PreferenceDataStore.getSleep(context).collectAsState(initial = null)
    //val restData by PreferenceDataStore.getRest(context).collectAsState(initial = 0)

    val goalPersonalData by PreferenceDataStore.getGoalWeight(context).collectAsState(initial = 0)
    val goalExerciseData by PreferenceDataStore.getGoalExercise(context).collectAsState(initial = 0)
    val goalSleepData by PreferenceDataStore.getGoalSleep(context).collectAsState(initial = 0)

    val weightProgress by remember(personalData, goalPersonalData) {
        derivedStateOf {
            if (goalPersonalData != 0 && personalData != null) {
                personalData!!.weight.toFloat() / goalPersonalData.toFloat()
            } else 0f
        }
    }

    val exerciseProgress by remember(exerciseData, goalExerciseData) {
        derivedStateOf {
            if (goalExerciseData != 0  && exerciseData != null) {
                exerciseData?.toFloat()?.div(goalExerciseData.toFloat())
            } else 0f
        }
    }

    val sleepProgress by remember(sleepData, goalSleepData) {
        derivedStateOf {
            if (goalSleepData != 0  && sleepData != null) {
                sleepData?.toFloat()?.div(goalSleepData.toFloat())
            } else 0f
        }
    }


    LaunchedEffect(personalData, exerciseData, sleepData, goalPersonalData, goalExerciseData, goalSleepData) {
        Log.e("goalPersonalData", goalPersonalData.toString())
        Log.e("goalExerciseData", goalExerciseData.toString())
        Log.e("goalSleepData", goalSleepData.toString())

        Log.e("weightProgress", weightProgress.toString())
        Log.e("exerciseProgress", exerciseProgress.toString())
        Log.e("sleepProgress", sleepProgress.toString())

        Log.e("personalData", personalData.toString())
        Log.e("exerciseData", exerciseData.toString())
        Log.e("sleepData", sleepData.toString())
    }

    LaunchedEffect(weightProgress, exerciseProgress, sleepProgress) {
        Log.e("DerivedProgress", "Weight: $weightProgress, Exercise: $exerciseProgress, Sleep: $sleepProgress")
    }

    val isDataReady = remember(
        personalData, exerciseData, sleepData,
        goalPersonalData, goalExerciseData, goalSleepData
    ) {
        personalData != null && exerciseData != null && sleepData != null
    }

    if (!isDataReady) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())  // 스크롤 가능하게 유지
            .padding(16.dp),
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

            Spacer(modifier = Modifier.weight(1f))

            IconButton(onClick = { /*설정 - Todo*/ }) {
                Icon(
                    Icons.Default.Settings,
                    contentDescription = "setting",
                    modifier = Modifier.size(24.dp)
                )
            }
        }



        Column (
            modifier = Modifier.background(Color.White, RoundedCornerShape(4.dp))
        ) {
            Text(
               text = "Health Goals",
               fontWeight = FontWeight.Bold,
               fontSize = 24.sp,
                modifier = Modifier.padding(16.dp)
            )

            AccountGoalsProgress(name = "Target Weight", goal = goalPersonalData.toString(), value = weightProgress, color = redPersonalColor2, icon = R.drawable.personal_icon) {
                navController.navigateFromGoal(it)
            }
            exerciseProgress?.let {
                AccountGoalsProgress(name = "Target Exercise Time", goal = goalExerciseData.toString(), value = it, color = blueExercise2, icon = R.drawable.baseline_fitness_center_24) {
                    navController.navigateFromGoal(it)
                }
            }
            sleepProgress?.let {
                AccountGoalsProgress(name = "Target Sleep Time", goal = goalSleepData.toString(), value = it, color = purpleRestColor2, icon = R.drawable.rest_icon) {
                    navController.navigateFromGoal(it)
                }
            }
            //AccountGoalsProgress(name = "Rest Time", value = restData.toFloat(), color = Color.Green)
        }
    }
}


@Composable
fun AccountGoalsProgress(
    name: String,
    goal: String,
    value: Float,
    color: Color,
    icon: Int,
    onClickProgress: (String) -> Unit
) {
    var progress by remember { mutableFloatStateOf(0f) }

    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 1500, easing = FastOutSlowInEasing)
    )

    LaunchedEffect(value) {
        delay(500)
        progress = value
        Log.e("progress", progress.toString())
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(16.dp)
            .background(Color.LightGray, RoundedCornerShape(8.dp))
            .clickable { onClickProgress(name) }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = "icon",
                tint = color,
                modifier = Modifier.size(24.dp)
            )

            Text(
                text = name,
                fontSize = 16.sp,
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = when (color) {
                    redPersonalColor2 -> "$goal kg"
                    blueExercise2 -> "$goal min"
                    purpleRestColor2 -> "$goal h"
                    else -> "$goal h"
                },
                fontSize = 16.sp,
                color = color,
                fontWeight = FontWeight.Bold
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.Gray)
            ) {
                val density = LocalDensity.current
                val animatedWidthDp = with(density) {
                    (constraints.maxWidth * animatedProgress).toDp()
                }
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(animatedWidthDp)
                        .background(color, RoundedCornerShape(16.dp))
                        .animateContentSize()
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${(animatedProgress * 100).toInt()}%",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.DarkGray,
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}



private fun NavController.navigateFromGoal(goal: String) {
    when (goal) {
        "Target Weight" -> navigate("personal?type=goal")
        "Target Exercise Time" -> navigate("exercise?type=goal")
        "Target Sleep Time" -> navigate("rest?type=goal")
    }
}