package com.xm.examples.coroutine.store

import com.xm.examples.coroutine.data.model.Answer
import com.xm.examples.coroutine.ui.model.SurveyQuestion
import com.xm.tka.Effects
import com.xm.tka.coroutine.FlowReduceContext
import com.xm.tka.coroutine.FlowReduced
import com.xm.tka.toEffect
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

fun HomeAction.GetQuestions.reduced(
    context: FlowReduceContext<HomeState, HomeAction>,
    state: HomeState,
    env: SurveyEnvironment
): FlowReduced<HomeState, HomeAction> = with(context) {
    // do nothing if questions already loaded
    if (state.questions.isNotEmpty()) return@with state + Effects.flowNone()
    state.copy(
        isLoading = true
    ) + env.repository.getQuestions()
        .flowOn(Dispatchers.IO)
        .map { HomeAction.QuestionsLoaded(it) }
        .toEffect()
}

fun SurveyAction.ResetQuestion.reduced(
    context: FlowReduceContext<SurveyState, SurveyAction>,
    state: SurveyState,
): FlowReduced<SurveyState, SurveyAction> = with(context) {
    state.copy(
        surveyQuestions = state.surveyQuestions.map {
            it.copy(
                answer = null,
                hasError = false
            )
        }
    ) + Effects.flowNone()
}

fun SurveyAction.SubmitAnswer.reduced(
    context: FlowReduceContext<SurveyState, SurveyAction>,
    state: SurveyState,
    env: SurveyEnvironment
): FlowReduced<SurveyState, SurveyAction> = with(context) {
    val answer = Answer(id = id, answerText = answerText)
    state.copy(
        isLoading = true
    ) + env.repository.submitAnswer(answer)
        .flowOn(Dispatchers.IO)
        .map { SurveyAction.AnswerSubmitted(it) }
        .toEffect()
}

fun HomeAction.QuestionsLoaded.reduced(
    context: FlowReduceContext<HomeState, HomeAction>,
    state: HomeState,
): FlowReduced<HomeState, HomeAction> = with(context) {
    result.fold(
        onSuccess = { data ->
            state.copy(
                questions = data,
                isLoading = false,
                error = null
            ) + Effects.flowNone()
        },
        onFailure = { _ ->
            state.copy(
                error = "issue happened while fetching survey",
                isLoading = false
            ) + Effects.flowNone()
        }
    )
}

fun SurveyAction.CreateNewSurvey.reduced(
    context: FlowReduceContext<SurveyState, SurveyAction>,
    state: SurveyState,
    env: SurveyEnvironment
): FlowReduced<SurveyState, SurveyAction> = with(context) {
    if (state.surveyQuestions.isNotEmpty()) return@with state + Effects.flowNone()
    state + Effects.justFlow(
        SurveyAction.SurveyLoaded(
            env.repository
                .getInMemoryQuestions()
                .map { SurveyQuestion(it) }
        )
    )
}

fun SurveyAction.SurveyLoaded.reduced(
    context: FlowReduceContext<SurveyState, SurveyAction>,
    state: SurveyState
): FlowReduced<SurveyState, SurveyAction> = with(context) {
    state.copy(
        surveyQuestions = surveyQuestions,
        isLoading = false
    ) + Effects.flowNone()
}


fun SurveyAction.AnswerSubmitted.reduced(
    context: FlowReduceContext<SurveyState, SurveyAction>,
    state: SurveyState,
): FlowReduced<SurveyState, SurveyAction> = with(context) {
    val surveyQuestionIndex =
        state.surveyQuestions.indexOfFirst { it.question.id == result.answer.id }
    if (surveyQuestionIndex == -1) return@with state + Effects.flowNone()
    val hasError = result is AnswerFailure
    state.copy(
        surveyQuestions = state.surveyQuestions.updatedWith(
            answer = result.answer,
            hasError = hasError
        ),
        isLoading = false
    ) + Effects.flowNone()
}

fun List<SurveyQuestion>.updatedWith(answer: Answer, hasError: Boolean): List<SurveyQuestion> {
    return toMutableList().apply {
        val surveyQuestionIndex = indexOfFirst { it.question.id == answer.id }
        if (surveyQuestionIndex == -1) return this
        this[surveyQuestionIndex] =
            this[surveyQuestionIndex].copy(answer = answer, hasError = hasError)
    }
}
