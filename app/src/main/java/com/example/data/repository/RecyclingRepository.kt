package com.example.data.repository

import android.util.Log
import com.example.data.database.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import java.util.UUID

class RecyclingRepository(private val database: AppDatabase) {

    private val logDao = database.recyclingLogDao()
    private val challengeDao = database.weeklyChallengeDao()
    private val badgeDao = database.earnedBadgeDao()
    private val profileDao = database.userProfileDao()

    // Flow streams for UI layers
    val allLogs: Flow<List<RecyclingLog>> = logDao.getAllLogs()
    val allChallenges: Flow<List<WeeklyChallenge>> = challengeDao.getAllChallenges()
    val allBadges: Flow<List<EarnedBadge>> = badgeDao.getAllBadges()
    val userProfile: Flow<UserProfile?> = profileDao.getUserProfileFlow()

    suspend fun initializeDatabaseIfEmpty() {
        // Initialize User Profile
        val currentProfile = profileDao.getUserProfileDirect()
        if (currentProfile == null) {
            val newProfile = UserProfile(
                userId = "champion",
                name = "Eco Kid",
                avatarEmoji = "🦊",
                xp = 0,
                streakCount = 1,
                lastActiveTimestamp = System.currentTimeMillis()
            )
            profileDao.insertOrUpdateProfile(newProfile)
        }

        // Initialize Challenges
        val currentChallenges = challengeDao.getAllChallenges().firstOrNull()
        if (currentChallenges.isNullOrEmpty()) {
            val defaultChallenges = listOf(
                WeeklyChallenge(
                    challengeId = "plastic_patrol",
                    title = "Plastic Captain 🥤",
                    description = "Recycle 5 plastic bottles to help marine life!",
                    targetCount = 5,
                    currentCount = 0,
                    itemType = "plastic",
                    rewardXp = 40
                ),
                WeeklyChallenge(
                    challengeId = "paper_knight",
                    title = "Forest Hero 📚",
                    description = "Recycle 8 paper items to save trees!",
                    targetCount = 8,
                    currentCount = 0,
                    itemType = "paper",
                    rewardXp = 50
                ),
                WeeklyChallenge(
                    challengeId = "glass_guardian",
                    title = "Glass Wizard 💎",
                    description = "Recycle 3 glass products to keep playgrounds safe!",
                    targetCount = 3,
                    currentCount = 0,
                    itemType = "glass",
                    rewardXp = 60
                ),
                WeeklyChallenge(
                    challengeId = "metal_miner",
                    title = "Can Crusader 🥫",
                    description = "Recycle 4 aluminum empty cans!",
                    targetCount = 4,
                    currentCount = 0,
                    itemType = "metal",
                    rewardXp = 40
                ),
                WeeklyChallenge(
                    challengeId = "eco_combo",
                    title = "Super Eco Champion 🌟",
                    description = "Recycle 15 total items of any kind!",
                    targetCount = 15,
                    currentCount = 0,
                    itemType = "all",
                    rewardXp = 100
                )
            )
            challengeDao.insertChallenges(defaultChallenges)
        }
    }

    suspend fun logRecycling(itemType: String, quantity: Int, materialName: String): Double {
        val newLog = RecyclingLog(
            itemType = itemType,
            quantity = quantity,
            materialName = materialName,
            timestamp = System.currentTimeMillis()
        )
        // 1. Insert Log
        logDao.insertLog(newLog)

        // 2. Update Streak & Active user profile
        var profile = profileDao.getUserProfileDirect() ?: UserProfile()
        val now = System.currentTimeMillis()
        val diffMs = now - profile.lastActiveTimestamp

        val updatedStreak = when {
            profile.lastActiveTimestamp == 0L -> 1
            diffMs < 30 * 60 * 1000 -> profile.streakCount // If logged within 30 mins, keep streak
            diffMs <= 36 * 60 * 60 * 1000 -> profile.streakCount + 1 // Consecutive day (approx < 36 hrs)
            else -> 1 // Streak lost (reset)
        }

        // Add 10 XP base experience per item logged
        val baseLogXp = 10 * quantity
        val newXp = profile.xp + baseLogXp

        val updatedProfile = profile.copy(
            xp = newXp,
            streakCount = updatedStreak,
            lastActiveTimestamp = now
        )
        profileDao.insertOrUpdateProfile(updatedProfile)

        // 3. Update Weekly Challenges progress
        val challenges = challengeDao.getAllChallenges().firstOrNull() ?: emptyList()
        challenges.forEach { challenge ->
            if (!challenge.isCompleted) {
                val matchesType = challenge.itemType == "all" || challenge.itemType.equals(itemType, ignoreCase = true)
                if (matchesType) {
                    val newCount = minOf(challenge.targetCount, challenge.currentCount + quantity)
                    val isNowCompleted = newCount >= challenge.targetCount
                    val updatedChallenge = challenge.copy(
                        currentCount = newCount,
                        isCompleted = isNowCompleted
                    )
                    challengeDao.updateChallenge(updatedChallenge)
                }
            }
        }

        // 4. Perform Badges check after logging
        checkAndAwardBadges(updatedProfile, quantity)

        return newLog.weightKg
    }

