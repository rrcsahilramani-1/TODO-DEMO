package com.demo.todo

import android.app.Application
import com.demo.todo.data.AppDatabase
import com.demo.todo.data.TaskRepository
import com.demo.todo.settings.ThemePreferences

/**
 * Manual dependency container — the app is small enough that a DI framework
 * would cost more than it saves.
 */
class TodoApplication : Application() {

    lateinit var repository: TaskRepository
        private set

    lateinit var themePreferences: ThemePreferences
        private set

    override fun onCreate() {
        super.onCreate()
        repository = TaskRepository(AppDatabase.get(this).taskDao())
        themePreferences = ThemePreferences(this)
    }
}
