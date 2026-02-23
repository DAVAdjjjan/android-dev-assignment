package com.assignments.lab3.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedResultDao {

    @Query("SELECT * FROM saved_results ORDER BY createdAt DESC")
    fun getAll(): Flow<List<SavedResultEntity>>

    @Insert
    suspend fun insert(result: SavedResultEntity): Long

    @Update
    suspend fun update(result: SavedResultEntity)

    @Delete
    suspend fun delete(result: SavedResultEntity)

    @Query("DELETE FROM saved_results")
    suspend fun deleteAll()
}
