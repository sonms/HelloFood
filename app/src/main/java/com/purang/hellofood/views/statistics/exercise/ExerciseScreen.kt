package com.purang.hellofood.views.statistics.exercise

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.purang.hellofood.BottomNavItem
import com.purang.hellofood.utils.PreferenceDataStore
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseScreen(
    navController: NavController,
    type : String
) {
    var min by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = "Please enter exercise time", style = MaterialTheme.typography.titleMedium) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "뒤로가기")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            //운동시간
            OutlinedTextField(
                value = min,
                onValueChange = { newValue ->
                    if (newValue.isEmpty() || newValue.matches(Regex("\\d*(\\.\\d*)?"))) {
                        min = newValue
                    }
                },
                label = { Text("Time (minute)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            /*// 키 입력
            OutlinedTextField(
                value = height,
                onValueChange = { newValue ->
                    if (newValue.isEmpty() || newValue.matches(Regex("\\d*(\\.\\d*)?"))) {
                        height = newValue
                    }
                },
                label = { Text("Height (cm)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )*/

            //Spacer(modifier = Modifier.height(24.dp))

            // 저장 버튼
            FilledIconButton(
                onClick = {
                    if (min.isNotEmpty() && type != "goal") {
                        coroutineScope.launch {
                            Log.e("setExerciseTime", min.toString())
                            PreferenceDataStore.setExerciseTime(context, min.toInt())
                        }
                        navController.navigate(BottomNavItem.Calendar.screenRoute) {
                            popUpTo(BottomNavItem.Calendar.screenRoute) { inclusive = true }
                            launchSingleTop = true
                        }
                    } else if (type == "goal") {
                        coroutineScope.launch {
                            PreferenceDataStore.setGoalExercise(context, goalExercise = min.toInt())
                        }
                        //navController.navigate(BottomNavItem.Calendar.screenRoute)
                        navController.navigate(BottomNavItem.Account.screenRoute) {
                            popUpTo(BottomNavItem.Account.screenRoute) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                },
                enabled = min.isNotEmpty(),
                modifier = Modifier.size(56.dp)
            ) {
                Icon(Icons.Default.Check, contentDescription = "저장")
            }
        }
    }
}
