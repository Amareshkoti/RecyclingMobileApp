package com.example.data.database

import androidx.room.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface RecyclingLogDao {
    @Query("SELECT * FROM recycling_logs ORDER BY timestamp DESC")
    fun getAllLogs(): Flow<List<RecyclingLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: RecyclingLog)

    @Query("DELETE FROM recycling_logs WHERE id = :id")
    suspend fun deleteLogById(id: Int)

    @Query("DELETE FROM recycling_logs")
    suspend fun clearAllLogs()
}

@Dao
interface WeeklyChallengeDao {
    @Query("SELECT * FROM weekly_challenges")
    fun getAllChallenges(): Flow<List<WeeklyChallenge>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChallenges(challenges: List<WeeklyChallenge>)

    @Update
    suspend fun updateChallenge(challenge: WeeklyChallenge)

    @Query("DELETE FROM weekly_challenges")
    suspend fun clearAllChallenges()
}

@Dao
interface EarnedBadgeDao {
    @Query("SELECT * FROM earned_badges ORDER BY earnedTimestamp DESC")
    fun getAllBadges(): Flow<List<EarnedBadge>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertBadge(badge: EarnedBadge)

    @Query("DELETE FROM earned_badges")
    suspend fun clearAllBadges()
}

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profile WHERE userId = 'champion' LIMIT 1")
    fun getUserProfileFlow(): Flow<UserProfile?>

    @Query("SELECT * FROM user_profile WHERE userId = 'champion' LIMIT 1")
    suspend fun getUserProfileDirect(): UserProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: UserProfile)
}

@Database(
    entities = [RecyclingLog::class, WeeklyChallenge::class, EarnedBadge::class, UserProfile::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun recyclingLogDao(): RecyclingLogDao
    abstract fun weeklyChallengeDao(): WeeklyChallengeDao
    abstract fun earnedBadgeDao(): EarnedBadgeDao
    abstract fun userProfileDao(): UserProfileDao
}
