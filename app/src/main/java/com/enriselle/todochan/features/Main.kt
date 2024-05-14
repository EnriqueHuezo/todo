package com.enriselle.todochan.features

import android.os.Parcelable
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.enriselle.todochan.ui.theme.TodochanTheme
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Main(
    onDetailsTodoClick: (Todo) -> Unit,
    onAddNewTodoClick: () -> Unit,
    todoViewModel: TodoViewModel
) {
    val todoList = todoViewModel.todoList.collectAsState()

    TodochanTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(text = "¿Qué vamos a hacer hoy?")
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        onAddNewTodoClick()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add new todo"
                    )
                }
            },
            snackbarHost = {
                SnackbarHost(hostState = todoViewModel.snackBarHostState)
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                when {
                    todoList.value.isNotEmpty() -> {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                        ) {
                            itemsIndexed(todoList.value) { index, item ->
                                val isCompletedUiStyle = if (item.completed) { TextDecoration.LineThrough } else { TextDecoration.None }
                                    ListItem(
                                    modifier = Modifier
                                        .clickable {
                                            onDetailsTodoClick(item)
                                        },
                                    headlineContent = {
                                        Text(
                                            text = item.title,
                                            textDecoration = isCompletedUiStyle
                                        )
                                    },
                                    trailingContent = {
                                        Row {
                                            IconButton(
                                                onClick = {
                                                    todoViewModel.onEvent(TodoFormEvent.DeleteTodo(item))
                                                }
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Delete,
                                                    contentDescription = "Delete Todo"
                                                )
                                            }

                                            Checkbox(
                                                checked = item.completed,
                                                onCheckedChange = { newState ->
                                                    todoViewModel.onEvent(TodoFormEvent.CompletedChanged(id = item, completed = newState))
                                                }
                                            )
                                        }
                                    }
                                )

                                Divider()
                            }
                        }
                    }
                    else -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                text = "¡Agrega una nueva tarea!",
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
@Preview
fun MainPreview() {
    Main(
        onAddNewTodoClick = {},
        onDetailsTodoClick = {},
        todoViewModel = TodoViewModel()
    )
}