package com.purang.hellofood.views.calendar

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.purang.hellofood.R
import com.purang.hellofood.models.ScheduleData
import com.purang.hellofood.ui.theme.greenFoodColor1
import com.purang.hellofood.utils.FirebaseUserManager
import com.purang.hellofood.viewmodels.ScheduleViewModel
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarDataListScreen(
    navController: NavController,
    selectedDate : String,
    scheduleViewModel: ScheduleViewModel = hiltViewModel()
) {
    val calendarData by scheduleViewModel.schedules.observeAsState(initial = emptyList())
    val userId = FirebaseUserManager.userId.toString()

    LaunchedEffect(userId) {
        // 선택된 날짜에서 년-월 부분만 추출하여 YearMonth 객체 생성
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val localDate = LocalDate.parse(selectedDate, dateFormatter)
        val yearMonth = YearMonth.from(localDate)

        // 또는 더 간단하게 문자열 조작으로 처리
        // val yearMonth = YearMonth.parse(selectedDate.substring(0, 7))

        scheduleViewModel.fetchEventsByMonth(yearMonth)
        scheduleViewModel.fetchSchedules(userId)
        Log.e("calendarData", calendarData.toString())
    }

    var isDeleteDialogOpen by remember {
        mutableStateOf(false)
    }

    var deleteItem by remember {
        mutableStateOf<com.purang.hellofood.models.ScheduleData?>(null)
    }

    val filteredData by remember(selectedDate, calendarData) {
        derivedStateOf {
            calendarData.filter { it.date == selectedDate }
        }
    }

    Column (
        modifier = Modifier.fillMaxSize().padding(16.dp)
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

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            itemsIndexed(
                items = filteredData ?: emptyList()
            ) { _, item ->
                ScheduleItem(
                    item = item,
                    onItemClick = {
                        navController.navigate("edit?type=edit&scheduleId=${item.scheduleId}")
                    },
                    onItemLongClick = {
                        deleteItem = it
                        isDeleteDialogOpen = !isDeleteDialogOpen
                    }
                )
            }
        }
    }
}