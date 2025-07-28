package com.petchat.android.ui.pet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PetViewModel : ViewModel() {
    
    private val _uiState = MutableStateFlow(PetUiState())
    val uiState: StateFlow<PetUiState> = _uiState.asStateFlow()
    
    init {
        loadPetInfo()
    }
    
    private fun loadPetInfo() {
        // 模拟加载宠物信息
        _uiState.value = PetUiState(
            petInfo = PetInfo(
                name = "小白",
                breed = "金毛犬",
                age = 3,
                height = "60cm",
                weight = "28kg",
                birthday = "2021-03-15",
                avatar = "🐕",
                healthStatus = HealthStatus.EXCELLENT
            ),
            todayReminders = listOf(
                Reminder("喂食", "🍖", ReminderStatus.COMPLETED, null),
                Reminder("遛弯", "🚶", ReminderStatus.PENDING, "14:00"),
                Reminder("洗澡", "🛁", ReminderStatus.SCHEDULED, "明天")
            ),
            isMoreFunctionsExpanded = false
        )
    }
    
    fun toggleMoreFunctions() {
        _uiState.value = _uiState.value.copy(
            isMoreFunctionsExpanded = !_uiState.value.isMoreFunctionsExpanded
        )
    }
    
    fun updateReminderStatus(reminderName: String, status: ReminderStatus) {
        viewModelScope.launch {
            val updatedReminders = _uiState.value.todayReminders.map { reminder ->
                if (reminder.name == reminderName) {
                    reminder.copy(status = status)
                } else {
                    reminder
                }
            }
            _uiState.value = _uiState.value.copy(todayReminders = updatedReminders)
        }
    }
}

data class PetUiState(
    val petInfo: PetInfo? = null,
    val todayReminders: List<Reminder> = emptyList(),
    val isMoreFunctionsExpanded: Boolean = false,
    val isLoading: Boolean = false
)

data class PetInfo(
    val name: String,
    val breed: String,
    val age: Int,
    val height: String,
    val weight: String,
    val birthday: String,
    val avatar: String,
    val healthStatus: HealthStatus
)

data class Reminder(
    val name: String,
    val icon: String,
    val status: ReminderStatus,
    val time: String?
)

enum class ReminderStatus {
    COMPLETED,
    PENDING,
    SCHEDULED
}

enum class HealthStatus {
    EXCELLENT,
    GOOD,
    FAIR,
    NEEDS_ATTENTION
}