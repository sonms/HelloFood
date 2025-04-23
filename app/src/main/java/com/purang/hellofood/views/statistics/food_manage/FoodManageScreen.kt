package com.purang.hellofood.views.statistics.food_manage

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.purang.hellofood.utils.FirebaseUserManager
import com.purang.hellofood.utils.PreferenceDataStore
import com.purang.hellofood.viewmodels.FoodLogViewModel
import com.purang.hellofood.viewmodels.GeminiViewModel
import kotlinx.coroutines.launch
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodManageScreen(
    navController: NavController,
    geminiViewModel: GeminiViewModel = hiltViewModel(),
    viewModel: FoodLogViewModel = hiltViewModel(),
) {
    var water by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val userId = FirebaseUserManager.userId.toString()
    val foodLogs by viewModel.foodLogs.collectAsState()

    LaunchedEffect (Unit) {
        viewModel.fetchFoodLogs(userId)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = "Enter your eating habits information", style = MaterialTheme.typography.titleMedium) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "뒤로가기")
                    }
                },

                actions = {
                    IconButton(onClick = {
                        coroutineScope.launch {
                            PreferenceDataStore.setWater(context, String.format(Locale.getDefault(),"%.1f", (water.toInt()/1000)).toDouble())
                        }
                    }) {
                        Icon(Icons.Default.Check, contentDescription = "저장")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            OutlinedTextField(
                value = water,
                onValueChange = { newValue ->
                    if (newValue.isEmpty() || newValue.matches(Regex("\\d*(\\.\\d*)?"))) {
                        water = newValue
                    }
                },
                label = { Text("Water (ml)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Food Logs",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            LazyColumn {
                itemsIndexed(
                    items = foodLogs
                ) { _, foodLog ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .clickable {
                                geminiViewModel.fetchFoodLog(foodLog)
                                navController.navigate("analysis")
                            },
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 4.dp
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)

                        ) {
                            Text(text = "🍽 ${foodLog.foodName}", fontWeight = FontWeight.Bold)
                            Text(text = "🔥 ${foodLog.calories} kcal")
                            Text(text = "💪 Protein: ${foodLog.proteins}g")
                            Text(text = "🥑 Fat: ${foodLog.fats}g")
                            Text(text = "🧪 Vitamin: ${foodLog.vitamin}mg")
                            Text(text = "📝 ${foodLog.foodDescription}")
                        }
                    }
                }
            }
        }
    }

}