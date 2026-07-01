package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "recycling_logs")
data class RecyclingLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val itemType: String, // "paper", "plastic", "glass", "metal"
    val quantity: Int,
    val materialName: String, // e.g., "Soda Can", "Cereal Box"
    val timestamp: Long = System.currentTimeMillis()
) {
    val weightKg: Double
        get() = when (itemType.lowercase()) {
            "paper" -> quantity * 0.15
            "plastic" -> quantity * 0.08
            "glass" -> quantity * 0.50
            "metal" -> quantity * 0.12
            else -> quantity * 0.10
        }

    val energySavedHours: Double
        get() = when (itemType.lowercase()) {
            "paper" -> quantity * 0.5   // 0.5 hrs of lightbulb
            "plastic" -> quantity * 1.5 // 1.5 hrs of TV
            "glass" -> quantity * 3.0   // 3 hrs of LED light
            "metal" -> quantity * 4.0   // 4 hrs of game console
            else -> quantity * 1.0
        }

    val waterSavedCups: Double
        get() = when (itemType.lowercase()) {
            "paper" -> quantity * 3.0
            "plastic" -> quantity * 2.0
            "glass" -> quantity * 1.0
            "metal" -> quantity * 5.0
            else -> quantity * 1.5
        }
}

@Entity(tableName = "weekly_challenges")
data class WeeklyChallenge(
    @PrimaryKey val challengeId: String,
    val title: String,
    val description: String,
    val targetCount: Int,
    val currentCount: Int,
    val itemType: String, // "plastic", "paper", "glass", "metal", "all"
    val rewardXp: Int,
    val isCompleted: Boolean = false,
    val isRewardClaimed: Boolean = false
)

@Entity(tableName = "earned_badges")
data class EarnedBadge(
    @PrimaryKey val badgeId: String,
    val title: String,
    val description: String,
    val emoji: String,
    val colorHex: String,
    val earnedTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val userId: String = "champion", // single user profile
    val name: String = "Eco Hero",
    val avatarEmoji: String = "🦊", // fox, cat, dinosaur, leaf, rocket etc.
    val xp: Int = 0,
    val streakCount: Int = 0,
    val lastActiveTimestamp: Long = 0L
) {
    val level: Int
        get() = (xp / 100) + 1 // 100 XP per level

    val xpInCurrentLevel: Int
        get() = xp % 100
}
