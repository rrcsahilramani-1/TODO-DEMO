package com.demo.todo.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.demo.todo.settings.ThemeMode
import com.demo.todo.settings.ThemePreferences
import kotlinx.coroutines.flow.StateFlow

class ThemeViewModel(private val preferences: ThemePreferences) : ViewModel() {

    val themeMode: StateFlow<ThemeMode> = preferences.mode

    fun setThemeMode(mode: ThemeMode) = preferences.set(mode)

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                ThemeViewModel(TaskViewModel.app(this).themePreferences)
            }
        }
    }
}
