package com.enriselle.todochan.features

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.enriselle.todochan.ui.theme.TodochanTheme
import java.time.Instant
import java.time.ZoneId

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewTodo(
    onAddClick: () -> Unit,
    onBackClick: () -> Unit,
    todoViewModel: TodoViewModel
) {
    val data = todoViewModel.data
    val showDateDialog = remember { mutableStateOf(false) }
    //val snackBarHostState = SnackbarHostState()

    if (showDateDialog.value) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDateDialog.value = !showDateDialog.value },
            confirmButton = {
                TextButton(
                    onClick = {
                        val date = datePickerState.selectedDateMillis
                        date?.let {
                            val localDate =
                                Instant.ofEpochMilli(it).atZone(ZoneId.of("UTC")).toLocalDate()
                            todoViewModel.onEvent(TodoFormEvent.DueDateChanged(dueDate = "${localDate.dayOfMonth}/${localDate.month}/${localDate.year}"))
                        }
                        showDateDialog.value = !showDateDialog.value
                    }
                ) {
                    Text(text = "Guardar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    TodochanTheme {
        Scaffold(
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        todoViewModel.onEvent(TodoFormEvent.Submit)

                        if (todoViewModel.flag.value) {
                            onAddClick()
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Save new todo"
                    )
                }
            },
            topBar = {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { onBackClick() }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }

                    TopAppBar(
                        title = {
                            Text(text = "¡Ingresa los datos!")
                        }
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
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Column {
                        Text(text = "Título")
                        OutlinedTextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = data.title,
                            onValueChange = { newTitle ->
                                todoViewModel.onEvent(TodoFormEvent.TitleChanged(title = newTitle))
                            }
                        )
                    }

                    Column {
                        Text(text = "Descripción")
                        OutlinedTextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = data.description,
                            onValueChange = { newDescription ->
                                todoViewModel.onEvent(TodoFormEvent.DescriptionChanged(description = newDescription))
                            }
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = data.dueDate)

                        Button(
                            onClick = { showDateDialog.value = !showDateDialog.value }
                        ) {
                            Text(text = "Seleccionar fecha")
                        }
                    }
                }
            }
        }
    }
}
