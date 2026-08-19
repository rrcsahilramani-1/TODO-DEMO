package com.demo.todo.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    /** Undated tasks sort last; within a group, oldest first. */
    @Query(
        """
        SELECT * FROM tasks
        ORDER BY isDone ASC,
                 CASE WHEN dueDate IS NULL THEN 1 ELSE 0 END ASC,
                 dueDate ASC,
                 createdAt ASC
        """
    )
    fun observeAll(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getById(id: Long): TaskEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: TaskEntity): Long

    @Update
    suspend fun update(task: TaskEntity)

    @Delete
    suspend fun delete(task: TaskEntity)

    @Query("UPDATE tasks SET isDone = :done WHERE id = :id")
    suspend fun setDone(id: Long, done: Boolean)

    @Query("DELETE FROM tasks WHERE isDone = 1")
    suspend fun deleteCompleted(): Int
}
