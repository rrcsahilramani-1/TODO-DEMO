package com.demo.todo.ui.home

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.TaskAlt
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.demo.todo.data.TaskEntity
import com.demo.todo.ui.components.EmptyState
import com.demo.todo.ui.components.TaskRow
import com.demo.todo.ui.formatTodayHeader
import com.demo.todo.vm.TaskStats

@Composable
fun HomeScreen(
    tasks: List<TaskEntity>,
    stats: TaskStats,
    onToggle: (TaskEntity) -> Unit,
    onEdit: (TaskEntity) -> Unit,
    onDelete: (TaskEntity) -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = contentPadding.calculateTopPadding() + 8.dp,
            // Leave room for the FAB above the bottom bar.
            bottom = contentPadding.calculateBottomPadding() + 88.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = "Today",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = formatTodayHeader(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        item { SummaryCard(stats) }

        item {
            Text(
                text = "Tasks",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = 8.dp),
            )
        }

        if (tasks.isEmpty()) {
            item {
                EmptyState(
                    icon = Icons.Outlined.TaskAlt,
                    title = "Nothing due today",
                    subtitle = "Tap the + button to add your first task.",
                )
            }
        } else {
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

@Composable
private fun SummaryCard(stats: TaskStats) {
    val progress by animateFloatAsState(targetValue = stats.progress, label = "progress")

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                StatItem("Total", stats.total)
                StatItem("Completed", stats.completed)
                StatItem("Pending", stats.pending)
            }

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.15f),
                    strokeCap = androidx.compose.ui.graphics.StrokeCap.Round,
                    gapSize = 0.dp,
                    drawStopIndicator = {},
                )
                Text(
                    text = if (stats.total == 0) {
                        "No tasks yet"
                    } else {
                        "${(stats.progress * 100).toInt()}% complete"
                    },
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                )
            }
        }
    }
}

@Composable
private fun StatItem(label: String, value: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value.toString(),
            style = MaterialTheme.typography.headlineSmall,
            color = Color.Unspecified,
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.75f),
        )
    }
}
