package com.purang.hellofood.utils

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

sealed class FontSize(val size: Float) {
    data object Small : FontSize(16f)
    data object Medium : FontSize(20f)
    data object Large : FontSize(24f)
}

private val Context.dataStore by preferencesDataStore(name = "login_prefs")
private val Context.dataStore2 by preferencesDataStore(name = "search_prefs")
private val Context.dataStore3 by preferencesDataStore(name = "user_prefs")

object PreferenceDataStore {
    private val FONT_SIZE_KEY = stringPreferencesKey("font_size")
    private val ALARM_SETTING_KEY = booleanPreferencesKey("alarm_setting") // 알람 설정 키 추가
    private val LOGIN_STATE = booleanPreferencesKey("login_state")

    private val WEIGHT_KEY = intPreferencesKey("weight")
    private val HEIGHT_KEY = intPreferencesKey("height")
    private val BMI_KEY = intPreferencesKey("bmi")

    private val EXERCISE_TIME_KEY = intPreferencesKey("exercise")

    private val WATER_KEY = doublePreferencesKey("water")

    private val SLEEP_KEY = intPreferencesKey("sleep")
    private val REST_KEY = intPreferencesKey("rest")

    private val GOAL_WEIGHT_KEY = intPreferencesKey("goal_weight")
    private val GOAL_SLEEP_KEY = intPreferencesKey("goal_sleep")
    private val GOAL_EXERCISE_KEY = intPreferencesKey("goal_exercise")


    fun getLoginState(context: Context): Flow<Boolean> {
        return context.dataStore.data.map { preferences ->
            preferences[LOGIN_STATE] ?: false
        }
    }

