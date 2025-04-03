package com.purang.hellofood.views.calendar

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.uuranus.schedule.calendar.compose.ScheduleCalendar
import com.uuranus.schedule.calendar.compose.ScheduleDate
import com.uuranus.schedule.calendar.compose.ScheduleInfo
import com.uuranus.schedule.calendar.compose.ScheduleCalendarDefaults
import java.time.YearMonth
import com.purang.hellofood.ui.theme.*
import com.purang.hellofood.utils.FirebaseUserManager
import com.purang.hellofood.viewmodels.ScheduleViewModel
import com.purang.hellofood.views.loading.LoadingState
import com.purang.hellofood.views.schedule.DeleteItemDialog
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarScreen(
    navController: NavController,
    scheduleViewModel: ScheduleViewModel = hiltViewModel()
) {
    val listState = rememberLazyListState()
    //스크롤 시 접힐때
    val scrollOffset = remember { mutableFloatStateOf(0f) }  // 스크롤 이동 거리
    val isCollapsed = scrollOffset.floatValue > 30f
    // LazyColumn 스크롤 시 offset 값 변화
    LaunchedEffect(remember { derivedStateOf { listState.firstVisibleItemIndex } }) {
        scrollOffset.floatValue = listState.firstVisibleItemScrollOffset.toFloat()
    }

    val userId = FirebaseUserManager.userId

    LaunchedEffect(userId) {
        if (userId != null) {
            scheduleViewModel.fetchSchedules(userId)
        }
    }

    val userMonthData by scheduleViewModel.schedules.observeAsState() //schedules.collectAsState(initial = emptyList())
    var selectDate by remember {
        mutableStateOf("")
    }

    val clickDateCalendarList by remember(selectDate) {
        derivedStateOf {
            userMonthData?.filter { it.date == selectDate }
        }
    }

    val calendarList by remember(userMonthData) {
        derivedStateOf {
            userMonthData
                ?.groupBy { it.date } // date 기준으로 그룹화
                ?.mapKeys { (date, _) ->
                    val parts = date.split("-")
                    ScheduleDate.create(parts[0].toInt(), parts[1].toInt(), parts[2].toInt())
                }
                ?.mapValues { (_, items) ->
                    val colors = listOf(redInLight, orange, yellow, green, blueExLight)

                    // ScheduleData 리스트 생성
                    val scheduleDataList = items.mapIndexed { index, item ->
                        val color = colors[index % colors.size] // 색상 순환 적용

                        com.uuranus.schedule.calendar.compose.ScheduleData(
                            title = item.eventType,
                            color = color,
                            detail = item.description
                        )
                    }

                    // ScheduleInfo<String?> 대신 ScheduleInfo<List<ScheduleData>>로 변경
                    ScheduleInfo(
                        isCheckNeeded = false,
                        schedules = scheduleDataList
                    )
                } ?: emptyMap() // null 방지
        }
    }

    var pageChangeDate by remember {
        mutableStateOf(YearMonth.now().toString())
    }

    LaunchedEffect(pageChangeDate) {
        val yearMonth = YearMonth.parse(pageChangeDate)
        scheduleViewModel.fetchEventsByMonth(yearMonth)
        LoadingState.hide()
    }

    var isDeleteDialogOpen by remember {
        mutableStateOf(false)
    }

    var deleteItem by remember {
        mutableStateOf<com.purang.hellofood.models.ScheduleData?>(null)
    }

    Box(
        modifier = Modifier.fillMaxSize().padding(10.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier.fillMaxSize().wrapContentHeight()
            ) {
                item {
                    if (!isCollapsed) {
                        Box(
                            modifier = Modifier.fillMaxWidth().animateContentSize().fillParentMaxHeight()
                        ) {
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

                                    navController.navigate("list?date=${selectDate}")
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
                    itemsIndexed(clickDateCalendarList!!) { _, item ->
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
        }

        FloatingActionButton(
            onClick = { navController.navigate("edit?type=Add") },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = mintColor4
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Edit")
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ScheduleItem(
    item : com.purang.hellofood.models.ScheduleData,
    onItemClick : (com.purang.hellofood.models.ScheduleData) -> Unit,
    onItemLongClick : (com.purang.hellofood.models.ScheduleData) -> Unit
) {
    val color = when (item.eventType) {
        "운동" -> blueColor5
        "식사" -> mintColor4
        "병원" -> redInLight
        "복약" -> yellow
        else -> Color.Gray
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .combinedClickable(
                onClick = {
                    onItemClick(item)
                },
                onLongClick = {
                    onItemLongClick(item)
                },
            ),
        shape = RoundedCornerShape(8.dp), // 8.dp 둥근 모서리
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp,
            pressedElevation = 8.dp
        ), // 약간의 그림자
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),

        ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp), // 카드 내의 여백
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 좌측 색상 표시
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(color = color)
            )

            Spacer(modifier = Modifier.width(16.dp))

            // 오른쪽에 텍스트 내용
            Column(
                modifier = Modifier.weight(1f) // 나머지 공간을 차지
            ) {
                // 제목
                Text(
                    text = item.eventType,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.Bold
                )

                // 내용
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = item.description.toString(),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.Bold
                )

                // 날짜
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = item.date,
                    color = Color.Gray
                )
            }
        }
    }
}