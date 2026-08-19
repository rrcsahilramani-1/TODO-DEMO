package com.demo.todo.data

import kotlinx.coroutines.flow.Flow

class TaskRepository(private val dao: TaskDao) {

    fun observeAll(): Flow<List<TaskEntity>> = dao.observeAll()

    suspend fun getById(id: Long): TaskEntity? = dao.getById(id)

    /** Inserts when [task] has id 0, updates otherwise. */
    suspend fun save(task: TaskEntity) {
        if (task.id == 0L) dao.insert(task) else dao.update(task)
    }

    suspend fun restore(task: TaskEntity) {
        dao.insert(task)
    }

    suspend fun setDone(id: Long, done: Boolean) = dao.setDone(id, done)

    suspend fun delete(task: TaskEntity) = dao.delete(task)

    suspend fun clearCompleted(): Int = dao.deleteCompleted()
}
