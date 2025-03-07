package com.xm.examples.coroutine.data.repository

import com.xm.examples.coroutine.data.model.Answer
import com.xm.examples.coroutine.data.model.Question
import com.xm.examples.coroutine.store.AnswerSubmission
import kotlinx.coroutines.flow.Flow

interface Repository {
    fun getQuestions(): Flow<Result<List<Question>>>
    fun getInMemoryQuestions(): List<Question>
    fun submitAnswer(answer: Answer): Flow<AnswerSubmission>
}