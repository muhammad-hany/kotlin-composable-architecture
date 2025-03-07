package com.xm.examples.coroutine.store

import com.xm.examples.coroutine.data.model.Question

sealed class HomeAction {
    data object GetQuestions : HomeAction()
    data class QuestionsLoaded(val result: Result<List<Question>>) : HomeAction()
}