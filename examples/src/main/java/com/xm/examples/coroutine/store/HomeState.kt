package com.xm.examples.coroutine.store

import com.xm.examples.coroutine.data.model.Question

data class HomeState (
    val questions: List<Question> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)