    suspend fun setLoginState(context: Context, isLoggedIn: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[LOGIN_STATE] = isLoggedIn
        }
    }

    suspend fun setExerciseTime(context: Context, exerciseTime: Int) {
        context.dataStore3.edit { preferences ->
            preferences[EXERCISE_TIME_KEY] = exerciseTime
        }
    }

    fun getExerciseTime(context: Context): Flow<Int> {
        return context.dataStore3.data.map { preferences ->
            preferences[EXERCISE_TIME_KEY] ?: 0
        }
    }

    suspend fun setWater(context: Context, water: Double) {
        context.dataStore3.edit { preferences ->
            preferences[WATER_KEY] = water
        }
    }

    fun getWater(context: Context): Flow<Double> {
        return context.dataStore3.data.map { preferences ->
            preferences[WATER_KEY] ?: 0.0
        }
    }

    suspend fun setSleep(context: Context, sleep: Int) {
        context.dataStore3.edit { preferences ->
            preferences[SLEEP_KEY] = sleep
        }
    }

    fun getSleep(context: Context): Flow<Int> {
        return context.dataStore3.data.map { preferences ->
            preferences[SLEEP_KEY] ?: 0
        }
    }

    suspend fun setRest(context: Context, rest: Int) {
        context.dataStore3.edit { preferences ->
            preferences[REST_KEY] = rest
        }
    }

    fun getRest(context: Context): Flow<Int> {
        return context.dataStore3.data.map { preferences ->
            preferences[REST_KEY] ?: 0
        }
    }

    suspend fun setGoalWeight(context: Context, goalWeight: Int) {
        context.dataStore3.edit { preferences ->
            preferences[GOAL_WEIGHT_KEY] = goalWeight
        }
    }

    fun getGoalWeight(context: Context): Flow<Int> {
        return context.dataStore3.data.map { preferences ->
            preferences[GOAL_WEIGHT_KEY] ?: 0
        }
    }


    suspend fun setGoalSleep(context: Context, goalSleep: Int) {
        context.dataStore3.edit { preferences ->
            preferences[GOAL_SLEEP_KEY] = goalSleep
        }
    }

    fun getGoalSleep(context: Context): Flow<Int> {
        return context.dataStore3.data.map { preferences ->
            preferences[GOAL_SLEEP_KEY] ?: 0
        }
    }

    suspend fun setGoalExercise(context: Context, goalExercise: Int) {
        context.dataStore3.edit { preferences ->
            preferences[GOAL_EXERCISE_KEY] = goalExercise
        }
    }

    fun getGoalExercise(context: Context): Flow<Int> {
        return context.dataStore3.data.map { preferences ->
            preferences[GOAL_EXERCISE_KEY] ?: 0
        }
    }


    //user pref
    private fun calculateBMI(weight: Int, height: Int): Int {
        return if (height > 0) {
            val heightInMeters = height / 100.0
            (weight / (heightInMeters * heightInMeters)).toInt()
        } else {
            0
        }
    }
    data class UserData(val weight: Int, val height: Int, val bmi: Int)

    suspend fun setUserData(context: Context, weight: String, height: String) {
        val bmi = calculateBMI(weight.toInt(), height.toInt()) // BMI 계산
        context.dataStore3.edit { preferences ->
            preferences[WEIGHT_KEY] = weight.toInt()
            preferences[HEIGHT_KEY] = height.toInt()
            preferences[BMI_KEY] = bmi
        }
    }

    fun getUserData(context: Context): Flow<UserData> {
        return context.dataStore3.data.map { preferences ->
            val weight = preferences[WEIGHT_KEY] ?: 0
            val height = preferences[HEIGHT_KEY] ?: 0
            val bmi = preferences[BMI_KEY] ?: 0
            UserData(weight, height, bmi)
        }
    }


    // 글자 크기 Flow
    fun getFontSizeFlow(context: Context): Flow<FontSize> = context.dataStore.data
        .map { preferences ->
            when (preferences[FONT_SIZE_KEY]) {
                "SMALL" -> FontSize.Small
                "MEDIUM" -> FontSize.Medium
                "LARGE" -> FontSize.Large
                else -> FontSize.Medium // 기본 값
            }
        }

    // 글자 크기 저장
    suspend fun setFontSize(context: Context, fontSize: FontSize) {
        context.dataStore.edit { preferences ->
            preferences[FONT_SIZE_KEY] = when (fontSize) {
                FontSize.Small -> "SMALL"
                FontSize.Medium -> "MEDIUM"
                FontSize.Large -> "LARGE"
            }
        }
    }


    private val searchKey = stringPreferencesKey("recent_search")

    suspend fun saveSearchQuery(context: Context, query: String) {
        context.dataStore2.edit { prefs ->
            val existingList = prefs[searchKey]?.split(",") ?: emptyList()
            val updatedList = (listOf(query) + existingList).distinct().take(10) // 최근 검색어 10개 유지
            prefs[searchKey] = updatedList.joinToString(",")
        }
    }

    suspend fun deleteSearchQuery(context: Context, query: String) {
        context.dataStore2.edit { prefs ->
            val existingList = prefs[searchKey]?.split(",") ?: emptyList()
            val updatedList = existingList.filter { it != query }
            prefs[searchKey] = updatedList.joinToString(",")
        }
    }

    suspend fun deleteAllSearchQuery(context: Context) {
        context.dataStore2.edit { prefs ->
            prefs[searchKey] = ""
        }
    }


    fun getSearchQueries(context: Context): Flow<List<String>> {
        return context.dataStore2.data.map { prefs ->
            prefs[searchKey]?.split(",") ?: emptyList()
        }
    }


    //알람 설정 Flow
    fun getAlarmFlow(context: Context): Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[ALARM_SETTING_KEY] ?: true // 기본 값: true (알람 ON)
        }

    //알람 설정 저장
    /*suspend fun setAlarmEnabled(context: Context, isEnabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[ALARM_SETTING_KEY] = isEnabled
            if (isEnabled) {

            } else {
                AlarmHelper(context).cancelAllAlarms()
            }
        }
    }*/
}