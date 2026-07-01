package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.*
import com.example.data.repository.RecyclingRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class RecyclingViewModel(private val repository: RecyclingRepository) : ViewModel() {

    init {
        viewModelScope.launch {
            repository.initializeDatabaseIfEmpty()
        }
    }

    // State flows from repository
    val userProfile: StateFlow<UserProfile?> = repository.userProfile
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val recyclingLogs: StateFlow<List<RecyclingLog>> = repository.allLogs
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val weeklyChallenges: StateFlow<List<WeeklyChallenge>> = repository.allChallenges
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val earnedBadges: StateFlow<List<EarnedBadge>> = repository.allBadges
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Derived stats for educational child display
    val totalWeightKg: StateFlow<Double> = recyclingLogs
        .map { logs -> logs.sumOf { it.weightKg } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalEnergySavedHours: StateFlow<Double> = recyclingLogs
        .map { logs -> logs.sumOf { it.energySavedHours } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalWaterSavedCups: StateFlow<Double> = recyclingLogs
        .map { logs -> logs.sumOf { it.waterSavedCups } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // User interaction events
    fun logRecycling(itemType: String, quantity: Int, materialName: String, onComplete: (Double) -> Unit) {
        viewModelScope.launch {
            val weightSaved = repository.logRecycling(itemType, quantity, materialName)
            onComplete(weightSaved)
        }
    }

    fun claimReward(challengeId: String, onComplete: (Int) -> Unit) {
        viewModelScope.launch {
            val XpGained = repository.claimChallengeReward(challengeId)
            onComplete(XpGained)
        }
    }

    fun updateProfile(name: String, avatar: String) {
        viewModelScope.launch {
            repository.updateProfileNameAndAvatar(name, avatar)
        }
    }

    fun resetChallenges() {
        viewModelScope.launch {
            repository.resetWeeklyChallenges()
        }
    }

    fun deleteLog(log: RecyclingLog) {
        viewModelScope.launch {
            repository.deleteLogAndDeductXp(log)
        }
    }

    fun clearAll() {
        viewModelScope.launch {
            repository.clearAllData()
        }
    }
}

class RecyclingViewModelFactory(private val repository: RecyclingRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RecyclingViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RecyclingViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
