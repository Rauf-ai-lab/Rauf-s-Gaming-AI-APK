package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface GameProfileDao {
    @Query("SELECT * FROM game_profiles ORDER BY lastPlayedTime DESC")
    fun getAllProfiles(): Flow<List<GameProfile>>

    @Query("SELECT * FROM game_profiles WHERE id = :id LIMIT 1")
    suspend fun getProfileById(id: Long): GameProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: GameProfile): Long

    @Update
    suspend fun updateProfile(profile: GameProfile)

    @Query("DELETE FROM game_profiles WHERE id = :id")
    suspend fun deleteProfile(id: Long)

    @Query("SELECT COUNT(*) FROM game_profiles")
    suspend fun getCount(): Int
}

@Dao
interface ChatMessageDao {
    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC")
    fun getAllMessages(): Flow<List<ChatMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessage): Long

    @Query("DELETE FROM chat_messages")
    suspend fun clearHistory()
}

@Dao
interface PerformanceDao {
    @Query("SELECT * FROM performance_snapshots ORDER BY timestamp DESC LIMIT 30")
    fun getRecentSnapshots(): Flow<List<PerformanceSnapshot>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSnapshot(snapshot: PerformanceSnapshot)

    @Query("DELETE FROM performance_snapshots")
    suspend fun clearSnapshots()
}
