package com.purang.hellofood.viewmodels

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.purang.hellofood.models.FoodItem
import com.purang.hellofood.models.FoodLog
import com.purang.hellofood.models.NutritionSummary
import com.purang.hellofood.repositories.FoodLogRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class FoodLogViewModel @Inject constructor(
    private val repository: FoodLogRepository
) : ViewModel() {
    private val _shoppingExistence = MutableStateFlow(false)
    val shoppingExistence: StateFlow<Boolean> = _shoppingExistence
    private val _recipeExistence = MutableStateFlow(false)
    val recipeExistence: StateFlow<Boolean> = _recipeExistence

    private val _shoppingList = MutableStateFlow<List<FoodItem>>(emptyList())
    val shoppingList: StateFlow<List<FoodItem>> = _shoppingList
    private val _recipeList = MutableStateFlow<List<FoodItem>>(emptyList())
    val recipeList: StateFlow<List<FoodItem>> = _recipeList

    fun setShoppingExistence(value: Boolean) {
        _shoppingExistence.value = value
    }
    fun setRecipesExistence(value: Boolean) {
        _recipeExistence.value = value
    }


    private val _nutritionSummary = MutableStateFlow(NutritionSummary())
    val nutritionSummary: StateFlow<NutritionSummary> = _nutritionSummary

    @RequiresApi(Build.VERSION_CODES.O)
    fun fetchMonthlyNutrition(userId: String) {
        viewModelScope.launch {
            val now = LocalDateTime.now()
            val currentMonthLogs = repository.getFoodLogs(userId)
                .filter {
                    try {
                        val logDate = LocalDateTime.parse(it.createdAt)
                        logDate.year == now.year && logDate.monthValue == now.monthValue
                    } catch (e: Exception) {
                        false
                    }
                }

            val totalCalories = currentMonthLogs.sumOf { it.calories?.toDouble() ?: 0.0 }
            val totalCarbs = currentMonthLogs.sumOf { it.carbohydrates?.toDouble() ?: 0.0 }
            val totalProteins = currentMonthLogs.sumOf { it.proteins?.toDouble() ?: 0.0 }
            val totalFats = currentMonthLogs.sumOf { it.fats?.toDouble() ?: 0.0 }
            val totalVitamins = currentMonthLogs.sumOf { it.vitamin?.toDouble() ?: 0.0 }

            _nutritionSummary.value = NutritionSummary(
                calories = totalCalories.toFloat(),
                carbohydrates = totalCarbs.toFloat(),
                proteins = totalProteins.toFloat(),
                fats = totalFats.toFloat(),
                vitamins = totalVitamins.toFloat()
            )
        }
    }

    private val _foodLogs = MutableStateFlow<List<FoodLog>>(emptyList())
    val foodLogs: StateFlow<List<FoodLog>> = _foodLogs

    fun fetchFoodLogs(userId: String) {
        viewModelScope.launch {
            val logs = repository.getFoodLogs(userId)
            _foodLogs.value = logs
        }
    }

    fun fetchShoppingList(userId: String) {
        viewModelScope.launch {
            val logs = repository.getShoppingList(userId)
            _shoppingList.value = logs
        }
    }

    fun fetchRecipeList(userId: String) {
        viewModelScope.launch {
            val logs = repository.getRecipeList(userId)
            _recipeList.value = logs
        }
    }


    fun saveFoodLogToFireStore(navController: NavController, foodLog: FoodLog, userId: String) {
        viewModelScope.launch {
            val db = Firebase.firestore
            val userFoodLogsRef = db.collection("users").document(userId).collection("foodLogs")

            userFoodLogsRef.add(foodLog)
                .addOnSuccessListener {
                    Log.d("Firestore", "FoodLog 저장 성공: ${it.id}")
                    navController.navigate("analysis")
                }
                .addOnFailureListener { e ->
                    Log.e("Firestore", "FoodLog 저장 실패", e)
                }
        }
    }

    fun saveShoppingItemToFireStore(foodItem: FoodItem, userId: String) {
        viewModelScope.launch {
            val db = Firebase.firestore
            val userFoodLogsRef = db.collection("users").document(userId).collection("shopping")

            userFoodLogsRef.add(foodItem)
                .addOnSuccessListener {
                    Log.d("FoodItem", "shopping 저장 성공: ${it.id}")
                }
                .addOnFailureListener { e ->
                    Log.e("FoodItem", "shopping 저장 실패", e)
                }
        }
    }

    fun saveRecipeItemToFireStore(foodItem: FoodItem, userId: String) {
        viewModelScope.launch {
            val db = Firebase.firestore
            val userFoodLogsRef = db.collection("users").document(userId).collection("recipe")

            userFoodLogsRef.add(foodItem)
                .addOnSuccessListener {
                    Log.d("FoodItem", "recipe 저장 성공: ${it.id}")
                }
                .addOnFailureListener { e ->
                    Log.e("FoodItem", "recipe 저장 실패", e)
                }
        }
    }



    fun deleteFoodItemShoppingToFireStore(foodItem: FoodItem, userId: String) {
        viewModelScope.launch {
            val db = Firebase.firestore
            val userShoppingRef = db
                .collection("users")
                .document(userId)
                .collection("shopping")


            val documentId = foodItem.foodId

            if (documentId.isNullOrEmpty()) {
                Log.e("FoodItem", "삭제 실패: 문서 ID가 없음")
                return@launch
            }

            userShoppingRef.document(documentId).delete()
                .addOnSuccessListener {
                    Log.d("FoodItem", "삭제 성공: $documentId")
                }
                .addOnFailureListener { e ->
                    Log.e("FoodItem", "삭제 실패", e)
                }
        }
    }

    fun deleteFoodItemRecipeToFireStore(foodItem: FoodItem, userId: String) {
        viewModelScope.launch {
            val db = Firebase.firestore
            val userRecipeRef = db
                .collection("users")
                .document(userId)
                .collection("recipe")


            val documentId = foodItem.foodId

            if (documentId.isNullOrEmpty()) {
                Log.e("recipe", "삭제 실패: 문서 ID가 없음")
                return@launch
            }

            userRecipeRef.document(documentId).delete()
                .addOnSuccessListener {
                    Log.d("recipe", "삭제 성공: $documentId")
                }
                .addOnFailureListener { e ->
                    Log.e("recipe", "삭제 실패", e)
                }
        }
    }

    fun deleteFoodLogToFireStore(foodItem: FoodLog, userId: String) {
        viewModelScope.launch {
            val db = Firebase.firestore
            val userFoodLogRef = db
                .collection("users")
                .document(userId)
                .collection("foodLogs")


            val documentId = foodItem.foodId

            if (documentId.isEmpty()) {
                Log.e("recipe", "삭제 실패: 문서 ID가 없음")
                return@launch
            }

            userFoodLogRef.document(documentId).delete()
                .addOnSuccessListener {
                    Log.d("FoodLog", "삭제 성공: $documentId")
                }
                .addOnFailureListener { e ->
                    Log.e("FoodLog", "삭제 실패", e)
                }
        }
    }
}