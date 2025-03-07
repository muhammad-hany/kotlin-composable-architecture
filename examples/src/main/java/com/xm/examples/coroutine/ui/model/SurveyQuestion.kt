package com.xm.examples.coroutine.ui.model

import com.xm.examples.coroutine.data.model.Answer
import com.xm.examples.coroutine.data.model.Question

data class SurveyQuestion (
    val question: Question,
    val answer: Answer? = null,
    val hasError: Boolean = false
) {
    val successfullyAnswered: Boolean = answer != null && !hasError
}