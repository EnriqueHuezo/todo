package com.enriselle.todochan.data.domain.usecase

data class ValidationResponse(
    val state: Boolean,
    val errorMessage: String? = null
)