package com.demo.todo.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import com.demo.todo.TodoApplication
import com.demo.todo.data.TaskEntity
import com.demo.todo.data.TaskRepository
import com.demo.todo.ui.startOfDay
import com.demo.todo.ui.todayStartMillis
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class TaskFilter(val label: String) { ALL("All"), PENDING("Pending"), COMPLETED("Completed") }

data class TaskStats(val total: Int, val completed: Int) {
    val pending: Int get() = total - completed
    val progress: Float get() = if (total == 0) 0f else completed.toFloat() / total
}

data class TasksUiState(
    val filter: TaskFilter = TaskFilter.ALL,
    /** Everything in the DB, already sorted by the DAO. */
    val allTasks: List<TaskEntity> = emptyList(),
    /** [allTasks] narrowed by [filter] — what the Tasks screen renders. */
    val filteredTasks: List<TaskEntity> = emptyList(),
    /** Tasks due today, plus overdue and undated ones — what the Home screen renders. */
    val todayTasks: List<TaskEntity> = emptyList(),
    val todayStats: TaskStats = TaskStats(0, 0),
    val overallStats: TaskStats = TaskStats(0, 0),
)

class TaskViewModel(private val repository: TaskRepository) : ViewModel() {

    private val filter = MutableStateFlow(TaskFilter.ALL)

    /** Set when a task is deleted so the UI can offer an Undo. */
    private val _recentlyDeleted = MutableStateFlow<TaskEntity?>(null)
    val recentlyDeleted: StateFlow<TaskEntity?> = _recentlyDeleted.asStateFlow()

    val uiState: StateFlow<TasksUiState> =
        combine(repository.observeAll(), filter) { tasks, activeFilter ->
            val today = todayStartMillis()
            val todayTasks = tasks.filter { it.dueDate == null || startOfDay(it.dueDate) <= today }
            TasksUiState(
                filter = activeFilter,
                allTasks = tasks,
                filteredTasks = when (activeFilter) {
                    TaskFilter.ALL -> tasks
                    TaskFilter.PENDING -> tasks.filter { !it.isDone }
                    TaskFilter.COMPLETED -> tasks.filter { it.isDone }
                },
                todayTasks = todayTasks,
                todayStats = TaskStats(todayTasks.size, todayTasks.count { it.isDone }),
                overallStats = TaskStats(tasks.size, tasks.count { it.isDone }),
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = TasksUiState(),
        )

    fun setFilter(value: TaskFilter) {
        filter.value = value
    }

    fun toggleDone(task: TaskEntity) = viewModelScope.launch {
        repository.setDone(task.id, !task.isDone)
    }

    fun delete(task: TaskEntity) = viewModelScope.launch {
        repository.delete(task)
        _recentlyDeleted.value = task
    }

    fun undoDelete() = viewModelScope.launch {
        _recentlyDeleted.value?.let { repository.restore(it) }
        _recentlyDeleted.value = null
    }

    fun consumeDeleteEvent() {
        _recentlyDeleted.value = null
    }

    fun clearCompleted() = viewModelScope.launch {
        repository.clearCompleted()
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                TaskViewModel(app(this).repository)
            }
        }

        internal fun app(extras: CreationExtras): TodoApplication =
            extras[APPLICATION_KEY] as TodoApplication
    }
}
