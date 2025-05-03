package com.purang.hellofood.views.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.purang.hellofood.R
import com.purang.hellofood.models.FoodLog
import com.purang.hellofood.models.HealthTipData
import com.purang.hellofood.models.HealthTips
import com.purang.hellofood.utils.FirebaseUserManager
import com.purang.hellofood.utils.FontSize
import com.purang.hellofood.utils.PreferenceDataStore
import com.purang.hellofood.utils.FontUtils
import com.purang.hellofood.viewmodels.ScheduleViewModel
import com.purang.hellofood.views.calendar.ScheduleItem
import com.purang.hellofood.views.schedule.DeleteItemDialog
import java.time.LocalDate
import java.time.YearMonth

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(
    navController : NavController,
    scheduleViewModel: ScheduleViewModel = hiltViewModel()
) {
    val userId = FirebaseUserManager.userId
    val context = LocalContext.current

    var isDeleteDialogOpen by remember {
        mutableStateOf(false)
    }

    var deleteItem by remember {
        mutableStateOf<com.purang.hellofood.models.ScheduleData?>(null)
    }

    /*val selectCalendarData by homeViewModel.clickCalendarData.observeAsState(emptyList())
    val monthData by homeViewModel.sortedMonthEvents.observeAsState(emptyList())*/
    LaunchedEffect(userId) {
        if (userId != null) {
            scheduleViewModel.fetchSchedules(userId)
        }
    }

    val userMonthData by scheduleViewModel.schedules.observeAsState() //schedules.collectAsState(initial = emptyList())
    val fontSize by PreferenceDataStore.getFontSizeFlow(context).collectAsState(initial = FontSize.Medium)

    var healthTipData by remember {
        mutableStateOf<HealthTipData?>(null)
    }
    LaunchedEffect(Unit) {
        healthTipData =  HealthTips.getAll().shuffled()[0]
        scheduleViewModel.fetchEventsByMonth(YearMonth.now())
    }

    val today = LocalDate.now() // 오늘 날짜
    val todayScheduleList by remember(userMonthData) {
        derivedStateOf {
            userMonthData?.filter { it.date == today.toString() } // 날짜가 오늘인 데이터만 필터링
        }
    }



    //- 최근 측정 건강 통계 -> 최근 분석한 음식
    //- 건강 팁
    //- 오늘 할일 스케줄

    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
    ) {
        LazyColumn (
            modifier = Modifier
                .wrapContentSize()
                .padding(10.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {


            item {
                HealthTip(healthTipData,fontSize)
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Today's Schedule",
                    fontWeight = FontWeight.Bold,
                    style = FontUtils.getTextStyle(fontSize.size + 4f)
                )
            }

            if (todayScheduleList.isNullOrEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Empty Schedule \n Register your schedule!",
                        fontWeight = FontWeight.Bold,
                        style = FontUtils.getTextStyle(fontSize.size + 4f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            } else {
                itemsIndexed(
                    items = todayScheduleList ?: emptyList()
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

    if (isDeleteDialogOpen) {
        DeleteItemDialog(
            item = deleteItem,
            onCancelClick = {
                isDeleteDialogOpen = !isDeleteDialogOpen
            },
            onConfirmClick = { item ->
                if (item != null) {
                    isDeleteDialogOpen = !isDeleteDialogOpen
                    scheduleViewModel.deleteSchedule(item.scheduleId, item.userId)
                }
            }
        )
    }
}

@Composable
fun HealthTip(healthTipData: HealthTipData?, fontSize: FontSize) {
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(10.dp)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.shiny_light_bulb_icon),
            contentDescription = "bulb"
        )

        Column (
            modifier = Modifier
                .wrapContentSize()
                .padding(10.dp)
        ) {
            Text(
                text = healthTipData?.tipTitle.toString(),
                fontWeight = FontWeight.Bold,
                style = FontUtils.getTextStyle(fontSize.size + 4f)
            )
            Text(
                text = healthTipData?.tipDescription.toString(),
                fontWeight = FontWeight.Medium,
                style = FontUtils.getTextStyle(fontSize.size)
            )
        }
    }
}

@Composable
fun CurrentDataUI(
   item : FoodLog
) {

}