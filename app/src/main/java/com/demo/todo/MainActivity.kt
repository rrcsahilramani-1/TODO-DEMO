package com.demo.todo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.demo.todo.ui.nav.TodoNavHost
import com.demo.todo.ui.theme.TodoTheme
import com.demo.todo.vm.ThemeViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            val themeViewModel: ThemeViewModel = viewModel(factory = ThemeViewModel.Factory)
            val themeMode by themeViewModel.themeMode.collectAsStateWithLifecycle()

            TodoTheme(themeMode = themeMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    TodoNavHost(
                        themeMode = themeMode,
                        onThemeModeChange = themeViewModel::setThemeMode,
                    )
                }
            }
        }
    }
}