    suspend fun claimChallengeReward(challengeId: String): Int {
        val challenges = challengeDao.getAllChallenges().firstOrNull() ?: return 0
        val challenge = challenges.find { it.challengeId == challengeId } ?: return 0
        if (challenge.isCompleted && !challenge.isRewardClaimed) {
            // Update challenge state
            challengeDao.updateChallenge(challenge.copy(isRewardClaimed = true))

            // Add XP Reward
            val profile = profileDao.getUserProfileDirect() ?: UserProfile()
            val updatedProfile = profile.copy(xp = profile.xp + challenge.rewardXp)
            profileDao.insertOrUpdateProfile(updatedProfile)

            // Recheck level based badges
            checkAndAwardBadges(updatedProfile, 0)
            return challenge.rewardXp
        }
        return 0
    }

    suspend fun updateProfileNameAndAvatar(name: String, avatar: String) {
        val profile = profileDao.getUserProfileDirect() ?: UserProfile()
        profileDao.insertOrUpdateProfile(profile.copy(name = name, avatarEmoji = avatar))
    }

    suspend fun resetWeeklyChallenges() {
        val challenges = challengeDao.getAllChallenges().firstOrNull() ?: return
        val resetList = challenges.map {
            it.copy(currentCount = 0, isCompleted = false, isRewardClaimed = false)
        }
        challengeDao.insertChallenges(resetList)
    }

    suspend fun deleteLogAndDeductXp(log: RecyclingLog) {
        logDao.deleteLogById(log.id)
        val profile = profileDao.getUserProfileDirect() ?: return
        val deductAmount = 10 * log.quantity
        val updatedXp = maxOf(0, profile.xp - deductAmount)
        profileDao.insertOrUpdateProfile(profile.copy(xp = updatedXp))
    }

    suspend fun clearAllData() {
        // Safe reset back to demo status
        logDao.clearAllLogs()
        challengeDao.clearAllChallenges()
        badgeDao.clearAllBadges()
        val defaultProfile = UserProfile(
            userId = "champion",
            name = "Eco Kid",
            avatarEmoji = "🦊",
            xp = 0,
            streakCount = 1,
            lastActiveTimestamp = System.currentTimeMillis()
        )
        profileDao.insertOrUpdateProfile(defaultProfile)
        initializeDatabaseIfEmpty()
    }

    private suspend fun checkAndAwardBadges(profile: UserProfile, lastLoggedQuantity: Int) {
        val logs = logDao.getAllLogs().firstOrNull() ?: emptyList()
        val totalQuantity = logs.sumOf { it.quantity }
        val paperQuantity = logs.filter { it.itemType.equals("paper", true) }.sumOf { it.quantity }
        val plasticQuantity = logs.filter { it.itemType.equals("plastic", true) }.sumOf { it.quantity }
        val glassQuantity = logs.filter { it.itemType.equals("glass", true) }.sumOf { it.quantity }
        val metalQuantity = logs.filter { it.itemType.equals("metal", true) }.sumOf { it.quantity }

        // 1. Sprout (First timer)
        if (logs.isNotEmpty()) {
            badgeDao.insertBadge(
                EarnedBadge(
                    badgeId = "badge_first_log",
                    title = "Green 🌱 Sprout",
                    description = "Completed your very first recycling entry!",
                    emoji = "🌱",
                    colorHex = "#81C784"
                )
            )
        }

        // 2. Plastic Patrol (10 Plastics)
        if (plasticQuantity >= 10) {
            badgeDao.insertBadge(
                EarnedBadge(
                    badgeId = "badge_plastic_patrol",
                    title = "Plastic Patrol 🥤",
                    description = "Kept 10 plastic bottles or wrappers safe from harm!",
                    emoji = "🥤",
                    colorHex = "#64B5F6"
                )
            )
        }

        // 3. Forest Hero (15 Papers)
        if (paperQuantity >= 15) {
            badgeDao.insertBadge(
                EarnedBadge(
                    badgeId = "badge_forest_hero",
                    title = "Forest Guardian 📚",
                    description = "Recycled 15 paper products to keep forests happy and green!",
                    emoji = "🌲",
                    colorHex = "#9CCC65"
                )
            )
        }

        // 4. Glass Wizard (5 Glass bottles)
        if (glassQuantity >= 5) {
            badgeDao.insertBadge(
                EarnedBadge(
                    badgeId = "badge_glass_wizard",
                    title = "Glass Glass-adiator 💎",
                    description = "Kept 5 sharp glass jars safe and clean!",
                    emoji = "🔮",
                    colorHex = "#4DD0E1"
                )
            )
        }

        // 5. Metal Miner (8 Cans)
        if (metalQuantity >= 8) {
            badgeDao.insertBadge(
                EarnedBadge(
                    badgeId = "badge_metal_miner",
                    title = "Can Crusher 🥫",
                    description = "Recycled 8 aluminum cans or metal tins!",
                    emoji = "🥫",
                    colorHex = "#FFB74D"
                )
            )
        }

        // 6. Consistent Streak badge
        if (profile.streakCount >= 3) {
            badgeDao.insertBadge(
                EarnedBadge(
                    badgeId = "badge_streak_3",
                    title = "Super Eco Streak 🔥",
                    description = "Recycled on consecutive days with an amazing eco streak!",
                    emoji = "🔥",
                    colorHex = "#F06292"
                )
            )
        }

        // 7. Level Achievement badge
        if (profile.level >= 3) {
            badgeDao.insertBadge(
                EarnedBadge(
                    badgeId = "badge_level_3",
                    title = "Earth Protector 👑",
                    description = "Reached Level 3 with superb active recycling habits!",
                    emoji = "👑",
                    colorHex = "#BA68C8"
                )
            )
        }
    }
}
