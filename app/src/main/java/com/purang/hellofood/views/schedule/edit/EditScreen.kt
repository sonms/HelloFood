package com.purang.hellofood.views.schedule.edit

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.sharp.Info
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.purang.hellofood.BottomNavItem
import com.purang.hellofood.MainActivity
import com.purang.hellofood.R
import com.purang.hellofood.models.ScheduleData
import com.purang.hellofood.ui.theme.blueExercise2
import com.purang.hellofood.ui.theme.greenFoodColor2
import com.purang.hellofood.ui.theme.mintColor4
import com.purang.hellofood.ui.theme.purpleRestColor2
import com.purang.hellofood.ui.theme.redPersonalColor2
import com.purang.hellofood.utils.FirebaseUserManager
import com.purang.hellofood.viewmodels.ScheduleViewModel
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.YearMonth
import java.util.Calendar
import java.util.Date
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EditScreen (
    navController: NavController,
    type : String,
    scheduleId : String,
    scheduleViewModel: ScheduleViewModel = hiltViewModel()
) {
    val userId = FirebaseUserManager.userId

    LaunchedEffect(userId) {
        if (userId != null && type == "Add") {
            scheduleViewModel.fetchSchedules(userId)
        } else if (userId != null && scheduleId != "-1") {
            scheduleViewModel.fetchSchedule(userId, scheduleId)
        }
    }
    val scheduleData by scheduleViewModel.schedule.collectAsState(initial = null)

    //val userMonthData by scheduleViewModel.schedules.observeAsState() //schedules.collectAsState(initial = emptyList())

    //UI 관리용
    var isOpenCalendar by remember {
        mutableStateOf(false)
    }

    var isOpenTime by remember {
        mutableStateOf(false)
    }

    //data 처리용
    var editSelectedDate by remember {
        mutableStateOf(LocalDate.now().toString())
    }
    var editSelectedTime by remember {
        mutableStateOf("")
    }
    var editCategory by remember {
        mutableStateOf("")
    }
    var editTitle by remember {
        mutableStateOf("")
    }
    var editDescription by remember {
        mutableStateOf("")
    }
    val chipList = listOf("Exercise", "Personal", "Food", "Rest")

    //오류제어
    var isTitleError by remember {
        mutableStateOf(true)
    }
    //타입을 선택안함 - 카테고리
    var isTypeError by remember {
        mutableStateOf(true)
    }

    if (scheduleData != null) {
        LaunchedEffect(scheduleData) {
            editTitle = scheduleData?.title.toString()
            editCategory = scheduleData?.eventType.toString()
            editDescription = scheduleData?.description.toString()
            editSelectedTime = scheduleData?.time.toString()
            editSelectedDate = scheduleData?.date.toString()
        }
    }




    Column(
        modifier = Modifier.fillMaxSize()
    ) {
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

            Spacer(modifier = Modifier.weight(1f))

            IconButton(
                modifier = Modifier.size(48.dp),
                enabled = !isTitleError && !isTypeError,
                colors = IconButtonDefaults.iconButtonColors(contentColor = MaterialTheme.colorScheme.primary),
                interactionSource = remember { MutableInteractionSource() },
                onClick = {
                    if (!isTitleError && !isTypeError && type == "Add") { //추가하기
                        val schedule = ScheduleData(
                            userId = FirebaseUserManager.userId.toString(),
                            date = editSelectedDate,
                            time = editSelectedTime,
                            year = editSelectedDate.substring(0..3).toInt(), //yyyy-MM-dd
                            month = editSelectedDate.substring(5..6).toInt(),
                            eventType = editCategory,
                            title = editTitle,
                            description = editDescription,
                        )

                        scheduleViewModel.addSchedule(schedule = schedule)

                        navController.navigate(BottomNavItem.Calendar.screenRoute) {
                            popUpTo(BottomNavItem.Calendar.screenRoute) { inclusive = true }
                            launchSingleTop = true
                        }
                    } else if (!isTitleError && !isTypeError && type == "edit") { //수정하기
                        val schedule = ScheduleData(
                            userId = FirebaseUserManager.userId.toString(),
                            date = editSelectedDate,
                            time = editSelectedTime,
                            year = editSelectedDate.substring(0..3).toInt(), //yyyy-MM-dd
                            month = editSelectedDate.substring(5..6).toInt(),
                            eventType = editCategory,
                            title = editTitle,
                            description = editDescription,
                        )

                        scheduleViewModel.editSchedule(schedule = schedule)

                        navController.navigate(BottomNavItem.Calendar.screenRoute) {
                            popUpTo(BottomNavItem.Calendar.screenRoute) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                }
            ) {
                Icon(Icons.Default.Check, contentDescription = "confirm")
            }
        }


        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()) // 스크롤 가능하게 설정
                .padding(16.dp)
        ) {
            Row {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // 날짜 선택 Card
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .padding(8.dp),
                        elevation = CardDefaults.cardElevation(4.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = {
                                    isOpenCalendar = !isOpenCalendar
                                }
                            ) {
                                Icon(painterResource(R.drawable.baseline_calendar_month_24), contentDescription = "Select Date")
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = editSelectedDate.substring(5..9), style = MaterialTheme.typography.bodyLarge)
                        }
                    }

                    // 시간 선택 Card
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .padding(8.dp),
                        elevation = CardDefaults.cardElevation(4.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = {
                                    isOpenTime = !isOpenTime
                                }
                            ) {
                                Icon(painterResource(R.drawable.baseline_access_time_24), contentDescription = "Select Time")
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = editSelectedTime, style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            CategoryChipGroup(
                //initCategory = editCategory,
                chipDataList = chipList,
                isSelected = isTypeError,
                onClick = { category ->
                    if (isTypeError) {
                        isTypeError = false
                    }
                    editCategory = category
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Column (
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Title",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.Start) // 왼쪽 정렬
                        .padding(bottom = 4.dp) // 필드와 간격 추가
                )

                // 제목 입력
                OutlinedTextField(
                    value = editTitle,
                    onValueChange = {
                        editTitle = it
                        if (editTitle.isEmpty()) {
                            isTitleError = true
                        } else {
                            isTitleError = false
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    supportingText = {
                        if (isTitleError) {
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = "Please write a title",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    trailingIcon = {
                        if (isTitleError)
                            Icon(Icons.Default.Warning ,"error", tint = MaterialTheme.colorScheme.error)
                    },
                    placeholder = {
                        Text(text = "Enter schedule title")
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Description(Optional)",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.Start) // 왼쪽 정렬
                        .padding(bottom = 4.dp) // 필드와 간격 추가
                )

                OutlinedTextField(
                    value = editDescription,
                    onValueChange = { editDescription = it },
                    placeholder = {
                        Text(text = "Enter schedule description(Optional)")
                    },
                    maxLines = 5,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 50.dp, max = 200.dp) // 최대 높이 제한
                        .verticalScroll(rememberScrollState()) // 내용이 넘칠 때 스크롤 가능
                )
            }
        }
    }

    if (isOpenCalendar) {
        EditCalendar(
            selectDate = editSelectedDate,
            isOpenCalendar = isOpenCalendar,
            onClickCancel = {
                isOpenCalendar = false
            },
            onClickConfirm = {
                editSelectedDate = it
                isOpenCalendar = false
            }
        )
    }

    if (isOpenTime) {
        EditTime (
            showTimePicker = isOpenTime,
            onClickConfirm = {
                isOpenTime = false
                editSelectedTime = it
            },
            onClickCancel = {
                isOpenTime = false
            }
        )
    }
}

@Composable
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
fun EditCalendar(
    selectDate: String?,
    isOpenCalendar: Boolean,
    onClickCancel: () -> Unit,
    onClickConfirm: (yyyyMMddHHmm: String) -> Unit
) {
    var isOpen by remember {
        mutableStateOf(isOpenCalendar)
    }

    if (isOpen) {
        var selectedDate by remember { mutableStateOf<String?>(LocalDate.now().toString()) }
        // DatePickerDialog (날짜 선택)
        DatePickerDialog(
            modifier = Modifier.wrapContentSize(),
            onDismissRequest = {
                onClickCancel()
                isOpen = false
            },
            confirmButton = {

            },
            colors = DatePickerDefaults.colors(
                /*containerColor = Color.White,
                weekdayContentColor = Color.Black,
                titleContentColor = Color.Black,
                disabledDayContentColor = blueColor2,
                dayContentColor = Color.Black,
                todayDateBorderColor = blueColor4,
                selectedDayContainerColor = blueColor3*/
                titleContentColor = Color.Red, // 다이얼로그 제목 텍스트 색상
                headlineContentColor = Color.Blue, // 선택된 날짜(헤드라인) 색상
                weekdayContentColor = Color.Green, // 요일(월, 화, 수 등) 텍스트 색상
                subheadContentColor = Color.Magenta, // 서브헤드 텍스트 색상
               //navigationContentColor = Color.Cyan, // 이전/다음 버튼 텍스트 색상
                yearContentColor = Color.Gray, // 연도 선택 리스트의 일반 연도 색상
                selectedYearContentColor = Color.Yellow, // 선택된 연도의 텍스트 색상
                dayContentColor = Color.Black, // 날짜 기본 텍스트 색상
                selectedDayContentColor = Color.White, // 선택된 날짜 텍스트 색상
                todayContentColor = Color.Red // 오늘 날짜 텍스트 색상
            ),
            shape = RoundedCornerShape(6.dp)
        ) {
            val currentDate = LocalDate.now()
            val datePickerState = rememberDatePickerState(
                yearRange = currentDate.year..currentDate.year + 1,
                initialDisplayMode = DisplayMode.Picker,
                initialSelectedDateMillis = System.currentTimeMillis()
            )

            DatePicker(state = datePickerState)

            Column (
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Button(
                        modifier = Modifier.wrapContentSize(),
                        onClick = {
                            onClickCancel()
                            isOpen = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = mintColor4, contentColor = Color.White),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(text = "Cancel", fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.width(5.dp))

                    Button(
                        modifier = Modifier.wrapContentSize(),
                        onClick = {
                            datePickerState.selectedDateMillis?.let { selectedDateMillis ->
                                selectedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                    .format(Date(selectedDateMillis))
                            }
                            isOpen = false
                            onClickConfirm(selectedDate.toString())
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = mintColor4, contentColor = Color.White),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(text = "Confirm", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EditTime(
    showTimePicker: Boolean,
    onClickConfirm: (String) -> Unit,
    onClickCancel: () -> Unit
) {
    var isShowTimePicker by remember { mutableStateOf(showTimePicker) }

    val calendar = Calendar.getInstance()
    var selectedHour by remember { mutableIntStateOf(calendar.get(Calendar.HOUR_OF_DAY)) }
    var selectedMinute by remember { mutableIntStateOf(calendar.get(Calendar.MINUTE)) }
    val timeState = rememberTimePickerState(
        initialHour = selectedHour,
        initialMinute = selectedMinute
    )

    if (isShowTimePicker) {
        Dialog(
            onDismissRequest = {
                onClickCancel()
                isShowTimePicker = false
            },
            properties = DialogProperties(
                usePlatformDefaultWidth = false // 플랫폼 기본 너비를 사용하지 않음
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 20.dp, end = 20.dp)
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        onClickCancel()
                        isShowTimePicker = false
                    },
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .background(color = Color.White, shape = RoundedCornerShape(12.dp))
                        .padding(top = 28.dp, start = 20.dp, end = 20.dp, bottom = 12.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TimePicker(state = timeState)

                    Row(
                        modifier = Modifier
                            .padding(top = 12.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = {
                            onClickCancel()
                            isShowTimePicker = false
                        }) {
                            Text(
                                text = "Cancel",
                                fontWeight = FontWeight.Bold
                            )
                        }
                        TextButton(onClick = {
                            selectedHour = timeState.hour
                            selectedMinute = timeState.minute
                            val formatTime = String.format("%02d:%02d", selectedHour, selectedMinute)
                            onClickConfirm(formatTime)
                            isShowTimePicker = false
                        }) {
                            Text(
                                text = "Confirm",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CategoryChipGroup(
    chipDataList: List<String>,
    onClick: (String) -> Unit,
    isSelected: Boolean
) {
    var selectedCategory by remember { mutableStateOf("") }

    /*Column {
        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(16.dp),
            maxItemsInEachRow = 2,
            horizontalArrangement = Arrangement.spacedBy(8.dp), // 칩 간의 가로 간격 추가
            verticalArrangement = Arrangement.spacedBy(8.dp) // 칩 간의 세로 간격 추가
        ) {
            chipDataList.forEach { category ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .padding(4.dp)
                ) {
                    CategoryChip(
                        category = category,
                        isSelected = category == selectedCategory,
                        onClick = {
                            selectedCategory = category
                            onClick(category)
                        }
                    )
                }
            }
        }*/
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly, // 가로 간격 균등 배치
            verticalArrangement = Arrangement.spacedBy(8.dp) // 세로 간격 조정
        ) {
            chipDataList.forEach { category ->
                CategoryChip(
                    category = category,
                    isSelected = category == selectedCategory,
                    onClick = {
                        selectedCategory = category
                        onClick(category)
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .padding(4.dp)
                )
            }
        }

        if (isSelected) {
            Text(
                text = "Please select a category",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 10.dp, start = 10.dp),
                color = Color.Red
            )
        }
    }
}

@Composable
fun CategoryChip(
    category: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier : Modifier,
) {
    val backgroundColor = if (isSelected) mintColor4 else MaterialTheme.colorScheme.surface
    val contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface

    AssistChip(
        onClick = { onClick() },
        label = {
            Text(
                category,
                fontWeight = FontWeight.Bold
            )
        },
        leadingIcon = {
            when (category) {
                "Exercise" -> {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_fitness_center_24),
                        contentDescription = "Exercise",
                        tint = blueExercise2
                    )
                }

                "Personal" -> {
                    Icon(
                        painter = painterResource(id = R.drawable.personal_icon),
                        contentDescription = "Personal",
                        tint = redPersonalColor2
                    )
                }

                "Food" -> {
                    Icon(
                        painter = painterResource(id = R.drawable.food_icon),
                        contentDescription = "Food",
                        tint = greenFoodColor2
                    )
                }

                "Rest" -> {
                    Icon(
                        painter = painterResource(id = R.drawable.rest_icon),
                        contentDescription = "Rest",
                        tint = purpleRestColor2
                    )
                }
            }
        },
        modifier = modifier,// 칩 자체에 패딩을 추가
        colors = AssistChipDefaults.assistChipColors(
            containerColor = backgroundColor,
            labelColor = contentColor
        ),
    )
}