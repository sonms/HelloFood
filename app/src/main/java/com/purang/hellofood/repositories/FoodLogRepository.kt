package com.purang.hellofood.repositories

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.purang.hellofood.models.FoodItem
import com.purang.hellofood.models.FoodLog
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FoodLogRepository @Inject constructor(private val firestore: FirebaseFirestore) {

    suspend fun getFoodLogs(userId: String): List<FoodLog> {
        return try {
            val snapshot = firestore.collection("users")
                .document(userId)
                .collection("foodLogs")
                .get()
                .await()

            val foodList = mutableListOf<FoodLog>()

            for (document in snapshot.documents) {
                val item = document.toObject(FoodLog::class.java)
                item?.let {
                    it.foodId = document.id
                    foodList.add(it)
                }
            }

            foodList
        } catch (e: Exception) {
            Log.e("Firestore", "FoodLog 불러오기 실패", e)
            emptyList()
        }
    }

    suspend fun getShoppingList(userId: String): List<FoodItem> {
        return try {
            val snapshot = firestore.collection("users")
                .document(userId)
                .collection("shopping")
                .get()
                .await()

            val shoppingList = mutableListOf<FoodItem>()

            for (document in snapshot.documents) {
                val item = document.toObject(FoodItem::class.java)
                item?.let {
                    it.foodId = document.id // ← 문서 ID를 직접 넣어주기
                    shoppingList.add(it)
                }
            }

            shoppingList
        } catch (e: Exception) {
            Log.e("Firestore", "shopping 불러오기 실패", e)
            emptyList()
        }
    }

    suspend fun getRecipeList(userId: String): List<FoodItem> {
        return try {
            val snapshot = firestore.collection("users")
                .document(userId)
                .collection("recipe")
                .get()
                .await()

            val recipeList = mutableListOf<FoodItem>()

            for (document in snapshot.documents) {
                val item = document.toObject(FoodItem::class.java)
                item?.let {
                    it.foodId = document.id // ← 문서 ID를 직접 넣어주기
                    recipeList.add(it)
                }
            }

            recipeList
        } catch (e: Exception) {
            Log.e("Firestore", "recipe 불러오기 실패", e)
            emptyList()
        }
    }
}