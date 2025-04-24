package com.purang.hellofood.viewmodels

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.purang.hellofood.models.ScheduleData
import com.purang.hellofood.repositories.ScheduleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.YearMonth
import javax.inject.Inject

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val scheduleRepository: ScheduleRepository
) : ViewModel() {

    private val _selectedMonth = MutableLiveData<YearMonth>()
    val selectedMonth: LiveData<YearMonth> = _selectedMonth

    private val _userId = MutableLiveData<String>()

    private val _schedule = MutableStateFlow<ScheduleData?>(null)
    val schedule: StateFlow<ScheduleData?> = _schedule.asStateFlow()

    //사용자와 스케줄id로 데이터 1개 가져오기
    fun fetchSchedule(userId: String, scheduleId: String) {
        viewModelScope.launch {
            _schedule.value = scheduleRepository.getUserScheduleById(userId, scheduleId)
        }
    }

    // 사용자 ID와 선택한 월이 변경될 때마다 자동으로 데이터 가져오기
    @RequiresApi(Build.VERSION_CODES.O)
    val schedules: LiveData<List<ScheduleData>> = MediatorLiveData<List<ScheduleData>>().apply {
        fun update() {
            val userId = _userId.value
            val selectedMonth = _selectedMonth.value
            Log.d("ScheduleViewModel", "Trigger update - userId: $userId, selectedMonth: $selectedMonth")

            if (userId != null && selectedMonth != null) {
                viewModelScope.launch {
                    val scheduleList = scheduleRepository.getUserSchedules(
                        userId,
                        selectedMonth.year,
                        selectedMonth.monthValue
                    )
                    Log.d("ScheduleViewModel", "Schedule data fetched: size=${scheduleList.size}")
                    postValue(scheduleList)
                }
            } else {
                Log.w("ScheduleViewModel", "Skipping update: Missing userId or selectedMonth")
            }
        }

        addSource(_userId) { update() }
        addSource(_selectedMonth) { update() }
    }




    /*private val _selectedMonth = MutableLiveData<YearMonth>()

    private val _schedules = MutableStateFlow<List<ScheduleData>>(emptyList())*/
    //val schedules: StateFlow<List<ScheduleData>> = _schedules.asStateFlow()

    //val selectedMonth: LiveData<YearMonth> = _selectedMonth

    // selectedMonth가 변경될 때마다 자동으로 데이터를 가져오도록 설정
    /*@RequiresApi(Build.VERSION_CODES.O)
    val schedules: LiveData<List<ScheduleData>> = _selectedMonth.switchMap { selectedMonth ->
        liveData {
            val userId = "현재 로그인된 사용자 ID" // 실제로는 Firebase Auth에서 가져와야 함
            val scheduleList = scheduleRepository.getUserSchedules(userId, selectedMonth.year, selectedMonth.monthValue)
            emit(scheduleList)
        }
    }*/

    /*
    * fun loadSchedules(userId: String) {
        viewModelScope.launch {
            scheduleRepository.getUserSchedules(userId)
                .catch { e -> e.printStackTrace() } // 예외 처리
                .collectLatest { scheduleList ->
                    _schedules.value = scheduleList
                }
        }
    }
    * */
    /*fun loadSchedules(userId: String) {
        viewModelScope.launch {
            _selectedMonth.switchMap { selectedMonth ->
               scheduleRepository.getUserSchedules(userId, selectedMonth.year, selectedMonth.monthValue)
                    .catch { e -> e.printStackTrace() } // 예외 처리
                    .collectLatest { scheduleList ->
                        _schedules.value = scheduleList
                    }
            }
        }
    }*/

    fun addSchedule(schedule: ScheduleData) {
        viewModelScope.launch {
            val success = scheduleRepository.addSchedule(schedule)
            if (success) {
                fetchSchedules(schedule.userId)
            }
        }
    }

    fun editSchedule(schedule: ScheduleData) {
        viewModelScope.launch {
            val success = scheduleRepository.updateSchedule(schedule)
            if (success) {
                fetchSchedules(schedule.userId)
            }
        }
    }

    fun deleteSchedule(scheduleId: String, userId: String) {
        viewModelScope.launch {
            val success = scheduleRepository.deleteSchedule(userId, scheduleId)
            if (success) {
                fetchSchedules(userId)
            }
        }
    }

    // 일정 다시 불러오기
    fun fetchSchedules(userId: String) {
        _userId.value = userId // LiveData 변경 트리거
    }

    fun fetchEventsByMonth(yearMonth: YearMonth) {
        _selectedMonth.value = yearMonth
    }
}