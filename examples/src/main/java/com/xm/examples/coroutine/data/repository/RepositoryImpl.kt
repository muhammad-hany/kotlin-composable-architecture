package com.xm.examples.coroutine.data.repository

import com.xm.examples.coroutine.api.SurveyApi
import com.xm.examples.coroutine.data.model.Answer
import com.xm.examples.coroutine.data.model.Question
import com.xm.examples.coroutine.store.AnswerFailure
import com.xm.examples.coroutine.store.AnswerSubmission
import com.xm.examples.coroutine.store.AnswerSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class RepositoryImpl(private val api: SurveyApi) : Repository {

    private val questions = mutableListOf<Question>()

    override fun getQuestions(): Flow<Result<List<Question>>> = flow {
        if (questions.isNotEmpty())  emit(Result.success(questions))
        try {
            val response= api.getQuestions()
            questions.clear()
            questions.addAll(response)
            emit(Result.success(response))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override fun getInMemoryQuestions(): List<Question> = questions

    override fun submitAnswer(answer: Answer): Flow<AnswerSubmission> = flow {
        try {
            api.submitAnswer(answer)
            emit(AnswerSuccess(answer))
        } catch (e: Exception) {
            emit(AnswerFailure(e, answer))
        }
    }

}