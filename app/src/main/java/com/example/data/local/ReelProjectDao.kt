package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ReelProjectDao {
    @Query("SELECT * FROM reel_projects ORDER BY createdAt DESC")
    fun getAllProjects(): Flow<List<ReelProjectEntity>>

    @Query("SELECT * FROM reel_projects WHERE id = :id LIMIT 1")
    suspend fun getProjectById(id: Long): ReelProjectEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: ReelProjectEntity): Long

    @Delete
    suspend fun deleteProject(project: ReelProjectEntity)

    @Query("DELETE FROM reel_projects WHERE id = :id")
    suspend fun deleteProjectById(id: Long)
}
