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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.purang.hellofood.ui.theme.blueColor5
import com.purang.hellofood.ui.theme.blueExLight
import com.purang.hellofood.ui.theme.green
import com.purang.hellofood.ui.theme.mintColor4
import com.purang.hellofood.ui.theme.orange
import com.purang.hellofood.ui.theme.redInLight
import com.purang.hellofood.ui.theme.yellow
import com.purang.hellofood.utils.FirebaseUserManager
import com.purang.hellofood.viewmodels.HomeViewModel
import com.purang.hellofood.viewmodels.ScheduleViewModel
import com.purang.hellofood.views.loading.LoadingState
import com.uuranus.schedule.calendar.compose.ScheduleCalendar
import com.uuranus.schedule.calendar.compose.ScheduleCalendarDefaults
import com.uuranus.schedule.calendar.compose.ScheduleData
import com.uuranus.schedule.calendar.compose.ScheduleDate
import com.uuranus.schedule.calendar.compose.ScheduleInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.YearMonth
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarScreen(
    navController: NavController,
    scheduleViewModel: ScheduleViewModel = hiltViewModel()
) {
    val userId = FirebaseUserManager.userId
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    //스크롤 시 접힐때
    val scrollOffset = remember { mutableStateOf(0f) }  // 스크롤 이동 거리
    val isCollapsed = scrollOffset.value > 150f
    // LazyColumn 스크롤 시 offset 값 변화
    LaunchedEffect(remember { derivedStateOf { listState.firstVisibleItemIndex } }) {
        scrollOffset.value = listState.firstVisibleItemScrollOffset.toFloat()
    }

    /*val selectCalendarData by homeViewModel.clickCalendarData.observeAsState(emptyList())
    val monthData by homeViewModel.sortedMonthEvents.observeAsState(emptyList())*/
    LaunchedEffect(userId) {
        if (userId != null) {
            scheduleViewModel.fetchSchedules(userId)
        }
    }

    //유저 데이터 가져오기
    val userMonthData by scheduleViewModel.schedules.observeAsState() //schedules.collectAsState(initial = emptyList())

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


    var selectDate by remember {
        mutableStateOf("")
    }

    // clickDateCalendarList 해당 selectDate에 맞는 일정으로 업데이트
    val clickDateCalendarList by remember(selectDate) {
        derivedStateOf {
            userMonthData?.filter { it.date == selectDate }
        }
    }


    var isDeleteDialogOpen by remember {
        mutableStateOf(false)
    }

    var deleteItem by remember {
        mutableStateOf<com.purang.hellofood.models.ScheduleData?>(null)
    }

    var pageChangeDate by remember {
        mutableStateOf(YearMonth.now().toString())
    }

    LaunchedEffect(pageChangeDate) {
        val yearMonth = YearMonth.parse(pageChangeDate)
        scheduleViewModel.fetchEventsByMonth(yearMonth)
        LoadingState.hide()
    }

    Column(
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
                            modifier = Modifier.fillMaxWidth(), // 🔥 fillMaxSize() → fillMaxWidth() 변경
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
                text = "정말 삭제하시겠습니까?",
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
                        text = "취소",
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
                        text = "확인",
                        color = Color.White
                    )
                }
            }
        }
    }
}