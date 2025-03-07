package com.xm.examples.coroutine.store

sealed class GlobalAction {
    data class Survey (val action: SurveyAction): GlobalAction()
    data class Home (val action: HomeAction): GlobalAction()
}
