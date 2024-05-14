package com.enriselle.todochan.data.domain.usecase

import android.util.Patterns

class ValidateField {
    fun execute(text: String): ValidationResponse {
        if (text.isBlank()) {
            return ValidationResponse(
                state = false,
                errorMessage = "El titulo esta vacio"
            )
        }
        
        return ValidationResponse(
            state = true
        )
    }
}