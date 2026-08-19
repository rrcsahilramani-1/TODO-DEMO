package com.demo.todo.vm

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.demo.todo.data.TaskEntity
import com.demo.todo.data.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AddEditUiState(
    val title: String = "",
    val description: String = "",
    val dueDate: Long? = null,
    val isEditing: Boolean = false,
    val loaded: Boolean = false,
    val showTitleError: Boolean = false,
) {
    val canSave: Boolean get() = title.isNotBlank()
}

class AddEditViewModel(
    private val repository: TaskRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    /** -1 means "new task"; any other value is an existing row id. */
    private val taskId: Long = savedStateHandle.get<Long>(ARG_TASK_ID) ?: NEW_TASK_ID

    private var existing: TaskEntity? = null

    private val _state = MutableStateFlow(AddEditUiState(isEditing = taskId != NEW_TASK_ID))
    val state: StateFlow<AddEditUiState> = _state.asStateFlow()

    init {
        if (taskId == NEW_TASK_ID) {
            _state.update { it.copy(loaded = true) }
        } else {
            viewModelScope.launch {
                val task = repository.getById(taskId)
                existing = task
                _state.update {
                    if (task == null) {
                        it.copy(loaded = true, isEditing = false)
                    } else {
                        it.copy(
                            title = task.title,
                            description = task.description,
                            dueDate = task.dueDate,
                            isEditing = true,
                            loaded = true,
                        )
                    }
                }
            }
        }
    }

    fun onTitleChange(value: String) =
        _state.update { it.copy(title = value, showTitleError = false) }

    fun onDescriptionChange(value: String) = _state.update { it.copy(description = value) }

    fun onDueDateChange(value: Long?) = _state.update { it.copy(dueDate = value) }

    /** Invokes [onSaved] only once the row is actually written. */
    fun save(onSaved: () -> Unit) {
        val current = _state.value
        if (!current.canSave) {
            _state.update { it.copy(showTitleError = true) }
            return
        }
        viewModelScope.launch {
            val base = existing
            val task = base?.copy(
                title = current.title.trim(),
                description = current.description.trim(),
                dueDate = current.dueDate,
            ) ?: TaskEntity(
                title = current.title.trim(),
                description = current.description.trim(),
                dueDate = current.dueDate,
            )
            repository.save(task)
            onSaved()
        }
    }

    companion object {
        const val ARG_TASK_ID = "taskId"
        const val NEW_TASK_ID = -1L

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                AddEditViewModel(
                    repository = TaskViewModel.app(this).repository,
                    savedStateHandle = createSavedStateHandle(),
                )
            }
        }
    }
}
