package com.demo.todo.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String = "",
    /** Epoch millis at local midnight of the due day, or null when the task has no date. */
    val dueDate: Long? = null,
    val isDone: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
)
