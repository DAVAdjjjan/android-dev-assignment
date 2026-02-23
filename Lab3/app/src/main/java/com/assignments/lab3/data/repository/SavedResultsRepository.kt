package com.assignments.lab3.data.repository

import com.assignments.lab3.data.local.SavedResultDao
import com.assignments.lab3.data.local.SavedResultEntity
import kotlinx.coroutines.flow.Flow

class SavedResultsRepository(private val dao: SavedResultDao) {

    fun getAll(): Flow<List<SavedResultEntity>> = dao.getAll()

    suspend fun insert(result: SavedResultEntity): Long = dao.insert(result)

    suspend fun update(result: SavedResultEntity) = dao.update(result)

    suspend fun delete(result: SavedResultEntity) = dao.delete(result)

    suspend fun deleteAll() = dao.deleteAll()
}
