package com.demo.todo.settings

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class ThemeMode { SYSTEM, LIGHT, DARK }

/** Theme choice persisted in SharedPreferences — one value, no need for DataStore. */
class ThemePreferences(context: Context) {

    private val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)

    private val _mode = MutableStateFlow(read())
    val mode: StateFlow<ThemeMode> = _mode.asStateFlow()

    fun set(mode: ThemeMode) {
        prefs.edit().putString(KEY_THEME, mode.name).apply()
        _mode.value = mode
    }

    private fun read(): ThemeMode =
        runCatching { ThemeMode.valueOf(prefs.getString(KEY_THEME, null) ?: ThemeMode.SYSTEM.name) }
            .getOrDefault(ThemeMode.SYSTEM)

    private companion object {
        const val KEY_THEME = "theme_mode"
    }
}
