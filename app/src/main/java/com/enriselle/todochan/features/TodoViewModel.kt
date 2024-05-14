package com.enriselle.todochan.features

import android.os.Parcelable
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.enriselle.todochan.data.domain.usecase.ValidateField
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
data class Todo(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "",
    val description: String = "",
    val timeStamp: String = "",
    val dueDate: String = "",
    val completed: Boolean = false
): Parcelable

sealed class TodoFormEvent {
    data class TitleChanged(val title: String): TodoFormEvent()
    data class DescriptionChanged(val description: String): TodoFormEvent()
    data class TimeStampChanged(val timeStamp: String): TodoFormEvent()
    data class DueDateChanged(val dueDate: String): TodoFormEvent()
    data class CompletedChanged(val id: Todo, val completed: Boolean): TodoFormEvent()
    data class DeleteTodo(val id: Todo): TodoFormEvent()
    data object Submit: TodoFormEvent()
}

class TodoViewModel: ViewModel() {
    private val _todoList: MutableStateFlow<List<Todo>> = MutableStateFlow(emptyList())
    val todoList: StateFlow<List<Todo>> = _todoList.asStateFlow()
    val flag = mutableStateOf(false)
    var data by mutableStateOf(Todo())
    val snackBarHostState = SnackbarHostState()

    fun onEvent(event: TodoFormEvent) {
        when(event) {
            is TodoFormEvent.CompletedChanged -> {
                findAndChangeCompletedStateTodo(
                    event.id, event.completed
                )
            }
            is TodoFormEvent.DescriptionChanged -> {
                data = data.copy(description = event.description)

            }
            is TodoFormEvent.DueDateChanged -> {
                data = data.copy(dueDate = event.dueDate)

            }
            TodoFormEvent.Submit -> {
                saveNewTodo()
            }
            is TodoFormEvent.TimeStampChanged -> {
                data = data.copy(timeStamp = event.timeStamp)

            }
            is TodoFormEvent.TitleChanged -> {
                data = data.copy(title = event.title)

            }

            is TodoFormEvent.DeleteTodo -> {
                deleteTodo(event.id)
            }
        }
    }

    private fun findAndChangeCompletedStateTodo(selectedItem: Todo, completedState: Boolean) {
        val updatedList = _todoList.value.toMutableList().apply {
            val indexTodo = indexOf(selectedItem)
            this[indexTodo] = this[indexTodo].copy(completed = completedState)
        }

        _todoList.value = updatedList
    }

    private fun deleteTodo(selectedItem: Todo) {
        val updateList = _todoList.value.toMutableList().apply {
            this.remove(selectedItem)
        }

        _todoList.value = updateList
    }

    private fun showSnackBar(message: String) {
        viewModelScope.launch {
            snackBarHostState.showSnackbar(
                message,
                duration = SnackbarDuration.Short
            )
        }
    }

    private fun verifyIfExistTheSameTodo(todo: Todo): Boolean {
        return _todoList.value.any { it.title == todo.title }
    }

    private fun saveNewTodo() {
        flag.value = false
        val titleResult = ValidateField().execute(data.title)
        val descriptionResult = ValidateField().execute(data.description)
        val dueDate = ValidateField().execute(data.dueDate)

        val hasError = listOf(
            titleResult,
            descriptionResult,
            dueDate
        ).any { !it.state }

        if (hasError) {
            showSnackBar("Llene todos los campos")
            return
        }

        val newTodo = Todo(
            id = data.id,
            title = data.title,
            description = data.description,
            timeStamp = data.timeStamp,
            dueDate = data.dueDate,
            completed = data.completed
        )

        if (verifyIfExistTheSameTodo(newTodo)) {
            showSnackBar("Ya existe un todo con el mismo titulo")
            return
        }

        val updateList = _todoList.value.toMutableList().apply {
            this.add(newTodo)
        }

        _todoList.value = updateList
        data = data.copy(title = "")
        data = data.copy(description = "")
        data = data.copy(dueDate = "")
        flag.value = true
        showSnackBar("¡Agregado correctamente!")
    }
}