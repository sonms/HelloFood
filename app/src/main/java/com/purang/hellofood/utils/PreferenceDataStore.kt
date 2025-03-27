package com.purang.hellofood.utils

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

sealed class FontSize(val size: Float) {
    data object Small : FontSize(16f)
    data object Medium : FontSize(20f)
    data object Large : FontSize(24f)
}

private val Context.dataStore by preferencesDataStore(name = "disease_settings")
private val Context.dataStore2 by preferencesDataStore(name = "search_prefs")
object PreferenceDataStore {
    private val FONT_SIZE_KEY = stringPreferencesKey("font_size")
    private val ALARM_SETTING_KEY = booleanPreferencesKey("alarm_setting") // 알람 설정 키 추가

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