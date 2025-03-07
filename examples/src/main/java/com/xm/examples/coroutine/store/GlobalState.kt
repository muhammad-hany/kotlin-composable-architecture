package com.xm.examples.coroutine.store

data class GlobalState (
    val survey: SurveyState = SurveyState(),
    val home: HomeState = HomeState()
)
