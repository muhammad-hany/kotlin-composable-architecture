package com.xm.examples.coroutine.api

import com.xm.examples.coroutine.data.model.Answer
import com.xm.examples.coroutine.data.model.Question
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface SurveyApi {

    @GET("questions")
    suspend fun getQuestions(): List<Question>

    @POST("question/submit")
    suspend fun submitAnswer(@Body answer: Answer): Unit
}