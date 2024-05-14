package com.enriselle.todochan.features

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun Details(
    onBackClick: () -> Unit,
    todo: Todo?
) {
    Column {
        todo?.let {
            Text(text = it.title)
            Text(text = it.description)
            Text(text = it.dueDate)
        }
    }
}