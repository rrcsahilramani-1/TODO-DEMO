package com.demo.todo.ui.nav

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.CheckCircleOutline
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.demo.todo.settings.ThemeMode
import com.demo.todo.ui.edit.AddEditTaskScreen
import com.demo.todo.ui.home.HomeScreen
import com.demo.todo.ui.settings.SettingsScreen
import com.demo.todo.ui.tasks.TasksScreen
import com.demo.todo.vm.AddEditViewModel
import com.demo.todo.vm.TaskViewModel

private enum class TopLevelDestination(
    val route: String,
    val label: String,
    val icon: ImageVector,
) {
    HOME("home", "Home", Icons.Outlined.Home),
    TASKS("tasks", "Tasks", Icons.Outlined.CheckCircleOutline),
    SETTINGS("settings", "Settings", Icons.Outlined.Settings),
}

private const val ROUTE_EDIT = "edit"

@Composable
fun TodoNavHost(
    themeMode: ThemeMode,
    onThemeModeChange: (ThemeMode) -> Unit,
    modifier: Modifier = Modifier,
    taskViewModel: TaskViewModel = viewModel(factory = TaskViewModel.Factory),
) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    val uiState by taskViewModel.uiState.collectAsStateWithLifecycle()
    val recentlyDeleted by taskViewModel.recentlyDeleted.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(recentlyDeleted) {
        val task = recentlyDeleted ?: return@LaunchedEffect
        val result = snackbarHostState.showSnackbar(
            message = "Deleted \"${task.title}\"",
            actionLabel = "Undo",
        )
        if (result == SnackbarResult.ActionPerformed) {
            taskViewModel.undoDelete()
        } else {
            taskViewModel.consumeDeleteEvent()
        }
    }

    val onBottomBar = TopLevelDestination.entries.any { it.route == currentRoute }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            AnimatedVisibility(
                visible = onBottomBar,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                NavigationBar {
                    TopLevelDestination.entries.forEach { destination ->
                        val selected = backStackEntry?.destination?.hierarchy
                            ?.any { it.route == destination.route } == true
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(destination.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(destination.icon, contentDescription = destination.label)
                            },
                            label = { Text(destination.label) },
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = currentRoute == TopLevelDestination.HOME.route ||
                    currentRoute == TopLevelDestination.TASKS.route,
                enter = scaleIn(),
                exit = scaleOut(),
            ) {
                FloatingActionButton(
                    onClick = { navController.navigate("$ROUTE_EDIT/${AddEditViewModel.NEW_TASK_ID}") },
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Add task")
                }
            }
        },
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = TopLevelDestination.HOME.route,
        ) {
            composable(TopLevelDestination.HOME.route) {
                HomeScreen(
                    tasks = uiState.todayTasks,
                    stats = uiState.todayStats,
                    onToggle = taskViewModel::toggleDone,
                    onEdit = { navController.navigate("$ROUTE_EDIT/${it.id}") },
                    onDelete = taskViewModel::delete,
                    contentPadding = padding,
                )
            }

            composable(TopLevelDestination.TASKS.route) {
                TasksScreen(
                    tasks = uiState.filteredTasks,
                    filter = uiState.filter,
                    onFilterChange = taskViewModel::setFilter,
                    onToggle = taskViewModel::toggleDone,
                    onEdit = { navController.navigate("$ROUTE_EDIT/${it.id}") },
                    onDelete = taskViewModel::delete,
                    contentPadding = padding,
                )
            }

            composable(TopLevelDestination.SETTINGS.route) {
                SettingsScreen(
                    themeMode = themeMode,
                    onThemeModeChange = onThemeModeChange,
                    completedCount = uiState.overallStats.completed,
                    onClearCompleted = taskViewModel::clearCompleted,
                    contentPadding = padding,
                )
            }

            composable(
                route = "$ROUTE_EDIT/{${AddEditViewModel.ARG_TASK_ID}}",
                arguments = listOf(
                    navArgument(AddEditViewModel.ARG_TASK_ID) {
                        type = NavType.LongType
                        defaultValue = AddEditViewModel.NEW_TASK_ID
                    },
                ),
            ) {
                AddEditTaskScreen(onDone = { navController.popBackStack() })
            }
        }
    }
}
