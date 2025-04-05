package com.purang.hellofood.views.schedule

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.purang.hellofood.ui.theme.blueColor5
import com.purang.hellofood.ui.theme.greenFoodColor2
import com.purang.hellofood.viewmodels.ScheduleViewModel
import com.purang.hellofood.views.calendar.CalendarScreen
import com.purang.hellofood.views.statistics.StatisticsScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ScheduleScreen(
    navController: NavController,
    scheduleViewModel: ScheduleViewModel = hiltViewModel()
) {
    /*val selectCalendarData by homeViewModel.clickCalendarData.observeAsState(emptyList())
    val monthData by homeViewModel.sortedMonthEvents.observeAsState(emptyList())*/


    //유저 데이터 가져오기
    //val userMonthData by scheduleViewModel.schedules.observeAsState() //schedules.collectAsState(initial = emptyList())



    // clickDateCalendarList 해당 selectDate에 맞는 일정으로 업데이트







    /*Column(
        modifier = Modifier.fillMaxSize().padding(10.dp)
    ) {
        LazyColumn(
            modifier = Modifier.wrapContentSize()
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillParentMaxHeight()// 명시적으로 높이를 설정합니다.
                ) {
                    if (!isCollapsed) { // 스크롤을 내려서 접혀있는 상태에서는 표시 안 함
                        ScheduleCalendar(
                            modifier = Modifier.fillMaxWidth(),
                            initialDate = ScheduleDate.create(YearMonth.now().year, YearMonth.now().monthValue, 1),
                            schedules = calendarList,
                            isMondayFirst = false,
                            calendarColors = ScheduleCalendarDefaults.colors(
                                lightColors = ScheduleCalendarDefaults.defaultLightColors().copy(
                                    dayOfWeeks = blueColor5,
                                    saturdayColor = blueExLight,
                                    sundayColor = redInLight,
                                    todayIndicatorColor = blueColor5,
                                    dateScheduleTextColor = Color.White,
                                    dateColor = blueColor5,
                                ),
                                darkColors = ScheduleCalendarDefaults.defaultDarkColors().copy(
                                    dayOfWeeks = blueColor5,
                                    saturdayColor = blueExLight,
                                    sundayColor = redInLight,
                                    todayIndicatorColor = blueColor5,
                                    dateScheduleTextColor = Color.White,
                                    dateColor = blueColor5
                                ),
                            ),
                            onDayClick = { selectedDate ->
                                val formattedMonth = String.format(Locale.getDefault(), "%02d", selectedDate.month)
                                val formattedDate = String.format(Locale.getDefault(), "%02d", selectedDate.date)
                                selectDate = "${selectedDate.year}-$formattedMonth-$formattedDate"
                            },
                            onPageChanged = { pageDate ->
                                val formattedMonth = String.format(Locale.getDefault(), "%02d", pageDate.month)
                                pageChangeDate = "${pageDate.year}-$formattedMonth"
                            }
                        )
                    }
                }
            }

            if (clickDateCalendarList?.isNotEmpty() == true) {
                itemsIndexed(
                    items = clickDateCalendarList!!
                ) { _, item ->
                    ScheduleItem(
                        item = item,
                        onItemClick = {
                            navController.navigate("detail?schedule=${item.scheduleId}")
                        },
                        onItemLongClick = {
                            deleteItem = it
                            isDeleteDialogOpen = !isDeleteDialogOpen
                        }
                    )
                }
            }
        }
    }*/
    val pagerState = rememberPagerState(pageCount = { 2 }) // 2개의 페이지 (캘린더, 통계)
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize()) {
        val tabTitles = listOf("Calendar", "Statistics")

        // 상단 탭 UI
        TabRow(
            selectedTabIndex = pagerState.currentPage
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                    text = {
                        Text(
                            title,
                            color = if (pagerState.currentPage == index) greenFoodColor2 else Color.Gray
                        )
                    }
                )
            }
        }

        // 뷰페이저
        ScheduleViewPager(pagerState, navController)
    }
}




@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ScheduleViewPager(
    pagerState: PagerState,
    navController: NavController,
) {
    HorizontalPager(
        modifier = Modifier.fillMaxSize(),
        state = pagerState
    ) { page ->
        when (page) {
            0 -> CalendarScreen(navController) // 캘린더 화면
            1 -> StatisticsScreen(navController) // 통계 화면
        }
    }
}

@Composable
fun DeleteItemDialog(
    item : com.purang.hellofood.models.ScheduleData?,
    onConfirmClick : (com.purang.hellofood.models.ScheduleData?) -> Unit,
    onCancelClick : () -> Unit
) {
    Dialog(
        onDismissRequest = { onCancelClick() }
    ) {
        Card (
            modifier = Modifier
                .width(320.dp)
                .wrapContentHeight()
                .padding(10.dp),
            shape = RoundedCornerShape(8.dp),
        ) {
            Text(
                modifier = Modifier.padding(top = 20.dp, start = 20.dp, bottom = 10.dp),
                text = "Are you sure you want to delete it?",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )

            Row (
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(20.dp)
            ) {
                Button(
                    modifier = Modifier.padding(end = 5.dp),
                    onClick = { onCancelClick() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = blueColor5, // 버튼 배경색
                        contentColor = Color.White // 텍스트 색상 설정
                    ),
                ) {
                    Text(
                        text = "Cancel",
                        color = Color.White
                    )
                }

                Button(
                    onClick = { onConfirmClick(item) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = blueColor5, // 버튼 배경색
                        contentColor = Color.White // 텍스트 색상 설정
                    ),
                ) {
                    Text(
                        text = "Confirm",
                        color = Color.White
                    )
                }
            }
        }
    }
}