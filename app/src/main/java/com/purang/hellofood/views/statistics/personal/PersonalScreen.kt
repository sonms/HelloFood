package com.purang.hellofood.views.statistics.personal

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
fun PersonalScreen(navController: NavController, type : String) {
    var weight by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = "Enter your physical information", style = MaterialTheme.typography.titleMedium) },
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
            // 체중 입력
            OutlinedTextField(
                value = weight,
                onValueChange = { newValue ->
                    if (newValue.isEmpty() || newValue.matches(Regex("\\d*(\\.\\d*)?"))) {
                        weight = newValue
                    }
                },
                label = { Text("Weight (kg)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 키 입력
            if (type != "goal") {
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
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 저장 버튼
            FilledIconButton(
                onClick = {
                    if (weight.isNotEmpty() && height.isNotEmpty() && type != "goal") {
                        coroutineScope.launch {
                            PreferenceDataStore.setUserData(context, weight = weight, height = height)
                        }
                        navController.navigate(BottomNavItem.Calendar.screenRoute) {
                            popUpTo(BottomNavItem.Calendar.screenRoute) { inclusive = true }
                            launchSingleTop = true
                        }
                    } else if (weight.isNotEmpty() && type == "goal") {
                        coroutineScope.launch {
                            PreferenceDataStore.setGoalWeight(context, goalWeight = weight.toInt())
                        }
                        navController.navigate(BottomNavItem.Account.screenRoute) {
                            popUpTo(BottomNavItem.Account.screenRoute) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                },
                enabled = if (type != "goal") {
                    weight.isNotEmpty() && height.isNotEmpty()
                } else {
                    weight.isNotEmpty()
                },
                modifier = Modifier.size(56.dp)
            ) {
                Icon(Icons.Default.Check, contentDescription = "저장")
            }
        }
    }
}
