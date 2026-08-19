package com.demo.todo.ui.tasks

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.ChecklistRtl
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.demo.todo.data.TaskEntity
import com.demo.todo.ui.components.EmptyState
import com.demo.todo.ui.components.TaskRow
import com.demo.todo.vm.TaskFilter

@Composable
fun TasksScreen(
    tasks: List<TaskEntity>,
    filter: TaskFilter,
    onFilterChange: (TaskFilter) -> Unit,
    onToggle: (TaskEntity) -> Unit,
    onEdit: (TaskEntity) -> Unit,
    onDelete: (TaskEntity) -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.padding(
                start = 16.dp,
                end = 16.dp,
                top = contentPadding.calculateTopPadding() + 8.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = "All tasks",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TaskFilter.entries.forEach { entry ->
                    val selected = entry == filter
                    FilterChip(
                        selected = selected,
                        onClick = { onFilterChange(entry) },
                        label = { Text(entry.label) },
                        leadingIcon = if (selected) {
                            {
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(FilterChipDefaults.IconSize),
                                )
                            }
                        } else {
                            null
                        },
                    )
                }
            }
        }

        if (tasks.isEmpty()) {
            EmptyState(
                icon = Icons.Outlined.ChecklistRtl,
                title = when (filter) {
                    TaskFilter.ALL -> "No tasks yet"
                    TaskFilter.PENDING -> "Nothing pending"
                    TaskFilter.COMPLETED -> "Nothing completed yet"
                },
                subtitle = when (filter) {
                    TaskFilter.ALL -> "Tap the + button to add your first task."
                    TaskFilter.PENDING -> "Every task is done. Nice work."
                    TaskFilter.COMPLETED -> "Tick a task off to see it here."
                },
                modifier = Modifier.padding(top = 24.dp),
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = 12.dp,
                    bottom = contentPadding.calculateBottomPadding() + 88.dp,
                ),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(tasks, key = { it.id }) { task ->
                    TaskRow(
                        task = task,
                        onToggle = { onToggle(task) },
                        onEdit = { onEdit(task) },
                        onDelete = { onDelete(task) },
                    )
                }
            }
        }
    }
}
