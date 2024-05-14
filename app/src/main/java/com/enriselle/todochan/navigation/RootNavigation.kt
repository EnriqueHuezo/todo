package com.enriselle.todochan.navigation

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.core.os.bundleOf
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.enriselle.todochan.features.Details
import com.enriselle.todochan.features.Main
import com.enriselle.todochan.features.NewTodo
import com.enriselle.todochan.features.Todo
import com.enriselle.todochan.features.TodoViewModel
import com.enriselle.todochan.ui.theme.TodochanTheme

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RootNavigation(
    todoViewModel: TodoViewModel
) {
    val navController = rememberNavController()
    TodochanTheme {
        NavHost(
            navController = navController,
            route = Graph.MAIN.route,
            startDestination = MainRoute.MainScreen.route
        ) {
            composable(MainRoute.MainScreen.route) {
                Main(
                    onDetailsTodoClick = { todo ->
                        navController.currentBackStackEntry?.savedStateHandle?.set("todo", todo)
                        navController.navigate(MainRoute.DetailsScreen.route)
                    },
                    onAddNewTodoClick = {
                        navController.navigate(MainRoute.CreateTodoScreen.route)
                    },
                    todoViewModel = todoViewModel
                )
            }

            composable(MainRoute.DetailsScreen.route) {
                val sharedTodo = navController.previousBackStackEntry?.savedStateHandle?.get<Todo>("todo")
                Details(
                    onBackClick = {
                        navController.navigate(MainRoute.MainScreen.route)
                    },
                    todo = sharedTodo
                )
            }

            composable(MainRoute.CreateTodoScreen.route) {
                NewTodo(
                    onAddClick = {
                        navController.navigate(MainRoute.MainScreen.route) {
                            popUpTo(MainRoute.MainScreen.route)
                        }
                    },
                    onBackClick = {
                        navController.navigate(MainRoute.MainScreen.route) {
                            popUpTo(MainRoute.MainScreen.route) {
                                inclusive = true
                            }
                        }
                    },
                    todoViewModel = todoViewModel
                )
            }
        }
    }
}
sealed class Graph(val route: String) {
    data object MAIN: Graph(route = "MAIN")
}

sealed class MainRoute(val route: String) {
    data object MainScreen: MainRoute(route = "Main")
    data object DetailsScreen: MainRoute(route = "Details")
    data object CreateTodoScreen: MainRoute(route = "NewTodo")
}