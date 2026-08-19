package com.demo.todo.ui.edit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.demo.todo.ui.formatDueDate
import com.demo.todo.ui.startOfDay
import com.demo.todo.vm.AddEditViewModel
import java.util.Calendar
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTaskScreen(
    onDone: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AddEditViewModel = viewModel(factory = AddEditViewModel.Factory),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var showDatePicker by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(if (state.isEditing) "Edit task" else "New task") },
                navigationIcon = {
                    IconButton(onClick = onDone) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            OutlinedTextField(
                value = state.title,
                onValueChange = viewModel::onTitleChange,
                label = { Text("Title") },
                singleLine = true,
                isError = state.showTitleError,
                supportingText = if (state.showTitleError) {
                    { Text("Title can't be empty") }
                } else {
                    null
                },
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                    imeAction = ImeAction.Next,
                ),
                modifier = Modifier.fillMaxWidth(),
            )

            OutlinedTextField(
                value = state.description,
                onValueChange = viewModel::onDescriptionChange,
                label = { Text("Description (optional)") },
                minLines = 3,
                modifier = Modifier.fillMaxWidth(),
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Due date",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                androidx.compose.foundation.layout.Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    OutlinedButton(
                        onClick = { showDatePicker = true },
                        modifier = Modifier.weight(1f),
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.CalendarToday,
                            contentDescription = null,
                            modifier = Modifier.height(18.dp),
                        )
                        Text(
                            text = state.dueDate?.let { formatDueDate(it) } ?: "Pick a date",
                            modifier = Modifier.padding(start = 8.dp),
                        )
                    }
                    if (state.dueDate != null) {
                        IconButton(onClick = { viewModel.onDueDateChange(null) }) {
                            Icon(
                                imageVector = Icons.Outlined.Close,
                                contentDescription = "Clear due date",
                            )
                        }
                    }
                }
            }

            Button(
                onClick = { viewModel.save(onDone) },
                enabled = state.canSave,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
            ) {
                Text("Save")
            }
        }
    }

    if (showDatePicker) {
        // DatePicker works in UTC millis; convert to and from local midnight.
        val pickerState = androidx.compose.material3.rememberDatePickerState(
            initialSelectedDateMillis = state.dueDate?.let { localToUtcMillis(it) },
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        pickerState.selectedDateMillis?.let {
                            viewModel.onDueDateChange(utcToLocalStartOfDay(it))
                        }
                        showDatePicker = false
                    },
                ) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            },
        ) {
            DatePicker(state = pickerState)
        }
    }
}

private fun localToUtcMillis(localMillis: Long): Long {
    val cal = Calendar.getInstance().apply { timeInMillis = localMillis }
    val utc = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
        clear()
        set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
    }
    return utc.timeInMillis
}

private fun utcToLocalStartOfDay(utcMillis: Long): Long {
    val utc = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply { timeInMillis = utcMillis }
    val local = Calendar.getInstance().apply {
        clear()
        set(utc.get(Calendar.YEAR), utc.get(Calendar.MONTH), utc.get(Calendar.DAY_OF_MONTH))
    }
    return startOfDay(local.timeInMillis)
}
